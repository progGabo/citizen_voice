import { Component, Input } from '@angular/core';
import { ExportService } from '../../services/export.service';
import { MatDialog } from '@angular/material/dialog';
import { ErrorComponent } from '../dialogs/error/error.component';
import { ExportDialogComponent } from '../dialogs/export-dialog/export-dialog.component';
import { Questionare, QuestionareType, questionType, SurveyBe, VotingBe } from '../../models/SurveysVoting';
import { ChoiceQuestion, ProcessedChoices, ProcessedData, ProcessedQuestions, QuestionareForProcessing, ScaleQuestion, TextQuestion } from '../../models/Export';

@Component({
  selector: 'app-export-button',
  templateUrl: './export-button.component.html',
  styleUrl: './export-button.component.scss'
})
export class ExportButtonComponent {
  @Input() exportingSurvey: boolean = false;
  @Input() urlToFetchData: string = "";
  @Input() urlToFetchQuestionare: string = "";
  @Input() urlToFetchAllData: string = "";

  exportInProgress: boolean = false;
  
  constructor(private exportService: ExportService, private dialog: MatDialog){}

  handleExportClick(){
    this.dialog.open(ExportDialogComponent, {data: {showCheckbox: this.exportingSurvey, title: "Nastavenie exportu dát"}}).afterClosed().subscribe(res => {
      if(typeof res === "string") return;
      this.exportService.fetchData(this.urlToFetchQuestionare, !this.exportingSurvey)?.subscribe({
        next: (questionare) => {
          if(this.exportingSurvey){
            questionare = new SurveyBe(questionare).convertToQuestionare();
          }
          else{
            questionare = new VotingBe(questionare).convertToQuestionare();
          }
          if(res.all){
            this.exportService.fetchData(this.urlToFetchAllData, !this.exportingSurvey)?.subscribe({
              next: async (fullStats) => {
                this.exportInProgress = true;
                let processedData = new ProcessedData();
                if(this.exportingSurvey){
                  processedData = this.processFullDataSurvey(questionare, fullStats);
                }
                else{
                  processedData = this.processDataVotings(questionare, fullStats)
                }
                processedData.extended = true;
                if(res.format === "csv")
                  this.exportService.exportToCsv(processedData);
                else
                  await this.exportService.exportToPdf(processedData)
                this.exportInProgress = false;
              },
              error: err => {
                this.dialog.open(ErrorComponent, {data: {text: "Nastala neočakávaná chyba pri získavaní dát pre export."}})
              }
            })
          }
          else{
            this.exportService.fetchData(this.urlToFetchData, !this.exportingSurvey)?.subscribe({
              next: async (stats) => {
                this.exportInProgress = true;
                let processedData = new ProcessedData();
                if(this.exportingSurvey){
                  processedData = this.processDataSurvey(questionare, stats);
                }
                else{
                  processedData = this.processDataVotings(questionare, stats);
                }
                if(res.format === "csv")
                  this.exportService.exportToCsv(processedData);
                else
                  await this.exportService.exportToPdf(processedData);
                this.exportInProgress = false;
              },
              error: err => {
                this.dialog.open(ErrorComponent, {data: {text: "Nastala neočakávaná chyba pri získavaní dát pre export."}})
              }
            })      
          }
        },
        error: err => {
          this.dialog.open(ErrorComponent, {data: {text: "Nastala neočakávaná chyba pri získavaní dát pre export."}})    
        }
      })
    })
  }

  processDataSurvey(questionare: Questionare, data: any){
    let processedData = new ProcessedData();
    processedData.questionareName = questionare.name;
    processedData.type = questionare.type === QuestionareType.Survey ? "Prieskum" : "Hlasovanie";
    processedData.numberOfRespondents = data.totalResponses;

    for(let question of questionare.questions){
      let processedQuestion = new ProcessedQuestions();
      processedQuestion.questionName = question.questionText;

      if(question.questionType === questionType.FreeAnswer){
        let statsForQuestion = data.textQuestions.filter((q: any) => Number(q.questionId) === question.id)[0];
        processedQuestion.numberOfResponders = statsForQuestion.totalAnswers;

        if(statsForQuestion.answers){
          processedQuestion.answers = statsForQuestion.answers;
        }

        processedData.freeAnswerQuestions.push(processedQuestion);
      }
      else if(question.questionType === questionType.Scale){
        let statsForQuestion = data.scaleQuestions.filter((q: any) => Number(q.questionId) === question.id)[0];
        processedQuestion.numberOfResponders = statsForQuestion.totalVotes;

        for(let i = 1; i < 6; i++){
          let newChoice = new ProcessedChoices();
          newChoice.text = `${i}`;
          newChoice.numberOfResponders = statsForQuestion.countsPerValue[`${i}`];
          newChoice.percentage = Math.round((((newChoice.numberOfResponders * 100) / statsForQuestion.totalVotes)* 100) / 100);
          processedQuestion.choices.push(newChoice);
        }

        processedData.scaleQuestions.push(processedQuestion);
      }
      else{
        let statsForQuestion = data.choiceQuestions.filter((q: any) => Number(q.questionId) === question.id)[0];
        processedQuestion.numberOfResponders = statsForQuestion.totalVotes;

        for(let ans of question.answers){
          let newChoice = new ProcessedChoices();
          newChoice.text = ans.answerText!;
          newChoice.numberOfResponders = statsForQuestion.countsPerChoice[`${ans.id}`];
          newChoice.percentage = Math.round((((newChoice.numberOfResponders * 100) / statsForQuestion.totalVotes)* 100) / 100);
          processedQuestion.choices.push(newChoice);
        }

        if(question.questionType === questionType.MutlitpleChoice){
          processedData.multipleChoiceQuestions.push(processedQuestion);
        }
        else{
          processedData.singleChoiceQuestions.push(processedQuestion);
        }
      }
    }
    return processedData;
  }

  processFullDataSurvey(questionare: Questionare, fullData: any){
    let data = fullData.content;
    let processingQuestionare = this.createQuestionareForProcessing(questionare);
    processingQuestionare.totalResponses = data.length;
    
    for(let userAns of data){
      for(let ans of userAns.answers){
        if(ans.type === "SCALE"){
          let q = processingQuestionare.scaleQuestions.filter(s => s.questionId === ans.questionId)[0];
          q.totalVotes++;
          q.countsPerValue[`${ans.scaleValue}`]++;
        }
        else if(ans.type === "TEXT"){
          let q = processingQuestionare.textQuestions.filter(s => s.questionId === ans.questionId)[0];
          q.totalAnswers++;
          q.answers.push(ans.answerContent);
        }
        else{
          let q = processingQuestionare.choiceQuestions.filter(s => s.questionId === ans.questionId)[0];
          q.totalVotes++;
          for(let selectedChoice of ans.surveyResponseChoices){
            q.countsPerChoice[`${selectedChoice.questionChoiceId}`]++;
          }
        }
      }
    }

    return this.processDataSurvey(questionare, processingQuestionare);
  }

  processDataVotings(questionare: Questionare, data: any){
    let voteCounts: number[] = []; //every question is single choice, so we keep track of number of respondets for every question a then we use MAX num as number of respondets to questionare
    let processedData = new ProcessedData();
    processedData.questionareName = questionare.name;
    processedData.type = questionare.type === QuestionareType.Survey ? "Prieskum" : "Hlasovanie";

    for(let question of questionare.questions){
      let processedQuestion = new ProcessedQuestions();
      processedQuestion.questionName = question.questionText;
      let numberOfRespondents = 0;
      for(let ans of question.answers){
        let dataAns = data.filter((obj: any) => Number(obj.answerId) === ans.id)[0];
        numberOfRespondents += dataAns.voteCount;

        let newChoice = new ProcessedChoices();
        newChoice.numberOfResponders = dataAns.voteCount;
        newChoice.percentage = Math.round((dataAns.percentage * 100) / 100);
        newChoice.text = ans.answerText!;
        processedQuestion.choices.push(newChoice);
      }

      processedQuestion.numberOfResponders = numberOfRespondents;
      voteCounts.push(numberOfRespondents);
      processedData.singleChoiceQuestions.push(processedQuestion);
    }

    processedData.numberOfRespondents = Math.max(...voteCounts);
    return processedData;
  }

  createQuestionareForProcessing(questionare: Questionare){
    let processingQuestionare = new QuestionareForProcessing();
    processingQuestionare.surveyId = questionare.id;
    for(let question of questionare.questions){
      if(question.questionType === questionType.Scale){
        let newQuest = new ScaleQuestion();
        newQuest.questionId = question.id;
        for(let i = 1; i < 6; i++){
          newQuest.countsPerValue[`${i}`] = 0;
        }
        processingQuestionare.scaleQuestions.push(newQuest);
      }
      else if(question.questionType === questionType.FreeAnswer){
        let newQuest = new TextQuestion();
        newQuest.questionId = question.id;
        processingQuestionare.textQuestions.push(newQuest);
      }
      else{
        let newQuest = new ChoiceQuestion();
        newQuest.questionId = question.id;
        for(let ans of question.answers){
          newQuest.countsPerChoice[`${ans.id}`] = 0; 
        }
        processingQuestionare.choiceQuestions.push(newQuest);
      }
    }
    return processingQuestionare;
  }
}