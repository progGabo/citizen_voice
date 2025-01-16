import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PetitionExportButtonComponent } from './petition-export-button.component';

describe('PetitionExportButtonComponent', () => {
  let component: PetitionExportButtonComponent;
  let fixture: ComponentFixture<PetitionExportButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PetitionExportButtonComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PetitionExportButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
