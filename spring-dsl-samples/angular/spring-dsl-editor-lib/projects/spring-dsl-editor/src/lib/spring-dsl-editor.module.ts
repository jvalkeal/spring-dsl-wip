import { ModuleWithProviders, NgModule } from '@angular/core';
import { SpringDslEditorComponent } from './spring-dsl-editor.component';
import { SpringMonacoEditorComponent } from './spring-monaco-editor/spring-monaco-editor.component';
import { NGX_MONACO_EDITOR_CONFIG, NgxMonacoEditorConfig } from './spring-monaco-editor/config';

@NgModule({
  imports: [
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
  public static forRoot(config: NgxMonacoEditorConfig = {}): ModuleWithProviders {
    return {
      ngModule: SpringDslEditorModule,
      providers: [
        { provide: NGX_MONACO_EDITOR_CONFIG, useValue: config }
      ]
    };
  }
}
