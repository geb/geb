package geb

import spock.lang.*
import geb.test.GebSpecWithServer

class ContentAccessShortcutsSpec extends GebSpecWithServer {

	def setupSpec() {
		responseHtml {
			form {
				input(type: 'text', id: 'text', value: 'Initial text')
				input(type: 'checkbox', id: 'checked-box', value: '123', checked: 'checked')
				input(type: 'checkbox', id: 'unchecked-box', value: '123')
				input(type: 'radio', name: 'radio', value: 'first')
				input(type: 'radio', name: 'radio', value: 'second', checked: 'checked')
				input(type: 'radio', name: 'radio', value: 'third')
				select(id: 'select') {
					option('Option #1', value: '1')
					option('Option #2', value: '2', selected: 'selected')
				}
				select(id: 'multiselect', multiple: 'multiple') {
					option('Option #1', value: '1')
					option('Option #2', value: '2', selected: 'selected')
					option('Option #3', value: '3')
					option('Option #4', value: '4', selected: 'selected')
				}
			}
		}
	}

	@Unroll()
	def "content values can be read using equals"() {
		when:
		to ContentAccessShortcutsPage
		def container = contentContainerFactory.call()

		then:
		container.textInput == 'Initial text'
		container.checkedBox == '123'
		container.uncheckedBox == false
		container.radioGroup == 'second'
		container.select == '2'
		container.multiselect == ['2', '4']

		where:
		contentContainerFactory << [{ page }, { wholePageModule }]
	}

	@Unroll()
	def "content values can be set using assignment"() {
		given:
		to ContentAccessShortcutsPage
		def container = contentContainerFactory.call()

		when:
		container.textInput = 'New text'
		then:
		container.textInput.value() == 'New text'

		when:
		container.checkedBox = false
		then:
		container.checkedBox.value() == false

		when:
		container.uncheckedBox = true
		then:
		container.uncheckedBox.value() == '123'

		when:
		container.radioGroup = 'third'
		then:
		container.radioGroup*.value().findAll { it } == ['third']

		when:
		container.select = '1'
		then:
		container.select.value() == '1'

		when:
		container.multiselect = ['1', '3']
		then:
		container.multiselect.value() == ['1', '3']

		where:
		contentContainerFactory << [{ page }, { wholePageModule }]
	}
}

class ContentAccessShortcutsPage extends Page {
	final static Closure CONTENT_CLOSURE = {
		textInput { $('#text') }
		checkedBox { $('#checked-box') }
		uncheckedBox { $('#unchecked-box') }
		radioGroup { $('input', name: 'radio') }
		select { $('#select') }
		multiselect { $('#multiselect') }
	}

	static content = {
		Closure baseContent = CONTENT_CLOSURE.clone()
		baseContent.delegate = delegate
		baseContent.call()
		wholePageModule { module WholePageModule }
	}
}

class WholePageModule extends Module {
	static content = ContentAccessShortcutsPage.CONTENT_CLOSURE.clone()
}
