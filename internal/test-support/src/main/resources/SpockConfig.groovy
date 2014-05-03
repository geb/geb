import geb.test.CrossBrowser
import geb.interaction.InteractionsSupportSpec

def sauceBrowser = System.getProperty("geb.sauce.browser")
if (sauceBrowser) {
	runner {
		include CrossBrowser
		if (sauceBrowser.startsWith('safari')) {
			exclude InteractionsSupportSpec
		}
	}
}