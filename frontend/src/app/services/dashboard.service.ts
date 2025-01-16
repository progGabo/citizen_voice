import { HttpClient, HttpHandler, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { config } from '../environments/enviroment.dev';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  constructor(private _httpClient: HttpClient) { }


  getPendingAccounts(){

    let token = sessionStorage.getItem("adminToken");
    if(!token) return;

    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);

    return this._httpClient.get(`${config.backendURL}/api/admin/organization/pending`, {headers});
  }

  approveAccount(userId: number){
    let token = sessionStorage.getItem("adminToken");
    if(!token) return;
    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);

    return this._httpClient.get(`${config.backendURL}/api/admin/organization/activate/${userId}`, {headers});
  }

  declineAccount(){

  }
}
