import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  isLoggedIn = false;
  isAdmin = false;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.user.subscribe((user) => {
      this.isLoggedIn = !!user;
      this.isAdmin = this.authService.isAdmin();
    });
  }
}
