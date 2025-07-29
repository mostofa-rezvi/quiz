import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormArray, Validators, AbstractControl} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {QuizService} from '../../services/quiz.service';
import {HttpErrorResponse} from '@angular/common/http';
import {QuizType} from '../../models/quizType.model';
import {QuestionType} from '../../models/questionType.model';
import {Quiz} from '../../models/quiz.model';

@Component({
  selector: 'app-quiz-create',
  templateUrl: './quiz-create.component.html',
  styleUrl: './quiz-create.component.scss'
})
export class QuizCreateComponent implements OnInit {
  quizForm: FormGroup;
  quizId: number | null = null;
  isEditMode = false;
  selectedFile: File | null = null;
  fileUploadMessage: string = '';
  quizTypes = Object.values(QuizType);
  questionTypes = Object.values(QuestionType);

  constructor(
    private fb: FormBuilder,
    private quizService: QuizService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.quizForm = this.fb.group({
      title: ['', Validators.required],
      durationMinutes: ['', [Validators.required, Validators.min(1)]],
      type: [QuizType.MCQ, Validators.required],
      questions: this.fb.array([], Validators.required)
    });
  }

  ngOnInit(): void {
    this.quizId = this.route.snapshot.params['id'];
    if (this.quizId) {
      this.isEditMode = true;
      this.loadQuizForEdit(this.quizId);
    }
  }

  loadQuizForEdit(id: number): void {
    this.quizService.getQuizById(id).subscribe(
      (quiz: Quiz) => {
        this.quizForm.patchValue({
          title: quiz.title,
          durationMinutes: quiz.durationMinutes,
          type: quiz.type
        });

        this.questions.clear();

        quiz.questions?.forEach(q => {
          const questionGroup = this.fb.group({
            text: [q.text, Validators.required],
            type: [q.type, Validators.required],
            options: this.fb.array([]),
            correctAnswer: [q.correctAnswer || '']
          });

          if (q.type === QuestionType.MCQ && q.options) {
            const optionsArray = questionGroup.get('options') as FormArray;
            q.options.forEach(opt => {
              optionsArray.push(this.fb.group({
                text: [opt.text, Validators.required],
                correct: [opt.correct]
              }));
            });
          }
          this.questions.push(questionGroup);
        });
      },
      error => {
        console.error('Error loading quiz for edit', error);
        alert('Could not load quiz for editing.');
      }
    );
  }

  get questions(): FormArray {
    return this.quizForm.get('questions') as FormArray;
  }

  addQuestion(): void {
    const questionType = this.quizForm.get('type')?.value || QuestionType.MCQ;
    const questionGroup = this.fb.group({
      text: ['', Validators.required],
      type: [questionType, Validators.required],
      options: this.fb.array([], this.optionsValidator),
      correctAnswer: ['']
    });
    this.questions.push(questionGroup);
    this.updateQuestionValidators(questionGroup);
  }

  removeQuestion(index: number): void {
    this.questions.removeAt(index);
  }

  getOptions(question: AbstractControl): FormArray {
    return question.get('options') as FormArray;
  }

  addOption(questionIndex: number): void {
    const options = this.getOptions(this.questions.at(questionIndex));
    options.push(this.fb.group({
      text: ['', Validators.required],
      correct: [false]
    }));
  }

  removeOption(questionIndex: number, optionIndex: number): void {
    const options = this.getOptions(this.questions.at(questionIndex));
    options.removeAt(optionIndex);
  }

  optionsValidator(control: AbstractControl): { [key: string]: any } | null {
    const optionsArray = control as FormArray;
    if (!optionsArray || optionsArray.length === 0) {
      return {'noOptions': true};
    }
    if (optionsArray.length < 2) {
      return {'minTwoOptions': true};
    }
    const correctOptions = optionsArray.controls.filter(opt => opt.get('correct')?.value === true);
    if (correctOptions.length !== 1) {
      return {'oneCorrectOption': true};
    }
    return null;
  }

  onQuestionTypeChange(questionGroup: AbstractControl): void {
    this.updateQuestionValidators(questionGroup);
  }

  updateQuestionValidators(questionGroup: AbstractControl): void {
    const typeControl = questionGroup.get('type');
    const optionsControl = questionGroup.get('options');
    const correctAnswerControl = questionGroup.get('correctAnswer');

    if (typeControl?.value === QuestionType.MCQ) {
      optionsControl?.setValidators([Validators.required, this.optionsValidator]);
      correctAnswerControl?.clearValidators();
      correctAnswerControl?.setValue('');
    } else if (typeControl?.value === QuestionType.SHORT_ANSWER) {
      optionsControl?.clearValidators();
      if (optionsControl instanceof FormArray) {
        optionsControl.clear();
      }
      correctAnswerControl?.setValidators(Validators.required);
    }
    optionsControl?.updateValueAndValidity();
    correctAnswerControl?.updateValueAndValidity();
    questionGroup.updateValueAndValidity();
  }


  onSubmitManual(): void {
    if (this.quizForm.invalid) {
      this.quizForm.markAllAsTouched();
      alert('Please correct the form errors.');
      console.log('Form is invalid', this.quizForm.errors);
      return;
    }

    const quizData: Quiz = this.quizForm.value;

    if (this.isEditMode && this.quizId) {
      this.quizService.updateQuiz(this.quizId, quizData).subscribe(
        () => {
          alert('Quiz updated successfully!');
          this.router.navigate(['/quizzes']);
        },
        error => {
          console.error('Error updating quiz:', error);
          alert('Failed to update quiz. Please try again.');
        }
      );
    } else {
      this.quizService.createQuizManually(quizData).subscribe(
        () => {
          alert('Quiz created successfully!');
          this.router.navigate(['/quizzes']);
        },
        error => {
          console.error('Error creating quiz:', error);
          alert('Failed to create quiz. Please try again.');
        }
      );
    }
  }

  onFileSelected(event: any): void {
    const files = event.target.files;
    if (files && files.length > 0) {
      this.selectedFile = files[0];
      this.fileUploadMessage = `Selected: ${this.selectedFile?.name}`;
    } else {
      this.selectedFile = null;
      this.fileUploadMessage = '';
    }
  }

  onUploadExcel(): void {
    if (this.selectedFile) {
      this.quizService.uploadQuizExcel(this.selectedFile).subscribe(
        response => {
          this.fileUploadMessage = `Successfully uploaded ${response.length} quizzes!`;
          alert(this.fileUploadMessage);
          this.router.navigate(['/quizzes']);
        },
        (error: HttpErrorResponse) => {
          console.error('File upload error:', error);
          if (error.status === 400 && error.error) {
            this.fileUploadMessage = `Upload failed: ${error.error.message || 'Invalid Excel format.'}`;
            alert(this.fileUploadMessage);
          } else {
            this.fileUploadMessage = 'File upload failed. Please check console for details.';
            alert(this.fileUploadMessage);
          }
        }
      );
    } else {
      alert('Please select an Excel file to upload.');
    }
  }

  setCorrectOption(questionIndex: number, optionIndex: number): void {
    const optionsArray = this.getOptions(this.questions.at(questionIndex));
    optionsArray.controls.forEach(optionGroup => {
      optionGroup.get('correct')?.setValue(false);
    });
    optionsArray.at(optionIndex).get('correct')?.setValue(true);
  }

  protected readonly QuestionType = QuestionType;
}
