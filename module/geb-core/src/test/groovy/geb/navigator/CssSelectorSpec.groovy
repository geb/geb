package geb.navigator

import geb.Browser
import org.openqa.selenium.WebDriver
import spock.lang.Shared
import spock.lang.Specification

import static geb.navigator.CssSelector.Type.*

class CssSelectorSpec extends Specification {

	@Shared Browser browser
	@Shared WebDriver driver
	@Shared Navigator onPage

	def setupSpec() {
		browser = new Browser()
		browser.go(getClass().getResource("/test.html") as String)
		onPage = browser.navigatorFactory.base
	}

	def "selector type matching rules"() {
		expect: selector.matches(element) == expectedMatch

		where:
		selector                               | element                                 | expectedMatch
		new CssSelector(ELEMENT, "div")        | onPage.find("#article-1").getElement(0) | true
		new CssSelector(ELEMENT, "p")          | onPage.find("#article-1").getElement(0) | false
		new CssSelector(ID, "article-1")       | onPage.find("#article-1").getElement(0) | true
		new CssSelector(ID, "article-1")       | onPage.find("#article-2").getElement(0) | false
		new CssSelector(HTML_CLASS, "article") | onPage.find("#article-1").getElement(0) | true
		new CssSelector(HTML_CLASS, "article") | onPage.find(".content").getElement(0)   | false
	}

	def "compilation of CSS selectors"() {
		when:
		List<List<CssSelector>> selectors = CssSelector.compile(selector)

		then:
		selectors[index]*.toString() == expectedSelectors

		where:
		selector                                                                                        | index | expectedSelectors
		"div"                                                                                           | 0     | ["div"]
		".something"                                                                                    | 0     | [".something"]
		"#id-with-hyphens"                                                                              | 0     | ["#id-with-hyphens"]
		"blockquote.special"                                                                            | 0     | ["blockquote", ".special"]
		"blockquote#special"                                                                            | 0     | ["blockquote", "#special"]
		"blockquote#the_id.the_class"                                                                   | 0     | ["blockquote", "#the_id", ".the_class"]
		"blockquote.the_class#the_id"                                                                   | 0     | ["blockquote", ".the_class", "#the_id"]
		"blockquote#the_id.the_class p.last.wow.oh-yeah a"                                              | 0     | ["blockquote", "#the_id", ".the_class", " ", "p", ".last", ".wow", ".oh-yeah", " ", "a"]
		"blockquote#the_id.the_class p.last.wow.oh-yeah a  , div.totally p.rocks.your, div#socks a.off" | 0     | ["blockquote", "#the_id", ".the_class", " ", "p", ".last", ".wow", ".oh-yeah", " ", "a"]
		"blockquote#the_id.the_class p.last.wow.oh-yeah a  , div.totally p.rocks.your, div#socks a.off" | 1     | ["div", ".totally", " ", "p", ".rocks", ".your"]
		"blockquote#the_id.the_class p.last.wow.oh-yeah a  , div.totally p.rocks.your, div#socks a.off" | 2     | ["div", "#socks", " ", "a", ".off"]
	}
}
