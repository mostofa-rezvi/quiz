import {QuizType} from './quizType.model';
import {Question} from './question.model';

export interface Quiz {
  id?: number;
  title: string;
  durationMinutes: number;
  type: QuizType;
  questions?: Question[];

  setValue(b: boolean): void;
}
