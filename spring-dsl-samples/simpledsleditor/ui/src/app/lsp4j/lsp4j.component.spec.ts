import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { Lsp4jComponent } from './lsp4j.component';

describe('Lsp4jComponent', () => {
  let component: Lsp4jComponent;
  let fixture: ComponentFixture<Lsp4jComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ Lsp4jComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(Lsp4jComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
