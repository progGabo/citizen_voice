import { Component, Input } from '@angular/core';
import { Carousel } from '../../models/CarouselDataSets';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { UserService } from '../../services/user.service';
import { ErrorComponent } from '../dialogs/error/error.component';

@Component({
  selector: 'app-carousel',
  templateUrl: './testCarousel.component.html',
  styleUrls: ['./testCarousel.scss'],  
})
export class TestCarouselComponent {
    @Input() automaticSliding: boolean = false;
    @Input() automaticSlideIntervalMs: number = 5000;
    @Input() slides: Carousel[] = [];
    @Input() linkPrefix: string = "";
    @Input() buttonText: string = "Prejsť na článok"
    @Input() loggedInMandatory: boolean = false;
    @Input() errorText: string = "";
    
    currentSlideIndex = 0;
    autoslideInterval: any;

    constructor(private _router: Router, private dialog: MatDialog, private userService: UserService){}

    ngOnInit(){
      this.startInterval();
    }

    startInterval(){
      if(this.automaticSliding){
        this.autoslideInterval = setInterval(() => this.goToNextSlide(), this.automaticSlideIntervalMs);
      }
    }

    stopInterval(){
      if(this.autoslideInterval){
        clearInterval(this.autoslideInterval);
        this.autoslideInterval = null;
      }
    }

    ngOnDestroy(){
      this.stopInterval();
    }

    isAnimated = false;
    goToPrevSlide() {
      this.currentSlideIndex = (this.currentSlideIndex - 1 + this.slides.length) % this.slides.length;
    }

    goToNextSlide() {
      if(this.slides.length > 1){
        this.isAnimated = true;
        setTimeout(() => this.isAnimated = false, 500)
        this.currentSlideIndex = (this.currentSlideIndex + 1) % this.slides.length;
      }
    }

    getRouterLink() {
      const path = `/${this.linkPrefix}/${this.slides[this.currentSlideIndex].id}`;
      return path;
    }

    openLinkNewTab(){
      if(this.loggedInMandatory && !this.userService.getIsLoggedIn()){
        this.dialog.open(ErrorComponent, {data: {text: this.errorText}});
        return;
      }
      const url = this._router.serializeUrl(this._router.createUrlTree([this.getRouterLink()]));
      window.open(url, "_blank")
    }

    getText(text: string){
      if(text.length > 200){
        return text.slice(0,200).concat("...");
      }
      return text;
    }
}