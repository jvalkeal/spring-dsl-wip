import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SpringDslEditorLibComponent } from './spring-dsl-editor-lib.component';

describe('SpringDslEditorLibComponent', () => {
  let component: SpringDslEditorLibComponent;
  let fixture: ComponentFixture<SpringDslEditorLibComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SpringDslEditorLibComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SpringDslEditorLibComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
