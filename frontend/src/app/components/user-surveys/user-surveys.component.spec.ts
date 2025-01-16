import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserSurveysComponent } from './user-surveys.component';

describe('UserSurveysComponent', () => {
  let component: UserSurveysComponent;
  let fixture: ComponentFixture<UserSurveysComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserSurveysComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserSurveysComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
