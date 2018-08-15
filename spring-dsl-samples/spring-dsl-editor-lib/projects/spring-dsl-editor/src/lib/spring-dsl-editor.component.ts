import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { listen, MessageConnection } from 'vscode-ws-jsonrpc';
import {
  BaseLanguageClient, CloseAction, ErrorAction,
  createConnection, MonacoServices, MonacoLanguageClient
} from 'monaco-languageclient';

const normalizeUrl = require('normalize-url');

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
    console.info('ngOnInit');
  }

  public initLanguageClient(editor: any) {
    console.info('initLanguageClient', editor);
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
