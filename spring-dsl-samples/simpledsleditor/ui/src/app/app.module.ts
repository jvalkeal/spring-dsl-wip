import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { SpringDslEditorComponent } from './spring-dsl-editor/spring-dsl-editor.component';
import { MonacoEditorModule, NgxMonacoEditorConfig } from 'ngx-monaco-editor';

const monacoConfig: NgxMonacoEditorConfig = {
  onMonacoLoad: () => {
    console.log((<any>window).monaco);
    (<any>window).monaco.languages.register({ id: 'simple' });
  }
};

@NgModule({
  declarations: [
    AppComponent,
    SpringDslEditorComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    MonacoEditorModule.forRoot(monacoConfig)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
