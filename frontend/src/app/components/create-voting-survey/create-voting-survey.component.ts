import { Component } from '@angular/core';
import { UserService } from '../../services/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Answer, Question, Questionare, QuestionareType, questionType } from '../../models/SurveysVoting';
import { MatDialog } from '@angular/material/dialog';
import { ErrorComponent } from '../dialogs/error/error.component';
import { SurveysVotingService } from '../../services/surveys-voting.service';
import { SuccessComponent } from '../dialogs/success/success.component';

@Component({
  selector: 'app-create-voting-survey',
  templateUrl: './create-voting-survey.component.html',
  styleUrl: './create-voting-survey.component.scss'
})
export class CreateVotingSurveyComponent {

  isCreatingSurvey: boolean = false;
  name: string = "";
  allQuestions: Question[];

  constructor(private userService: UserService, private route: ActivatedRoute, private router: Router, private dialog: MatDialog, private surveyVotingService: SurveysVotingService){
    let firstQuestion = new Question();
    firstQuestion.answers = [new Answer()];
    this.allQuestions = [firstQuestion];
  }

  ngOnInit(){
    if(!this.userService.getIsAdmin()){
      this.router.navigateByUrl("/")
    }
    this.isCreatingSurvey = this.route.snapshot.routeConfig?.path === "create_survey"
  }

  addNewQuestion(){
    let newQuestion = new Question();
    newQuestion.answers = [new Answer(), new Answer()];
    this.allQuestions.push(newQuestion);
  }

  removeQuestion(questionId: number){
    this.allQuestions.splice(questionId, 1);
  }

  error(text: string){
    this.dialog.open(ErrorComponent, {
      data: {text}
    })
  }

  finalizeForm(){
    if(!this.name){
      this.error(`Zadajte názov ${this.isCreatingSurvey ? 'prieskumu' : 'hlasovania'}`)
      return;
    }
    for(let i = 0; i < this.allQuestions.length; i++){
      let question = this.allQuestions[i];
      if(!question.questionText){
        this.error(`Zadajte text ${i + 1}. otázky`)
        return;
      }
      if(question.questionType === questionType.MutlitpleChoice || question.questionType === questionType.SingleChoice){
        if(question.answers.length < 2){
          this.error(`Otázka ${i + 1} má málo možností`);
          return;
        }
        for(let j = 0; j < question.answers.length; j++){
          let ans = question.answers[j]
          if(!ans.answerText){
            this.error(`Zadajte text odpovede v ${i + 1}. otázke, v ${j + 1}. odpovedi`);
            return;
          }
        }
      }
    }

    //send to BE
    let finalForm = new Questionare();
    finalForm.type = this.isCreatingSurvey ? QuestionareType.Survey : QuestionareType.Voting;
    finalForm.questions = this.allQuestions;
    finalForm.name = this.name;
    if(this.isCreatingSurvey){
      this.surveyVotingService.sendSurvey(finalForm)?.subscribe({
        next: res => {
          const dialogRef = this.dialog.open(SuccessComponent, {data: {text: `Ďakujeme za vytvorenie ${this.isCreatingSurvey ? 'prieskumu' : 'hlasovania'}.`}});
          dialogRef.afterClosed().subscribe(() => {
            this.router.navigateByUrl("/home");
          })
        },
        error: err => {
          console.log(err)
        }
      })
    }
    else{
      this.surveyVotingService.sendVoting(finalForm)?.subscribe({
        next: res => {
          const dialogRef = this.dialog.open(SuccessComponent, {data: {text: `Ďakujeme za vytvorenie ${this.isCreatingSurvey ? 'prieskumu' : 'hlasovania'}.`}});
          dialogRef.afterClosed().subscribe(() => {
            this.router.navigateByUrl("/home");
          })
        },
        error: err => {
          console.log(err)
        }
      })
    }
  }
}
