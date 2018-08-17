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
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { listen, MessageConnection } from 'vscode-ws-jsonrpc';
import {
  BaseLanguageClient, CloseAction, ErrorAction,
  createConnection, MonacoServices, MonacoLanguageClient
} from 'monaco-languageclient';

const normalizeUrl = require('normalize-url');

/**
 * Component handling high level integration with a monaco editor.
 *
 * @author Janne Valkealahti
 */
@Component({
  selector: 'spring-dsl-editor',
  templateUrl: './spring-dsl-editor.component.html',
  styleUrls: ['./spring-dsl-editor.component.css']
})
export class SpringDslEditorComponent implements OnInit {

  editorOptions = {theme: 'vs', language: 'simple'};
  code = '';
  private editor: any;

  constructor() {
  }

  ngOnInit() {
  }

  public initLanguageClient(editor: any) {
    this.editor = editor;
    MonacoServices.install(editor);
    const url = this.createUrl('ws');
    const webSocket = new WebSocket(url);

    listen({
      webSocket,
      onConnection: connection => {
        // create and start the language client
        const languageClient = this.createLanguageClient(connection);
        const disposable = languageClient.start();
        connection.onClose(() => disposable.dispose());
      }
    });
  }

  private createLanguageClient(connection: MessageConnection): BaseLanguageClient {
    return new MonacoLanguageClient({
      name: 'Sample Language Client',
      clientOptions: {
        // use a language id as a document selector
        documentSelector: ['simple'],
        synchronize: {
        },
        // disable the default error handler
        errorHandler: {
          error: () => ErrorAction.Continue,
          closed: () => CloseAction.DoNotRestart
        }
      },
      // create a language client connection from the JSON RPC connection on demand
      connectionProvider: {
        get: (errorHandler, closeHandler) => {
          return Promise.resolve(createConnection(connection, errorHandler, closeHandler));
        }
      }
    });
  }

  private createUrl(path: string): string {
    const protocol = location.protocol === 'https:' ? 'wss' : 'ws';
    return normalizeUrl(`${protocol}://${location.host}${location.pathname}${path}`);
  }

}
