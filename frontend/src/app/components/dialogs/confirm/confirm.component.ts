import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-confirm',
  templateUrl: './confirm.component.html',
  styleUrl: './confirm.component.scss'
})
export class ConfirmComponent {

  constructor(private dialogRef: MatDialogRef<ConfirmComponent>, @Inject(MAT_DIALOG_DATA) public data: { text: string }){
    this.dialogRef.disableClose = true;
  }

  onCancel(){
    this.dialogRef.close(false);
  }

  onConfirm(){
    this.dialogRef.close(true);
  }
}
