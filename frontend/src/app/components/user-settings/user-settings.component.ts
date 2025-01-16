import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { Carousel } from '../../models/CarouselDataSets';
import { CarouselDataService } from '../../services/carousel-data.service';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { TestCarouselComponent } from '../cv_carousel/testCarousel.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.scss']
})
export class UserSettingsComponent implements OnInit {
  @ViewChild("articleCarousel") articleCarousel!: TestCarouselComponent;
  
  @Input() isAdmin: boolean = false;
  @Input() isLoggedIn: boolean = false;
  @Input() logoClickFunction: Function = () => {};

  @Output() isAdminChange = new EventEmitter<boolean>();
  @Output() isLoggedInChange = new EventEmitter<boolean>();
  
  userForm: FormGroup;
  sideNavOpened: boolean = false;
  articleCarouselData: Carousel[] = []
  petitionCarouselData: Carousel[] = []
  currentTab: number = 0;

  constructor(private fb: FormBuilder, private userService: UserService,private carouselDataService: CarouselDataService, private router: Router) {
    this.userForm = this.fb.group({
      firstName: [''],
      lastName: [''],
      address: [''],
      city: [''],
      postalCode: [''],
      email: [''],
      temporaryStayCity: [''],
      temporaryStayAddress: [''],
      temporaryStayPostalCode: [''],
      gender: [''],
    });
  }

  ngOnInit(): void {
    this.isAdmin = this.userService.getIsAdmin();
    this.isLoggedIn = this.userService.getIsLoggedIn();
    this.loadUserData();
  }
  
  loadUserData(): void {
    const userData = this.userService.getUserData();
    if (userData) {
      this.userForm.patchValue(userData);
    }
  }
  
  onSubmit(): void {
    console.log('Form data:', this.userForm.value);
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
}