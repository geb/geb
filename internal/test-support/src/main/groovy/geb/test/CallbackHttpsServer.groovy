package geb.test

import org.mortbay.jetty.Connector
import org.mortbay.jetty.security.SslSocketConnector

class CallbackHttpsServer extends CallbackHttpServer {

	protected Connector createConnector(int port) {
		def connector = new SslSocketConnector()
		connector.port = port
		connector.password = 'password'
		connector.keyPassword = 'password'
		connector.trustPassword = 'password'
		connector.keystore = getClass().getResource('/keystore.jks').toString()
		connector
	}

	String getProtocol() {
		'https'
	}
}
