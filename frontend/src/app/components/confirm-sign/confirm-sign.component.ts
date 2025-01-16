import { Component } from '@angular/core';
import { PetitionsService } from '../../services/petitions.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-confirm-sign',
  templateUrl: './confirm-sign.component.html',
  styleUrl: './confirm-sign.component.scss'
})
export class ConfirmSignComponent {
  inProgress: boolean = true;
  success: boolean = false;
  serverError: boolean = false;
  alreadySigned: boolean = false;

  constructor(private petitionService: PetitionsService, private route: ActivatedRoute){}

  ngOnInit(){
    const tokenSnapshot = this.route.snapshot.paramMap.get("token");
    if(!tokenSnapshot){
      this.inProgress = false;
      this.success = false;
      this.serverError = false;
      return;
    }

    this.petitionService.confirmSignature(tokenSnapshot).subscribe({
      next: () => {
        this.inProgress = false;
        this.success = true;
      },
      error: (e) => {
        if(e.message === "409"){
          this.alreadySigned = true;
        }
        this.inProgress = false;
        this.success = false;
        this.serverError = true;
      }
    })
  }
}