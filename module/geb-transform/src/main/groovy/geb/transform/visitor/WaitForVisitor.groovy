package geb.transform.visitor

import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.stmt.AssertStatement
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.ast.expr.*
import static org.codehaus.groovy.syntax.Types.ASSIGNMENT_OPERATOR
import static org.codehaus.groovy.syntax.Types.ofType

import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException

class WaitForVisitor extends ClassCodeVisitorSupport {

	SourceUnit sourceUnit

	WaitForVisitor(SourceUnit sourceUnit) {
		this.sourceUnit = sourceUnit
	}

	void visitExpressionStatement(ExpressionStatement statement) {
		if (statement.expression in MethodCallExpression) {
			MethodCallExpression expression = statement.expression
			if (expression.methodAsString == 'waitFor' && expression.arguments in ArgumentListExpression) {
				ArgumentListExpression arguments = expression.arguments
				if (arguments.expressions && arguments.expressions[-1] in ClosureExpression) {
					rewriteClosureStatements(arguments.expressions[-1])
				}
			}
		}
	}

	protected SourceUnit getSourceUnit() {
		sourceUnit
	}

	private void rewriteClosureStatements(ClosureExpression closureExpression) {
		BlockStatement blockStatement = closureExpression.code
		ListIterator iterator = blockStatement.statements.listIterator()
		while(iterator.hasNext()) {
			iterator.set(rewriteClosureStatement(iterator.next()))
		}
		iterator.add(new ExpressionStatement(new ConstantExpression(true)))
	}

	Statement rewriteClosureStatement(Statement statement) {
		Statement result = statement
		Expression toBeRewritten = getRewrittenExpression(statement)
		if (toBeRewritten) {
			result = new AssertStatement(new BooleanExpression(toBeRewritten))
			result.setSourcePosition(statement)
		}
		return result
	}

	Expression getRewrittenExpression(Statement statement) {
		if (statement in ExpressionStatement) {
			ExpressionStatement expressionStatement = statement
			if (!(expressionStatement.expression in DeclarationExpression)) {
				checkIsValidCondition(expressionStatement)
				return expressionStatement.expression
			}
		}
	}

	void checkIsValidCondition(ExpressionStatement statement) {
		if (statement.expression in BinaryExpression) {
			BinaryExpression binaryExpression = statement.expression
			if (ofType(binaryExpression.operation.type, ASSIGNMENT_OPERATOR)) {
				reportError(statement, "Expected a condition, but found an assignment. Did you intend to write '==' ?")
			}
		}
	}

	private void reportError(Statement statement, String message) {
		def line = statement.lineNumber > 0 ? statement.lineNumber : statement.lastLineNumber
		def column = statement.columnNumber > 0 ? statement.columnNumber : statement.lastColumnNumber
		def errorMessage = new SyntaxErrorMessage(new SyntaxException(message, line, column), sourceUnit)
		sourceUnit.errorCollector.addErrorAndContinue(errorMessage)
	}
}

