/*
 * Copyright 2015 the original author or authors.
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
package fixture

import geb.test.GebSpecWithCallbackServer

class GebSpecWithServerUsingJavascript extends GebSpecWithCallbackServer {

    String jquery() {
        resourceJavascript("jquery-2.1.4.min")
    }

    String interactjs() {
        resourceJavascript("interact-1.2.4.min")
    }

    String resourceJavascript(String resourceName) {
        javascript(getClass().getResource("/${resourceName}.js").text)
    }

    String javascript(String code) {
        """
            <script type="text/javascript">
                $code
            </script>
        """
    }
}