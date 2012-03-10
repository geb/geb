package geb.transform.implicitassertions

import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.control.SourceUnit

import geb.transform.implicitassertions.visitor.EvaluatedClosureVisitor

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
class EvaluatedClosureTransformation implements ASTTransformation {
    void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        sourceUnit.AST.classes.each { it.visitContents(new EvaluatedClosureVisitor(sourceUnit)) }
    }
}