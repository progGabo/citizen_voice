import { Routes } from '@angular/router';
import { ViewArticleComponent } from './components/view-article/view-article.component';
import { HomeComponent } from './components/home/home.component';
import { ViewPetitionComponent } from './components/view-petition/view-petition.component';
import { CreateVotingSurveyComponent } from './components/create-voting-survey/create-voting-survey.component';
import { FillSurveyVotingComponent } from './components/fill-survey-voting/fill-survey-voting.component';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { UserSettingsComponent } from './components/user-settings/user-settings.component';
import { ActivateAccountComponent } from './components/activate-account/activate-account.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { UserPetitionsComponent } from './components/user-petitions/user-petitions.component';
import { UserArticlesComponent } from './components/user-articles/user-articles.component';
import { UserSurveysComponent } from './components/user-surveys/user-surveys.component';
import { CreateArticleComponent } from './components/create-article/create-article.component';
import { CanDeactivateGuard } from './guards/can-deactivate';
import { SignPetitionComponent } from './components/dialogs/sign-petition/sign-petition.component';
import { ConfirmSignComponent } from './components/confirm-sign/confirm-sign.component';
import { TestPageComponent } from './components/test-page/test-page.component';

export const routes: Routes = [
    {path: "", redirectTo: "home", pathMatch: "full"},
    {path: "home", component: HomeComponent},
    {path: "article/:id", component: ViewArticleComponent},
    {path: "petition/:id", component: ViewPetitionComponent},
    {path: "create_voting", component: CreateVotingSurveyComponent},
    {path: "create_survey", component: CreateVotingSurveyComponent},
    {path: "fill_out/:type/:id", component: FillSurveyVotingComponent},
    {path: "not_found", component: NotFoundComponent},
    {path: 'settings', component: UserSettingsComponent},
    {path: 'settings/my_petitions', component: UserPetitionsComponent},
    {path: 'settings/my_articles', component: UserArticlesComponent},
    {path: 'settings/my_surveys', component: UserSurveysComponent},
    {path: "activate/:token", component: ActivateAccountComponent},
    {path: "dashboard", component: AdminDashboardComponent},
    {path: "create_article", component: CreateArticleComponent, canDeactivate: [CanDeactivateGuard]},
    {path: "create_petition", component: CreateArticleComponent, canDeactivate: [CanDeactivateGuard]},
    {path: "confirm_petition_signature/:token", component: ConfirmSignComponent},
    {path: "test", component: TestPageComponent},
    {path: "**", component: NotFoundComponent} // nechavaj dole aby bolo dobre
];
