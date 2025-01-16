import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { Carousel } from '../../models/CarouselDataSets';
import { CarouselDataService } from '../../services/carousel-data.service';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { TestCarouselComponent } from '../cv_carousel/testCarousel.component';
import { Router } from '@angular/router';
import { Petition } from '../../models/Petitions';

@Component({
  selector: 'app-user-petitions',
  templateUrl: './user-petitions.component.html',
  styleUrls: ['./user-petitions.component.scss']
})
export class UserPetitionsComponent implements OnInit {
  @Input() isAdmin: boolean = false;
  @Input() isLoggedIn: boolean = false;
  @Input() logoClickFunction: Function = () => {};
  
  @Output() isAdminChange = new EventEmitter<boolean>();
  @Output() isLoggedInChange = new EventEmitter<boolean>();
    
  sideNavOpened: boolean = false;
  currentTab: number = 0;
  petitions: Petition[] = [];
  pagedPetitions: Petition[] = [];
  filteredPetitions: Petition[] = [];
  pageSize = 3;
  currentPage = 0;

  constructor(private userService: UserService, private router: Router) {
   
  }

  ngOnInit(): void {
    this.isAdmin = this.userService.getIsAdmin();
    this.isLoggedIn = this.userService.getIsLoggedIn();
    this.loadUserPetitions();
  }
  
  
  onSubmit(): void {

  }

  logout(){
    this.router.navigate(['/home']);
    this.userService.logout();
    this.goToHomePage();
    console.log('User logged out');
  }

  goToHomePage(){
    console.log(this.currentTab)
    this.currentTab = 0;
  }

  get goToHomePageFunc(){
    return this.goToHomePage.bind(this);
  }

  openSideNav(){
    this.sideNavOpened = true;
    console.log("click")
  }

  closeSideNav(){
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
  handleAllPetitionsButton(){
    this.filteredPetitions = this.petitions;
    this.updatePagedPetitions();
  }
  handleActivePetitionsButton(){
    this.filteredPetitions = this.petitions.filter(p => p.isActive);
    this.updatePagedPetitions();
  }
  handleInactivePetitionsButton(){
    this.filteredPetitions = this.petitions.filter(p => !p.isActive);
    this.updatePagedPetitions();
  }

  loadUserPetitions(): void {
    this.userService.getCreatedPetitions()?.subscribe((data: Petition[]) => {
      this.petitions = data;
      this.filteredPetitions = data;
      this.updatePagedPetitions();
    });
  }



  onPageChange(event: any): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.updatePagedPetitions();
  }

  updatePagedPetitions(): void {
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.pagedPetitions = this.filteredPetitions.slice(startIndex, endIndex);
  }

  handleBtnClick(id: string | number){
    this.router.navigate([`/petition/${id}`]);
  }
}
