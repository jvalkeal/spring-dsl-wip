import { SpringDslEditorModuleModule } from './spring-dsl-editor-module.module';

describe('SpringDslEditorModuleModule', () => {
  let springDslEditorModuleModule: SpringDslEditorModuleModule;

  beforeEach(() => {
    springDslEditorModuleModule = new SpringDslEditorModuleModule();
  });

  it('should create an instance', () => {
    expect(springDslEditorModuleModule).toBeTruthy();
  });
});
