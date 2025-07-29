import {Component, OnInit} from '@angular/core';
import {QuizService} from '../../services/quiz.service';
import {AttemptService} from '../../services/attempt.service';
import {Router} from '@angular/router';
import {QuizResponse} from '../../models/quizResponse.model';
import {AttemptStartResponse} from '../../models/AttemptStartResponse.model';
import {AuthService} from '../../services/auth.service';

declare var bootstrap: any;

@Component({
  selector: 'app-quiz-list',
  templateUrl: './quiz-list.component.html',
  styleUrls: ['./quiz-list.component.scss']
})
export class QuizListComponent implements OnInit {
  quizzes: QuizResponse[] = [];
  isLoading = true;
  quizSubmittedScore: number | null = null;
  quizSubmittedTotal: number | null = null;
  quizSubmittedTitle: string | null = null;
  isAdmin = false;

  constructor(
    private quizService: QuizService,
    private attemptService: AttemptService,
    private router: Router,
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.loadQuizzes();
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras.state) {
      this.quizSubmittedScore = navigation.extras.state['score'];
      this.quizSubmittedTotal = navigation.extras.state['totalQuestions'];
      this.quizSubmittedTitle = navigation.extras.state['quizTitle'];
      this.showMarksPopup();
    }
  }

  loadQuizzes(): void {
    this.isLoading = true;
    this.quizService.getAllQuizzes().subscribe(
      data => {
        this.quizzes = data;
        this.isLoading = false;
      },
      error => {
        console.error('Error fetching quizzes:', error);
        this.isLoading = false;
        alert('Failed to load quizzes.');
      }
    );
  }

  startQuiz(quizId: number): void {
    this.attemptService.startAttempt(quizId).subscribe(
      (attemptResponse: AttemptStartResponse) => {
        this.router.navigate(['/quizzes/attempt', quizId], {state: {attemptId: attemptResponse.id}});
      },
      error => {
        console.error('Error starting quiz attempt:', error);
        alert('Failed to start quiz. Please try again.');
      }
    );
  }

  editQuiz(id: number): void {
    this.router.navigate(['/quizzes/update', id]);
  }

  deleteQuiz(id: number): void {
    if (confirm('Are you sure you want to delete this quiz? This action cannot be undone.')) {
      this.quizService.deleteQuiz(id).subscribe(
        () => {
          alert('Quiz deleted successfully!');
          this.loadQuizzes();
        },
        error => {
          console.error('Error deleting quiz:', error);
          alert('Failed to delete quiz. It might have existing attempts.');
        }
      );
    }
  }

  showMarksPopup(): void {
    if (this.quizSubmittedScore !== null && this.quizSubmittedTotal !== null && this.quizSubmittedTitle !== null) {
      const myModal = new bootstrap.Modal(document.getElementById('marksModal'), {
        keyboard: false
      });
      myModal.show();
    }
  }

  closeMarksPopup(): void {
    this.quizSubmittedScore = null;
    this.quizSubmittedTotal = null;
    this.quizSubmittedTitle = null;
    const myModalEl = document.getElementById('marksModal');
    if (myModalEl) {
      const modalInstance = bootstrap.Modal.getInstance(myModalEl);
      if (modalInstance) {
        modalInstance.hide();
      }
    }
  }
}
