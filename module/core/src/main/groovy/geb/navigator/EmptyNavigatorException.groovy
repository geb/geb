package geb.navigator

class EmptyNavigatorException extends IllegalStateException {

	EmptyNavigatorException() {
		super("The Navigator instance is empty")
	}

}
