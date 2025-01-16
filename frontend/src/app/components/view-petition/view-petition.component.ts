import { Component, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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
import { Petition } from '../../models/Petitions';
import { PetitionsService } from '../../services/petitions.service';
import { UserService } from '../../services/user.service';
import { MatDialog } from '@angular/material/dialog';
import { ErrorComponent } from '../dialogs/error/error.component';
import { SignPetitionComponent } from '../dialogs/sign-petition/sign-petition.component';

@Component({
  selector: 'app-view-petition',
  templateUrl: './view-petition.component.html',
  styleUrl: './view-petition.component.scss',
  encapsulation: ViewEncapsulation.None
})

export class ViewPetitionComponent {
  
  id: string = "";
  Editor = ClassicEditor;
  config: EditorConfig = {}
  petitionToRender: Petition = new Petition;
  isLayoutReady: boolean = false;
  loadingInProgress: boolean = false;
  alreadySigned: boolean = false;
  signatureValid: boolean = false;

  constructor(private route: ActivatedRoute, 
              private petitionService: PetitionsService, 
              private router: Router, 
              private userService: UserService,
              private dialog: MatDialog
            ){}

  ngOnInit(){
    const idSnapshot = this.route.snapshot.paramMap.get("id");
    if(idSnapshot){
      this.id = idSnapshot; 

      setTimeout(() => {
        if(!this.isLayoutReady)
          this.loadingInProgress = true;
      }, 500)
      
      this.petitionService.getPetition(Number(this.id)).subscribe({
        next: petition => {
          this.loadingInProgress = false;
          this.petitionToRender = petition;
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
            initialData: this.petitionToRender.text,
            menuBar: {
              isVisible: false
            },
          };
          this.isLayoutReady = true;
        },
        error: err => {
          this.loadingInProgress = false;
          this.router.navigateByUrl("/not_found")
        }
      })
    }

    this.petitionService.getSignedPetition(Number(this.id))?.subscribe({
      next: signed => {        
        let obj = signed.body;
        this.alreadySigned = obj.signed;
        this.signatureValid = obj.verified;
      },
      error: err => {
        console.error("CHYBA")
      }
    })
  }

  ngAfterViewInit(){
   
  }

  previewReady(editor: any){
    editor.enableReadOnlyMode("1");
  }

  signPetition(){
    if(!this.userService.getIsLoggedIn()){
      this.dialog.open(ErrorComponent, {data: {text: "Pre podpísanie petície sa musíte prihlásiť"}});
      return;
    }
    let userData = this.userService.getUserData();
    this.dialog.open(SignPetitionComponent, {data: {firstName: userData.firstName, lastName: userData.lastName, city: userData.city, mail: userData.email, petitionId: Number(this.id)}})
  }
}
