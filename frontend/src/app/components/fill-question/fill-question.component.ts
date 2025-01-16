import { Component, Input, SimpleChanges } from '@angular/core';
import { Question, questionType } from '../../models/SurveysVoting';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { ChartConfiguration } from 'chart.js';


@Component({
  selector: 'app-fill-question',
  templateUrl: './fill-question.component.html',
  styleUrl: './fill-question.component.scss',
  animations:[
    trigger('resizeBackground', [
      state('small', style({
        width: "0%",
      })),
      state('large', style({
        width: '{{finalWidth}}'
      }), {params: {finalWidth: '0%'}}),
      transition('small <=> large', [
        animate("500ms ease-in-out")
      ])
    ])
  ]
})
export class FillQuestionComponent {
  @Input() question: Question = new Question;
  @Input() questionIdx: number = -1;
  @Input() showResults: boolean = false;
  @Input() results: Array<number> = [];
  @Input() animationState: string = "small";

  defaultOrangeColor: string = "#FF6E11";
  defaultNavyColor: string = "#000080";

  graphsData: ChartConfiguration<'bar'>['data'] = 
    {
      labels: ['1', '2', '3', '4', '5'],
      datasets: [{data: this.results, backgroundColor: Array.from({length: 5}, () => this.defaultOrangeColor), label: "Odpovede"}]
    }
  

  chartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    plugins: {
      legend: {
        labels: {
          generateLabels: () => {
            return [{text: "Odpovede", fillStyle: this.defaultOrangeColor}, {text: "Vaša odpoveď", fillStyle: this.defaultNavyColor}]
          }
        }
      }
    }
  }

  ngOnChanges(changes: SimpleChanges){
    if(changes['results'] && this.question.questionType === questionType.Scale){
      let selectedIdx = -1;
      for(let i = 0; i < 5; i++){
        if(this.question.answers[i].selected){
          selectedIdx = i;
        }
          
      }
      this.graphsData =
      {
        labels: ['1', '2', '3', '4', '5'],
        datasets: [{data: changes['results'].currentValue, backgroundColor: Array.from({length: 5}, (_, idx) => idx === selectedIdx ? this.defaultNavyColor : this.defaultOrangeColor), label: "Odpovede"}]
      }
    }
  }

  selectedOption: string | null = null; 

  selectScaleOption(index: number){
    for(let i = 0; i < this.question.answers.length; i++){
      if(i === index){
        this.question.answers[i].selected = !this.question.answers[i].selected;
      }
      else{
        this.question.answers[i].selected = false;
      }
      //@ts-ignore
      this.graphsData.datasets[0].backgroundColor[i] = this.question.answers[i].selected ? this.defaultNavyColor : this.defaultOrangeColor;
    }
  }

  selectSingleOption(index: number){
    for(let i = 0; i < this.question.answers.length; i++){
      if(i === index){
        this.question.answers[i].selected = true;
      }
      else{
        this.question.answers[i].selected = false;
      }
    }
  }

  selectMultipleOption(index: number){
    this.question.answers[index].selected = !this.question.answers[index].selected;
  }

  isRadioSelected(index: number){
    return this.question.answers[index].selected
  }

  getWidthString(num: number){
    return `${num}%`;
  }
}
