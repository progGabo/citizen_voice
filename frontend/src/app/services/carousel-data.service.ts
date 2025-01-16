import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Carousel } from '../models/CarouselDataSets';
import { catchError, map, of } from 'rxjs';
import { Article } from '../models/Articles';
import { ArticlesService } from './articles.service';
import { Observable } from 'ckeditor5';
import { PetitionsService } from './petitions.service';
import { SurveysVotingService } from './surveys-voting.service';

@Injectable({
  providedIn: 'root'
})
export class CarouselDataService {

  constructor(private _httpClient: HttpClient, 
              private articleService: ArticlesService, 
              private petitionService: PetitionsService, 
              private surveyVotingService: SurveysVotingService
            ) { }

  petitionMockData: Carousel[] = [
    {
      author: "Peter Vanat",
      text: "Zadarmo parking vsade v KE PLS",
      title: "Parkovanie v KE opat zadarmo",
      id: 1
    },
    {
      author: "Katarina Siva",
      text: "Figma je bolest, podme to zmenit",
      title: "Make figma die",
      id: 2
    },
    {
      author: "Kristian Stutiak",
      text: "A falu, ahol tartózkodom, jelenleg magyar fennhatóság alatt áll. Változtassunk ezen",
      title: "Peder szlovák falu",
      id: 3
    },
    {
      author: "Jakub Smallcicky",
      text: "Testujem v Jave uz nejaky ten piatok. Od zaciatku nefunguje obycajne porovnavanie casu.",
      title: "Testujme v produkcii",
      id: 4
    },
    {
      author: "Gabriel Strakay",
      text: "Python je abstrakcia C++, ale viete o tom ze FastAPI je abstrackia pekneho zivota?",
      title: "Make FastAPI fast",
      id: 5
    },
  ]


  getCarouselArticleData(){
    //http request

    return this.articleService.getPagedArticles(5, 0).pipe(
      map(arr => {
        let toRender: Carousel[] = arr.map(article => {
          let newSlide: Carousel = new Carousel;
          newSlide.author = article.author;
          newSlide.id = article.id;
          newSlide.title = article.title;
          newSlide.text = this.sanitizeText(article.text);
          return newSlide
        })

        return toRender;
      }),
      catchError((err: any) => {
        console.log(`CarouselDataService.getCarouselArticleData error ${err}`)
        return of([]);
      })
    )
  }

  sanitizeText(text: string){
    let tmpDiv = document.createElement("div")
    tmpDiv.innerHTML = text;
    let firstChild = tmpDiv.firstChild;
    if(!firstChild) return "";
    let nodeName = firstChild.nodeName.toLowerCase();

    while(nodeName === "figure" && firstChild){ //first element in article is image
      firstChild = firstChild?.nextSibling;
      if(!firstChild) return ""
      nodeName = firstChild.nodeName.toLowerCase();
    }

    if(!firstChild.textContent) return "";
    return firstChild.textContent.concat("...")
  }

  getCarouselPetitionData(){
    return this.petitionService.getPagedPetitions(5, 0).pipe(
      map(arr => {
        let toRender: Carousel[] = arr.map(petition => {
          let newSlide: Carousel = new Carousel;
          newSlide.author = petition.author;
          newSlide.id = petition.id;
          newSlide.title = petition.title;
          newSlide.text = this.sanitizeText(petition.text);
          return newSlide
        })

        return toRender;
      }),
      catchError((err: any) => {
        console.log(`CarouselDataService.getCarouselArticleData error ${err}`)
        return of([]);
      })
    )
  }

  getCarouselSurveyData(){
    return this.surveyVotingService.getPagedSurvey(5, 0).pipe(
      map(arr => {
        let toRender: Carousel[] = arr.map(survey => {
          let newSlide: Carousel = new Carousel;
          newSlide.author = survey.authorName;
          newSlide.id = survey.id;
          newSlide.title = survey.name;
          newSlide.text = "";
          return newSlide
        })
        return toRender;
      }),
      catchError((err: any) => {
        console.log(`CarouselDataService.getCarouselSurveyData error ${err}`)
        return of([]);
      })
    )
  }

  getCarouselVotingData(){
    return this.surveyVotingService.getPagedVotings(5, 0).pipe(
      map(arr => {
        let toRender: Carousel[] = arr.map(voting => {
          let newSlide: Carousel = new Carousel;
          newSlide.author = voting.authorName;
          newSlide.id = voting.id;
          newSlide.title = voting.name;
          newSlide.text = "";
          return newSlide
        })
        return toRender;
      }),
      catchError((err: any) => {
        console.log(`CarouselDataService.getCarouselVotingData error ${err}`)
        return of([]);
      })
    )
  }

}
