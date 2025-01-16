import { Component } from '@angular/core';
import { UserService } from '../../services/user.service';
import { ActivatedRoute, Route } from '@angular/router';

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrl: './activate-account.component.scss'
})
export class ActivateAccountComponent {
  inProgress: boolean = true;
  success: boolean | undefined;
  serverError: boolean = false;

  newCodeSent: boolean = false;
  newCodeSentSuccess: boolean = false;
  isOrganization: boolean = false;

  constructor(private userService: UserService, private route: ActivatedRoute){}

  ngOnInit(){
    const tokenSnapshot = this.route.snapshot.paramMap.get("token");
    if(!tokenSnapshot){
      this.inProgress = false;
      this.success = false;
      this.serverError = false;
      return;
    }
    
    this.userService.activateAccount(tokenSnapshot).subscribe({
      next: (data) => {
        this.isOrganization = data.body.isOrganization
        this.success = true;
        this.inProgress = false;
        this.serverError = false;
      },
      error: (err) => {
        this.success = false;
        this.inProgress = false;
        this.serverError = true;
      }
    })
    
  }
}
