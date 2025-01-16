import { Injectable } from '@angular/core';
import { UserService } from './user.service';
import { Article, ArticleResponse, Comment } from '../models/Articles';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { config } from '../environments/enviroment.dev';
import { catchError, map, Observable, of, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ArticlesService {

  constructor(private userService: UserService, private httpClient: HttpClient) { }

  saveArticle(title: string, article: string){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);

    let currTime = new Date();

    return this.httpClient.post(`${config.backendURL}/api/article/new`, {title, content: article, publishDate: currTime.toISOString()}, {headers, observe: 'response'}).pipe(
      map((res: HttpResponse<any>) => {
        return res;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  getArticle(id: number){

    return this.httpClient.get(`${config.backendURL}/api/article/${id}`, {observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        let articleResponseObj: ArticleResponse = new ArticleResponse(res.body)
        let article: Article = articleResponseObj.convertArticleResponseToArticle();
        return article;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`))
      })
    )
  }

  getPagedArticles(pageSize: number, currPage: number){
    return this.httpClient.get(`${config.backendURL}/api/article/all?page=${currPage}&size=${pageSize}&sort=createdAt,desc`, {observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        let responseArticles = res.body.content;
        let resultArticles: Article[] = [];
        for(let article of responseArticles){
          let responseArticle = new ArticleResponse(article);
          resultArticles.push(responseArticle.convertArticleResponseToArticle())
        }
        return resultArticles;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`))
      })
    )
  }

  getAllArticles(){
    return this.httpClient.get(`${config.backendURL}/api/article/all`, {observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        let responseArticles = res.body.content;
        let resultArticles: Article[] = [];
        for(let article of responseArticles){
          let responseArticle = new ArticleResponse(article);
          resultArticles.push(responseArticle.convertArticleResponseToArticle())
        }
        return resultArticles;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`))
      })
    )
  }

  getCommentsForArtilce(articleId: number): Observable<Comment[]>{
    //http request
    let comments: Comment[] = [
      {
        author: "Peter Vanat",
        text: "test comment",
        authorId: 15
      },
      {
        author: "Andrej Kováč",
        text: "Komentar super brutalny",
        authorId: 3
      }
    ]
    return of(comments)
  }

  likeArticle(articleId: number){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);

    return this.httpClient.post(`${config.backendURL}/api/article/user/like/${articleId}`, null, {headers, observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        return true;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  getLikedArticles(){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);

    return this.httpClient.get(`${config.backendURL}/api/article/user/get/liked`, {headers, observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        return res;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`))
      })
    )
  }

}