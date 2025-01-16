import { Component, EventEmitter, Input, Output, SimpleChanges, ViewChild } from '@angular/core';
import { Article } from '../../models/Articles';
import { MatPaginator } from '@angular/material/paginator';
import { Router } from '@angular/router';
import { ArticlesService } from '../../services/articles.service';
import { CarouselDataService } from '../../services/carousel-data.service';

@Component({
  selector: 'app-articles',
  templateUrl: './articles.component.html',
  styleUrl: './articles.component.scss'
})
export class ArticlesComponent {
  @ViewChild(MatPaginator) paginator: any;

  @Input() selectedSort: string = "";
  @Output() selectedSortChange = new EventEmitter<string>();

  articles: Article[] = []
  originalArticles: Article[] = [];

  constructor(private router: Router, private articlesService: ArticlesService, private carouselService: CarouselDataService){}

  pageSize = 3;
  currentPage = 0;
  pagedArticles: Article[] = [];

  ngOnInit(){
    this.articlesService.getAllArticles().subscribe({
      next: res => {
        this.articles = res.map(article => {
          article.text = this.carouselService.sanitizeText(article.text);
          return article;
        });
        this.originalArticles = res.map(article => {
          article.text = this.carouselService.sanitizeText(article.text);
          return article;
        });
        this.updatePagedArticles();
      },
      error: err => {
        console.log("ZLE")
      }
    })
  }

  ngOnChanges(changes: SimpleChanges){
    if(changes['selectedSort'] && changes['selectedSort'].currentValue.includes("search_")){
      this.sortArticles(changes['selectedSort'].currentValue)
    }
  }

  sortArticles(sortOption: string){
    if(sortOption.includes("search_")){
      let option = sortOption.split("search_")[1];
      this.articles = this.originalArticles.filter(article => {
        return article.title.toLowerCase().includes(option) || article.text.toLowerCase().includes(option)
      })
      this.selectedSort = `Text: ${option}`
    }
    else if(sortOption === "new"){
      this.selectedSort = "Najnovšie"
      this.articles = this.originalArticles.toSorted((a,b) => { return b.publishDate.getTime() - a.publishDate.getTime()})
    }
    else{
      this.selectedSort = "Najstaršie"
      this.articles = this.originalArticles.toSorted((a,b) => { return a.publishDate.getTime() - b.publishDate.getTime()})
    }
    this.updatePagedArticles();
  }

  removeSort(){
    this.selectedSortChange.emit("");
    this.selectedSort = "";
    this.articles = this.originalArticles.toSorted((a,b) => a.id - b.id)
    this.updatePagedArticles();
  }


  onPageChange(event: any){
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.updatePagedArticles();
  }

  updatePagedArticles(){
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.pagedArticles = this.articles.slice(startIndex, endIndex);
  }

  getShortenedText(fullText: string){
    if(fullText.length > 200){
      return `${fullText.substring(0, 200)}...`;
    }
    return fullText;
  }

  handleBtnClick(id: string | number){
    const url = this.router.serializeUrl(this.router.createUrlTree([`/article/${id}`]));
    window.open(url, "_blank")
  }

}
