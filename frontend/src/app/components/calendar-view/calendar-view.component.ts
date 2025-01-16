import { Component } from '@angular/core';
import { EventsService } from '../../services/events.service';
import { debounceTime, map, Observable, of, startWith } from 'rxjs';
import { FormControl } from '@angular/forms';
import { EventCalendar, Location } from '../../models/Events';

@Component({
  selector: 'app-calendar-view',
  templateUrl: './calendar-view.component.html',
  styleUrl: './calendar-view.component.scss'
})
export class CalendarViewComponent {

  monthsMap: Record<string, string> = {
    "jan": "Január",
    "feb": "Február",
    "mar": "Marec",
    "apr": "Apríl",
    "Máj": "Máj",
    "Jún": "Jún",
    "Júl": "Júl",
    "aug": "August",
    "sep": "September",
    "okt": "Október",
    "nov": "November",
    "dec": "December",
  }

  days = ["po", "ut", "st", "št", "pi", "so", "ne"]

  onCompare(left: any, right: any){
    return left.key;
  }

  currMonth = new Date().toLocaleString("sk-sk", {month: "short"})
  startYear = 2024;
  maxYear = this.startYear + 20;
  
  selectedMonth = this.currMonth;
  selectedYear = new Date().getFullYear();
  locations: Location[] = []
  filteredLocations: Observable<Location[] | null> = of(null);
  formControl = new FormControl("");

  events: EventCalendar[] = []
  selectedCity: string = "";

  constructor(private eventService: EventsService) { }

  ngOnInit(){
    this.formControl.valueChanges.pipe(debounceTime(300)).subscribe(val => {
      this.onInputChange(val!);
    })
    this.eventService.getAllLocations().subscribe((res: any) => {
      this.locations = res.content
      this.filteredLocations = of(this.locations)
    });
  }

  onInputChange(val: string | Location){
    if(typeof val !== "string"){
      this.eventService.getAllEventsByLocation(val).subscribe((res: any) => {
        this.events = res.content.map((resEvent: any ) => {
          let newEvent = new EventCalendar;
          newEvent.description = resEvent.description;
          newEvent.name = resEvent.name;
          newEvent.event_from = new Date(resEvent.dateFrom);
          newEvent.event_to = new Date(resEvent.dateTo);
          return newEvent;
        })
      })
      return;
    }
    
    this.events = []
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

  getLocName(selectedValue: Location){
    return selectedValue.name;
  }


 
  getYearsToGenerate(){
    let res = []
    for(let i = this.startYear; i <= this.startYear + 20; i++){
      res.push(i)
    }
    return res;
  }

  setSelectedMonth(selected: string){
    this.selectedMonth = selected;
  }
  setSelectedYear(year: number){
    this.selectedYear = year;
  }

  shouldMoveNext(){
    return !(this.selectedMonth === "dec" && this.selectedYear === this.maxYear);
  }

  shouldMovePrev(){
    return !(this.selectedMonth === "jan" && this.selectedYear === this.startYear);
  }

  nextMonth(){
    if(!this.shouldMoveNext()) return;

    let keys = Object.keys(this.monthsMap);
    let selectedIndex = keys.indexOf(this.selectedMonth);
    if(selectedIndex === 11){
      this.selectedYear += 1;
      this.selectedMonth = keys[0];
    }
    else{
      this.selectedMonth = keys[selectedIndex + 1];
    }
  }

  prevMonth(){
    if(!this.shouldMovePrev()) return;

    let keys = Object.keys(this.monthsMap);
    let selectedIndex = keys.indexOf(this.selectedMonth);
    if(selectedIndex === 0){
      this.selectedYear -= 1;
      this.selectedMonth = keys[keys.length - 1];
    }
    else{
      this.selectedMonth = keys[selectedIndex - 1];
    }
  }

  getAllDatesInMonth(){
    let keys = Object.keys(this.monthsMap);
    let monthNum = keys.indexOf(this.selectedMonth) + 1;
    
    let lastDay = new Date(this.selectedYear, monthNum, 0).getDate();
    let res = []
    for(let i = 1; i <= lastDay; i++){
      res.push(new Date(this.selectedYear, monthNum - 1, i))
    }
    return res;
  }  

  getAllDatesInGivenMonth(month: number, year: number){
    let lastDay = new Date(year, month, 0).getDate();
    let res = []
    for(let i = 1; i <= lastDay; i++){
      res.push(new Date(year, month - 1, i))
    }
    return res;
  }

  getStartPadding(){
    let allDates = this.getAllDatesInMonth();
    let firstDay = allDates[0].toLocaleString("sk-sk", {weekday: "short"})

    let numberOfPaddingDays = this.days.indexOf(firstDay);
    
    let keys = Object.keys(this.monthsMap);
    let monthNum = keys.indexOf(this.selectedMonth);
    let year = this.selectedYear;

    if(monthNum === 0){
      year--;
      monthNum = 12;
    }
    
    let allPrevMonthDays = this.getAllDatesInGivenMonth(monthNum, year);
    return allPrevMonthDays.reverse().slice(0, numberOfPaddingDays).reverse()
  }
  
  getEndPadding(){

    let allDates = this.getAllDatesInMonth();
    let lastDay = allDates[allDates.length - 1].toLocaleString("sk-sk", {weekday: "short"})
    
    let keys = Object.keys(this.monthsMap);
    let monthNum = keys.indexOf(this.selectedMonth);
    
    let numberOfPaddingDays = 6 - this.days.indexOf(lastDay);
    let year = this.selectedYear;
    if(monthNum === 12){
      year++;
      monthNum = 0;
    }

    let allNextMonthDays = this.getAllDatesInGivenMonth(monthNum + 2, year);
    
    return allNextMonthDays.slice(0, numberOfPaddingDays);
  }
}