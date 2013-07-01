package geb.error

class UndefinedAtCheckerException extends GebException {
	UndefinedAtCheckerException(String className) {
		super("No at checker has been defined for class $className.")
	}
}
