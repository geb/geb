package geb.navigator

/**
 * A delegating meta class implementation that intercepts field access using the .@ operator and sends it to getProperty("@$name")
 */
class AttributeAccessingMetaClass extends DelegatingMetaClass {

	AttributeAccessingMetaClass(MetaClass delegate) {
		super(delegate)
	}

	Object getAttribute(Object object, String attribute) {
		object.getProperty("@$attribute")
	}

}
