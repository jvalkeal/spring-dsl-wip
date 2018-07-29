#!/bin/bash
set -e
npm install
rm -fr *.vsix
npm run vsce-package
code --uninstall-extension Pivotal.vscode-simple || echo "WARN: Can't uninstall old version."
rm -fr ~/.vscode/extensions/pivotal.vscode-simple*
code --install-extension *.vsix
