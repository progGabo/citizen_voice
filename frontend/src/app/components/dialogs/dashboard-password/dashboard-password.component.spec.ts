import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardPasswordComponent } from './dashboard-password.component';

describe('DashboardPasswordComponent', () => {
  let component: DashboardPasswordComponent;
  let fixture: ComponentFixture<DashboardPasswordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardPasswordComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashboardPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
