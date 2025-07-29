import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {HttpErrorResponse} from '@angular/common/http';
import {User} from '../../models/user.model';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  usernameForm: FormGroup;
  passwordForm: FormGroup;
  currentUser: User | null;

  usernameLoading = false;
  usernameMessage: string | null = null;
  usernameSuccess: boolean = false;

  passwordLoading = false;
  passwordMessage: string | null = null;
  passwordSuccess: boolean = false;

  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.currentUser = this.authService.currentUserValue;

    this.usernameForm = this.fb.group({
      newUsername: [this.currentUser?.username || '', [Validators.required, Validators.minLength(3)]]
    });

    this.passwordForm = this.fb.group({
      oldPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmNewPassword: ['', Validators.required]
    }, {validators: this.passwordMatchValidator});
  }

  ngOnInit(): void {
  }

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const newPassword = control.get('newPassword');
    const confirmNewPassword = control.get('confirmNewPassword');

    if (!newPassword || !confirmNewPassword) {
      return null;
    }

    return newPassword.value === confirmNewPassword.value ? null : {passwordsMismatch: true};
  }

  onUpdateUsername(): void {
    this.usernameLoading = true;
    this.usernameMessage = null;
    this.usernameSuccess = false;

    if (this.usernameForm.invalid) {
      this.usernameForm.markAllAsTouched();
      this.usernameLoading = false;
      return;
    }

    const {newUsername} = this.usernameForm.value;
    this.authService.updateUsername({newUsername}).subscribe(
      () => {
        this.usernameMessage = 'Username updated successfully! Please log in again.';
        this.usernameSuccess = true;
        this.authService.logout();
      },
      (error: HttpErrorResponse) => {
        this.usernameMessage = error.error || 'Failed to update username.';
        this.usernameSuccess = false;
        console.error('Username update error:', error);
      }
    ).add(() => {
      this.usernameLoading = false;
    });
  }

  onChangePassword(): void {
    this.passwordLoading = true;
    this.passwordMessage = null;
    this.passwordSuccess = false;

    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      this.passwordLoading = false;
      return;
    }

    const {oldPassword, newPassword} = this.passwordForm.value;
    this.authService.changePassword({oldPassword, newPassword}).subscribe(
      () => {
        this.passwordMessage = 'Password changed successfully! Please log in again.';
        this.passwordSuccess = true;
        this.passwordForm.reset();
        this.authService.logout();
      },
      (error: HttpErrorResponse) => {
        this.passwordMessage = error.error || 'Failed to change password.';
        this.passwordSuccess = false;
        console.error('Password change error:', error);
      }
    ).add(() => {
      this.passwordLoading = false;
    });
  }
}
