import { ModuleWithProviders, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SpringDslEditorComponent } from './spring-dsl-editor.component';
import { SpringMonacoEditorComponent } from './spring-monaco-editor/spring-monaco-editor.component';
import { SPRING_DSL_EDITOR_CONFIG, SpringDslEditorConfig } from './spring-monaco-editor/config';

@NgModule({
  imports: [
    FormsModule
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
export class SpringDslEditorModule {
  public static forRoot(config: SpringDslEditorConfig = {}): ModuleWithProviders {
    return {
      ngModule: SpringDslEditorModule,
      providers: [
        { provide: SPRING_DSL_EDITOR_CONFIG, useValue: config }
      ]
    };
  }
}
