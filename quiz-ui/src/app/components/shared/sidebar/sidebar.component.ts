import {Component, OnInit} from '@angular/core';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent implements OnInit {
  isCollapsed = false;
  isAdmin = false;

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    this.authService.user.subscribe(user => {
      this.isAdmin = this.authService.isAdmin();
    });
  }

  toggleSidebar() {
    this.isCollapsed = !this.isCollapsed;
  }
}
