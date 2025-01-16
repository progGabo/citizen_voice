import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatTabsModule } from '@angular/material/tabs';
import { AppComponent } from './app.component'; 
import { AsyncPipe, CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from './components/header/header.component';
import {MatIconModule} from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatSidenavModule } from '@angular/material/sidenav';
import { LoginRegisterDialogComponent } from './components/dialogs/login-register-dialog/login-register-dialog.component';
import { MatDialogActions, MatDialogClose, MatDialogContent, MatDialogModule, MatDialogTitle } from '@angular/material/dialog';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import { TestCarouselComponent } from './components/cv_carousel/testCarousel.component';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { ViewArticleComponent } from './components/view-article/view-article.component';
import { routes } from './app.routes';
import { HomeComponent } from './components/home/home.component';
import { ArticlesComponent } from './components/articles/articles.component';
import { MatCardModule } from '@angular/material/card';
import { MatPaginatorModule } from '@angular/material/paginator';
import { PetitionsComponent } from './components/petitions/petitions.component';
import { ViewPetitionComponent } from './components/view-petition/view-petition.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { CreateVotingSurveyComponent } from './components/create-voting-survey/create-voting-survey.component';
import { CreatingQuestionComponent } from './components/creating-question/creating-question.component';
import { ErrorComponent } from './components/dialogs/error/error.component';
import { FillSurveyVotingComponent } from './components/fill-survey-voting/fill-survey-voting.component';
import { FillQuestionComponent } from './components/fill-question/fill-question.component';
import { MatRadioModule } from '@angular/material/radio';
import { VotingsSurveysComponent } from './components/votings-surveys/votings-surveys.component';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { MAT_SNACK_BAR_DEFAULT_OPTIONS } from '@angular/material/snack-bar';
import { SuccessComponent } from './components/dialogs/success/success.component';
import { UserSettingsComponent } from './components/user-settings/user-settings.component';
import { ActivateAccountComponent } from './components/activate-account/activate-account.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AddEventComponent } from './components/dialogs/add-event/add-event.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { provideNativeDateAdapter } from '@angular/material/core';
import { CalendarViewComponent } from './components/calendar-view/calendar-view.component';
import { OneDayComponent } from './components/calendar-view/one-day/one-day.component';
import { MatSelectModule } from '@angular/material/select';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { EventsViewerComponent } from './components/dialogs/events-viewer/events-viewer.component';
import { MatExpansionModule } from '@angular/material/expansion';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { DashboardPasswordComponent } from './components/dialogs/dashboard-password/dashboard-password.component';
import { UserPetitionsComponent } from './components/user-petitions/user-petitions.component';
import { UserArticlesComponent } from './components/user-articles/user-articles.component';
import { UserSurveysComponent } from './components/user-surveys/user-surveys.component';
import { CreateArticleComponent } from './components/create-article/create-article.component';
import { CKEditorModule } from "@ckeditor/ckeditor5-angular";
import { ConfirmComponent } from './components/dialogs/confirm/confirm.component';
import { SignPetitionComponent } from './components/dialogs/sign-petition/sign-petition.component';
import { LoadingSpinnerComponent } from './components/loading-spinner/loading-spinner.component';
import { CitySelectorComponent } from './components/city-selector/city-selector.component';
import { CommentComponent } from './components/comment/comment.component';
import {MatChipsModule} from '@angular/material/chips';
import { ConfirmSignComponent } from './components/confirm-sign/confirm-sign.component';
import { BaseChartDirective, provideCharts, withDefaultRegisterables  } from 'ng2-charts';
import { ExportButtonComponent } from './components/export-button/export-button.component';
import { TestPageComponent } from './components/test-page/test-page.component';
import { ExportDialogComponent } from './components/dialogs/export-dialog/export-dialog.component';
import { PetitionExportButtonComponent } from './components/petition-export-button/petition-export-button.component';
import { MatTooltipModule } from '@angular/material/tooltip';

@NgModule({
  declarations: [
    AppComponent, 
    HeaderComponent,
    LoginRegisterDialogComponent,
    TestCarouselComponent,
    ViewArticleComponent,
    HomeComponent,
    ArticlesComponent,
    PetitionsComponent,
    ViewPetitionComponent,
    UserSettingsComponent,
    CreateVotingSurveyComponent,
    CreatingQuestionComponent,
    ErrorComponent,
    FillSurveyVotingComponent,
    FillQuestionComponent,
    VotingsSurveysComponent,
    NotFoundComponent,
    SuccessComponent,
    ActivateAccountComponent,
    AddEventComponent,
    CalendarViewComponent,
    OneDayComponent,
    EventsViewerComponent,
    AdminDashboardComponent,
    DashboardPasswordComponent,
    UserPetitionsComponent,
    UserSurveysComponent,
    UserArticlesComponent,
    CreateArticleComponent,
    ConfirmComponent,
    SignPetitionComponent,
    LoadingSpinnerComponent,
    CitySelectorComponent,
    CommentComponent,
    ConfirmSignComponent,
    ExportButtonComponent,
    ExportDialogComponent,
    PetitionExportButtonComponent,

    TestPageComponent
  ],
  imports: [
    BrowserModule, 
    BrowserAnimationsModule, 
    MatTabsModule,
    CommonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    FormsModule,
    MatButtonModule,
    MatMenuModule,
    MatSidenavModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
    MatDialogModule,
    MatButtonToggleModule,
    MatCardModule,
    MatPaginatorModule,
    ReactiveFormsModule,
    MatCheckboxModule,
    MatRadioModule,
    MatProgressSpinnerModule,
    MatDatepickerModule,
    MatSelectModule,
    MatAutocompleteModule,
    AsyncPipe,
    MatExpansionModule,
    CKEditorModule,
    MatChipsModule,
    BaseChartDirective,
    MatTooltipModule,
    RouterModule.forRoot(routes) //nechavaj uplne dole
  ],
  providers: [provideHttpClient(), {provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: {duration: 5000}}, provideNativeDateAdapter(), provideCharts(withDefaultRegisterables())],
  bootstrap: [AppComponent] 
})
export class AppModule { }