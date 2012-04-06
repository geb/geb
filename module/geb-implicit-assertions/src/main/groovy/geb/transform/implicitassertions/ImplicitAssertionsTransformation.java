package geb.transform.implicitassertions;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.control.SourceUnit;

import geb.transform.implicitassertions.visitor.EvaluatedClosureVisitor;

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class ImplicitAssertionsTransformation implements ASTTransformation {
    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
		for (ClassNode classNode : sourceUnit.getAST().getClasses()) {
			classNode.visit(new EvaluatedClosureVisitor(sourceUnit));
		}
    }
}