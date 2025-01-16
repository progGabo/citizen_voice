import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { LoginRegisterDialogComponent } from '../dialogs/login-register-dialog/login-register-dialog.component';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { AddEventComponent } from '../dialogs/add-event/add-event.component';


@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  @Input() isAdmin: boolean = false;
  @Input() isLoggedIn: boolean = false;
  @Input() logoClickFunction: Function = () => {};
  @Input() search: string = "";

  @Output() isAdminChange = new EventEmitter<boolean>();
  @Output() isLoggedInChange = new EventEmitter<boolean>();
  @Output() searchChange = new EventEmitter<string>();

  dialog = inject(MatDialog);

  searchbarPlaceholder: string = "Zadajte text pre vyhladavanie"

  userName: string = sessionStorage.getItem("userName") || "";

  logoUri: string = "assets/Logo.png";

  smallScreen: boolean = screen.width <= 600;

  constructor(private userService: UserService,private router: Router) {}

  ngOnInit(){
    this.isAdmin = this.userService.getIsAdmin();
    this.isLoggedIn = this.userService.getIsLoggedIn();
  }

  handleSearchChange(event: Event){
    const target: HTMLInputElement = event.target as HTMLInputElement;
    this.search = target.value;
  }


  handleSearchIconClick(){
    if(this.search.includes("search_")){
      this.search = this.search.split("search_")[1]; //if search icon is pressed multiple times prefix "search_" is added on each click, this prevents that
    }
    this.searchChange.emit(`search_${this.search}`.toLowerCase())
  }

  handleLogoutButton(){
    this.isLoggedIn = false;
    this.isAdmin = false;
    this.isAdminChange.emit(false);
    this.isLoggedInChange.emit(false);
    this.userService.logout();
  }

  handleLoginButton(){
    const dialogRef = this.dialog.open(LoginRegisterDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      this.userName = result.userName;
      this.isLoggedIn = result.successful;
      this.isLoggedInChange.emit(this.isLoggedIn);
      this.isAdmin = result.isAdmin;
      this.isAdminChange.emit(this.isAdmin);
    })
  }

  handleSettingsButton(): void {
    this.router.navigate(['/settings']);
  }
  

  toggleAdmin(){
    this.isAdmin = !this.isAdmin;
    this.isAdminChange.emit(this.isAdmin);
    this.userService.toggleIsAdmin()
  }

  addEvent(){
    const dialogRef = this.dialog.open(AddEventComponent);
  }

  addArticle(){

  }

  createSurvey(){
    this.router.navigate(['/create_survey']);
  }

  createVoting(){
    this.router.navigate(['/create_voting']);
  }
}
