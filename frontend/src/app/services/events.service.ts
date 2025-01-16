import { Injectable } from '@angular/core';
import { EventCalendar, EventCalendarRequests, Location } from '../models/Events';
import { catchError, map, of, throwError } from 'rxjs';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { config } from '../environments/enviroment.dev';
import { UserService } from './user.service';

@Injectable({
  providedIn: 'root'
})
export class EventsService {

  constructor(private httpClient: HttpClient, private userService: UserService) { }

  events: EventCalendar[] = [
    {
      name: "Test 1", 
      place: "Košice",
      description: "",
      event_from: new Date(2024, 10, 5),
      event_to: new Date(2024, 10, 8)
    },
    {
      name: "Test 2", 
      place: "Košice",
      description: "",
      event_from: new Date(2024, 10, 5),
      event_to: new Date(2024, 10, 5)
    },
    {
      name: "Test 3", 
      place: "Košice",
      description: "",
      event_from: new Date(2024, 9, 1),
      event_to: new Date(2024, 9, 31)
    },
    {
      name: "Test 4", 
      place: "Košice",
      description: "Super brutalny event",
      event_from: new Date(2024, 10, 25),
      event_to: new Date(2024, 10, 25)
    },
    {
      name: "Test 5", 
      place: "Košice",
      description: "",
      event_from: new Date(2025, 0, 21),
      event_to: new Date(2025, 0, 21)
    },
    {
      name: "Test 6", 
      place: "Bratislava",
      description: "",
      event_from: new Date(2024, 11, 10),
      event_to: new Date(2024, 11, 10)
    },
    {
      name: "Test 7", 
      place: "Bratislava",
      description: "",
      event_from: new Date(2025, 1, 14),
      event_to: new Date(2025, 1, 14)
    },
    {
      name: "Test 8", 
      place: "Bratislava",
      description: "",
      event_from: new Date(2025, 1, 10),
      event_to: new Date(2025, 1, 16)
    },
    {
      name: "Test 9", 
      place: "Bánska Bystrica",
      description: "",
      event_from: new Date(2025, 2, 10),
      event_to: new Date(2025, 2, 15)
    },
    {
      name: "Test 10", 
      place: "Bánska Bystrica",
      description: "",
      event_from: new Date(2025, 2, 12),
      event_to: new Date(2025, 2, 14)
    },
    {
      name: "Test 11", 
      place: "Bratislava",
      description: "",
      event_from: new Date(2024, 11, 5),
      event_to: new Date(2024, 11, 6)
    },
    {
      name: "Test 12", 
      place: "Košice",
      description: "",
      event_from: new Date(2024, 10, 25),
      event_to: new Date(2024, 10, 25)
    },
    {
      name: "Voľby", 
      place: "global",
      description: "Prezidentské voľby",
      event_from: new Date(2025, 4, 24),
      event_to: new Date(2025, 4, 25)
    },
    {
      name: "Sviatok", 
      place: "global",
      description: "Sviatok všetkých svätých",
      event_from: new Date(2024, 10, 1),
      event_to: new Date(2024, 10, 1)
    }
  ]

  locations: Location[] = [
    {
      id: 1,
      name: "Prešov"
    },
    {
      id: 2,
      name: "Košice"
    },
    {
      id: 3,
      name: "Žilina"
    },
    {
      id: 4,
      name: "Bánska bystrica"
    },
    {
      id: 5,
      name: "Nitra"
    },
    {
      id: 6,
      name: "Trenčín"
    },
    {
      id: 7,
      name: "Trnava"
    },
    {
      id: 8,
      name: "Bratislava"
    }

  ]

  getAllLocations(){
    return this.httpClient.get(`${config.backendURL}/api/city/all`)
  }

  getAllEventsByLocation(location: Location){
    return this.httpClient.get(`${config.backendURL}/api/event/all/${location.id}`)
  }

  sendNewEvent(data: EventCalendarRequests){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let headers = new HttpHeaders({
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
    });

    return this.httpClient.post(`${config.backendURL}/api/event/new`, JSON.stringify(data), {headers, observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        return true;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }
}
