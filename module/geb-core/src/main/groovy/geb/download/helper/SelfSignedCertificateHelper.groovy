package geb.download.helper

import javax.net.ssl.*
import java.security.KeyStore

class SelfSignedCertificateHelper {
	String keystoreFileName
	String keystoreFilePassword

	SelfSignedCertificateHelper(String keystoreFileName, String keystoreFilePassword) {
		this.keystoreFileName = keystoreFileName
		this.keystoreFilePassword = keystoreFilePassword
	}

	void acceptCertificatesFor(HttpsURLConnection con) {
		con.setSSLSocketFactory(socketFactory)
		con.setHostnameVerifier(hostnameVerifier)
	}

	private SSLSocketFactory getSocketFactory() {
		def keyStore = KeyStore.getInstance(KeyStore.defaultType)
		keyStore.load(ClassLoader.getResourceAsStream(keystoreFileName), keystoreFilePassword.toCharArray())
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.defaultAlgorithm);
		tmf.init(keyStore);
		SSLContext ctx = SSLContext.getInstance('TLS');
		ctx.init(null, tmf.trustManagers, null);
		return ctx.socketFactory
	}

	private HostnameVerifier getHostnameVerifier() {
		return new HostnameVerifier() {
			boolean verify(String hostname, SSLSession sslSession) {
				return hostname == 'localhost'
			}
		}
	}

}
