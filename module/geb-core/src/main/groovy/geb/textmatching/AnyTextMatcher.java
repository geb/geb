package geb.textmatching;

import java.util.Arrays;

class AnyTextMatcher extends CompositeTextMatcher {

    public AnyTextMatcher(TextMatcher[] matchers) {
        super(matchers);
    }

    @Override
    public boolean matches(String text) {
        return Arrays.stream(matchers).anyMatch(m -> m.matches(text));
    }
}
