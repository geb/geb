package geb.navigator

/**
 * SelectionContext allows you to access the selector used for creating a navigator
 */
class SelectionContext {
    String selector

    public SelectionContext(String selector) {
        this.selector = selector
    }
}
