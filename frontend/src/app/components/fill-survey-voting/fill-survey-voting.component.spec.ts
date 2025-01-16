import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FillSurveyVotingComponent } from './fill-survey-voting.component';

describe('FillSurveyVotingComponent', () => {
  let component: FillSurveyVotingComponent;
  let fixture: ComponentFixture<FillSurveyVotingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FillSurveyVotingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FillSurveyVotingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
