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
package geb.transform

import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.ExpressionStatement

import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
class AttributeAccessingMetaClassRegisteringTransformation implements ASTTransformation {
	@Override
	void visit(ASTNode[] nodes, SourceUnit source) {
		source.AST.classes.each {
			if (extendsFromAttributeAccessEnabledClass(it)) {
				changeMetaClassImplementation(it)
			}
		}
	}

	void changeMetaClassImplementation(ClassNode classNode) {
		Statement metaClassRegisterStatement = new ExpressionStatement(
			new StaticMethodCallExpression(
				new ClassNode(AttributeAccessingMetaClassRegistrar),
				'registerFor',
				new ClassExpression(classNode.plainNodeReference)
			)
		)

		classNode.addStaticInitializerStatements([metaClassRegisterStatement], false)
	}

	boolean extendsFromAttributeAccessEnabledClass(ClassNode classNode) {
		boolean isAttributeAccessEnabledClass = false
		ClassNode superClass = classNode.superClass

		while (superClass && !isAttributeAccessEnabledClass) {
			isAttributeAccessEnabledClass = (superClass.name in ['geb.navigator.AbstractNavigator', 'geb.content.TemplateDerivedPageContent'])
			superClass = superClass.superClass
		}

		isAttributeAccessEnabledClass
	}
}
