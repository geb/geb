package geb.transform

import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.control.SourceUnit

import geb.transform.visitor.WaitForVisitor

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class WaitForArgumentClosureTransformation implements ASTTransformation{
    void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        sourceUnit.AST.classes.each { it.visitContents(new WaitForVisitor(sourceUnit)) }
    }
}