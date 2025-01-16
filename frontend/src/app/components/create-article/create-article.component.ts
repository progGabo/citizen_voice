import { Component, HostListener, ViewEncapsulation } from '@angular/core';
import { UserService } from '../../services/user.service';
import { MatDialog } from '@angular/material/dialog';
import {
	ClassicEditor,
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
	Undo,
	type EditorConfig
} from 'ckeditor5';
import { CanComponentDeactivate } from '../../guards/can-deactivate';
import { ConfirmComponent } from '../dialogs/confirm/confirm.component';
import { ErrorComponent } from '../dialogs/error/error.component';
import { ArticlesService } from '../../services/articles.service';
import { SuccessComponent } from '../dialogs/success/success.component';
import { ActivatedRoute, Router } from '@angular/router';
import { PetitionsService } from '../../services/petitions.service';
import { FormControl } from '@angular/forms';
import { debounceTime, Observable, of } from 'rxjs';
import { EventsService } from '../../services/events.service';
import { Location } from '../../models/Events';

@Component({
  selector: 'app-create-article',
  templateUrl: './create-article.component.html',
  styleUrl: './create-article.component.scss',
  encapsulation: ViewEncapsulation.None
})
export class CreateArticleComponent implements CanComponentDeactivate{

  loggedIn: boolean = false;
  isAdmin: boolean = false;
  isLayoutReady: boolean = false;
  Editor = ClassicEditor;
  config: EditorConfig = {};
  selectedImage: HTMLImageElement | null = null;
  editorData: string = ""
  title: string = ""
  articleSaved: boolean = false;
  signeeCount = 0;
  userCityId: number = -1;
  selectedCityId: string = "";

  creatingArticle: boolean = true;
  sendingInProgress: boolean = false;
  showEditor: boolean = false;
  formControl = new FormControl("");
  filteredLocations:  Observable<Location[] | null> = of(null);
  locations: Location[] = [];

  constructor(private userService: UserService, 
			  private dialog: MatDialog, 
			  private articleService: ArticlesService, 
			  private router: Router, 
			  private route: ActivatedRoute, 
			  private petitionService: PetitionsService,
			  private eventService: EventsService
			 ){
				this.userCityId = Number(userService.getUserCityId());
			 }

  ngOnInit(){
	this.creatingArticle = this.route.snapshot.url[0].path === "create_article"
	
	this.formControl.valueChanges.pipe(debounceTime(300)).subscribe(val => {
		this.onInputChange(val!);
	})

	this.eventService.getAllLocations().subscribe((res: any) => {
		this.locations = res.content
		this.filteredLocations = of(this.locations)
	  });

    if(this.userService.getIsLoggedIn()){
		this.loggedIn = true;
      	if(this.creatingArticle){
			if(this.userService.getIsAdmin()){
				this.isAdmin = true;
				this.showEditor = true;
			}
			else{
				this.showEditor = false;
			}
	  	}
	  	else{
			this.showEditor = true;
	  	}
     }
  }

  onInputChange(val: string | Location){
    if(typeof val !== "string"){
	  this.selectedCityId = String(val.id);
      return;
    }

    let filterVal = val.toLowerCase();
    
    if(filterVal.length === 0){
      this.filteredLocations = of(this.locations);
      return;
    }
    let filtered = this.locations.filter((opt: Location) => opt.name.toLowerCase().includes(filterVal));
    if(filtered.length === 0){
      filtered = this.locations;
    }
    this.filteredLocations = of(filtered);
  }

  getLocName(selectedValue: Location){
    return selectedValue.name;
  }

  ngAfterViewInit(){
    this.config =  {
			toolbar: {
				items: [
					'undo',
					'redo',
					'|',
					'heading',
					'|',
					'bold',
					'italic',
					'underline',
					'|',
					'link',
					'insertTable',
					'highlight',
					'blockQuote',
					'codeBlock',
					'|',
					'bulletedList',
					'numberedList',
					'todoList',
					'outdent',
					'indent'
				],
				shouldNotGroupWhenFull: false
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
			balloonToolbar: ['bold', 'italic', '|', 'link', '|', 'bulletedList', 'numberedList'],
			heading: {
				options: [
					{
						model: 'paragraph',
						title: 'Paragraph',
						class: 'ck-heading_paragraph'
					},
					{
						model: 'heading1',
						view: 'h1',
						title: 'Heading 1',
						class: 'ck-heading_heading1'
					},
					{
						model: 'heading2',
						view: 'h2',
						title: 'Heading 2',
						class: 'ck-heading_heading2'
					},
					{
						model: 'heading3',
						view: 'h3',
						title: 'Heading 3',
						class: 'ck-heading_heading3'
					},
					{
						model: 'heading4',
						view: 'h4',
						title: 'Heading 4',
						class: 'ck-heading_heading4'
					},
					{
						model: 'heading5',
						view: 'h5',
						title: 'Heading 5',
						class: 'ck-heading_heading5'
					},
					{
						model: 'heading6',
						view: 'h6',
						title: 'Heading 6',
						class: 'ck-heading_heading6'
					}
				]
			},
			image: {
				toolbar: [
					'toggleImageCaption',
					'imageTextAlternative',
					'|',
					'imageStyle:inline',
					'imageStyle:wrapText',
					'imageStyle:breakText',
					'|',
					'resizeImage'
				]
			},
			link: {
				addTargetToExternalLinks: true,
				defaultProtocol: 'https://',
				decorators: {
					toggleDownloadable: {
						mode: 'manual',
						label: 'Downloadable',
						attributes: {
							download: 'file'
						}
					}
				}
			},
			list: {
				properties: {
					styles: true,
					startIndex: true,
					reversed: true
				}
			},
			menuBar: {
				isVisible: true
			},
			placeholder: `Tu napíšte obsah ${this.creatingArticle ? "článku" : "petície"}`,
			table: {
				contentToolbar: ['tableColumn', 'tableRow', 'mergeTableCells', 'tableProperties', 'tableCellProperties']
			}
		};

    this.isLayoutReady = true;
  }

  onReady(editor: any){
    editor.plugins.get('FileRepository').createUploadAdapter = (loader: any) => {
      return new MyUploadAdapter(loader);
    };

    editor.model.document.on('change:data', () => {
      const editableElement = editor.editing.view.document.getRoot().getChild(0);
      const images = editableElement.getChildren();

      images.forEach((image: any) => {
        if (image.is('element', 'image')) {
          const viewImage = editor.editing.mapper.toViewElement(image);
          viewImage.on('click', () => {
            this.selectImage(viewImage);
          });
        }
      });
    });
  }

  selectImage(viewImage: any) {
    const imgElement = viewImage.getChild(0); // Získajte <img> element
    this.selectedImage = imgElement; // Uloženie vybraného obrázka
   // this.updateMarginFields(); // Aktualizácia vstupov na hodnoty aktuálneho obrázka
  }

  canDeactivate(){
    if(this.editorData.length != 0 && !this.articleSaved){
		let text = this.creatingArticle ? "Článok nie je uložený. Ak opustíte stránku bez uloženia jeho obsah bude stratený" : "Petícia nie je uložená. Ak opustíte stránku bez uloženia jej obsah bude stratený"
      	return this.dialog.open(ConfirmComponent, {data: {text}})
                          .afterClosed()
    }
    return true;
  }

  @HostListener("window:beforeunload", ["$event"])
  confirmRefresh(event: Event){
    if(this.editorData != "" && !this.articleSaved){
      event.preventDefault();
    }
  }

  handleSaveButton(){
	if(this.title === ""){
		this.dialog.open(ErrorComponent, {data: {text: `Zadajte názov ${this.creatingArticle ? "článku" : "petície"}`}});
		return;
	}
	if(this.editorData === ""){
		this.dialog.open(ErrorComponent, {data: {text: `Obsah ${this.creatingArticle ? "článku" : "petície"} nemôže byť prázdny`}});
		return;
	}

	if(this.creatingArticle){
		this.sendingInProgress = true;
		this.articleService.saveArticle(this.title, this.editorData)?.subscribe({
			next: res => {
				this.sendingInProgress = false;
				this.dialog.open(SuccessComponent, {data: {text: "Článok bol úspešne pridaný."}})
						.afterClosed()
						.subscribe(() =>{
								this.articleSaved = true;
								this.router.navigateByUrl("/home")
						});
				
			},
			error: err => {
				this.sendingInProgress = false;
				if(err.message === "500"){
					this.dialog.open(ErrorComponent, {data: {text: "Nastala chyba pri nahrávaní článku."}})
				}
			}
		});
	}
	else{
		if(this.userCityId === -1 && this.selectedCityId.length === 0){
			this.dialog.open(ErrorComponent, {data: {text: `Vyberte mesto pre ktoré je petícia určená`}});
			return;
		}
		/* if(!this.signeeCount || this.signeeCount <= 0){
			this.dialog.open(ErrorComponent, {data: {text: `Zadajte valídny počet požadovaných podpisov`}});
			return;
		} */
		this.sendingInProgress = true;
		const cityId = this.userCityId === -1 ? this.selectedCityId : String(this.userCityId);
		this.petitionService.savePetition(this.title, this.editorData, this.signeeCount, cityId)?.subscribe({
			next: res => {
				this.sendingInProgress = false;
				this.dialog.open(SuccessComponent, {data: {text: "Petícia bola úspešne pridaná."}})
						.afterClosed()
						.subscribe(() =>{
								this.articleSaved = true;
								this.router.navigateByUrl("/home")
							})
			},
			error: err => {
				this.sendingInProgress = false;
				this.dialog.open(ErrorComponent, {data: {text: "Nastala chyba pri nahrávaní petície."}})
			}
		})
	}
  }

  changeMode(modeToChangeTo: string){
	let currRoute = this.route.snapshot.url[0].path;
	console.log(modeToChangeTo, currRoute)
	if((modeToChangeTo === "petition" && currRoute === "create_petition") || (modeToChangeTo === "article" && currRoute === "create_article")) return;
	if(modeToChangeTo === "petition"){
		this.router.navigateByUrl("/create_petition")
	}
	else{
		if(!this.userService.getIsAdmin()){
			this.dialog.open(ErrorComponent, {data: {text: "Na vytváranie člankov nemáte oprávnenie."}})
		}
		else
			this.router.navigateByUrl("/create_article")
	}
  }

}

class MyUploadAdapter{
  constructor(private loader: any) {}

  upload() {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        resolve({ default: reader.result });
      };
      reader.onerror = reject;
      this.loader.file.then((file: any) => {
        reader.readAsDataURL(file);
      });
    });
  }
}
