/* Copyright 2009 the original author or authors.
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
package geb.js

class AlertAndConfirmSupport {

	private final Closure javascriptInterfaceFactory
	
	AlertAndConfirmSupport(Closure javascriptInterfaceFactory) {
		this.javascriptInterfaceFactory = javascriptInterfaceFactory
	}
	
	private JavascriptInterface getJavascriptInterface() {
		def js = javascriptInterfaceFactory()
		if (js == null) {
			throw new IllegalStateException("javascriptInterfaceFactory returned null")
		} else if (!(js instanceof JavascriptInterface)) {
			throw new IllegalStateException("javascriptInterfaceFactory did not return a JavascriptInterface")
		}
		
		js
	}

	private getInstallGebStorageScript() {
		"""
			if (!window.geb) {
				window.geb = {};
			} 
		"""
	}
	
	private getInstallDialogStorageScript() {
		"""
			$installGebStorageScript
			
			if (!window.geb.dialogFunctions) {
				window.geb.dialogFunctions = new Array();
			}
			if (!window.geb.dialogMessages) {
				window.geb.dialogMessages = new Array();
			}
		"""
	}
	
	private popLastDialogMessage(JavascriptInterface js) {
		js.exec "return window.geb.dialogMessages.pop();"
	}

	private popLastDialogFunctionOnto(JavascriptInterface js, String onto) {
		js.exec "window.$onto = window.geb.dialogFunctions.pop();"
	}
	
	private installAlert(JavascriptInterface js) {
		js.exec """
			$installDialogStorageScript
			
			window.geb.dialogFunctions.push(window.alert);
			window.geb.dialogMessages.push(null);
			
			window.alert = function(msg) {
				window.geb.dialogMessages.pop();
				window.geb.dialogMessages.push(msg);
				return true;
			};
		"""
	}
	
	private installConfirm(boolean ok, JavascriptInterface js) {
		js.exec """
			$installDialogStorageScript
			
			window.geb.dialogFunctions.push(window.confirm);
			window.geb.dialogMessages.push(null);
			
			window.confirm = function(msg) {
				window.geb.dialogMessages.pop();
				window.geb.dialogMessages.push(msg);
				return $ok;
			};
		"""
	}

	private captureDialog(Closure installer, String function, Closure actions) {
		def js = getJavascriptInterface()
		
		installer(js)
		
		def actionsError = null
		try {
			actions()
		} catch (Throwable e) {
			actionsError = e
		}
		
		// Need to do this even if actions raised exception
		def message = popLastDialogMessage(js)
		popLastDialogFunctionOnto(js, function)
		
		if (actionsError) {
			throw actionsError
		} else {
			message
		}
	}
	
	private captureAlert(Closure actions) {
		captureDialog(this.&installAlert, 'alert', actions)
	}

	private captureConfirm(boolean ok, Closure actions) {
		captureDialog(this.&installConfirm.curry(ok), 'confirm', actions)
	}

	String withAlert(Closure actions) {
		def message = captureAlert(actions)
		if (message == null) {
			throw new AssertionError("no browser alert() was raised")
		} else {
			message
		}
	}

	void withNoAlert(Closure actions) {
		def message = captureAlert(actions)
		if (message != null) {
			throw new AssertionError("an unexpected browser alert() was raised (message: $message)")
		}
	}

	String withConfirm(Closure actions) {
		withConfirm(true, actions)
	}
	
	String withConfirm(boolean ok, Closure actions) {
		def message = captureConfirm(ok, actions)
		if (message == null) {
			throw new AssertionError("no browser confirm() was raised")
		} else {
			message
		}
	}

	void withNoConfirm(Closure actions) {
		def message = captureConfirm(false, actions)
		if (message != null) {
			throw new AssertionError("an unexpected browser confirm() was raised (message: $message)")
		}
	}
}