import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { NgxMonacoEditorConfig } from './spring-monaco-editor/config';
import { SpringDslEditorModuleModule } from './spring-dsl-editor-module/spring-dsl-editor-module.module';

const monacoConfig: NgxMonacoEditorConfig = {
  onMonacoLoad: () => {
    console.log((<any>window).monaco);
    (<any>window).monaco.languages.register({ id: 'simple' });
  }
};

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    SpringDslEditorModuleModule.forRoot(monacoConfig)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {}
