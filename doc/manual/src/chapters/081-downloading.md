# Direct Downloading

Geb features an API that can be used to make direct HTTP requests from the application that is executing the Geb scripts or tests. This facilitates fine grained requests and downloading content such as PDF, CSVs, images etc. into your scripts or tests to then do something with. 

The direct download API works by using [`java.net.HttpURLConnection`][httpurlconnection] to directly connect to a URL from the application executing Geb, bypassing WebDriver.

The Direct Download API is provided by the [`DownloadSupport`](api/geb/download/DownloadSupport.html) class, which is mixed in to pages and modules (which means you can just call these instance methods directly from anywhere where you would want to, e.g. drive blocks, in tests/specs, methods on page objects, methods on modules). Consult the [`DownloadSupport`](api/geb/download/DownloadSupport.html) API reference for the `download*` methods that are available.
 
## Downloading Example

For example, let's say you are using Geb to exercise a web application that generates PDF documents. The WebDriver API can only deal with HTML documents. You want to hit the PDF download link and also do some tests on the downloaded PDF. The direct download API is there to fill this need.

    Browser.drive {
        go "http://myapp.com/login"
        
        // login
        username = "me"
        password = "secret"
        login().click()
        
        // now find the PDF download link
        def downloadLink = $("a.pdf-download-link")
        
        // now get the PDF bytes
        def bytes = downloadBytes(downloadLink.@href)
    }

Simple enough, but consider what is happening behind the scenes. Our application required us to log in, which implies some kind of session state. Geb is using [`HttpURLConnection`][httpurlconnection] behind the scenes to get the content and before doing so the cookies from the real browser will be transferred allowing this connection to assume the same session. The PDF download link href may also be relative, and Geb handles this by resolving the link passed to the download function against the browser's current page URL.

## Fine Grained Request

The Direct Download API can also be used for making fine grained requests which can be useful for testing edge cases or abnormal behavior.

All of the `download*()` methods take an optional closure that can configure the [java.net.HttpURLConnection][httpurlconnection] that will be used to make the request (after the `Cookie` header has been set).

For example, we could test what happens when we send gibberish in the `Accept-Encoding` header. 

    Browser.drive {
        go "http://myapp.com/somepage"
        downloadText { HttpURLConnection connection ->
            connection.setRequestProperty("Accept-Encoding", "gibberish")
        }
    }

> Before doing something like the above, it's worth considering whether doing such testing via Geb (a browser automation tool) is the right thing to do. You may find that it's more appropriate to directly use HttpURLConnection without Geb. That said, there are scenarios where such fine grained request control can be useful.

## Dealing with untrusted certificates
When facing web applications using untrusted (e.g. self-signed) SSL certificates, you will likely get exceptions when trying to use Geb's download API. By overriding the behavior of the request you can get around this kind of problem. Using the following code will allow running requests against a server which uses a certificate from the given keystore:

    import geb.download.helper.SelfSignedCertificateHelper
    downloadText { HttpURLConnection connection ->
        if (connection instanceof HttpsURLConnection) {
            def helper = new SelfSignedCertificateHelper(getClass().getResource('/keystore.jks'), 'changeit')
            helper.acceptCertificatesFor(connection as HttpsURLConnection)
        }
    }


## Default Configuration

In the [configuration](configuration.html), the default behaviour of the HttpURLConnection object can be specified by providing a closure as the `defaultDownloadConfig` property.

    defaultDownloadConfig = { HttpURLConnection connection ->
        // configure the connection
    }

This config closure will be run first, so anything set here can be overridden using the fine grained request configuration shown above.

## Errors

Any I/O type errors that occur during a download operation (e.g. HTTP 500 responses) will result in a [`DownloadException`](api/geb/download/DownloadException.html) being thrown that wraps the original exception and provides access to the HttpURLConnection used to make the request.
