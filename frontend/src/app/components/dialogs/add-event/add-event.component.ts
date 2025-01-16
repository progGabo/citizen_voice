import { Component, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { debounceTime, Observable, of } from 'rxjs';
import { EventsService } from '../../../services/events.service';
import { MatDatepicker } from '@angular/material/datepicker';
import { ErrorComponent } from '../error/error.component';
import { EventCalendar, EventCalendarRequests, Location } from '../../../models/Events';
import { SuccessComponent } from '../success/success.component';

@Component({
  selector: 'app-add-event',
  templateUrl: './add-event.component.html',
  styleUrl: './add-event.component.scss'
})
export class AddEventComponent {
  @ViewChild("picker") datepicker!: MatDatepicker<any>;

  constructor(private dialog: MatDialogRef<AddEventComponent>, private eventService: EventsService, private fb: FormBuilder, private dialogOpn: MatDialog){
    this.dialog.disableClose = true
    if(screen.width > 600)
      this.dialog.updateSize("40%", "60%");

    this.formGroup = this.fb.group({
      name: ["", [Validators.required, Validators.maxLength(255)]],
      location: [new Location, Validators.required],
      description: ["", [Validators.required, Validators.maxLength(255)]],
      date_from: ["", Validators.required],
      date_to: ["", Validators.required]
    })

  }

  locations: Location[] = []
  filteredLocations: Observable<Location[] | null> = of(null);
  formControl = new FormControl("");
  formGroup: FormGroup;

  dateTime: Date = new Date();

  ngOnInit(){
    this.formControl.valueChanges.pipe(debounceTime(300)).subscribe(val => {
      this.onInputChange(val!);
    })
    this.eventService.getAllLocations().subscribe((res: any) => {
      this.locations = res.content
      //this.filteredLocations = of(this.locations)
    });
  }

  onInputChange(val: string | Location){
    const filterVal = typeof val === "string" ? val.toLowerCase() : val.name.toLowerCase();
    if(filterVal.length === 0){
      this.filteredLocations = of(null);
      return;
    }
    console.log(this.locations)
    this.filteredLocations = of(this.locations.filter((opt: Location) => opt.name.toLowerCase().includes(filterVal)));
  }

  getLocName(selectedValue: Location){
    return selectedValue.name;
  }

  open(){
    this.datepicker.open();
  }

  validate(){
    const values = this.formGroup.value;
    const location = this.formControl.value;

    if(values.name.length === 0){
      this.dialogOpn.open(ErrorComponent, {data: {text: "Vyplňte názov události"}})
      return;
    }
    if(!location){
      this.dialogOpn.open(ErrorComponent, {data: {text: "Vyplňte lokáciu události"}})
      return;
    }
    if(values.description.length === 0){
      this.dialogOpn.open(ErrorComponent, {data: {text: "Vyplňte popis události"}})
      return;
    }

    if(values.date_from.length === 0){
      this.dialogOpn.open(ErrorComponent, {data: {text: "Vyplňte dátum a čas začiatku události"}})
      return;
    }

    if(values.date_to.length === 0){
      this.dialogOpn.open(ErrorComponent, {data: {text: "Vyplňte dátum a čas ukončenia události"}})
      return;
    }

    if(values.name.length > 255){
      this.dialogOpn.open(ErrorComponent, {data: {text: "Meno nemôže mať viac ako 255 znakov"}})
    }
    if(values.description.length > 255){
      this.dialogOpn.open(ErrorComponent, {data: {text: "Popis nemôže mať viac ako 255 znakov"}})
    }

    let dateFrom = new Date(values.date_from)
    let dateTo = new Date(values.date_to)
    if(dateTo.getTime() - dateFrom.getTime() === 0){
      this.dialogOpn.open(ErrorComponent, {data: {text: "Událosť začína a končí v rovnaký deň, v rovnakom čase."}});
      return;
    }
    if(dateTo.getTime() - dateFrom.getTime() < 0){
      this.dialogOpn.open(ErrorComponent, {data: {text: "Událosť nemôže skončiť skôr ako začne."}})
      return;
    }

    if(typeof location !== "object"){
      this.dialogOpn.open(ErrorComponent, {data: {text: "Zadajte valídnu lokáciu"}})
    }
    else{
      let newEvent = new EventCalendarRequests;
      newEvent.name = values.name;
      newEvent.description = values.description;
      newEvent.cityId = (location as Location).id;
      newEvent.dateFrom = new Date(values.date_from).toISOString();
      newEvent.dateTo = new Date(values.date_to).toISOString();

      this.eventService.sendNewEvent(newEvent)?.subscribe({
        next: (res) => {
          this.dialogOpn.open(SuccessComponent, {data: {text: "Událosť bola úspšne pridaná"}});
          this.dialog.close()
        },
        error: (err) => {
          this.dialogOpn.open(ErrorComponent, {data: {text: "Nastala neočakávaná chyba."}});
        }
      })
    }
  }
}