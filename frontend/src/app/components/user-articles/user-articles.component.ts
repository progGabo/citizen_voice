import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { Article } from '../../models/Articles';

@Component({
  selector: 'app-user-articles',
  templateUrl: './user-articles.component.html',
  styleUrls: ['./user-articles.component.scss']
})
export class UserArticlesComponent implements OnInit {
  @Input() isAdmin: boolean = false;
  @Input() isLoggedIn: boolean = false;
  @Input() logoClickFunction: Function = () => {};
  
  @Output() isAdminChange = new EventEmitter<boolean>();
  @Output() isLoggedInChange = new EventEmitter<boolean>();

  sideNavOpened: boolean = false;
  currentTab: number = 0;
  articles: Article[] = [];
  pagedArticles: Article[] = [];
  filteredArticles: Article[] = [];
  pageSize = 3;
  currentPage = 0;

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit(): void {
    this.isAdmin = this.userService.getIsAdmin();
    this.isLoggedIn = this.userService.getIsLoggedIn();
    this.loadUserArticles();
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

  handleLogoutButton() {
    this.router.navigate(['/home']);
    this.isLoggedIn = false;
    this.isAdmin = false;
    this.isAdminChange.emit(false);
    this.isLoggedInChange.emit(false);
    this.userService.logout();
  }
  loadUserArticles(): void {
    this.userService.getCreatedArticles()?.subscribe((data: Article[]) => {
      this.articles = data;
      this.filteredArticles = data;
      this.updatePagedArticles();
    });
  }

  onPageChange(event: any): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.updatePagedArticles();
  }

  updatePagedArticles(): void {
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.pagedArticles = this.filteredArticles.slice(startIndex, endIndex);
  }

  handleBtnClick(id: string | number) {
    this.router.navigate([`/articles/${id}`]);
  }

  handleMyPetitionsButton(){
    this.router.navigate(['/settings/my_petitions']);
  }
  handleSettingsButton() {
    this.router.navigate(['/settings']);
  }
  handleMyArticlesButton(){
    this.router.navigate(['/settings/my_articles']);
  }
  handleMySurveysButton(){
    this.router.navigate(['/settings/my_surveys']);
  }

  formatDate(date: Date): string {
    return date.toLocaleDateString();
  }

  handleAllArticlesButton(){
    this.filteredArticles = this.articles;
    this.updatePagedArticles();
  }
  handleActiveArticlesButton(){
    this.filteredArticles = this.articles.filter(a => a.isActive);
    this.updatePagedArticles();
  }
  handleInactiveArticlesButton(){
    this.filteredArticles = this.articles.filter(a => !a.isActive);
    this.updatePagedArticles();
  }

}
