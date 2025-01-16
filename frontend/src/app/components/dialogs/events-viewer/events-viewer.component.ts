import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { EventCalendar } from '../../../models/Events';

@Component({
  selector: 'app-events-viewer',
  templateUrl: './events-viewer.component.html',
  styleUrl: './events-viewer.component.scss'
})
export class EventsViewerComponent {

  constructor(@Inject(MAT_DIALOG_DATA) public data: {events: EventCalendar[]}, private dialog: MatDialogRef<EventsViewerComponent>){
    if(screen.width > 600)
      this.dialog.updateSize("40%", "60%");
   }

  getFormatedDate(date: Date){
    let minutes = date.getMinutes() < 10 ? `0${date.getMinutes()}` : date.getMinutes()
    return `${date.getDate()}.${date.getMonth() + 1}  ${date.getHours()}:${minutes}`
  }

}