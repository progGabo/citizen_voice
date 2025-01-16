import { Component, Input } from '@angular/core';
import { Question, Questionare, QuestionareType, questionType } from '../../models/SurveysVoting';
import { ActivatedRoute, Router } from '@angular/router';
import { SurveysVotingService } from '../../services/surveys-voting.service';
import { MatDialog } from '@angular/material/dialog';
import { ErrorComponent } from '../dialogs/error/error.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SuccessComponent } from '../dialogs/success/success.component';

@Component({
  selector: 'app-fill-survey-voting',
  templateUrl: './fill-survey-voting.component.html',
  styleUrl: './fill-survey-voting.component.scss'
})
export class FillSurveyVotingComponent {
  
  constructor(
    private route: ActivatedRoute, 
    private surveyVotingService: SurveysVotingService, 
    private dialog: MatDialog, 
    private router: Router,
    private snackBar: MatSnackBar
  ){}

  id: number = -1;
  questionare: Questionare | undefined;
  showResults: boolean = false;
  results: Array<Array<number>> = [];
  loading: boolean = true;
  displayType: string = "prieskum";

  animationState: string = "small";

  ngOnInit(){
    const idSnapshot = this.route.snapshot.paramMap.get("id");
    const typeRoute = this.route.snapshot.paramMap.get("type");
    let isSurvey = true;
    if(typeRoute === "voting"){
      isSurvey = false;
      this.displayType = "hlasovanie"
    }
    else{
      if(typeRoute != "survey"){
        this.router.navigateByUrl("/not_found");
      }
    }

    if(idSnapshot){
      this.id = Number(idSnapshot);
      this.surveyVotingService.getQuestionsById(this.id, isSurvey).subscribe({
        next: res => {
          let questionIdx = 0;
          this.questionare = res
          for(let question of this.questionare?.questions){
            this.results[questionIdx++] = [-1]
          }
          this.loading = false;
        },
        error: err => {
          this.router.navigateByUrl("/not_found")
        }
      })
    }
    else{
      this.router.navigateByUrl("/home")
    }
  }

  error(text: string){
    this.dialog.open(ErrorComponent, {
      data: {text}
    })
  }

  validate(){
    let questions = this.questionare?.questions;
    for(let i = 0; i < questions!.length; i++){
      let question = questions![i];
      if(question.optional) continue;
      if(question.questionType !== questionType.FreeAnswer){
        let selected = question.answers.filter(ans => ans.selected);
        if(selected.length === 0){
          this.error(`V otázke ${i + 1} zvoľte aspoň jednu možnosť`);
          return false;
        }
      }
      else{
        if(question.answers[0].answerText?.length === 0){
          this.error(`V otázke ${i + 1} napíšte odpoveď`);
          return false;
        }
      }
    }
    return true;
  }

  send(){
    let valid = this.validate();
    if(valid){
      if(this.questionare?.type === QuestionareType.Survey){
        this.surveyVotingService.sendSurveyAnswer(this.questionare)?.subscribe({
          next: res => {
            console.log(res)
            this.results = res;
            const dialogRef = this.dialog.open(SuccessComponent, {data: {text: "Ďakujeme za vyplnenie formuláru", secondButtonString: "Zobraziť výsledky"}})
            dialogRef.afterClosed().subscribe((res) => {
              if(res != "showResults"){
                this.router.navigateByUrl("/home");
              }
              else{
                this.showResults = true;
                this.animationState = "large";
              }
            })
          },
          error: err => {
            if(err.message === "409"){
              this.dialog.open(ErrorComponent, {data: {text: "Na tento formulár ste už odpovedali"}})
            }
            else{
              console.log(err)
              this.dialog.open(ErrorComponent, {data: {text: "Nastala chyba pri odosielaní vašej odpovede"}})
            }
            
          }
        })
      }
      else{
        this.surveyVotingService.sendVotingAnswer(this.questionare!)?.subscribe({
          next: res => {
            this.results = res;
            const dialogRef = this.dialog.open(SuccessComponent, {data: {text: "Ďakujeme za vyplnenie formuláru", secondButtonString: "Zobraziť výsledky"}})
            dialogRef.afterClosed().subscribe((res) => {
              if(res != "showResults"){
                this.router.navigateByUrl("/home");
              }
              else{
                this.showResults = true;
                this.animationState = "large";
              }
            })
          },
          error: err => {
            if(err.message === "409"){
              this.dialog.open(ErrorComponent, {data: {text: "Na tento formulár ste už odpovedali"}})
            }
            else{
              console.log(err)
              this.dialog.open(ErrorComponent, {data: {text: "Nastala chyba pri odosielaní vašej odpovede"}})
            }
            
          }
        })
      }
      
    }
  }
  close(){
    this.router.navigateByUrl("/home");
  }
}
