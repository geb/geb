package geb

import geb.test.GebSpecWithCallbackServer
import org.codehaus.groovy.runtime.typehandling.GroovyCastException

class ContentUnwrappingSpec extends GebSpecWithCallbackServer {

    def setup() {
        to PageWithModule
    }

    def "unwrap module to its declared type"() {
        when:
        def module = theModule as DummyModule

        then:
        module instanceof DummyModule
    }

    def "unwrap module to its parent type"() {
        when:
        def module = theModule as Module

        then:
        module instanceof Module
    }

    def "throw relevant exception on unwrap attempt to incorrect type"() {
        when:
        def module = theModule as ContentUnwrappingSpec

        then:
        thrown(GroovyCastException)
    }
}

class PageWithModule extends Page {

    static content = {
        theModule { module(DummyModule) }
    }

}

class DummyModule extends Module { }