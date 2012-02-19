using "geb"

import geb.test.CallbackHttpServer

scenario "using geb", {

	given "geb goodness", {
		server = new CallbackHttpServer()
		server.start()
		baseUrl = server.baseUrl
		
		server.get = { req, res ->
			res.outputStream << """
			<html>
			<head>
				<script type="text/javascript">
					var v = 1;
				</script>
			</head>
			<body>
				<div class="d1" id="d1">d1</div>
			</body>
			</html>"""
		}
	}

	when "to", {
		browser.driver.javascriptEnabled = true
		to SomePage
	}

	then "at", {
		at SomePage
	}

	and "can get js object", {
		js.v.shouldBe 1
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