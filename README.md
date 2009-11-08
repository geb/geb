# Geb - A Groovy DSL for HTMLUnit

Geb is best explained by example…

	import org.codehaus.geb.Geb
	
	new Geb("http://google.com").with {
		get "/"
		form('f') {
			q = 'github geb'
			click 'btnG'
		}
		assert response.contentAsString =~ /Commit History for alkemist's Geb - GitHub/
	}
	
Or…

	import org.codehaus.geb.Geb
	
	new Geb("http://google.com").with {
		get "/" {
			q = 'github geb'
		}
		assert response.contentAsString =~ /Commit History for alkemist's Geb - GitHub/
	}


A `Geb` object is a wrapper for HTMLUnit's [WebClient](http://htmlunit.sourceforge.net/apidocs/com/gargoylesoftware/htmlunit/WebClient.html "WebClient (HtmlUnit 2.6 API)") with some Groovy syntactic sugar that makes automating interactions with web sites easy.

## Features

_More documentation will be forthcoming_

#### Credits

The API for Geb was inspired by the [grails functional-test plugin](http://grails.org/plugin/functional-test "Grails Plugin - Grails Functional Testing").