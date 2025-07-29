import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { AuthResponse } from '../models/authResponse.model';
import { LoginRequest } from '../models/loginRequest.model';
import { RegisterRequest } from '../models/registerRequest.model';
import { User } from '../models/user.model';
import { Role } from '../models/role.model';
import { UserUpdateRequest } from '../models/userUpdateRequest.model';
import { PasswordChangeRequest } from '../models/passwordChangeRequest.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authApiUrl = 'http://localhost:8080/api/auth';
  private userManagementApiUrl = 'http://localhost:8080/api/users';
  private userSubject: BehaviorSubject<User | null>;
  public user: Observable<User | null>;

  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    let initialUser: User | null = null;
    if (isPlatformBrowser(this.platformId)) {
      initialUser = JSON.parse(localStorage.getItem('currentUser') || 'null');
    }
    this.userSubject = new BehaviorSubject<User | null>(initialUser);
    this.user = this.userSubject.asObservable();
  }

  public get currentUserValue(): User | null {
    return this.userSubject.value;
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.authApiUrl}/login`, request).pipe(
      tap(response => {
        const user: User = {
          username: response.username,
          role: response.role as Role,
          token: response.token
        };
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('currentUser', JSON.stringify(user));
        }
        this.userSubject.next(user);
      })
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.authApiUrl}/register`, request).pipe(
      tap(response => {
        const user: User = {
          username: response.username,
          role: response.role as Role,
          token: response.token
        };
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('currentUser', JSON.stringify(user));
        }
        this.userSubject.next(user);
      })
    );
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('currentUser');
    }
    this.userSubject.next(null);
    this.router.navigate(['/login']);
  }

  updateCurrentUser(user: User): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('currentUser', JSON.stringify(user));
    }
    this.userSubject.next(user);
  }

  isLoggedIn(): boolean {
    const user = this.currentUserValue;
    return user !== null && user.token !== undefined && user.token !== null && user.token.length > 0;
  }

  isAdmin(): boolean {
    return this.currentUserValue?.role === Role.ROLE_ADMIN;
  }

  updateUsername(request: UserUpdateRequest): Observable<string> {
    return this.http.put(`${this.userManagementApiUrl}/update-username`, request, { responseType: 'text' });
  }

  changePassword(request: PasswordChangeRequest): Observable<string> {
    return this.http.put(`${this.userManagementApiUrl}/change-password`, request, { responseType: 'text' });
  }
}
