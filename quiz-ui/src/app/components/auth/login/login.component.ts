import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {first} from 'rxjs/operators';
import {HttpErrorResponse} from '@angular/common/http';
import {AuthService} from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage: string | null = null;
  returnUrl: string = '/';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    if (this.authService.currentUserValue) {
      this.router.navigate(['/']);
    }

    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  onSubmit(): void {
    this.isLoading = true;
    this.errorMessage = null;

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      this.isLoading = false;
      return;
    }

    const {username, password} = this.loginForm.value;

    this.authService.login({username, password})
      .pipe(first())
      .subscribe(
        () => {
          this.router.navigate([this.returnUrl]);
        },
        (error: HttpErrorResponse) => {
          console.error('Login error:', error);
          this.errorMessage = 'Login failed. Please check your username and password.';
          if (error.status === 401) {
            this.errorMessage = 'Invalid username or password.';
          } else if (error.error && error.error.message) {
            this.errorMessage = error.error.message;
          }
          this.isLoading = false;
        }
      );
  }
}
