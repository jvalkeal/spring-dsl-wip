'use strict';

import * as VSCode from 'vscode';
import * as commons from '@pivotal-tools/commons-vscode';

const SIMPLE_LANGUAGE_ID = "simple";

/** Called when extension is activated */
export function activate(context: VSCode.ExtensionContext) {
    let options : commons.ActivatorOptions = {
        DEBUG : false,
        CONNECT_TO_LS: false,
        extensionId: 'vscode-simple',
        jvmHeap: "48m",
        clientOptions: {
            documentSelector: [ SIMPLE_LANGUAGE_ID ]
        }
    };
    commons.activate(options, context);
}
