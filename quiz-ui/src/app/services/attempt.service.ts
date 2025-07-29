import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Attempt } from '../models/attempt.model';
import { AttemptSubmissionRequest } from '../models/attemptSubmissionRequest.model';
import { AttemptResult } from '../models/attemptResult.model';
import { AttemptStartResponse } from '../models/AttemptStartResponse.model';

@Injectable({
  providedIn: 'root',
})
export class AttemptService {
  private apiUrl = 'http://localhost:8080/api/attempts';

  constructor(private http: HttpClient) {}

  startAttempt(quizId: number): Observable<AttemptStartResponse> {
    return this.http.post<AttemptStartResponse>(
      `${this.apiUrl}/start/${quizId}`,
      {}
    );
  }

  submitAttempt(
    attemptId: number,
    submission: AttemptSubmissionRequest
  ): Observable<AttemptResult> {
    return this.http.post<AttemptResult>(
      `${this.apiUrl}/submit/${attemptId}`,
      submission
    );
  }

  getAttemptHistory(): Observable<AttemptResult[]> {
    return this.http.get<AttemptResult[]>(`${this.apiUrl}/history`);
  }

  getAttemptDetails(attemptId: number): Observable<AttemptResult> {
    return this.http.get<AttemptResult>(`${this.apiUrl}/${attemptId}`);
  }
}
