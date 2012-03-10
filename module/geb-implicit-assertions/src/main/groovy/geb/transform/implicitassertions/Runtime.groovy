package geb.transform.implicitassertions

import org.codehaus.groovy.runtime.InvokerHelper

class Runtime {
	public static boolean isVoidMethod(Object target, String method, Object... args) {
		Class[] argTypes = args.collect { it?.getClass() } as Class[]

		MetaClass metaClass = InvokerHelper.getMetaClass(target)

		MetaMethod metaMethod = metaClass.pickMethod(method, argTypes)
		if (metaMethod == null) {
			return false
		}

		Class returnType = metaMethod.getReturnType()
		return returnType == void.class || returnType == Void.class
	}
}
