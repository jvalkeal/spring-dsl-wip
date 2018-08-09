import { TestBed, inject } from '@angular/core/testing';

import { SpringDslEditorLibService } from './spring-dsl-editor-lib.service';

describe('SpringDslEditorLibService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SpringDslEditorLibService]
    });
  });

  it('should be created', inject([SpringDslEditorLibService], (service: SpringDslEditorLibService) => {
    expect(service).toBeTruthy();
  }));
});
