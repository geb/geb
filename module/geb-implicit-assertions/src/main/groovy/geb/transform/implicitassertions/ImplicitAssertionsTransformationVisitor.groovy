/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.transform.implicitassertions

import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import static org.codehaus.groovy.syntax.Types.ASSIGNMENT_OPERATOR
import static org.codehaus.groovy.syntax.Types.ofType
import static geb.transform.implicitassertions.ImplicitAssertionsTransformationUtil.*

class ImplicitAssertionsTransformationVisitor extends ClassCodeVisitorSupport {
	SourceUnit sourceUnit

	ImplicitAssertionsTransformationVisitor(SourceUnit sourceUnit) {
		this.sourceUnit = sourceUnit
	}

	@Override
	void visitField(FieldNode node) {
		if (node.static && node.initialExpression in ClosureExpression) {
			switch (node.name) {
				case 'at':
					transformEachStatement(node.initialExpression)
					break
				case 'content':
					visitContentDsl(node.initialExpression)
					break
			}
		}
	}

	private boolean lastArgumentIsClosureExpression(ArgumentListExpression arguments) {
		arguments.expressions && arguments.expressions[-1] in ClosureExpression
	}

	@Override
	void visitExpressionStatement(ExpressionStatement statement) {
		if (statement.expression in MethodCallExpression) {
			MethodCallExpression expression = statement.expression
			if (expression.methodAsString == 'waitFor' && expression.arguments in ArgumentListExpression) {
				ArgumentListExpression arguments = expression.arguments
				if (lastArgumentIsClosureExpression(arguments)) {
					transformEachStatement(arguments.expressions[-1])
				}
			} else {
				compensateForSpockIfNecessary(expression)
			}
		}
	}

	void compensateForSpockIfNecessary(MethodCallExpression expression) {
		if (expression.objectExpression in ClassExpression && expression.method in ConstantExpression) {
			ClassExpression classExpression = expression.objectExpression as ClassExpression
			ConstantExpression method = expression.method as ConstantExpression

			if (classExpression.type.name == "org.spockframework.runtime.SpockRuntime" && method.value == "verifyMethodCondition") {
				if (expression.arguments in ArgumentListExpression) {
					ArgumentListExpression arguments = expression.arguments as ArgumentListExpression
					List<Expression> argumentExpressions = arguments.expressions

					if (argumentExpressions.size() >= 8) {
						Expression verifyMethodConditionMethodArg = argumentExpressions.get(6)
						String methodName = getConstantValueOfType(extractRecordedValueExpression(verifyMethodConditionMethodArg), String)

						if (methodName) {
							Expression verifyMethodConditionArgsArgument = argumentExpressions.get(7)
							if (verifyMethodConditionArgsArgument in ArrayExpression) {

								List<Expression> values = (verifyMethodConditionArgsArgument as ArrayExpression).expressions.collect { Expression argumentExpression ->
									extractRecordedValueExpression(argumentExpression)
								}

								visitSpockValueRecordMethodCall(methodName, values)
							}
						}
					}
				}
			}
		}
	}

	Expression extractRecordedValueExpression(Expression valueRecordExpression) {
		if (valueRecordExpression in MethodCallExpression) {
			MethodCallExpression methodCallExpression = valueRecordExpression as MethodCallExpression

			if (methodCallExpression.arguments in ArgumentListExpression) {
				ArgumentListExpression arguments = methodCallExpression.arguments as ArgumentListExpression

				if (arguments.expressions.size() >= 2) {
					return arguments.expressions.get(1)
				}
			}
		}

		null
	}

	def getConstantValueOfType(Expression expression, Class type) {
		if (expression != null && expression in ConstantExpression) {
			Object value = ((ConstantExpression) expression).value
			type.isInstance(value) ? value : null
		} else {
			null
		}
	}

	void visitSpockValueRecordMethodCall(String name, List<Expression> arguments) {
		if (name == "waitFor") {
			if (!arguments.empty) {
				Expression lastArg = arguments.last()
				if (lastArg instanceof ClosureExpression) {
					transformEachStatement(lastArg as ClosureExpression)
				}
			}
		}
	}

	@Override
	protected SourceUnit getSourceUnit() {
		sourceUnit
	}

	private boolean requiredOptionSpecifiedAsFalse(ArgumentListExpression arguments) {
		MapExpression paramMap = arguments.expressions.find { it in MapExpression }
		paramMap?.mapEntryExpressions.any {
			if (it.keyExpression in ConstantExpression && it.valueExpression in ConstantExpression) {
				ConstantExpression key = it.keyExpression
				ConstantExpression value = it.valueExpression
				key.value == 'required' && value.value == false
			}
		}
	}

	private boolean waitOptionIsSpecified(ArgumentListExpression arguments) {
		MapExpression paramMap = arguments.expressions.find { it in MapExpression }
		paramMap?.mapEntryExpressions.any {
			if (it.keyExpression in ConstantExpression) {
				ConstantExpression key = it.keyExpression
				key.value == 'wait'
			}
		}
	}

	private void visitContentDsl(ClosureExpression closureExpression) {
		BlockStatement blockStatement = closureExpression.code
		blockStatement.statements.each { Statement statement ->
			if (statement in ExpressionStatement) {
				ExpressionStatement expressionStatement = statement
				if (expressionStatement.expression in MethodCallExpression) {
					MethodCallExpression methodCall = expressionStatement.expression
					if (methodCall.arguments in ArgumentListExpression) {
						ArgumentListExpression arguments = methodCall.arguments
						if (lastArgumentIsClosureExpression(arguments) && waitOptionIsSpecified(arguments) && !requiredOptionSpecifiedAsFalse(arguments)) {
							transformEachStatement(arguments.expressions[-1])
						}
					}
				}
			}
		}
	}

	private void transformEachStatement(ClosureExpression closureExpression) {
		BlockStatement blockStatement = closureExpression.code
		ListIterator iterator = blockStatement.statements.listIterator()
		while (iterator.hasNext()) {
			iterator.set(maybeTransform(iterator.next()))
		}
	}

	private Statement maybeTransform(Statement statement) {
		Statement result = statement
		Expression expression = getTransformableExpression(statement)
		if (expression) {
			result = transform(expression, statement)
		}
		return result
	}

	private Expression getTransformableExpression(Statement statement) {
		if (statement in ExpressionStatement) {
			ExpressionStatement expressionStatement = statement
			if (!(expressionStatement.expression in DeclarationExpression)
				&& isTransformable(expressionStatement)) {
				return expressionStatement.expression
			}
		}
	}

	boolean isTransformable(ExpressionStatement statement) {
		if (statement.expression in BinaryExpression) {
			BinaryExpression binaryExpression = statement.expression
			if (ofType(binaryExpression.operation.type, ASSIGNMENT_OPERATOR)) {
				reportError(statement, "Expected a condition, but found an assignment. Did you intend to write '==' ?", sourceUnit)
				false
			}
		}
		true
	}

	private Statement transform(Expression expression, Statement statement) {
		Statement replacement

		Expression recordedValueExpression = createRuntimeCall("recordValue", expression)
		BooleanExpression booleanExpression = new BooleanExpression(recordedValueExpression)

		Statement retrieveRecordedValueStatement = new ExpressionStatement(createRuntimeCall("retrieveRecordedValue"))

		Statement withAssertion = new AssertStatement(booleanExpression)
		withAssertion.setSourcePosition(expression)
		withAssertion.setStatementLabel((String) expression.getNodeMetaData("statementLabel"));

		BlockStatement assertAndRetrieveRecordedValue = new BlockStatement()
		assertAndRetrieveRecordedValue.addStatement(withAssertion)
		assertAndRetrieveRecordedValue.addStatement(retrieveRecordedValueStatement)

		if (expression in MethodCallExpression) {
			MethodCallExpression rewrittenMethodCall = expression

			Statement noAssertion = new ExpressionStatement(expression)
			StaticMethodCallExpression isVoidMethod = createRuntimeCall(
				"isVoidMethod",
				rewrittenMethodCall.objectExpression,
				rewrittenMethodCall.method,
				toArgumentArray(rewrittenMethodCall.arguments)
			)

			replacement = new IfStatement(new BooleanExpression(isVoidMethod), noAssertion, assertAndRetrieveRecordedValue)
		} else {
			replacement = assertAndRetrieveRecordedValue
		}

		replacement.setSourcePosition(statement)
		replacement
	}

	private StaticMethodCallExpression createRuntimeCall(String methodName, Expression... argumentExpressions) {
		ArgumentListExpression argumentListExpression = new ArgumentListExpression()
		for (Expression expression in argumentExpressions) {
			argumentListExpression.addExpression(expression)
		}

		new StaticMethodCallExpression(new ClassNode(Runtime), methodName, argumentListExpression)
	}

	private Expression toArgumentArray(Expression arguments) {
		List<Expression> argumentList
		if (arguments instanceof NamedArgumentListExpression) {
			argumentList = [arguments]
		} else {
			TupleExpression tuple = arguments
			argumentList = tuple.expressions
		}
		List<SpreadExpression> spreadExpressions = argumentList.findAll { it in SpreadExpression }
		if (spreadExpressions) {
			spreadExpressions.each { reportError(it, 'Spread expressions are not allowed here', sourceUnit) }
			return null
		} else {
			new ArrayExpression(ClassHelper.OBJECT_TYPE, argumentList);
		}
	}
}

