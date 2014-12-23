package geb.error;

import geb.Page;

public class AtCheckerVerificationFailedException extends GebAssertionError {

	private final static String message = "At checker verification failed for page [%s]";

	public AtCheckerVerificationFailedException(Page pageInstance) {
		super(String.format(message, pageInstance.getClass().getSimpleName()));
	}

	public AtCheckerVerificationFailedException(Class<? extends Page> pageClass) {
		super(String.format(message, pageClass.getSimpleName()));
	}
}
