import { Component, OnInit, forwardRef, Inject, Input, NgZone } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';


import { BaseEditor } from './base-editor';
import { SPRING_DSL_EDITOR_CONFIG, SpringDslEditorConfig } from './config';
import { NgxEditorModel } from './types';
import { fromEvent } from 'rxjs';


@Component({
  selector: 'spring-spring-monaco-editor',
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
      options.model = monaco.editor.createModel(options.model.value, options.model.language, options.model.uri);
    }

    this._editor = monaco.editor.create(this._editorContainer.nativeElement, options);

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
    this.onInit.emit(this._editor);
  }

}