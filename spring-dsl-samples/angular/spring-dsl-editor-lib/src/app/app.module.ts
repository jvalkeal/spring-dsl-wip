import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { SpringDslEditorModule } from 'spring-dsl-editor';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    SpringDslEditorModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
