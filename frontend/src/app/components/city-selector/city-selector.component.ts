import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EventsService } from '../../services/events.service';
import { Location } from '../../models/Events';
import { MatOptionSelectionChange } from '@angular/material/core';
import { MatSelectChange } from '@angular/material/select';

@Component({
  selector: 'app-city-selector',
  templateUrl: './city-selector.component.html',
  styleUrl: './city-selector.component.scss'
})
export class CitySelectorComponent {
  @Input() selected: string = "";
  @Output() selectedChange: EventEmitter<string> = new EventEmitter();

  allLocations: Location[] = [];

  constructor(private eventService: EventsService){}

  ngOnInit(){
    this.eventService.getAllLocations().subscribe((res: any) => this.allLocations = res.content)
  }

  handleChange(event: MatSelectChange){
    this.selectedChange.emit(event.value)
  }
}
