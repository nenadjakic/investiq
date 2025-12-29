import { CommonModule } from '@angular/common';
import { Component, OnInit, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { AnalysisService, AnalysisResponse, FollowUpResponse } from '../../app/core/services/analysis.service';
import { Chat } from '../../shared/chat/chat';

const STORAGE_KEY = 'chat:analysis:v1';

@Component({
  selector: 'app-analysis',
  standalone: true,
  imports: [CommonModule, FormsModule, Chat],
  templateUrl: './analysis.html',
})
export class Analysis implements OnInit {
  private analysisService = inject(AnalysisService);

  loading = signal<boolean>(false);
  error = signal<string | null>(null);
  sessionId = signal<string | null>(null);
  summary = signal<string | null>(null);
  details = signal<string[]>([]);

  // chat
  messages = signal<{role: 'user' | 'assistant', text: string}[]>([]);
  input = signal<string>('');
  chatDisabled = signal<boolean>(true);

  private analysisTimeoutHandle: any = null;
  private analysisSub: Subscription | null = null;

  ngOnInit(): void {
    // load persisted chat if present
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) {
        const parsed = JSON.parse(raw);
        if (parsed?.messages) {
          this.messages.set(parsed.messages);
        }
        if (parsed?.sessionId) {
          this.sessionId.set(parsed.sessionId);
          // If session exists, enable chat
          this.chatDisabled.set(false);
        }
      }
    } catch (e) {
      console.warn('Failed to load chat from storage', e);
    }
  }

  private saveMessages() {
    try {
      const payload = {
        sessionId: this.sessionId(),
        messages: this.messages(),
        savedAt: new Date().toISOString(),
      };
      localStorage.setItem(STORAGE_KEY, JSON.stringify(payload));
    } catch (e) {
      console.warn('Failed to save chat to storage', e);
    }
  }

  startAnalysis(): void {
    this.loading.set(true);
    this.error.set(null);
    this.summary.set(null);
    this.details.set([]);
    // Clear any existing chat messages when starting a new analysis
    this.messages.set([]);
    this.saveMessages();

    this.analysisService.startAnalysis({ includeHoldings: true }).subscribe({
      next: (res: AnalysisResponse) => {
        this.sessionId.set(res.sessionId);
        this.summary.set(res.summary);
        this.details.set(res.details ?? []);
        // Do NOT auto-insert summary into chat here; allow specific flows to decide
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Analysis failed', err);
        this.error.set('Failed to run analysis');
        this.loading.set(false);
      }
    });
  }

  /**
   * Run analysis invoked from the chat component's Run Analysis button.
   * This will run analysis, insert assistant summary into chat and enable the chat input.
   */
  startAnalysisFromChat(): void {
    if (this.loading()) return;
    this.chatDisabled.set(true);
    this.loading.set(true);
    this.error.set(null);
    this.summary.set(null);
    this.details.set([]);
    this.messages.set([]);
    this.saveMessages();

    // Safety timeout: clear disabled state if request doesn't complete within 15s
    if (this.analysisTimeoutHandle) {
      clearTimeout(this.analysisTimeoutHandle);
    }
    this.analysisTimeoutHandle = setTimeout(() => {
      if (this.loading()) {
        console.warn('Analysis appears to be taking too long — clearing disabled state');
        this.loading.set(false);
        this.chatDisabled.set(false);
        this.error.set('Analysis timed out — please try again.');
      }
    }, 15000);

    this.analysisSub = this.analysisService.startAnalysis({ includeHoldings: true }).subscribe({
      next: (res: AnalysisResponse) => {
        this.sessionId.set(res.sessionId);
        this.summary.set(res.summary);
        this.details.set(res.details ?? []);
        // Insert summary into chat and enable chat interaction
        this.messages.set([{ role: 'assistant', text: res.summary }]);
        this.chatDisabled.set(false);
        this.loading.set(false);
        if (this.analysisTimeoutHandle) {
          clearTimeout(this.analysisTimeoutHandle);
          this.analysisTimeoutHandle = null;
        }
        this.analysisSub = null;
        this.saveMessages();
      },
      error: (err) => {
        console.error('Analysis failed', err);
        this.error.set('Failed to run analysis');
        // Allow the user to try again — don't keep the chat permanently disabled
        this.chatDisabled.set(false);
        this.loading.set(false);
        if (this.analysisTimeoutHandle) {
          clearTimeout(this.analysisTimeoutHandle);
          this.analysisTimeoutHandle = null;
        }
        this.analysisSub = null;
      }
    });
  }

  cancelAnalysis(): void {
    if (this.analysisSub) {
      this.analysisSub.unsubscribe();
      this.analysisSub = null;
    }
    if (this.analysisTimeoutHandle) {
      clearTimeout(this.analysisTimeoutHandle);
      this.analysisTimeoutHandle = null;
    }
    this.loading.set(false);
    this.chatDisabled.set(false);
    this.error.set('Analysis cancelled');
  }

  startConversation(): void {
    if (!this.sessionId() || !this.summary()) return;
    if (this.messages().length > 0) return;
    this.messages.set([{ role: 'assistant', text: this.summary()! }]);
    this.chatDisabled.set(false);
    this.saveMessages();
  }

  resetConversation(): void {
    // Clear state and storage to start fresh
    this.messages.set([]);
    this.sessionId.set(null);
    this.summary.set(null);
    this.details.set([]);
    this.chatDisabled.set(true);
    this.loading.set(false);
    this.error.set(null);
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch (e) {
      console.warn('Failed to clear chat storage', e);
    }
  }

  onChatSend(text: string): void {
    if (!this.sessionId()) {
      this.messages.set([...this.messages(), { role: 'assistant', text: 'Please run the analysis first to start a session.' }]);
      this.saveMessages();
      return;
    }

    if (!text) return;
    this.messages.set([...this.messages(), { role: 'user', text }]);
    this.saveMessages();

    this.analysisService.askFollowUp(this.sessionId()!, { question: text }).subscribe({
      next: (res: FollowUpResponse) => {
        this.messages.set([...this.messages(), { role: 'assistant', text: res.answer }]);
        this.saveMessages();
      },
      error: (err) => {
        console.error('Follow-up failed', err);
        this.messages.set([...this.messages(), { role: 'assistant', text: 'Sorry, failed to get an answer.' }]);
        this.saveMessages();
      }
    });
  }
}
