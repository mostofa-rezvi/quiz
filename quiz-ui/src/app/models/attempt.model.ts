export interface Attempt {
  id: number;
  quizId: number;
  startTime: string;
  endTime?: string;
  score: number;
}
