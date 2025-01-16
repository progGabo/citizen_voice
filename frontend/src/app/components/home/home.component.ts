import { Component, SimpleChanges, ViewChild } from '@angular/core';
import { Carousel } from '../../models/CarouselDataSets';
import { CarouselDataService } from '../../services/carousel-data.service';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { TestCarouselComponent } from '../cv_carousel/testCarousel.component';
import { UserService } from '../../services/user.service';
import { MatDialog } from '@angular/material/dialog';
import { AddEventComponent } from '../dialogs/add-event/add-event.component';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  @ViewChild("articleCarousel") articleCarousel!: TestCarouselComponent;

  constructor(private carouselDataService: CarouselDataService, private userService: UserService, private dialog: MatDialog) { }

  isAdmin: boolean = false;
  isLoggedIn: boolean = false;
  sideNavOpened: boolean = false;
  articleCarouselData: Carousel[] = []
  petitionCarouselData: Carousel[] = []
  surveyCarouselData: Carousel[] = []
  votingCarouselData: Carousel[] = []
  currentTab: number = 0;
  _searchFilter: string = "";

  articlesSearch: string = "";
  petitionsSearch: string = "";
  votingsSurveysSearch: string = "";

  ngOnInit(){
    this.isAdmin = this.userService.getIsAdmin();
    this.isLoggedIn = this.userService.getIsLoggedIn();

    this.carouselDataService.getCarouselArticleData().subscribe(data => {
      this.articleCarouselData = data;
    })
    
    this.carouselDataService.getCarouselPetitionData().subscribe(data => {
      this.petitionCarouselData = data;
    })

    this.carouselDataService.getCarouselSurveyData().subscribe(data => {
      this.surveyCarouselData = data;
    })

    this.carouselDataService.getCarouselVotingData().subscribe(data => {
      this.votingCarouselData = data;
    })
  }

  set searchFilter(val: string){
    this._searchFilter = val;
    if(this.currentTab === 1){
      this.articlesSearch = val;
    }
    else if(this.currentTab === 2){
      this.petitionsSearch = val;
    }
    else if(this.currentTab === 3){
      this.votingsSurveysSearch = val;
    }
  }

  get searchFilter(){
    return this._searchFilter;
  }

  goToHomePage(){
    this.currentTab = 0;
  }

  get goToHomePageFunc(){
    return this.goToHomePage.bind(this);
  }

  openSideNav(){
    this.sideNavOpened = true;
  }

  closeSideNav(){
    this.sideNavOpened = false;
  }

  tabChange(event: MatTabChangeEvent){
    this.currentTab = event.index;
  }

  addEvent(){
    const dialogRef = this.dialog.open(AddEventComponent);
  }

}
