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
import { Component, OnInit, forwardRef, Inject, Input, NgZone } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { fromEvent } from 'rxjs';
import { BaseEditor } from './base-editor';
import { SPRING_DSL_EDITOR_CONFIG, SpringDslEditorConfig } from './config';
import { NgxEditorModel } from './types';

/**
 * Component handling low level integration with a monaco editor.
 *
 * @author Janne Valkealahti
 */
@Component({
  selector: 'spring-monaco-editor',
  templateUrl: './spring-monaco-editor.component.html',
  styleUrls: ['./spring-monaco-editor.component.css'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => SpringMonacoEditorComponent),
    multi: true
  }]
})
export class SpringMonacoEditorComponent extends BaseEditor implements ControlValueAccessor {
  private _value: string = '';

  propagateChange = (_: any) => {};
  onTouched = () => {};

  @Input('model')
  set model(model: NgxEditorModel) {
    this.options.model = model;
    if (this._editor) {
      this._editor.dispose();
      this.initMonaco(this.options);
    }
  }

  constructor(private zone: NgZone, @Inject(SPRING_DSL_EDITOR_CONFIG) private editorConfig: SpringDslEditorConfig) {
    super(editorConfig);
  }

  writeValue(value: any): void {
    this._value = value || '';
    // Fix for value change while dispose in process.
    setTimeout(() => {
      if (this._editor && !this.options.model) {
        this._editor.setValue(this._value);
      }
    });
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  protected initMonaco(options: any): void {

    const hasModel = !!options.model;

    if (hasModel) {
      console.log('model', options.model);
      options.model = monaco.editor.createModel(options.model.value, options.model.language, options.model.uri);
    }

    this._editor = monaco.editor.create(this.editorContainer.nativeElement, options);

    if (!hasModel) {
      this._editor.setValue(this._value);
    }

    this._editor.onDidChangeModelContent((e: any) => {
      const value = this._editor.getValue();
      this.propagateChange(value);
      // value is not propagated to parent when executing outside zone.
      this.zone.run(() => this._value = value);
    });

    this._editor.onDidBlurEditor((e: any) => {
      this.onTouched();
    });

    // refresh layout on resize event.
    if (this._windowResizeSubscription) {
      this._windowResizeSubscription.unsubscribe();
    }
    this._windowResizeSubscription = fromEvent(window, 'resize').subscribe(() => this._editor.layout());
    this.editorChange.emit(this._editor);
  }

}
