import {QuizType} from './quizType.model';
import {Question} from './question.model';

export interface Quiz {
  id?: number; // Optional, only for existing quizzes
  title: string;
  durationMinutes: number;
  type: QuizType;
  questions?: Question[];

  setValue(b: boolean): void;
}
