import {QuizType} from './quizType.model';

export interface QuizResponse { // For listing quizzes
  id: number;
  title: string;
  durationMinutes: number;
  type: QuizType;
  numberOfQuestions: number;
}
