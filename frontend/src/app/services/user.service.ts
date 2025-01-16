import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, of, throwError } from 'rxjs';
import { config } from '../environments/enviroment.dev';
import { LoginData, RegisterData, UserDetail } from '../models/User';
import { Observable } from 'rxjs';
import { Petition, PetitionResponse } from '../models/Petitions';
import { Article, ArticleResponse } from '../models/Articles';
import { SurveysVotingService } from './surveys-voting.service';
import { Questionare, SurveyBe, VotingBe } from '../models/SurveysVoting';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  
  //private apiUrl = 'API_ENDPOINT_HERE';

  constructor(private _httpClient: HttpClient) {}

  login(formData: LoginData){
    const headers = new HttpHeaders({
      "Content-Type": "application/json"
    })

    return this._httpClient.post(`${config.backendURL}/api/auth/login`, JSON.stringify(formData), { headers, observe: 'response' }).pipe(
      map((res: HttpResponse<any>) => {
        
        sessionStorage.setItem("userId", res.body.userId);
        sessionStorage.setItem("token", res.body.id_token);
        sessionStorage.setItem("isLoggedIn", "true");
        
        
        return this.setUserData();
        //este jeden request tu pojde na getUserData, tieto data ulozim sebe do sessionStorage a vybavenkos, blueprint je nizsie aby to ostalo consistent s userSettings
        // plus z tohto requestu vratim ci je admin alebo ne aby som to vedel v login-register-dialog componente spravne nastavit

        
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  setUserData(){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);
    

    return this._httpClient.get(`${config.backendURL}/api/users/detail`, {headers, observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        sessionStorage.setItem("userName", res.body.firstName);
        sessionStorage.setItem("userFirstName", res.body.firstName);
        sessionStorage.setItem("userLastName", res.body.lastName);
        sessionStorage.setItem("userEmail", res.body.email);
        sessionStorage.setItem("userAddress", res.body.address || "");
        sessionStorage.setItem("userCity", res.body.city || "");
        sessionStorage.setItem("userPostalCode", res.body.postal || "");
        sessionStorage.setItem("userId", res.body.id);

        sessionStorage.setItem("userCityId", res.body.cityId || -1);

        if(res.body.role === "ROLE_ORGANIZATION"){
          sessionStorage.setItem("isAdmin", "true");
        }
        else{
          sessionStorage.setItem("isAdmin", "false");
        }
        return {success: true, isAdmin: res.body.role === "ROLE_ORGANIZATION", userName: res.body.firstName};
      })
    )
  }

  register(registerData: RegisterData){
    const headers = new HttpHeaders({
      "Content-Type": "application/json"
    })
    return this._httpClient.post(`${config.backendURL}/api/auth/register`, JSON.stringify(registerData), {headers, observe: 'response'}).pipe(
      map((res: HttpResponse<any>) => {
        console.log(res);
        return true
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  logout(){
    sessionStorage.clear();
  }

  toggleIsAdmin(){
    if(sessionStorage.getItem("isAdmin")){
      sessionStorage.setItem("isAdmin", sessionStorage.getItem("isAdmin") === "true" ? "false" : "true")
    }
  }

  getIsLoggedIn(){
    return sessionStorage.getItem("isLoggedIn") === "true";
  }

  getIsAdmin(){
    return sessionStorage.getItem("isAdmin") === "true";
  }

  getUserData() {
    return {
      name: sessionStorage.getItem("userName"),
      firstName: sessionStorage.getItem("userFirstName"),
      lastName: sessionStorage.getItem("userLastName"),
      email: sessionStorage.getItem("userEmail"),
      address: sessionStorage.getItem("userAddress"),
      city: sessionStorage.getItem("userCity"),
      postalCode: sessionStorage.getItem("userPostalCode")
    };
  }

  updateUserData(data: { firstName: string; lastName: string; email: string; address: string; city: string; postalCode: string }) {
    sessionStorage.setItem("userFirstName", data.firstName);
    sessionStorage.setItem("userLastName", data.lastName);
    sessionStorage.setItem("userEmail", data.email);
    sessionStorage.setItem("userAddress", data.address);
    sessionStorage.setItem("userCity", data.city);
    sessionStorage.setItem("userPostalCode", data.postalCode);
  }

  activateAccount(token: string){
    return this._httpClient.get(`${config.backendURL}/api/auth/register-finish/${token}`, {observe: 'response'}).pipe(
      map((res: HttpResponse<any>) => {
        return res;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  loginAsAdmin(formData: LoginData){
    const headers = new HttpHeaders({
      "Content-Type": "application/json"
    })

    return this._httpClient.post(`${config.backendURL}/api/auth/login`, JSON.stringify(formData), { headers, observe: 'response' }).pipe(
      map((res: HttpResponse<any>) => {
        sessionStorage.setItem("adminToken", res.body.id_token);
        return {success: true};
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  getCreatedPetitions(){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);
    
    return this._httpClient.get<Petition[]>(`${config.backendURL}/api/petitions/user/petitions`, {headers, observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        let petitions: Petition[] = [];
        for(let petition of res.body.content){
          petitions.push(new PetitionResponse(petition).convertPetitionResponseToPetition());
        }
        return (petitions);
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  getCreatedArticles() {
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);

    const url = this.getIsAdmin() ? `${config.backendURL}/api/article/user/article` : `${config.backendURL}/api/article/user/get/liked`;   

    return this._httpClient.get<Article[]>(url, {headers, observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        let articles: Article[] = [];
        for(let article of res.body.content){
          articles.push(new ArticleResponse(article).convertArticleResponseToArticle());
        }
        return (articles);
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  getCreatedSurveys(){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let userId = sessionStorage.getItem("userId");
    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);
    
    return this._httpClient.get<Questionare[]>(`${config.backendURL}/api/survey/all`, {headers, observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        let userQuestionares: any[] = res.body.content.filter((obj: any) => Number(obj.author.id) === Number(userId));
        let questionares: Questionare[] = [];
        for(let questionare of userQuestionares){
          questionares.push(new SurveyBe(questionare).convertToQuestionare());
        }
        return (questionares);
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  getCreatedVotings(){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);
    
    return this._httpClient.get<Questionare[]>(`${config.backendURL}/api/voting/user/voting`, {headers, observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        let votings: Questionare[] = [];
        for(let voting of res.body.content){
          votings.push(new VotingBe(voting).convertToQuestionare());
        }
        return (votings);
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }
  
}