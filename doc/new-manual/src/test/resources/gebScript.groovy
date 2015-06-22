import geb.Page

// tag::script[]
go "some/page"
at(SomePage)
waitFor { $("p#status").text() == "ready" }
js.someJavaScriptFunction()
downloadText($("a.textFile").@href)
// end::script[]

class SomePage extends Page {
    static at = { title == "Some page"}
}