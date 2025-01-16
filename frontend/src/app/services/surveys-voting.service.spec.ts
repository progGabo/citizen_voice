import { TestBed } from '@angular/core/testing';

import { SurveysVotingService } from './surveys-voting.service';

describe('SurveysVotingService', () => {
  let service: SurveysVotingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SurveysVotingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
