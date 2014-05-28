/*
 * Copyright 2012 the original author or authors.
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
package geb.download.helper

import javax.net.ssl.*
import java.security.KeyStore

class SelfSignedCertificateHelper {
	URL keystoreUrl
	String keystoreFilePassword

	SelfSignedCertificateHelper(URL keystoreUrl, String keystoreFilePassword) {
		this.keystoreUrl = keystoreUrl
		this.keystoreFilePassword = keystoreFilePassword
	}

	void acceptCertificatesFor(HttpsURLConnection con) {
		con.setSSLSocketFactory(socketFactory)
		con.setHostnameVerifier(hostnameVerifier)
	}

	private SSLSocketFactory getSocketFactory() {
		def keyStore = KeyStore.getInstance(KeyStore.defaultType)
		keyStore.load(keystoreUrl.openStream(), keystoreFilePassword.toCharArray())
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.defaultAlgorithm);
		tmf.init(keyStore);
		SSLContext ctx = SSLContext.getInstance('TLS');
		ctx.init(null, tmf.trustManagers, null);
		return ctx.socketFactory
	}

	private HostnameVerifier getHostnameVerifier() {
		return new HostnameVerifier() {
			boolean verify(String hostname, SSLSession sslSession) {
				return true
			}
		}
	}

}
