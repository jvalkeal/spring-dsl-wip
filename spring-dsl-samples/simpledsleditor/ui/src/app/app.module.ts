import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { SpringDslEditorModule, SpringMonacoEditorConfig } from 'spring-dsl-editor';

const springMonacoEditorConfig: SpringMonacoEditorConfig = {
  onMonacoLoad: () => {
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
    SpringDslEditorModule.forRoot(springMonacoEditorConfig)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
