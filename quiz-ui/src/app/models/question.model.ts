import {Option} from './option.model';
import {QuestionType} from './questionType.model';

export interface Question {
  id?: number; // Optional, only for existing questions
  text: string;
  type: QuestionType;
  options?: Option[]; // For MCQ
  correctAnswer?: string; // For SHORT_ANSWER
}
