import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ErrorComponent } from '../error/error.component';

@Component({
  selector: 'app-export-dialog',
  templateUrl: './export-dialog.component.html',
  styleUrl: './export-dialog.component.scss'
})
export class ExportDialogComponent {
  result: any = {
    format: "",
    all: false
  };

  constructor(private dialogRef: MatDialogRef<ExportDialogComponent>, private dialog: MatDialog, @Inject(MAT_DIALOG_DATA) public data: { showCheckbox: boolean, title: string }){
    dialogRef.disableClose = true;
  }

  close(){
    if(this.result.format.length === 0){
      this.dialog.open(ErrorComponent, {data: {text: "Zvoľte formát"}});
      return;
    }
    this.dialogRef.close(this.result)
  }
}