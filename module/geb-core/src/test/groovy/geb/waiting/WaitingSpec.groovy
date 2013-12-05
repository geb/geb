package geb.waiting

import geb.test.GebSpecWithServer

class WaitingSpec extends GebSpecWithServer {

	def setupSpec() {
		server.get = { req, res ->
			res.outputStream << """
				<html>
				<head>
				  <script type="text/javascript" charset="utf-8">
				    function showIn(i) {
				      setTimeout(function() {
				        document.body.innerHTML = "<div><span>a</span></div>";
				      }, i * 1000);
				    }
				  </script>
				</head>
				<body>
				</body>
				</html>
			"""
		}
	}
}
