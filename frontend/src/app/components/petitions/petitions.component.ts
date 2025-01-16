import { Component, EventEmitter, Input, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { Router } from '@angular/router';
import { Petition } from '../../models/Petitions';
import { CarouselDataService } from '../../services/carousel-data.service';
import { PetitionsService } from '../../services/petitions.service';
import { FormControl } from '@angular/forms';
import { debounceTime, Observable, of } from 'rxjs';
import { Location } from '../../models/Events';
import { EventsService } from '../../services/events.service';

@Component({
  selector: 'app-petitions',
  templateUrl: './petitions.component.html',
  styleUrl: './petitions.component.scss'
})
export class PetitionsComponent {
  @ViewChild(MatPaginator) paginator: any;
  @Input() isLoggedIn: boolean = false;
  @Input() lastSelectedOption: string = "";
  @Output() lastSelectedOptionChange = new EventEmitter<string>();

  petitions: Petition[] = []

  constructor(private router: Router, private carouselService: CarouselDataService, private petitionService: PetitionsService, private eventService: EventsService){}

  pageSize = 3;
  currentPage = 0;
  pagedPetitions: Petition[] = [];
  showCitySelector: boolean = false;

  formControl = new FormControl("");
  filteredLocations:  Observable<Location[] | null> = of(null);
  locations: Location[] = [];

  allPetitions: Petition[] = [];

  lastSelectedCityId: number = -1;

  ngOnInit(){
    this.petitionService.getAllPetitions().subscribe(res => {
      this.petitions = res.map(petition => {
        petition.text = this.carouselService.sanitizeText(petition.text)
        return petition;
      })
      this.allPetitions = this.petitions;
      this.updatePagedPetitions();
    })

    this.formControl.valueChanges.pipe(debounceTime(300)).subscribe(val => {
      this.onInputChange(val!);
    })

    this.eventService.getAllLocations().subscribe((res: any) => {
      this.locations = res.content
      this.filteredLocations = of(this.locations)
    });
  }

   ngOnChanges(changes: SimpleChanges){
      if(changes['lastSelectedOption'] && changes['lastSelectedOption'].currentValue.includes("search_")){
        this.sortPetitions(changes['lastSelectedOption'].currentValue)
      }
    }

  sortPetitions(sortOption: string){
    if(sortOption.includes("search_")){
      let option = sortOption.split("search_")[1];
      this.petitions = this.allPetitions.filter(article => {
        return article.title.toLowerCase().includes(option) || article.text.toLowerCase().includes(option)
      })
      this.lastSelectedOption = `Text: ${option}`
    }
    else if(sortOption === "new"){
      this.lastSelectedOption = "Najnovšie"
      this.petitions = this.allPetitions.sort((a,b) => { return b.createdAt.getTime() - a.createdAt.getTime()})
    }
    else if(sortOption === "old"){
      this.lastSelectedOption = "Najstaršie"
      this.petitions = this.allPetitions.sort((a,b) => { return a.createdAt.getTime() - b.createdAt.getTime()})
    }
    else if(sortOption === "mostSigned"){
      this.lastSelectedOption = "Najviac podpísaných"
      this.petitions = this.allPetitions.sort((a,b) => { return b.numberOfSignees - a.numberOfSignees})
    }
    else{ //least Signed
      this.lastSelectedOption = "Najmenej podpísaných"
      this.petitions = this.allPetitions.sort((a,b) => { return a.numberOfSignees - b.numberOfSignees})
    }
    this.updatePagedPetitions()
  }


  onPageChange(event: any){
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.updatePagedPetitions();
  }

  updatePagedPetitions(){
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.pagedPetitions = this.petitions.slice(startIndex, endIndex);
  }

  getShortenedText(fullText: string){
    if(fullText.length > 200){
      return `${fullText.substring(0, 200)}...`;
    }
    return fullText;
  }

  handleBtnClick(id: string | number){
    const url = this.router.serializeUrl(this.router.createUrlTree([`/petition/${id}`]));
    window.open(url, "_blank");
  }

  createPetition(){
    this.router.navigateByUrl("/create_petition")
  }

  setShowCitySelector(){
    this.showCitySelector = true;
  }

  onInputChange(val: string | Location){
    if(typeof val !== "string"){
      this.lastSelectedCityId = val.id;
      this.petitions = this.allPetitions.filter(p => p.cityId === val.id)
      this.updatePagedPetitions()
      return;
    }
    
    let filterVal = val.toLowerCase();
    
    if(filterVal.length === 0){
      this.filteredLocations = of(this.locations);
      return;
    }
    let filtered = this.locations.filter((opt: Location) => opt.name.toLowerCase().includes(filterVal));
    if(filtered.length === 0){
      filtered = this.locations;
    }
    this.filteredLocations = of(filtered);
  }

  closeCitySelector(){
    this.showCitySelector = false;
    this.petitions = this.allPetitions;
    this.lastSelectedCityId = -1;
    this.formControl.setValue("");
    if(this.lastSelectedOption != ""){
      if(this.lastSelectedOption === "Najnovšie"){
        this.sortPetitions("new");  
      }
      else if(this.lastSelectedOption === "Najstaršie"){
        this.sortPetitions("old");  
      }
      else if(this.lastSelectedOption === "Najviac podpísaných"){
        this.sortPetitions("mostSigned");  
      }
      else{
        this.sortPetitions("leastSigned");
      }
    }
    else{ //updatePagedPetitions is called in sortPetitions so this else statement prevents two calls of the same fuction
      this.updatePagedPetitions();
    }
  }

  getLocName(selectedValue: Location){
    return selectedValue.name;
  }

  removeFilter(){
    this.lastSelectedOptionChange.emit("");
    this.lastSelectedOption = "";
    if(this.lastSelectedCityId == -1){
      this.petitions = this.allPetitions.sort((a,b) => a.id - b.id);
    }
    else{
      this.petitions = this.allPetitions.filter(p => p.cityId === this.lastSelectedCityId)
    }
    this.updatePagedPetitions();
  }
}
