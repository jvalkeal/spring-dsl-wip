import { ModuleWithProviders, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SpringDslEditorComponent } from '../spring-dsl-editor/spring-dsl-editor.component';
import { NGX_MONACO_EDITOR_CONFIG, NgxMonacoEditorConfig } from '../spring-monaco-editor/config';
import { SpringMonacoEditorComponent } from '../spring-monaco-editor/spring-monaco-editor.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
  ],
  declarations: [
    SpringDslEditorComponent,
    SpringMonacoEditorComponent
  ],
  exports: [
    SpringDslEditorComponent,
    SpringMonacoEditorComponent
  ]
})
export class SpringDslEditorModuleModule {
  public static forRoot(config: NgxMonacoEditorConfig = {}): ModuleWithProviders {
    return {
      ngModule: SpringDslEditorModuleModule,
      providers: [
        { provide: NGX_MONACO_EDITOR_CONFIG, useValue: config }
      ]
    };
  }
}
