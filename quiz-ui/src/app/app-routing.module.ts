import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {QuizListComponent} from './components/quiz-list/quiz-list.component';
import {QuizCreateComponent} from './components/quiz-create/quiz-create.component';

const routes: Routes = [
  { path: '', component: QuizListComponent },
  { path: 'quizzes/create', component: QuizCreateComponent },
  { path: 'quizzes/update/:id', component: QuizCreateComponent },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
