import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {QuizService} from '../../services/quiz.service';
import {AttemptService} from '../../services/attempt.service';
import {Subscription, interval} from 'rxjs';
import {takeWhile} from 'rxjs/operators';
import {Quiz} from '../../models/quiz.model';
import {AttemptAnswer} from '../../models/attemptAnswer.model';
import {AttemptSubmissionRequest} from '../../models/attemptSubmissionRequest.model';
import {AttemptResult} from '../../models/attemptResult.model';
import {QuestionType} from '../../models/questionType.model';

@Component({
  selector: 'app-quiz-attempt',
  templateUrl: './quiz-attempt.component.html',
  styleUrls: ['./quiz-attempt.component.scss']
})
export class QuizAttemptComponent implements OnInit, OnDestroy {
  quiz: Quiz | null = null;
  attemptId: number | null = null;
  userAnswers: { [questionId: number]: string } = {};
  isLoading = true;
  error: string | null = null;

  timeLeft: number = 0;
  timerSubscription: Subscription | null = null;
  isQuizSubmitted = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private quizService: QuizService,
    private attemptService: AttemptService
  ) {
  }

  ngOnInit(): void {
    const quizIdParam = this.route.snapshot.paramMap.get('id');
    const attemptIdState = history.state.attemptId;

    if (quizIdParam && attemptIdState) {
      const quizId = +quizIdParam;
      this.attemptId = attemptIdState;

      this.loadQuiz(quizId);
    } else {
      this.error = 'Invalid quiz or attempt ID.';
      this.isLoading = false;
    }
  }

  loadQuiz(quizId: number): void {
    this.quizService.getQuizById(quizId).subscribe(
      quiz => {
        this.quiz = quiz;
        this.timeLeft = quiz.durationMinutes * 60;
        this.isLoading = false;
        this.startTimer();

        this.quiz.questions?.forEach(q => {
          this.userAnswers[q.id!] = '';
        });
      },
      error => {
        console.error('Error loading quiz:', error);
        this.error = 'Failed to load quiz details.';
        this.isLoading = false;
      }
    );
  }

  startTimer(): void {
    this.timerSubscription = interval(1000)
      .pipe(takeWhile(() => this.timeLeft > 0 && !this.isQuizSubmitted))
      .subscribe(() => {
        this.timeLeft--;
        if (this.timeLeft <= 0) {
          this.submitQuiz(true);
        }
      });
  }

  formatTime(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${this.pad(minutes)}:${this.pad(remainingSeconds)}`;
  }

  pad(num: number): string {
    return num < 10 ? '0' + num : '' + num;
  }

  onOptionChange(questionId: number, optionText: string): void {
    this.userAnswers[questionId] = optionText;
  }

  onShortAnswerChange(questionId: number, event: Event): void {
    this.userAnswers[questionId] = (event.target as HTMLInputElement).value;
  }

  submitQuiz(isTimerExpired: boolean = false): void {
    if (this.isQuizSubmitted) return;

    if (!confirm(isTimerExpired ? 'Time is up! Your quiz will be submitted automatically.' : 'Are you sure you want to submit the quiz?')) {
      return;
    }

    this.isQuizSubmitted = true;
    this.timerSubscription?.unsubscribe();

    const submissionAnswers: AttemptAnswer[] = [];
    this.quiz?.questions?.forEach(q => {
      submissionAnswers.push({
        questionId: q.id!,
        userAnswer: this.userAnswers[q.id!] || ''
      });
    });

    const submissionRequest: AttemptSubmissionRequest = {
      answers: submissionAnswers
    };

    if (this.attemptId) {
      this.attemptService.submitAttempt(this.attemptId, submissionRequest).subscribe(
        (result: AttemptResult) => {
          alert(`Quiz submitted! Your score: ${result.score}/${result.totalQuestions}`);
          this.router.navigate(['/quizzes'], {
            state: {
              score: result.score,
              totalQuestions: result.totalQuestions,
              quizTitle: result.quizTitle
            }
          });
        },
        submitError => {
          console.error('Error submitting quiz:', submitError);
          alert('Failed to submit quiz. Please try again.');
          this.isQuizSubmitted = false;
        }
      );
    }
  }

  ngOnDestroy(): void {
    this.timerSubscription?.unsubscribe();
  }

  get QuestionType() {
    return QuestionType;
  }
}
