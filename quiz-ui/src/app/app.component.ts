import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from './services/auth.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit, OnDestroy {

  isSidebarCollapsed = false;
  isLoggedIn = false;
  username: string | null = null;
  private userSubscription: Subscription | undefined;

  constructor(private authService: AuthService) {
  }

  ngOnInit(): void {
    this.userSubscription = this.authService.user.subscribe(user => {
      this.isLoggedIn = !!user;
      this.username = user ? user.username : null;
    });
  }

  ngOnDestroy(): void {
    this.userSubscription?.unsubscribe();
  }

  toggleSidebar() {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  logout(): void {
    this.authService.logout();
  }
}
