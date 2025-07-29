import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors} from '@angular/forms';
import {Router} from '@angular/router';
import {first} from 'rxjs/operators';
import {HttpErrorResponse} from '@angular/common/http';
import {AuthService} from '../../../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  isLoading = false;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    if (this.authService.currentUserValue) {
      this.router.navigate(['/']);
    }

    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, {validators: this.passwordMatchValidator});
  }

  ngOnInit(): void {
  }

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (!password || !confirmPassword) {
      return null;
    }

    return password.value === confirmPassword.value ? null : {passwordsMismatch: true};
  }

  onSubmit(): void {
    this.isLoading = true;
    this.errorMessage = null;

    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      this.isLoading = false;
      return;
    }

    const {username, password} = this.registerForm.value;

    this.authService.register({username, password})
      .pipe(first())
      .subscribe(
        () => {
          alert('Registration successful! You are now logged in.');
          this.router.navigate(['/quizzes']);
        },
        (error: HttpErrorResponse) => {
          console.error('Registration error:', error);
          this.errorMessage = 'Registration failed. Please try again.';
          if (error.error && error.error.message) {
            this.errorMessage = error.error.message;
          }
          this.isLoading = false;
        }
      );
  }
}
