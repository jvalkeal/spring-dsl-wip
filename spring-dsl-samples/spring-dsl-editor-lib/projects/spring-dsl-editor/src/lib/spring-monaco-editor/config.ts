import { InjectionToken } from '@angular/core';

export const SPRING_DSL_EDITOR_CONFIG = new InjectionToken('SPRING_DSL_EDITOR_CONFIG');

export interface SpringDslEditorConfig {
  baseUrl?: string;
  defaultOptions?: { [key: string]: any; };
  onMonacoLoad?: Function;
}
