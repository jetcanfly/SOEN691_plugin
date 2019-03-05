package tutorial691.visitors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.ui.activities.IActivityListener;


public class ExceptionVisitor extends ASTVisitor{
	HashSet<TryStatement> overCatchTryStatement = new HashSet<>();
	HashSet<String> exceptionCatchHashSet = new HashSet<>();
	public HashSet<String> exceptionTryHashSet = new HashSet<>();
	HashMap<String, HashSet<String>> methodException;  // Given
	public IProject project;
	
	public ExceptionVisitor(HashMap<String, HashSet<String>> fileException) {
		this.methodException = fileException;
	}
	
	public ExceptionVisitor(HashMap<String, HashSet<String>> methodException, IProject project) {
		this.methodException = methodException;
		this.project = project;
	}
	
	@Override
	public boolean visit(TryStatement node) {
		CatchClauseVisitor catchVisitor = new CatchClauseVisitor(exceptionCatchHashSet);
		List<CatchClause> clauseList= node.catchClauses();
		for(CatchClause catchClause: clauseList) {
			catchClause.accept(catchVisitor);
		}
		
		Block tryBlock = node.getBody();
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor(methodException, project);
		tryBlock.accept(methodInvocationVisitor);
		exceptionTryHashSet.addAll(methodInvocationVisitor.exceptionTryHashSet);
		if(isOverCatch()) {
			overCatchTryStatement.add(node);
		}
		return super.visit(node);
	}
	
	public HashSet<TryStatement> getTryStatements() {
		return overCatchTryStatement;
	}
	
	public boolean isOverCatch() {
		int count = 0;
		for(String exception: exceptionTryHashSet) {
			if(exceptionCatchHashSet.contains(exception)) {
				count ++;
			}
		}
		return count < exceptionCatchHashSet.size()? true: false;
	}

}
