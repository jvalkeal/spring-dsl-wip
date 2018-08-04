import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SpringDslEditorComponent } from './spring-dsl-editor.component';
import { SpringDslDirective } from './spring-dsl.directive';
import { SpringDslPipe } from './spring-dsl.pipe';
import { SpringDslService } from './spring-dsl.service';

export * from './spring-dsl-editor.component';
export * from './spring-dsl.directive';
export * from './spring-dsl.pipe';
export * from './spring-dsl.service';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    SpringDslEditorComponent,
    SpringDslDirective,
    SpringDslPipe
  ],
  exports: [
    SpringDslEditorComponent,
    SpringDslDirective,
    SpringDslPipe
  ]
})
export class SpringDslModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: SpringDslModule,
      providers: [SpringDslService]
    };
  }
}
