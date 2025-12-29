import { Injectable, Inject, Optional } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Configuration } from '../api/configuration';

export interface AnalysisRequest {
  includeHoldings?: boolean;
  prompt?: string;
}

export interface AnalysisResponse {
  sessionId: string;
  summary: string;
  details?: string[];
  createdAt?: string;
}

export interface FollowUpRequest {
  question: string;
}

export interface FollowUpResponse {
  sessionId: string;
  answer: string;
}

@Injectable({ providedIn: 'root' })
export class AnalysisService {
  // Simple in-memory mock sessions map
  private mockSessions: Record<string, { history: {role: string, text: string}[] }> = {};

  constructor(private http: HttpClient, @Optional() private config?: Configuration) {}

  startAnalysis(req: AnalysisRequest): Observable<AnalysisResponse> {
    const url = `${this.config?.basePath ?? ''}/portfolio/analysis`;
    // Try real backend, fall back to mock on error
    return this.http.post<AnalysisResponse>(url, req).pipe(
      catchError((err) => {
        console.warn('Analysis endpoint unavailable, using mock', err);
        return of(this.mockStartAnalysis(req));
      })
    );
  }

  askFollowUp(sessionId: string, req: FollowUpRequest): Observable<FollowUpResponse> {
    const url = `${this.config?.basePath ?? ''}/portfolio/analysis/${encodeURIComponent(sessionId)}/query`;
    return this.http.post<FollowUpResponse>(url, req).pipe(
      catchError((err) => {
        console.warn('Follow-up endpoint unavailable, using mock', err);
        return of(this.mockFollowUp(sessionId, req));
      })
    );
  }

  // --- Mock implementations ---
  private mockStartAnalysis(req: AnalysisRequest): AnalysisResponse {
    const sessionId = `mock-${Date.now()}`;
    const summary = `Your portfolio looks healthy with a diversified allocation. Key recommendation: rebalance to reduce concentration in top holdings.`;
    const details = [
      'Top holding concentration: 28% in single stock',
      'Sector tilt: overweight in Technology',
      'Consider increasing exposure to bonds for risk reduction',
    ];

    this.mockSessions[sessionId] = { history: [{ role: 'assistant', text: summary }] };

    return {
      sessionId,
      summary,
      details,
      createdAt: new Date().toISOString(),
    };
  }

  private mockFollowUp(sessionId: string, req: FollowUpRequest): FollowUpResponse {
    const session = this.mockSessions[sessionId];
    if (!session) {
      // Unknown session -> create a new one
      const resp = this.mockStartAnalysis({ includeHoldings: true });
      return { sessionId: resp.sessionId, answer: `Started new mock session. ${resp.summary}` };
    }

    // Very simple pseudo-AI: echo question + canned answers
    const answer = `I understand you asked: "${req.question}". Based on your portfolio, here's a helpful tip: review positions with high concentration and consider diversification.`;
    session.history.push({ role: 'user', text: req.question });
    session.history.push({ role: 'assistant', text: answer });

    return { sessionId, answer };
  }
}
