import { Component, Input } from '@angular/core';
import { EventCalendar } from '../../../models/Events';
import { MatDialog } from '@angular/material/dialog';
import { EventsViewerComponent } from '../../dialogs/events-viewer/events-viewer.component';

@Component({
  selector: 'app-one-day',
  templateUrl: './one-day.component.html',
  styleUrl: './one-day.component.scss'
})
export class OneDayComponent {
  @Input() date: Date | null = null;
  @Input() disabled: boolean = false;
  @Input() events: EventCalendar[] = []

  hasEvent: boolean = false;
  eventsOnThisDate: EventCalendar[] = [];

  constructor(private dialog: MatDialog) { }

  ngOnInit(){
    if(!this.date) return;
    for(let event of this.events){
      let startTime = event.event_from.getTime()
      let endTime = event.event_to.getTime()
      

      let currTime = new Date(this.date.getFullYear(), this.date.getMonth(), this.date.getDate(), 0, 0, 1, 1).getTime();

      // one day event 
      if(this.getFormattedDate(event.event_from) === this.getFormattedDate(event.event_to) && this.getFormattedDate(this.date) === this.getFormattedDate(event.event_from)){
        this.eventsOnThisDate.push(event);
        this.hasEvent = true;
      }
      
      if(currTime >= startTime && currTime <= endTime){
        this.eventsOnThisDate.push(event);
        this.hasEvent = true;
      }
    }
  }

  getDate(){
    if(this.date != null)
      return `${this.date.getDate()}`;
    return "";
  }

  getFormattedDate(date: Date){
    return `${date.getDate()}.${date.getMonth() + 1}.${date.getFullYear()}`;
  }

  isTodayDate(){
    if(!this.date) return false;

    return this.getFormattedDate(this.date) === this.getFormattedDate(new Date());
  }

  handleClick(){
    if(!this.disabled && this.hasEvent)
      this.dialog.open(EventsViewerComponent, {data: { events: this.eventsOnThisDate}})
  }
}
