import geb.test.CrossBrowser

def usingSauce = System.getProperty("geb.sauce.browser")
if (usingSauce) {
	runner {
		include CrossBrowser
	}
}