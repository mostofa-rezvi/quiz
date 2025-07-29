import {AttemptAnswerDetail} from './attemptAnswerDetail.model';

export interface AttemptResult {
  id: number;
  quizId: number;
  quizTitle: string;
  startTime: string;
  endTime: string;
  score: number;
  totalQuestions: number;
  detailedAnswers: AttemptAnswerDetail[];
}

