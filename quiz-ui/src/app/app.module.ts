import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule, provideHttpClient, withFetch} from '@angular/common/http';
import {QuizListComponent} from './components/quiz-list/quiz-list.component';
import {QuizCreateComponent} from './components/quiz-create/quiz-create.component';
import {QuizAttemptComponent} from './components/quiz-attempt/quiz-attempt.component';
import {QuizResultComponent} from './components/quiz-result/quiz-result.component';
import {HomeComponent} from './components/home/home.component';
import {SidebarComponent} from './components/shared/sidebar/sidebar.component';
import { LoginComponent } from './components/auth/login/login.component';
import { RegisterComponent } from './components/auth/register/register.component';
import { SettingsComponent } from './components/settings/settings.component';
import { AuthInterceptor } from './interceptors/auth.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    QuizListComponent,
    QuizCreateComponent,
    QuizAttemptComponent,
    QuizResultComponent,
    HomeComponent,
    SidebarComponent,
    LoginComponent,
    RegisterComponent,
    SettingsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    provideClientHydration(),
    provideHttpClient(withFetch()),
     { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
