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

import geb.Configuration
import org.eclipse.jetty.server.Connector
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ServletContextHandler

import java.util.function.Supplier

abstract class TestHttpServer {

    protected final Supplier<Configuration> configurationSupplier

    protected server
    boolean started

    TestHttpServer(Configuration configuration) {
        this({ configuration })
    }

    TestHttpServer(Supplier<Configuration> configurationSupplier) {
        this.configurationSupplier = configurationSupplier
    }

    void start(int port = 0) {
        if (!started) {
            server = new Server()
            server.addConnector(createConnector(server, port))
            def context = new ServletContextHandler(server, "/")
            addServlets(context)
            server.start()
            started = true
            notifyPortHandler(server.connectors[0].localPort)
        }
    }

    void notifyPortHandler(int port) {
        def handler = configuration.rawConfig.testHttpServerPortHandler
        if (handler instanceof Closure) {
            handler.call(port)
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

    String getHost() {
        configuration.rawConfig.testHttpServerHost ?: "localhost"
    }

    def getBaseUrl() {
        "$protocol://$host:$port/"
    }

    abstract protected addServlets(ServletContextHandler context)

    protected Connector createConnector(Server server, int port) {
        def connector = new ServerConnector(server)
        connector.port = port
        connector
    }

    protected getConfiguration() {
        configurationSupplier.get()
    }
}