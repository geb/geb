package geb.textmatching;

abstract class CompositeTextMatcher implements TextMatcher {

    protected final TextMatcher[] matchers;

    protected CompositeTextMatcher(TextMatcher[] matchers) {
        this.matchers = matchers;
    }
}
