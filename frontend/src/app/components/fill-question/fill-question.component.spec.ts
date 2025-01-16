import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FillQuestionComponent } from './fill-question.component';

describe('FillQuestionComponent', () => {
  let component: FillQuestionComponent;
  let fixture: ComponentFixture<FillQuestionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FillQuestionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FillQuestionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
