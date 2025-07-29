import {Injectable} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse,
} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {AuthService} from '../services/auth.service';
import {Router} from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService, private router: Router) {
  }

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    const currentUser = this.authService.currentUserValue;
    const isLoggedIn = currentUser && currentUser.token;
    const isApiUrl = request.url.startsWith('http://localhost:8080/api');

    if (isLoggedIn && isApiUrl) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${currentUser?.token}`,
        },
      });
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 || error.status === 403) {
          if (this.authService.isLoggedIn()) {
            this.authService.logout();
            alert(
              'Your session has expired or access denied. Please log in again.'
            );
          }
        }
        return throwError(
          () => new Error(error.error?.message || error.statusText)
        );
      })
    );
  }
}
