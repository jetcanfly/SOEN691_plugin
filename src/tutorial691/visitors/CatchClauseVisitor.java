package tutorial691.visitors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.UnionType;

public class CatchClauseVisitor extends ASTVisitor {
	public int countOverCatchExit = 0;
	HashSet<String> exceptionType;
	
	public CatchClauseVisitor(HashSet<String> exceptionType) {
		this.exceptionType = exceptionType;
	}
	
	public boolean visit(CatchClause node) {
		SingleVariableDeclaration exception = node.getException();
		Type type = exception.getType();
		if(type.isUnionType()) {
			for(Object eachType: ((UnionType)type).types()) {
				ITypeBinding VB = ((Type)eachType).resolveBinding();
				String exceptionName = VB.getName();
				this.exceptionType.add(exceptionName);
			}
		}
		else {
			ITypeBinding VB = type.resolveBinding();
			String exceptionName = VB.getName();
			this.exceptionType.add(exceptionName);
		}
		
		String content = node.getBody().toString();  // anti-pattern: Over-catch and abort.
		if(content.contains("exit") || content.contains("abort")) {	
			this.countOverCatchExit ++;
		}
		
		return super.visit(node);
	}

}
