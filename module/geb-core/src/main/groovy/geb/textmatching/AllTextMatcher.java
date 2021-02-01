package geb.textmatching;

import java.util.Arrays;

class AllTextMatcher extends CompositeTextMatcher {

    public AllTextMatcher(TextMatcher[] matchers) {
        super(matchers);
    }

    @Override
    public boolean matches(String text) {
        return Arrays.stream(matchers).allMatch(m -> m.matches(text));
    }
}
