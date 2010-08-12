This plugin adds functional web testing to Grails via the [Geb/Spock](http://github.com/alkemist/geb-spock) library.

## Installation

To install the plugin you must first install the spock plugin.

For Grails 1.3.x:

    grails install-plugin spock 0.5-groovy-1.7-SNAPSHOT

For Grails 1.2.x:

    grails install-plugin spock 0.5-groovy-1.6-SNAPSHOT

Then install the `geb-spock` plugin:

    grails install-plugin geb-spock 0.1-SNAPSHOT

## How to use

Simply write specs that extend `grails.plugin.gebspock.GebSpec`. This will configure the URL to use for testing and set the reporting dir to be `target/test-reports/geb-spock` (which is where the response output gets written).

## Examples

There is an [example app available for download](http://github.com/downloads/alkemist/grails-geb-spock/person-scaffold-example.zip "") that demonstrates testing some Grails scaffolding.

Here is the spec in its entirety:

    import grails.plugin.gebspock.GebSpec

    import spock.lang.*

    @Stepwise
    class PersonCRUDSpec extends GebSpec {

        def "there are no people"() {
            when:
            to ListPage
            then:
            personRows.size() == 0
        }

        def "add a person"() {
            when:
            newPersonButton.click()
            then:
            at CreatePage
        }

        def "enter the details"() {
            when:
            form.enabled.check()
            form.firstName.value("Luke")
            form.lastName.value("Daley")
            createButton.click()
            then:
            at ShowPage
        }

        def "check the entered details"() {
            expect:
            enabled.text() == "True"
            firstName.text() == "Luke"
            lastName.text() == "Daley"
            id.text() ==~ /\d+/
        }

        def "edit the details"() {
            when:
            editButton.click()
            then:
            at EditPage
            when:
            form.enabled.uncheck()
            updateButton.click()
            then:
            at ShowPage
        }

        def "check in listing"() {
            when:
            to ListPage
            then:
            personRows.size() == 1
            def row = personRow(0)
            row.firstName.text() == "Luke"
            row.lastName.text() == "Daley"
        }

        def "show person"() {
            when:
            personRow(0).showLink.click()
            then:
            at ShowPage
        }

        def "delete user"() {
            given:
            def id = id.text()
            when:
            deleteButton.click()
            then:
            at ListPage
            message.text() == "Person $id deleted"
            personRows.size() == 0
        }
    }

Here is an example page object with a module:

    import org.codehaus.geb.spock.page.Module

    class ListPage extends ScaffoldPage {
        static url = "/person/list"

        static at = {
            heading.text() ==~ /Person List/
        }

        static content = {
            newPersonButton(to: CreatePage) { $("a").withTextMatching("New Person") }
            peopleTable { $("div.list").get("table", 0) }
            personRow { module PersonRow, personRows.get(it) }
            personRows(required: false) { peopleTable.get("tbody").get("tr") }
        }
    }

    class PersonRow extends Module {
        static content = {
            cell { $("td").get(it) }
            id { cell(0) }
            enabled { cell(1) }
            firstName { cell(2) }
            lastName { cell(3) }
            showLink(to: ShowPage) { id.get("a") }
        }
    }

For the full example, check out the example project.