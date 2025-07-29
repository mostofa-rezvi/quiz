import { Component, OnInit } from '@angular/core';
import { AttemptResult } from '../../models/attemptResult.model';
import { AttemptService } from '../../services/attempt.service';
import { QuestionType } from '../../models/questionType.model';

@Component({
  selector: 'app-quiz-result',
  templateUrl: './quiz-result.component.html',
  styleUrls: ['./quiz-result.component.scss'],
})
export class QuizResultComponent implements OnInit {
  attemptHistory: AttemptResult[] = [];
  selectedAttemptDetails: AttemptResult | null = null;
  isLoading = true;
  isLoadingDetails = false;
  error: string | null = null;

  currentPage: number = 1;
  itemsPerPage: number = 10;
  totalPages: number = 0;
  paginatedAttempts: AttemptResult[] = [];

  constructor(private attemptService: AttemptService) {}

  ngOnInit(): void {
    this.loadAttemptHistory();
  }

  loadAttemptHistory(): void {
    this.isLoading = true;
    this.attemptService.getAttemptHistory().subscribe(
      (data) => {
        this.attemptHistory = data.sort(
          (a, b) =>
            new Date(b.startTime).getTime() - new Date(a.startTime).getTime()
        );
        this.isLoading = false;
        this.calculatePagination();
      },
      (err) => {
        console.error('Error loading attempt history:', err);
        this.error = 'Failed to load quiz attempt history.';
        this.isLoading = false;
      }
    );
  }

  viewAttemptDetails(attemptId: number): void {
    this.isLoadingDetails = true;
    this.selectedAttemptDetails = null;
    this.attemptService.getAttemptDetails(attemptId).subscribe(
      (data) => {
        this.selectedAttemptDetails = data;
        this.isLoadingDetails = false;
      },
      (err) => {
        console.error('Error loading attempt details:', err);
        this.error = 'Failed to load attempt details.';
        this.isLoadingDetails = false;
      }
    );
  }

  clearDetails(): void {
    this.selectedAttemptDetails = null;
  }

  get QuestionType() {
    return QuestionType;
  }

  calculatePagination(): void {
    this.totalPages = Math.ceil(this.attemptHistory.length / this.itemsPerPage);
    if (this.currentPage > this.totalPages && this.totalPages > 0) {
      this.currentPage = this.totalPages;
    } else if (this.currentPage === 0 && this.totalPages > 0) {
      this.currentPage = 1;
    } else if (this.totalPages === 0) {
      this.currentPage = 1;
    }
  }

  updatePaginatedAttempts(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedAttempts = this.attemptHistory.slice(startIndex, endIndex);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePaginatedAttempts();
      this.clearDetails();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.goToPage(this.currentPage + 1);
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.goToPage(this.currentPage - 1);
    }
  }

  get pages(): number[] {
    return Array(this.totalPages)
      .fill(0)
      .map((x, i) => i + 1);
  }
}
