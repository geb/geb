/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.test

import org.mortbay.jetty.Connector
import org.mortbay.jetty.Server
import org.mortbay.jetty.bio.SocketConnector
import org.mortbay.jetty.servlet.Context

abstract class TestHttpServer {
	protected server
	boolean started

	void start(int port = 0) {
		if (!started) {
			server = new Server()
			server.addConnector(createConnector(port))
			def context = new Context(server, "/")
			addServlets(context)
			server.start()
			started = true
		}
	}

	void stop() {
		if (started) {
			server.stop()
			started = false
		}
	}

	def getPort() {
		server?.connectors[0].localPort
	}

	String getProtocol() {
		'http'
	}

	def getBaseUrl() {
		"$protocol://localhost:$port/"
	}

	def getBaseUrlAsUrl() {
		new URL(getBaseUrl())
	}

	abstract protected addServlets(Context context)

	protected Connector createConnector(int port) {
		def connector = new SocketConnector()
		connector.port = port
		connector
	}
}