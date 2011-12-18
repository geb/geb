package geb.interaction

import geb.Browser

class InteractionsSupport {

	Browser browser

	InteractionsSupport(Browser browser) {
		this.browser = browser
	}

	void interact(Closure interactionClosure) {
		ActionsDelegate actions = new ActionsDelegate(browser.driver)
		interactionClosure.delegate = actions
		interactionClosure.resolveStrategy = Closure.DELEGATE_FIRST
		interactionClosure.call()
		actions.perform()
	}

}