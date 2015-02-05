package geb.error;

public class UndefinedAtCheckerException extends GebException {

    public UndefinedAtCheckerException(String className) {
        super(String.format("No at checker has been defined for page class %s.", className));
    }
}
