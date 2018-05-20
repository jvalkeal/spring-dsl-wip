webpackJsonp(["main"],{

/***/ "./src/$$_lazy_route_resource lazy recursive":
/***/ (function(module, exports) {

function webpackEmptyAsyncContext(req) {
	// Here Promise.resolve().then() is used instead of new Promise() to prevent
	// uncatched exception popping up in devtools
	return Promise.resolve().then(function() {
		throw new Error("Cannot find module '" + req + "'.");
	});
}
webpackEmptyAsyncContext.keys = function() { return []; };
webpackEmptyAsyncContext.resolve = webpackEmptyAsyncContext;
module.exports = webpackEmptyAsyncContext;
webpackEmptyAsyncContext.id = "./src/$$_lazy_route_resource lazy recursive";

/***/ }),

/***/ "./src/app/app.component.css":
/***/ (function(module, exports) {

module.exports = ""

/***/ }),

/***/ "./src/app/app.component.html":
/***/ (function(module, exports) {

module.exports = "<!--The content below is only a placeholder and can be replaced.-->\n<div style=\"text-align:center\">\n  <h1>\n    Welcome to {{ title }}!\n  </h1>\n</div>\n<app-lsp4j></app-lsp4j>\n"

/***/ }),

/***/ "./src/app/app.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return AppComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

var AppComponent = /** @class */ (function () {
    function AppComponent() {
        this.title = 'Spring DLS LSP Demo';
    }
    AppComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["m" /* Component */])({
            selector: 'app-root',
            template: __webpack_require__("./src/app/app.component.html"),
            styles: [__webpack_require__("./src/app/app.component.css")]
        })
    ], AppComponent);
    return AppComponent;
}());



/***/ }),

/***/ "./src/app/app.module.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return AppModule; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_platform_browser__ = __webpack_require__("./node_modules/@angular/platform-browser/esm5/platform-browser.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__angular_forms__ = __webpack_require__("./node_modules/@angular/forms/esm5/forms.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__app_component__ = __webpack_require__("./src/app/app.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__lsp4j_lsp4j_component__ = __webpack_require__("./src/app/lsp4j/lsp4j.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_ngx_monaco_editor__ = __webpack_require__("./node_modules/ngx-monaco-editor/index.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};






var monacoConfig = {
    // baseUrl: 'app-name/assets',
    // defaultOptions: { scrollBeyondLastLine: false },
    onMonacoLoad: function () {
        console.log(window.monaco);
        window.monaco.languages.register({ id: 'simple' });
    }
};
var AppModule = /** @class */ (function () {
    function AppModule() {
    }
    AppModule = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_1__angular_core__["E" /* NgModule */])({
            declarations: [
                __WEBPACK_IMPORTED_MODULE_3__app_component__["a" /* AppComponent */],
                __WEBPACK_IMPORTED_MODULE_4__lsp4j_lsp4j_component__["a" /* Lsp4jComponent */]
            ],
            imports: [
                __WEBPACK_IMPORTED_MODULE_0__angular_platform_browser__["a" /* BrowserModule */],
                __WEBPACK_IMPORTED_MODULE_2__angular_forms__["a" /* FormsModule */],
                __WEBPACK_IMPORTED_MODULE_5_ngx_monaco_editor__["a" /* MonacoEditorModule */].forRoot(monacoConfig)
            ],
            providers: [],
            bootstrap: [__WEBPACK_IMPORTED_MODULE_3__app_component__["a" /* AppComponent */]]
        })
    ], AppModule);
    return AppModule;
}());



/***/ }),

/***/ "./src/app/lsp4j/lsp4j.component.css":
/***/ (function(module, exports) {

module.exports = ""

/***/ }),

/***/ "./src/app/lsp4j/lsp4j.component.html":
/***/ (function(module, exports) {

module.exports = "<div>\n  Use below monaco editor to play with simple dummy language.\n</div>\n<div>\n  <ngx-monaco-editor [options]=\"editorOptions\" [(ngModel)]=\"code\" (onInit)=\"onInit($event)\"></ngx-monaco-editor>\n</div>\n"

/***/ }),

/***/ "./src/app/lsp4j/lsp4j.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return Lsp4jComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_vscode_ws_jsonrpc__ = __webpack_require__("./node_modules/vscode-ws-jsonrpc/lib/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_vscode_ws_jsonrpc___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_vscode_ws_jsonrpc__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_monaco_languageclient__ = __webpack_require__("./node_modules/monaco-languageclient/lib/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_monaco_languageclient___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_monaco_languageclient__);
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};



// import { editor } from 'monaco-editor';
// import { editor, Position } from 'monaco-editor';
// import {IOverlayWidgetPosition} from 'monaco-editor';
// import {normalizeUrl} from 'normalize-url';
__webpack_require__("./node_modules/set-immediate/setImmediate.js");
var normalizeUrl = __webpack_require__("./node_modules/normalize-url/index.js");
var ReconnectingWebSocket = __webpack_require__("./node_modules/reconnecting-websocket/dist/index.js");
var Lsp4jComponent = /** @class */ (function () {
    // private xxx: monaco.editor.IStandaloneCodeEditor;
    function Lsp4jComponent() {
        // editorOptions = {theme: 'vs-dark', language: 'text/plain'};
        // editorOptions = {model: null, theme: 'vs-dark'};
        // editorOptions = {theme: 'vs', language: 'javascript'};
        this.editorOptions = { theme: 'vs', language: 'simple' };
        // code = 'int=1\nlong=100\ndouble=1.0\nstring=hello\n';
        this.code = '';
    }
    Lsp4jComponent.prototype.onInit = function (xxx) {
        var _this = this;
        this.xxx = xxx;
        // const pos: Position = xxx.getPosition();
        // console.log(xxx.getLanguages());
        var url = this.createUrl('/ws');
        var webSocket = this.createWebSocket(url);
        Object(__WEBPACK_IMPORTED_MODULE_1_vscode_ws_jsonrpc__["listen"])({
            webSocket: webSocket,
            onConnection: function (connection) {
                // create and start the language client
                var languageClient = _this.createLanguageClient(connection);
                var disposable = languageClient.start();
                connection.onClose(function () { return disposable.dispose(); });
            }
        });
    };
    Lsp4jComponent.prototype.ngOnInit = function () {
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
    };
    Lsp4jComponent.prototype.createLanguageClient = function (connection) {
        var services = Object(__WEBPACK_IMPORTED_MODULE_2_monaco_languageclient__["createMonacoServices"])(this.xxx);
        return new __WEBPACK_IMPORTED_MODULE_2_monaco_languageclient__["BaseLanguageClient"]({
            name: 'Sample Language Client',
            clientOptions: {
                // use a language id as a document selector
                documentSelector: ['simple'],
                synchronize: {},
                // disable the default error handler
                errorHandler: {
                    error: function () { return __WEBPACK_IMPORTED_MODULE_2_monaco_languageclient__["ErrorAction"].Continue; },
                    closed: function () { return __WEBPACK_IMPORTED_MODULE_2_monaco_languageclient__["CloseAction"].DoNotRestart; }
                }
            },
            services: services,
            // create a language client connection from the JSON RPC connection on demand
            connectionProvider: {
                get: function (errorHandler, closeHandler) {
                    return Promise.resolve(Object(__WEBPACK_IMPORTED_MODULE_2_monaco_languageclient__["createConnection"])(connection, errorHandler, closeHandler));
                }
            }
        });
    };
    Lsp4jComponent.prototype.createUrl = function (path) {
        var protocol = location.protocol === 'https:' ? 'wss' : 'ws';
        return normalizeUrl(protocol + "://" + location.host + location.pathname + path);
    };
    Lsp4jComponent.prototype.createWebSocket = function (url) {
        var socketOptions = {
            maxReconnectionDelay: 10000,
            minReconnectionDelay: 1000,
            reconnectionDelayGrowFactor: 1.3,
            connectionTimeout: 10000,
            maxRetries: Infinity,
            debug: false
        };
        return new ReconnectingWebSocket(url, undefined, socketOptions);
    };
    Lsp4jComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["m" /* Component */])({
            selector: 'app-lsp4j',
            template: __webpack_require__("./src/app/lsp4j/lsp4j.component.html"),
            styles: [__webpack_require__("./src/app/lsp4j/lsp4j.component.css")]
        }),
        __metadata("design:paramtypes", [])
    ], Lsp4jComponent);
    return Lsp4jComponent;
}());



/***/ }),

/***/ "./src/environments/environment.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return environment; });
// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.
var environment = {
    production: false
};


/***/ }),

/***/ "./src/main.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
Object.defineProperty(__webpack_exports__, "__esModule", { value: true });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_platform_browser_dynamic__ = __webpack_require__("./node_modules/@angular/platform-browser-dynamic/esm5/platform-browser-dynamic.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__app_app_module__ = __webpack_require__("./src/app/app.module.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__environments_environment__ = __webpack_require__("./src/environments/environment.ts");




if (__WEBPACK_IMPORTED_MODULE_3__environments_environment__["a" /* environment */].production) {
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["_8" /* enableProdMode */])();
}
Object(__WEBPACK_IMPORTED_MODULE_1__angular_platform_browser_dynamic__["a" /* platformBrowserDynamic */])().bootstrapModule(__WEBPACK_IMPORTED_MODULE_2__app_app_module__["a" /* AppModule */])
    .catch(function (err) { return console.log(err); });


/***/ }),

/***/ 0:
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__("./src/main.ts");


/***/ }),

/***/ 1:
/***/ (function(module, exports) {

/* (ignored) */

/***/ }),

/***/ 2:
/***/ (function(module, exports) {

/* (ignored) */

/***/ })

},[0]);
//# sourceMappingURL=main.bundle.js.map