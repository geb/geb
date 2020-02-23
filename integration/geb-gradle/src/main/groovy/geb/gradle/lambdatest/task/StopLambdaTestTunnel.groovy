/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.gradle.lambdatest.task

import geb.gradle.cloud.ExternalTunnel
import geb.gradle.lambdatest.LambdaTestTunnel
import geb.gradle.lambdatest.LambdaTestTunnelOps
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class StopLambdaTestTunnel extends DefaultTask {

    private static int infoAPIPort = LambdaTestTunnelOps.getAPIPort()
    ExternalTunnel tunnel

    @TaskAction
    void stop() {
        try {
            // Close LambdaTest Tunnel
            def delete
            def hostURL = "http://127.0.0.1:"
            def apiClosure = "/api/v1.0/stop"
            if (LambdaTestTunnel.userAPIPort > 0) {
                infoAPIPort = LambdaTestTunnel.userAPIPort
            }
            logger.info "disconnecting lambdatest tunnel having infoAPI port :" + infoAPIPort
            delete = new URL(hostURL + infoAPIPort + apiClosure).openConnection()
            delete.requestMethod = "DELETE"
            delete.setRequestProperty("Content-Type", "application/json")
            def deleteRC = delete.getResponseCode()
            if (deleteRC != 200) {
                logger.info "Tunnel not destroyed by API, using default destroy method"
                tunnel.stopTunnel()
            }
        }
        catch (Exception e)
        {
            logger.info "Error occured while closing tunnel" + e.printStackTrace()
        }
    }
}