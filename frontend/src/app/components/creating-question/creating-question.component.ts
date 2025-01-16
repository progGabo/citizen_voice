import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Answer, Question, questionType } from '../../models/SurveysVoting';

@Component({
  selector: 'app-creating-question',
  templateUrl: './creating-question.component.html',
  styleUrl: './creating-question.component.scss'
})
export class CreatingQuestionComponent {
  @Input() question: Question;
  @Input() questionIdx: number = -1;
  @Input() isCreatingSurvey: boolean = false;
  @Output() removeQuestion: EventEmitter<number> = new EventEmitter();

  constructor(){
    this.question = new Question();
  }

  setTypeToSingleChoice(){
    this.question.questionType = questionType.SingleChoice
    this.question.answers = [new Answer(), new Answer()]
  }
  setTypeToMultipleChoice(){
    this.question.questionType = questionType.MutlitpleChoice
    this.question.answers = [new Answer(), new Answer()]
  }
  setTypeToScale(){
    this.question.questionType = questionType.Scale
    this.question.answers = Array.from({length: 5}, () => new Answer())
  }
  setTypeToFreeAnswer(){
    this.question.questionType = questionType.FreeAnswer
    this.question.answers = [new Answer()]
  }

  setOptional(){
    this.question.optional = !this.question.optional;
  }

  remove(){
    this.removeQuestion.emit(this.questionIdx);
  }

  addAnswer(){
    this.question.answers.push(new Answer());
  }

  removeAnswer(answerId: number){
    this.question.answers.splice(answerId, 1);
  }
}
