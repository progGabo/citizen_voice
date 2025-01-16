import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { PetitionsService } from '../../../services/petitions.service';
import { SuccessComponent } from '../success/success.component';
import { ErrorComponent } from '../error/error.component';

@Component({
  selector: 'app-sign-petition',
  templateUrl: './sign-petition.component.html',
  styleUrl: './sign-petition.component.scss'
})
export class SignPetitionComponent {

  constructor(@Inject(MAT_DIALOG_DATA) public data: {firstName: string, lastName: string, city: string, mail: string, petitionId: number}, 
              private dialogRef: MatDialogRef<SignPetitionComponent>,
              private petitionService: PetitionsService,
              private dialog: MatDialog)
  {
    if(screen.width > 600)
      this.dialogRef.updateSize("40%", "50%");
    this.dialogRef.disableClose = true;
  }

  signPetition(){
    this.petitionService.signPetition(this.data.petitionId)?.subscribe({
      next: val => {
        let succ = this.dialog.open(SuccessComponent, {data: {text: "Petícia bola úspešne podpísaná", confirmButtonText: "Rozumiem"}})
        succ.afterClosed().subscribe(() => this.closeDialog())
      },
      error: err => {
        this.dialog.open(ErrorComponent, {data: {text: "Nastala chyba pri podpisovaní petície"}})
      }
    })
  }

  closeDialog(){
    this.dialogRef.close();
  }

}
