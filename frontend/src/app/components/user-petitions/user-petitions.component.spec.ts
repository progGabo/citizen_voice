import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserPetitionsComponent } from './user-petitions.component';

describe('UserPetitionsComponent', () => {
  let component: UserPetitionsComponent;
  let fixture: ComponentFixture<UserPetitionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserPetitionsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserPetitionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
