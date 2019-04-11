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
	static public HashMap<String, HashMap<String, Integer>> metricMap = new HashMap<String, HashMap<String,Integer>>();
	public int countOverCatchPerFile = 0;  // count how many Overcatch for each file
	public int countOverCatchExitPerFile = 0;  // count how many Overcatch and exit for each file
	
	HashMap<TryStatement, String> overCatchTryStatement = new HashMap<>();
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
		if(exceptionCatchHashSet.size() == 0) {  // mustn't be a overCatch
			return super.visit(node);
		}
		
		Block tryBlock = node.getBody();
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor(methodException, project);
		methodInvocationVisitor.tryStatement = node;
		tryBlock.accept(methodInvocationVisitor);
		exceptionTryHashSet.addAll(methodInvocationVisitor.exceptionTryHashSet);
		if(isOverCatch()) {
			overCatchTryStatement.put(node, exceptionTryHashSet.toString());
			// anti-pattern: overcatch and abort
			// actually there is a little noise. So the data will be manually correct.
			this.countOverCatchExitPerFile += catchVisitor.countOverCatchExit;  
			this.countOverCatchPerFile += 1;  
		}
		clear();
		return super.visit(node);
	}
	
	public HashMap<TryStatement, String> getTryStatements() {
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
	private void clear() {
		exceptionTryHashSet.clear();
		exceptionCatchHashSet.clear();
	}
	
}
