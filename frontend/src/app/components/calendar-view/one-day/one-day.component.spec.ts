import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OneDayComponent } from './one-day.component';

describe('OneDayComponent', () => {
  let component: OneDayComponent;
  let fixture: ComponentFixture<OneDayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OneDayComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OneDayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
