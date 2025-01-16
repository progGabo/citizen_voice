import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmSignComponent } from './confirm-sign.component';

describe('ConfirmSignComponent', () => {
  let component: ConfirmSignComponent;
  let fixture: ComponentFixture<ConfirmSignComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmSignComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfirmSignComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
