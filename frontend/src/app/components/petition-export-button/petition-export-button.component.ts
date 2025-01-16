import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ExportDialogComponent } from '../dialogs/export-dialog/export-dialog.component';
import { ExportService } from '../../services/export.service';
import { ErrorComponent } from '../dialogs/error/error.component';
import { PetitionsService } from '../../services/petitions.service';

@Component({
  selector: 'app-petition-export-button',
  templateUrl: './petition-export-button.component.html',
  styleUrl: './petition-export-button.component.scss'
})
export class PetitionExportButtonComponent {
  @Input() petitionId: number = -1;

  exportInProgress: boolean = false;

  constructor(private dialog: MatDialog, private exportService: ExportService, private petitionService: PetitionsService){}

  handleExportClick(){
    this.dialog.open(ExportDialogComponent, {data: {showCheckbox: false, title: "Nastavenie exportu podpisov"}}).afterClosed().subscribe(res => {
      this.petitionService.getAllSignees(this.petitionId)?.subscribe({
        next: signees => {
          this.petitionService.getPetition(this.petitionId).subscribe({
            next: petition => {
              if(res.format === "csv"){
                this.exportService.exportSignarutesToCsv(signees, petition.author, petition.title);
              }
              else{
                this.exportService.exportSignaturesToPdf(signees, petition.author, petition.title);
              }    
            },
            error: () => this.dialog.open(ErrorComponent, {data: {text: "Nastala neočakávaná chyba pri získavaní podpisov"}})
          })
        },
        error: () => this.dialog.open(ErrorComponent, {data: {text: "Nastala neočakávaná chyba pri získavaní podpisov"}})
      })
    })
  }
}
