package geb.textmatching;

import java.util.Arrays;

abstract class GroupTextMatcher implements TextMatcher {
    protected final TextMatcher[] matchers;

    protected GroupTextMatcher(TextMatcher[] matchers) {
        this.matchers = matchers;
    }

    static class AllTextMatcher extends GroupTextMatcher {

        public AllTextMatcher(TextMatcher[] matchers) {
            super(matchers);
        }

        @Override
        public boolean matches(String text) {
            return Arrays.stream(matchers).allMatch(m -> m.matches(text));
        }
    }

    static class AnyTextMatcher extends GroupTextMatcher {

        public AnyTextMatcher(TextMatcher[] matchers) {
            super(matchers);
        }

        @Override
        public boolean matches(String text) {
            return Arrays.stream(matchers).anyMatch(m -> m.matches(text));
        }
    }
}
