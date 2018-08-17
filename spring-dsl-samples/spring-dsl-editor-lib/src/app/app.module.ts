/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTabsModule, MatFormFieldModule, MatCheckboxModule, MatInputModule, MatIconModule } from '@angular/material';
import { AppComponent } from './app.component';
import { SpringDslEditorModule, SpringDslEditorConfig } from 'spring-dsl-editor';
import { EditorTabGroupComponent } from './editor-tab-group/editor-tab-group.component';

const springDslEditorConfig: SpringDslEditorConfig = {
  onMonacoLoad: () => {
    (<any>window).monaco.languages.register({ id: 'simple' });
  }
};

@NgModule({
  declarations: [
    AppComponent,
    EditorTabGroupComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    MatTabsModule,
    MatFormFieldModule,
    MatCheckboxModule,
    MatInputModule,
    MatIconModule,
    NoopAnimationsModule,
    SpringDslEditorModule.forRoot(springDslEditorConfig)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
