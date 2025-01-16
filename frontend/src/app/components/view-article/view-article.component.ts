import { Component, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ArticlesService } from '../../services/articles.service';
import { ClassicEditor, EditorConfig } from 'ckeditor5';

import {
	AccessibilityHelp,
	Autoformat,
	AutoImage,
	AutoLink,
	Autosave,
	BalloonToolbar,
	BlockQuote,
	Bold,
	CloudServices,
	Code,
	CodeBlock,
	Essentials,
	FindAndReplace,
	Heading,
	Highlight,
	HorizontalLine,
	HtmlEmbed,
	ImageBlock,
	ImageCaption,
	ImageInline,
	ImageInsertViaUrl,
	ImageResize,
	ImageStyle,
	ImageTextAlternative,
	ImageToolbar,
	ImageUpload,
	Indent,
	IndentBlock,
	Italic,
	Link,
	LinkImage,
	List,
	ListProperties,
	Paragraph,
	SelectAll,
	SpecialCharacters,
	SpecialCharactersArrows,
	SpecialCharactersCurrency,
	SpecialCharactersEssentials,
	SpecialCharactersLatin,
	SpecialCharactersMathematical,
	SpecialCharactersText,
	Strikethrough,
	Table,
	TableCellProperties,
	TableProperties,
	TableToolbar,
	TextTransformation,
	TodoList,
	Underline,
	Undo
} from 'ckeditor5';
import { Article, ArticleResponse, Comment } from '../../models/Articles';

@Component({
  selector: 'app-view-article',
  templateUrl: './view-article.component.html',
  styleUrl: './view-article.component.scss',
  encapsulation: ViewEncapsulation.None
})
export class ViewArticleComponent {

  id: string = "";
  Editor = ClassicEditor;
  config: EditorConfig = {}
  articleToRender: Article = new Article;
  isLayoutReady: boolean = false;
  loadingInProgress: boolean = false;
  comments: Comment[] = []
  articleLiked: boolean = false;


  constructor(private route: ActivatedRoute, private articleService: ArticlesService, private router: Router){}

  ngOnInit(){
    const idSnapshot = this.route.snapshot.paramMap.get("id");
    if(idSnapshot){
      this.id = idSnapshot;
      
      //ak nie je clanok nacitany po 500ms, tak sa zobrazi loader aby pouzivatel vedel ze sa daco deje 
      setTimeout(() => {
        if(!this.isLayoutReady)
          this.loadingInProgress = true;
      }, 500)

      this.articleService.getArticle(Number(this.id)).subscribe({
        next: article => {
          this.loadingInProgress = false;
          this.articleToRender = article;
          this.config = {
            toolbar: {
              items: [],
            },
            plugins: [
              AccessibilityHelp,
              Autoformat,
              AutoImage,
              AutoLink,
              Autosave,
              BalloonToolbar,
              BlockQuote,
              Bold,
              CloudServices,
              Code,
              CodeBlock,
              Essentials,
              FindAndReplace,
              Heading,
              Highlight,
              HorizontalLine,
              HtmlEmbed,
              ImageBlock,
              ImageCaption,
              ImageInline,
              ImageInsertViaUrl,
              ImageResize,
              ImageStyle,
              ImageTextAlternative,
              ImageToolbar,
              ImageUpload,
              Indent,
              IndentBlock,
              Italic,
              Link,
              LinkImage,
              List,
              ListProperties,
              Paragraph,
              SelectAll,
              SpecialCharacters,
              SpecialCharactersArrows,
              SpecialCharactersCurrency,
              SpecialCharactersEssentials,
              SpecialCharactersLatin,
              SpecialCharactersMathematical,
              SpecialCharactersText,
              Strikethrough,
              Table,
              TableCellProperties,
              TableProperties,
              TableToolbar,
              TextTransformation,
              TodoList,
              Underline,
              Undo
            ],
            initialData: this.articleToRender.text,
            menuBar: {
              isVisible: false
            },
          };

          this.articleService.getLikedArticles()?.subscribe({
            next: res => {
              for(let article of res.body.content){
                if(Number(article.id) === Number(this.id)){
                  this.articleLiked = true;
                  break;
                }
              }
            },
            error: err => {
              console.log(err);
            }
          })

          this.articleService.getCommentsForArtilce(Number(this.id)).subscribe({
            next: comments => {
              // zatial filtrujem na zaklade mena, idealne spolu s articlom posielat aj ID autora
              this.comments = comments.sort((c1, c2) => {
                if(c1.author === article.author) return -1;
                else if(c2.author === article.author) return 1;
                return 0;
              })
              this.isLayoutReady = true;
            },
            error: err => {
              this.comments = []
              this.isLayoutReady = true;
            }
          })
        },
        error: err => {
          this.loadingInProgress = false;
          this.router.navigateByUrl("/not_found")
        }
      })
      
      

    }
  }

  ngAfterViewInit(){
    
  }

  previewReady(editor: any){
    editor.enableReadOnlyMode("1");
  }

  likeArticle(){
    this.articleService.likeArticle(Number(this.id))?.subscribe({
      next: res => {
        console.log(res);
        this.articleLiked = !this.articleLiked;
      },
      error: err => {
        console.log(err)
      }
    })
  }
}
