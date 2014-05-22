package geb.error;

import com.google.common.base.Joiner;

public class UnableToSetElementException extends GebException {
	UnableToSetElementException(String element) {
		super(String.format("Unable to set the value of element %s as it is not a valid form element", element));
	}

	UnableToSetElementException(String... elements) {
		super(String.format("Unable to set the value of elements %s as they are not valid form elements", Joiner.on(", ").join(elements)));
	}
}
