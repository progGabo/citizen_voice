export class EventCalendar{
    name: string = "";
    place:  string = "";
    description: string = "";
    event_from: Date = new Date();
    event_to: Date = new Date();
}


export class EventCalendarRequests{
    name: string = "";
    description: string = "";
    dateFrom: string = "";
    dateTo: string = "";
    cityId: number = -1;


    parseEventCalendarToRequest(cityId: number, event: EventCalendar){
        this.cityId = cityId;
        this.name = event.name;
        this.description = event.description;
        this.dateFrom = event.event_from.toISOString();
        this.dateTo = event.event_to.toISOString();
    }
}

export class Location{
    id: number = -1;
    name: string = "";
}