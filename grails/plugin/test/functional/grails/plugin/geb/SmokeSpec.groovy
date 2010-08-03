package grails.plugin.geb
import geb.*
import spock.lang.*

class SmokeSpec extends GebSpec {

	@Shared messageSource
	
	def "default location is application root"() {
		when:
		to IndexPage
		then:
		at IndexPage
	}

	def "page urls are relative to app"() {
		when:
		to OtherPage
		then:
		at OtherPage
	}
	
	def "messageSource was autowired"() {
		expect:
		messageSource != null
	}
}

class IndexPage extends Page {
	static url = "index"
	static at = { div.text() == "index" }
	static content = {
		div { $('div') }
	}
}

class OtherPage extends Page {
	static url = "the/page"
	static at = { div.text() == "page" }
	static content = {
		div { $('div') }
	}
}