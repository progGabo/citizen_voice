import { Injectable } from '@angular/core';
import { catchError, forkJoin, map, Observable, of, throwError } from 'rxjs';
import { Answer, Question, Questionare, questionType, SurveyBe, VotingBe } from '../models/SurveysVoting';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { config } from '../environments/enviroment.dev';

@Injectable({
  providedIn: 'root'
})
export class SurveysVotingService {

  constructor(private httpClient: HttpClient) { }

  getAllQuestionares(){

    const surveys$ = this.getAllSurveys();
    const votings$ = this.getAllVotings();

    return forkJoin([surveys$, votings$]).pipe(
      map(([surveys, votings]) => {
        return [...surveys, ...votings]
      })
    )
  }

  getQuestionsById(id: number, isSurvey: boolean): Observable<Questionare>{
    //http call
    if(isSurvey){
      return this.httpClient.get(`${config.backendURL}/api/survey/${id}`, {observe: "response"}).pipe(
        map((res: HttpResponse<any>) => {
          let questionareBe = new SurveyBe(res.body)
          return questionareBe.convertToQuestionare();
        }),
        catchError((err: HttpErrorResponse) => {
          return throwError(() => new Error(`${err.status}`));
        })
      )
    }
    else{
      return this.httpClient.get(`${config.backendURL}/api/voting/${id}`, {observe: "response"}).pipe(
        map((res: HttpResponse<any>) => {
          let questionareBe = new VotingBe(res.body)
          return questionareBe.convertToQuestionare();
        }),
        catchError((err: HttpErrorResponse) => {
          return throwError(() => new Error(`${err.status}`));
        })
      )
    }
  }

  sendVoting(questionare: Questionare){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let questionareForBe = new VotingBe();
    questionareForBe.convertQuestionareToBeDTO(questionare);

    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);

    return this.httpClient.post(`${config.backendURL}/api/voting/new`, questionareForBe, {headers, observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        return res;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  sendSurvey(questionare: Questionare){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let questionareForBe = new SurveyBe();
    questionareForBe.convertQuestionareToBeDTO(questionare);

    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);

    return this.httpClient.post(`${config.backendURL}/api/survey/new`, questionareForBe, {headers, observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        return res;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  getPagedSurvey(pageSize: number, currPage: number){
    return this.httpClient.get(`${config.backendURL}/api/survey/all?page=${currPage}&size=${pageSize}&sort=createdAt,desc`, {observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        let responseSurveys = res.body.content;
        let resultSurveys: Questionare[] = [];
        for(let survey of responseSurveys){
           let newQuestionare = new SurveyBe(survey);
           resultSurveys.push(newQuestionare.convertToQuestionare());
        }

        return resultSurveys;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`))
      })
    )
  }

  getAllSurveys(){
    return this.httpClient.get(`${config.backendURL}/api/survey/all`, {observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        let responseSurveys = res.body.content;
        let resultSurveys: Questionare[] = []
        for(let s of responseSurveys){
          let newQuestionare = new SurveyBe(s);
          resultSurveys.push(newQuestionare.convertToQuestionare());
        }
        return resultSurveys;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  sendSurveyAnswer(filledQuestionare: Questionare){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);

    let resultQuestionare = filledQuestionare.convertToSurveyAnswerDto();
    return this.httpClient.post(`${config.backendURL}/api/survey/${filledQuestionare.id}/answer`, resultQuestionare, {headers, observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        let results: Array<Array<number>> = filledQuestionare.processAnswers(res.body.statistics);
        return results;
      }),
      catchError((err: HttpErrorResponse) => {
        console.log(err)
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }


  getAllVotings(){
    return this.httpClient.get(`${config.backendURL}/api/voting/all`, {observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        let responseSurveys = res.body.content;
        let resultSurveys: Questionare[] = []
        for(let s of responseSurveys){
          let newQuestionare = new VotingBe(s);
          resultSurveys.push(newQuestionare.convertToQuestionare());
        }
        return resultSurveys;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`));
      })
    )
  }

  getPagedVotings(pageSize: number, currPage: number){
    return this.httpClient.get(`${config.backendURL}/api/voting/all?page=${currPage}&size=${pageSize}&sort=createdAt,desc`, {observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        let responseSurveys = res.body.content;
        let resultSurveys: Questionare[] = [];
        for(let survey of responseSurveys){
           let newQuestionare = new VotingBe(survey);
           resultSurveys.push(newQuestionare.convertToQuestionare());
        }

        return resultSurveys;
      }),
      catchError((err: HttpErrorResponse) => {
        return throwError(() => new Error(`${err.status}`))
      })
    )
  }

  sendVotingAnswer(questionare: Questionare){
    const token = sessionStorage.getItem("token");
    if(!token) return;

    let bodyArr = questionare.convertToVotingAnswersDTO();
    let headers = new HttpHeaders();
    headers = headers.set("Authorization", `Bearer ${token}`);

    return this.httpClient.post(`${config.backendURL}/api/voting/${questionare.id}/vote`, {answerId: bodyArr}, {headers, observe: "response"}).pipe(
      map((res: HttpResponse<any>) => {
        console.log(res)
        let results: Array<Array<number>> = questionare.processVotingAnswers(res.body);
        return results;
      }),
      catchError((err: HttpErrorResponse) => {
        console.log(err)
        return throwError(() => new Error(`${err.status}`));
      })
    )

  }

}
