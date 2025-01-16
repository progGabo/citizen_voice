import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventsViewerComponent } from './events-viewer.component';

describe('EventsViewerComponent', () => {
  let component: EventsViewerComponent;
  let fixture: ComponentFixture<EventsViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventsViewerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventsViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
