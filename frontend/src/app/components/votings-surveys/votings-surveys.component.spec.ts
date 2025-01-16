import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VotingsSurveysComponent } from './votings-surveys.component';

describe('VotingsSurveysComponent', () => {
  let component: VotingsSurveysComponent;
  let fixture: ComponentFixture<VotingsSurveysComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VotingsSurveysComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VotingsSurveysComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
