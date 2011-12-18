package geb.interaction

import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.WebDriver

class ActionsDelegate {

	private Actions actions

	ActionsDelegate(WebDriver driver) {
		actions = new Actions(driver)
	}

	void methodMissing(String methodName, def args) {
		extractWebDriverElement(args, methodName)
	}

	private extractWebDriverElement(args, String methodName) {
		def arguments = args.collect { def arg ->

			def argument = arg

			try {
				argument = arg.getElement(0)
			} catch (MissingMethodException ex) {
				// Empty exception block where WebDriver element has already been extracted
			}

			return argument
		}

		actions."${methodName}"(* arguments)
	}

	void perform() {
		actions.build().perform()
	}

}
