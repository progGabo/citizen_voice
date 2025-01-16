import { Injectable } from '@angular/core';
import { UserService } from './user.service';
import { Petition, PetitionResponse } from '../models/Petitions';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { config } from '../environments/enviroment.dev';
import { catchError, map, of, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PetitionsService {

  constructor(private userService: UserService, private httpClient: HttpClient) { }

  savePetition(title: string, data: string, signeeCount: number, cityId: string){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);

    return this.httpClient.post(`${config.backendURL}/api/petitions`, {name: title, content: data, requiredSignees: signeeCount, cityId }, {headers, observe: 'response'}).pipe(
      map((res: HttpResponse<any>) => {
        return res;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  getPetition(id: number){
    return this.httpClient.get(`${config.backendURL}/api/petitions/${id}`, {observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        let petitionResponseObj: PetitionResponse = new PetitionResponse(res.body)
        let petition: Petition = petitionResponseObj.convertPetitionResponseToPetition();
        return petition;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`))
      })
    )
  }

  getSignedPetition(id: number){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);

    return this.httpClient.get(`${config.backendURL}/api/petitions/${id}/signed`, {headers, observe: 'response'}).pipe(
      map((res: HttpResponse<any>) => {
        return res;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  getAllPetitions(){
    return this.httpClient.get(`${config.backendURL}/api/petitions`, {observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        let responsePetitions = res.body.content;
        let resultPetitions: Petition[] = [];
        for(let petition of responsePetitions){
          let responsePetition = new PetitionResponse(petition);
          resultPetitions.push(responsePetition.convertPetitionResponseToPetition())
        }
        return resultPetitions;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`))
      })
    )
  }

  getPagedPetitions(pageSize: number, currPage: number){
    return this.httpClient.get(`${config.backendURL}/api/petitions?page=${currPage}&size=${pageSize}&sort=createdAt,desc`, {observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        let responsePetitions = res.body.content;
        let resultPetitions: Petition[] = [];
        for(let petition of responsePetitions){
          let responsePetition = new PetitionResponse(petition);
          resultPetitions.push(responsePetition.convertPetitionResponseToPetition())
        }
        return resultPetitions;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`))
      })
    )
  }

  signPetition(petitionId: number){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);

    return this.httpClient.get(`${config.backendURL}/api/petitions/sign/${petitionId}`, {headers, observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        return true;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  confirmSignature(token: string){
    return this.httpClient.get(`${config.backendURL}/api/petitions/sign/finish/${token}`, {observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        return true;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`))
      })
    )
  }

  getAllSignees(petitionId: number){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);

    return this.httpClient.get(`${config.backendURL}/api/petitions/${petitionId}/signees`, {headers, observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        return res.body;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }
}