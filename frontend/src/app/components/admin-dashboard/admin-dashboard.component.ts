import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DashboardPasswordComponent } from '../dialogs/dashboard-password/dashboard-password.component';
import { Router } from '@angular/router';
import { DashboardService } from '../../services/dashboard.service';
import { UserDetail } from '../../models/User';

class UserDetailExtended extends UserDetail{
  id: number = -1;
}

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss'
})
export class AdminDashboardComponent {
  verified: boolean = false;

  toBeVerified: UserDetailExtended[] = [];
  
  constructor(private dialog: MatDialog, private router: Router, private dashboardService: DashboardService){}

  ngOnInit(){
    this.dialog.open(DashboardPasswordComponent).afterClosed().subscribe(result => {
      if(result.leave){
        this.leave()
      }
      else{
        this.dashboardService.getPendingAccounts()?.subscribe((res: any) => {
          this.toBeVerified = res.content;
          console.log(this.toBeVerified)
        }) 
      }
    })
  }

  formatDate(dateStr: string){
    let date = new Date(dateStr);
    return `${date.getDate()}.${date.getMonth() + 1}.${date.getFullYear()}`
  }

  leave(){
    this.router.navigateByUrl("/home");
  }

  activate(userId: number, index: number){
    this.dashboardService.approveAccount(userId)?.subscribe(res => {
      this.toBeVerified.splice(index, 1);
    })
  }
}
