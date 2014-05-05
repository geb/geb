package geb.error;

import java.net.URL;

public class UnableToLoadException extends GebException {
	UnableToLoadException(URL configLocation, String environment, Throwable cause) {
		super(String.format("Unable to load configuration @ '%s' (with environment: %s)", configLocation, environment), cause);
	}

	UnableToLoadException(Class configClass, String environment, Throwable cause) {
		super(String.format("Unable to load configuration from class '%s' (with environment: %s)", configClass, environment), cause);
	}
}
