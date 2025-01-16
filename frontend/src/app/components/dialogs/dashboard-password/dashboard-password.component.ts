import { ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { UserService } from '../../../services/user.service';
import { ErrorComponent } from '../error/error.component';

@Component({
  selector: 'app-dashboard-password',
  templateUrl: './dashboard-password.component.html',
  styleUrl: './dashboard-password.component.scss'
})
export class DashboardPasswordComponent {

  password: string = "";
  email: string = "";

  constructor(private dialogRef: MatDialogRef<DashboardPasswordComponent>, private crd: ChangeDetectorRef, private userService: UserService, private dialog: MatDialog){
    dialogRef.disableClose = true;
  }

  leave(){
    this.dialogRef.close({leave: true})
  }

  checkPassword(){
    this.userService.loginAsAdmin({email: this.email, password: this.password, rememberMe: true}).subscribe({
      next: (data) => {
        this.dialogRef.close({leave: false});
      },
      error: (err) => {
        this.dialog.open(ErrorComponent, {data: {text: "Zadal si zly email alebo heslo!"}});
      }
    })
  }
}
