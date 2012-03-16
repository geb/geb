package geb.transform.implicitassertions;

import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.runtime.InvokerHelper;

public abstract class Runtime {

	@SuppressWarnings("UnusedDeclaration")
	public static boolean isVoidMethod(Object target, String method, Object... args) {
		
		Class[] argTypes = new Class[args.length];
		int i = 0;
		for (Object arg : args) {
			argTypes[i++] = arg == null ? null : arg.getClass();
		}

		MetaClass metaClass = target instanceof Class ? InvokerHelper.getMetaClass((Class) target) : InvokerHelper.getMetaClass(target);

		MetaMethod metaMethod = metaClass.pickMethod(method, argTypes);
		if (metaMethod == null) {
			return false;
		}

		Class returnType = metaMethod.getReturnType();
		return returnType == void.class || returnType == Void.class;
	}

	private static ThreadLocal<Object> recordedValue = new ThreadLocal<Object>();

	@SuppressWarnings("UnusedDeclaration")
	public static Object recordValue(Object value) {
		recordedValue.set(value);
		return value;
	}
	
	@SuppressWarnings("UnusedDeclaration")
	public static Object retrieveRecordedValue() {
		Object value = recordedValue.get();
		recordedValue.set(null);
		return value;
	}

}
