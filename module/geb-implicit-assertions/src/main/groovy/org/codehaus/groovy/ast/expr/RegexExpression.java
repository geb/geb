/*
 * Copyright 2003-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.ast.expr;

//import org.apache.log4j.Logger;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.GroovyCodeVisitor;

import java.lang.reflect.Method;

/**
 * Represents a regular expression of the form ~<double quoted string> which creates
 * a regular expression.
 *
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 */
@Deprecated
public class RegexExpression extends Expression {

	//private static final Logger LOG = Logger.getLogger(RegexExpression.class);

	private final Expression string;

	public RegexExpression(Expression string) {
		this.string = string;
		super.setType(ClassHelper.PATTERN_TYPE);
	}

	@SuppressWarnings("PMD.EmptyCatchBlock")
	public void visit(GroovyCodeVisitor visitor) {

		// find the visitRegexExpression if it exists, ignore otherwise
		try {
			Method method = visitor.getClass().getMethod("visitRegexExpression", RegexExpression.class);
			method.invoke(visitor, this);
		} catch (NoSuchMethodException ignored) {
			// no method is the most likely case
		}catch (Exception e) {
			//LOG.error("An exception occurred dispatching to visitRegexExpression", e);
		}
	}

	public Expression transformExpression(ExpressionTransformer transformer) {
		Expression ret = new RegexExpression(transformer.transform(string));
		ret.setSourcePosition(this);
		return ret;
	}

	public Expression getRegex() {
		return string;
	}

}