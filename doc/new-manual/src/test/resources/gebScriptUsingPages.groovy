// tag::script[]
import geb.Page

class InitialPage extends Page {
    static content = {
        button(to: NextPage) { $("input.do-stuff") }
    }
}

class NextPage extends Page {
}

to InitialPage
assert page instanceof InitialPage
page.button.click()
assert page instanceof NextPage
// tag::script[]