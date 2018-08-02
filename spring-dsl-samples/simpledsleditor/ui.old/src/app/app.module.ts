import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { Lsp4jComponent } from './lsp4j/lsp4j.component';
import { MonacoEditorModule, NgxMonacoEditorConfig } from 'ngx-monaco-editor';

const monacoConfig: NgxMonacoEditorConfig = {
  // baseUrl: 'app-name/assets',
  // defaultOptions: { scrollBeyondLastLine: false },
  onMonacoLoad: () => {
    console.log((<any>window).monaco);
    (<any>window).monaco.languages.register({ id: 'simple' });
  }
};

@NgModule({
  declarations: [
    AppComponent,
    Lsp4jComponent
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
