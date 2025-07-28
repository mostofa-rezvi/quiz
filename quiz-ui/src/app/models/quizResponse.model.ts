import {QuizType} from './quizType.model';

export interface QuizResponse {
  id: number;
  title: string;
  durationMinutes: number;
  type: QuizType;
  numberOfQuestions: number;
}
