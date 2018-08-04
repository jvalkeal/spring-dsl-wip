import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By }              from '@angular/platform-browser';
import { DebugElement }    from '@angular/core';

import { SpringDslEditorComponent } from './spring-dsl-editor.component';

describe('SpringDslEditorComponent', () => {

  let comp:    SpringDslEditorComponent;
  let fixture: ComponentFixture<SpringDslEditorComponent>;
  let de:      DebugElement;
  let el:      HTMLElement;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ SpringDslEditorComponent ], // declare the test component
    });

    fixture = TestBed.createComponent(SpringDslEditorComponent);

    comp = fixture.componentInstance; // BannerComponent test instance

    // query for the title <h1> by CSS element selector
    de = fixture.debugElement.query(By.css('h1'));
    el = de.nativeElement;
  });

  it('Should be false', () => {
    expect(false).toBe(true);
    expect(comp).toBeTruthy();
    expect(de).toBeTruthy();
    expect(el).toBeTruthy();
  });
});
