import { Component, OnInit } from '@angular/core';
import { listen, MessageConnection } from 'vscode-ws-jsonrpc';
import {
  BaseLanguageClient, CloseAction, ErrorAction,
  createMonacoServices, createConnection
} from 'monaco-languageclient';
import {EditorComponent} from 'ngx-monaco-editor/editor.component';

// import { editor } from 'monaco-editor';
// import { editor, Position } from 'monaco-editor';
// import {IOverlayWidgetPosition} from 'monaco-editor';

// import {normalizeUrl} from 'normalize-url';

require('set-immediate');

const normalizeUrl = require('normalize-url');
const ReconnectingWebSocket = require('reconnecting-websocket');

@Component({
  selector: 'app-lsp4j',
  templateUrl: './lsp4j.component.html',
  styleUrls: ['./lsp4j.component.css']
})
export class Lsp4jComponent implements OnInit {

  // editorOptions = {theme: 'vs-dark', language: 'text/plain'};
  // editorOptions = {model: null, theme: 'vs-dark'};
  editorOptions = {theme: 'vs', language: 'javascript'};
  code = 'int=1\nlong=100\ndouble=1.0\nstring=hello\n';
  private xxx: any;
  // private xxx: monaco.editor.IStandaloneCodeEditor;

  constructor() {
  }

  onInit(xxx: any) {
    this.xxx = xxx;
    // const pos: Position = xxx.getPosition();
    // console.log(xxx.getLanguages());

    const url = this.createUrl('/ws');
    const webSocket = this.createWebSocket(url);

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

  ngOnInit() {
    // const url = this.createUrl('/ws');
    // const webSocket = this.createWebSocket(url);
    //
    // listen({
    //   webSocket,
    //   onConnection: connection => {
    //     // create and start the language client
    //     const languageClient = this.createLanguageClient(connection);
    //     const disposable = languageClient.start();
    //     connection.onClose(() => disposable.dispose());
    //   }
    // });

  }

  private createLanguageClient(connection: MessageConnection): BaseLanguageClient {
    const services = createMonacoServices(this.xxx);
    return new BaseLanguageClient({
      name: 'Sample Language Client',
      clientOptions: {
        // use a language id as a document selector
        documentSelector: ['javascript'],
        synchronize: {
        },
        // disable the default error handler
        errorHandler: {
          error: () => ErrorAction.Continue,
          closed: () => CloseAction.DoNotRestart
        }
      },
      services,
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

  private createWebSocket(url: string): WebSocket {
    const socketOptions = {
      maxReconnectionDelay: 10000,
      minReconnectionDelay: 1000,
      reconnectionDelayGrowFactor: 1.3,
      connectionTimeout: 10000,
      maxRetries: Infinity,
      debug: false
    };
    return new ReconnectingWebSocket(url, undefined, socketOptions);
  }

}
