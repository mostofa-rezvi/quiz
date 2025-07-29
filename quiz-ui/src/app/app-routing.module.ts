import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { QuizListComponent } from './components/quiz-list/quiz-list.component';
import { QuizCreateComponent } from './components/quiz-create/quiz-create.component';
import { QuizAttemptComponent } from './components/quiz-attempt/quiz-attempt.component';
import { QuizResultComponent } from './components/quiz-result/quiz-result.component';

const routes: Routes = [
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path: 'quizzes', component: QuizListComponent },
  { path: 'quizzes/create', component: QuizCreateComponent },
  { path: 'quizzes/update/:id', component: QuizCreateComponent },
  { path: 'quizzes/attempt/:id', component: QuizAttemptComponent },
  { path: 'results', component: QuizResultComponent },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
