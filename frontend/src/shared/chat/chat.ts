import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, ViewChild, ElementRef, AfterViewInit, OnChanges, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.html',
})
export class Chat implements AfterViewInit, OnChanges {
  @Input() messages: { role: 'user' | 'assistant'; text: string }[] = [];
  @Input() placeholder: string = 'Type your message...';
  @Input() disabled: boolean = false;
  @Input() loading: boolean = false;
  @Input() loadingLabel: string = 'Processing…';
  @Input() runLabel: string = 'Run';
  @Input() cancelLabel: string = 'Cancel';
  @Output() send = new EventEmitter<string>();
  @Output() run = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  @ViewChild('messagesContainer') private messagesContainer!: ElementRef<HTMLDivElement>;

  input: string = '';

  ngAfterViewInit(): void {
    this.scrollToBottom();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['messages']) {
      setTimeout(() => this.scrollToBottom(), 0);
    }
  }

  onSend(): void {
    if (this.disabled) {
      this.run.emit();
      return;
    }

    const text = (this.input || '').trim();
    if (!text) return;
    this.send.emit(text);
    this.input = '';
    setTimeout(() => this.scrollToBottom(), 50);
  }

  onRun(): void {
    this.run.emit();
  }

  onCancel(): void {
    this.cancel.emit();
  }

  onKeydown(event: KeyboardEvent): void {
    if (this.disabled) return;
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.onSend();
    }
  }

  private scrollToBottom(): void {
    try {
      const el = this.messagesContainer?.nativeElement;
      if (el) {
        el.scrollTop = el.scrollHeight;
      }
    } catch (e) {
    }
  }
}
