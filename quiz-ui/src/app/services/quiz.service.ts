import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { QuizResponse } from '../models/quizResponse.model';
import { Quiz } from '../models/quiz.model';

@Injectable({
  providedIn: 'root',
})
export class QuizService {
  private apiUrl = 'http://localhost:8080/api/quizzes';

  constructor(private http: HttpClient) {}

  getAllQuizzes(): Observable<QuizResponse[]> {
    return this.http.get<QuizResponse[]>(this.apiUrl);
  }

  getQuizById(id: number): Observable<Quiz> {
    return this.http.get<Quiz>(`${this.apiUrl}/${id}`);
  }

  createQuizManually(quiz: Quiz): Observable<QuizResponse> {
    return this.http.post<QuizResponse>(`${this.apiUrl}/manual`, quiz);
  }

  uploadQuizExcel(file: File): Observable<QuizResponse[]> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    return this.http.post<QuizResponse[]>(`${this.apiUrl}/upload`, formData);
  }

  updateQuiz(id: number, quiz: Quiz): Observable<QuizResponse> {
    return this.http.put<QuizResponse>(`${this.apiUrl}/${id}`, quiz);
  }

  deleteQuiz(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
