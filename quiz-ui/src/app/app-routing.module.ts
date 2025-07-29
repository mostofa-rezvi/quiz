import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {QuizListComponent} from './components/quiz-list/quiz-list.component';
import {QuizCreateComponent} from './components/quiz-create/quiz-create.component';
import {QuizAttemptComponent} from './components/quiz-attempt/quiz-attempt.component';
import {QuizResultComponent} from './components/quiz-result/quiz-result.component';
import {RegisterComponent} from './components/auth/register/register.component';
import {LoginComponent} from './components/auth/login/login.component';
import {SettingsComponent} from './components/settings/settings.component';
import {AuthGuard} from './guards/auth.guard';
import {AdminGuard} from './guards/admin.guard';

const routes: Routes = [
  {path: '', component: HomeComponent, pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'settings', component: SettingsComponent, canActivate: [AuthGuard]},
  {path: 'quizzes', component: QuizListComponent, canActivate: [AuthGuard]},
  {
    path: 'quizzes/create',
    component: QuizCreateComponent,
    canActivate: [AuthGuard, AdminGuard],
  },
  {
    path: 'quizzes/update/:id',
    component: QuizCreateComponent,
    canActivate: [AuthGuard, AdminGuard],
  },
  {
    path: 'quizzes/attempt/:id',
    component: QuizAttemptComponent,
    canActivate: [AuthGuard],
  },
  {path: 'results', component: QuizResultComponent, canActivate: [AuthGuard]},
  {path: '**', redirectTo: ''},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {
}
