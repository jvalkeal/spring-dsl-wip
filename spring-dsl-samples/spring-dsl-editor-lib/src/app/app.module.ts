import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { SpringDslEditorModule, SpringDslEditorConfig } from 'spring-dsl-editor';

const monacoConfig: SpringDslEditorConfig = {
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
    SpringDslEditorModule.forRoot(monacoConfig)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
