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
