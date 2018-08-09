import { NgModule } from '@angular/core';
import { SpringDslEditorLibComponent } from './spring-dsl-editor-lib.component';
import { SpringMonacoEditorComponent } from './spring-monaco-editor/spring-monaco-editor.component';

@NgModule({
  imports: [
  ],
  declarations: [
    SpringDslEditorLibComponent,
    SpringMonacoEditorComponent
  ],
  exports: [
    SpringDslEditorLibComponent,
    SpringMonacoEditorComponent
  ]
})
export class SpringDslEditorLibModule { }
