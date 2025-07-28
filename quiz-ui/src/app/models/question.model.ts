import {Option} from './option.model';
import {QuestionType} from './questionType.model';

export interface Question {
  id?: number;
  text: string;
  type: QuestionType;
  options?: Option[];
  correctAnswer?: string;
}
