using "geb"

import geb.test.util.TestHttpServer

scenario "using geb", {

	given "geb goodness", {
		server = new TestHttpServer()
		server.start()
		baseUrl = server.baseUrl
		
		server.get = { req, res ->
			res.outputStream << """
			<html>
			<body>
				<div class="d1" id="d1">d1</div>
			</body>
			</html>"""
		}
	}

	when "to", {
		to SomePage
	}

	then "at", {
		at SomePage
	}

	and "page stuff", {
		page.div.text().shouldBe "d1"
	}
	
	and "stop server", {
		server.stop()
	}
}

class SomePage extends geb.Page {
	static content = {
		div { $("#d1") }
	}
}