import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { SpringDslEditorLibModule } from 'spring-dsl-editor-lib';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    SpringDslEditorLibModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
