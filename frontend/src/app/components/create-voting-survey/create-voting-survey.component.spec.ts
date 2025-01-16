import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateVotingSurveyComponent } from './create-voting-survey.component';

describe('CreateVotingSurveyComponent', () => {
  let component: CreateVotingSurveyComponent;
  let fixture: ComponentFixture<CreateVotingSurveyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateVotingSurveyComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateVotingSurveyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
