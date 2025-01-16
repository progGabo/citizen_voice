import { Component, EventEmitter, Input, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { Router } from '@angular/router';
import { Questionare, QuestionareType, questionType } from '../../models/SurveysVoting';
import { SurveysVotingService } from '../../services/surveys-voting.service';
import { UserService } from '../../services/user.service';
import { MatDialog } from '@angular/material/dialog';
import { ErrorComponent } from '../dialogs/error/error.component';

@Component({
  selector: 'app-votings-surveys',
  templateUrl: './votings-surveys.component.html',
  styleUrl: './votings-surveys.component.scss'
})
export class VotingsSurveysComponent {
  @ViewChild(MatPaginator) paginator: any;
  @Input() isLoggedIn: boolean = false;
  @Input() filterText: string = "";
  @Output() filterTextChange = new EventEmitter<string>();
  
  constructor(private router: Router, private surveyVotingService: SurveysVotingService, private userService: UserService, private dialog: MatDialog){}

  pageSize = 3;
  currentPage = 0;
  pagedQuestionares: Questionare[] = [];
  questionares: Questionare[] = [];
  allQuestionares: Questionare[] = [];

  ngOnInit(){
    this.surveyVotingService.getAllQuestionares().subscribe(res => {
      if(res){
        this.questionares = res;
        this.allQuestionares = res;
      }
      this.updatePagedQuestionares();
    })
  }

  ngOnChanges(changes: SimpleChanges){
    if(changes['filterText'] && changes['filterText'].currentValue.includes("search_")){
      this.handleFilterChange(changes['filterText'].currentValue)
    }
  }


  onPageChange(event: any){
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.updatePagedQuestionares();
  }

  updatePagedQuestionares(){
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.pagedQuestionares = this.questionares.slice(startIndex, endIndex);
  }

  getShortenedText(fullText: string){
    if(fullText.length > 200){
      return `${fullText.substring(0, 200)}...`;
    }
    return fullText;
  }

  handleBtnClick(id: string | number, type: QuestionareType){
    if(!this.userService.getIsLoggedIn()){
      const error = type === QuestionareType.Survey ? "prieskumu" : "hlasovania";
      this.dialog.open(ErrorComponent, {data: {text: `Pred vyplnením ${error} sa najprv prihláste`}})
      return;
    }
    let subRoute = type === QuestionareType.Survey ? "survey" : "voting";
    this.router.navigateByUrl(`/fill_out/${subRoute}/${id}`);
  }

  handleFilterChange(filter: string){
    if(filter.includes("search_")){
      let option = filter.split("search_")[1];
      this.filterText = `Text: ${option}`;
      this.questionares = this.allQuestionares.filter(q => q.name.toLowerCase().includes(option));
    }
    else if(filter != "all"){
      this.filterText = filter === "voting" ? "Hlasovania" : "Prieskumy"
      this.questionares = this.allQuestionares.filter(questionare => filter === "voting" ? questionare.type === QuestionareType.Voting : questionare.type === QuestionareType.Survey)
    }

    this.currentPage = 0;
    this.updatePagedQuestionares();
  }

  removeFilter(){
    this.filterTextChange.emit("");
    this.questionares = this.allQuestionares;
    this.filterText = "";
    this.updatePagedQuestionares();
  }
}
