import grails.plugin.geb.GebSpec

import spock.lang.*

import pages.*

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
		form.enabled = true
		form.firstName = "Luke"
		form.lastName = "Daley"
		createButton.click()
		then:
		at ShowPage
	}
	
	def "check the entered details"() {
		expect:
		firstName.text() == "Luke"
		lastName.text() == "Daley"
		enabled.text() == "True"
		id.text() ==~ /\d+/
	}

	def "edit the details"() {
		when:
		editButton.click()
		then:
		at EditPage
		when:
		form.enabled = false
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