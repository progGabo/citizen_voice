import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { Questionare } from '../../models/SurveysVoting';

@Component({
  selector: 'app-user-surveys',
  templateUrl: './user-surveys.component.html',
  styleUrls: ['./user-surveys.component.scss']
})
export class UserSurveysComponent implements OnInit {
  @Input() isAdmin: boolean = false;
  @Input() isLoggedIn: boolean = false;
  @Input() logoClickFunction: Function = () => {};

  @Output() isAdminChange = new EventEmitter<boolean>();
  @Output() isLoggedInChange = new EventEmitter<boolean>();

  sideNavOpened: boolean = false;
  currentTab: number = 0;
  surveys: Questionare[] = [];
  pagedSurveys: Questionare[] = [];
  filteredSurveys: Questionare[] = [];
  votings: Questionare[] = [];
  pageSize = 3;
  currentPage = 0;
  renderingSurvey: boolean = true;

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit(): void {
    this.isAdmin = this.userService.getIsAdmin();
    this.isLoggedIn = this.userService.getIsLoggedIn();
    this.loadUserSurveys();
  }

  logout() {
    this.router.navigate(['/home']);
    this.userService.logout();
    this.goToHomePage();
    console.log('User logged out');
  }

  goToHomePage() {
    console.log(this.currentTab);
    this.currentTab = 0;
  }

  get goToHomePageFunc() {
    return this.goToHomePage.bind(this);
  }

  openSideNav() {
    this.sideNavOpened = true;
    console.log("click");
  }

  closeSideNav() {
    this.sideNavOpened = false;
  }

  handleLogoutButton(){
    this.router.navigate(['/home']);
    this.isLoggedIn = false;
    this.isAdmin = false;
    this.isAdminChange.emit(false);
    this.isLoggedInChange.emit(false);
    this.userService.logout();
  }
  loadUserSurveys(): void {
    this.userService.getCreatedSurveys()?.subscribe((data: Questionare[]) => {
      this.surveys = data;
      this.filteredSurveys = data;
      this.updatePagedSurveys();
    });
  }
  loadUserVotings(): void {
    this.userService.getCreatedVotings()?.subscribe((data: Questionare[]) => {
      this.votings = data;
      this.filteredSurveys = data;
      this.updatePagedSurveys();
    });
  }

  onPageChange(event: any): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.updatePagedSurveys();
  }

  updatePagedSurveys(): void {
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.pagedSurveys = this.filteredSurveys.slice(startIndex, endIndex);
  }

  handleBtnClick(id: string | number) {
    this.router.navigate([`/surveys/${id}`]);
  }

  handleMyPetitionsButton(){
    this.router.navigate(['/settings/my_petitions']);
  }
  handleSettingsButton() {
    this.router.navigate(['/settings']);
  }
  handleMySurveysButton(){
    this.router.navigate(['/settings/my_surveys']);
  }
  handleMyArticlesButton(){
    this.router.navigate(['/settings/my_articless']);
  }

  handleAllSurveysButton(){
    this.filteredSurveys = this.surveys;
    this.updatePagedSurveys();
  }

  handleActiveSurveysButton(){
    this.filteredSurveys = this.surveys.filter(survey => survey.isActive);
    this.updatePagedSurveys();
  }

  handleInactiveSurveysButton(){
    this.filteredSurveys = this.surveys.filter(survey => !survey.isActive);
    this.updatePagedSurveys();
  }

  handleSwitch(string : string){
    if(string === "survey"){
      this.renderingSurvey = true;
      this.loadUserSurveys();
    }
    else {
      this.renderingSurvey = false;
      this.loadUserVotings();
    }
    
  }

}
