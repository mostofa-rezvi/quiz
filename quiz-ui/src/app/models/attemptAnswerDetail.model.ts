import {Option} from './option.model';
import {QuestionType} from './questionType.model';

export interface AttemptAnswerDetail {
  questionId: number;
  questionText: string;
  questionType: QuestionType;
  userAnswer: string;
  correctAnswer?: string;
  options?: Option[];
  isCorrect: boolean;
}
