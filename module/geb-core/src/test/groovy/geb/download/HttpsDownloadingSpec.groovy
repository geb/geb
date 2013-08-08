package geb.download

import geb.Configuration
import geb.download.helper.SelfSignedCertificateHelper
import geb.test.CallbackHttpsServer
import geb.test.GebSpecWithServer
import geb.test.TestHttpServer

import javax.net.ssl.HttpsURLConnection

class HttpsDownloadingSpec extends GebSpecWithServer {

	private static final Closure<Void> CONFIGURE_CONNECTION_FOR_SELF_SIGNED_CERT = { HttpURLConnection connection ->
		if (connection instanceof HttpsURLConnection) {
			def helper = new SelfSignedCertificateHelper(getClass().getResource('/keystore.jks'), 'password')
			helper.acceptCertificatesFor(connection as HttpsURLConnection)
		}
	}

	Configuration config
	ConfigObject rawConfig

	def setup() {
		config = browser.config
		rawConfig = config.rawConfig

		server.get = { req, res ->
			res.contentType = "text/plain"
			res.outputStream << "from https"
		}
	}

	TestHttpServer getServerInstance() {
		new CallbackHttpsServer()
	}

	void 'can download from endpoints with self-signed certificates'() {
		expect:
		downloadText(browser.baseUrl, CONFIGURE_CONNECTION_FOR_SELF_SIGNED_CERT) == 'from https'
	}

	void 'download connections can be configured globally'() {
		when:
		rawConfig.defaultDownloadConfig = CONFIGURE_CONNECTION_FOR_SELF_SIGNED_CERT

		then:
		downloadText(browser.baseUrl) == 'from https'
	}
}
