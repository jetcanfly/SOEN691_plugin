package tutorial691.visitors;

import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.Statement;


public class LogAndThrowVisitor extends ASTVisitor{
	HashSet<CatchClause> logAndThrowCathesCatchClauses = new HashSet<>();
	
	public HashSet<CatchClause> getLogAndThrowCathesCatchClauses() {
		return logAndThrowCathesCatchClauses;
	}
	
	@Override
	public boolean visit(CatchClause node) {
		if (isLogAndThrow(node)) {
			logAndThrowCathesCatchClauses.add(node);
		}
		
		return super.visit(node);
	}
	
	private boolean isLogAndThrow(CatchClause node) {
		boolean throwflag = false;
		boolean printstacktraceflag = false;
		boolean logflag = false;
		boolean printflag = false;
		
		List<Statement> statements = node.getBody().statements();		
		for (Statement statement : statements) {
			System.out.println(statement.getClass());
			if (statement.getNodeType() == ASTNode.THROW_STATEMENT) {
				throwflag = true;
			} else {
				if(containsIgnoreCase(statement.toString(), "printStackTrace")) {
					printstacktraceflag = true;
				}
				if(containsIgnoreCase(statement.toString(), "print")){
					printflag = true;
				}
				if( (containsIgnoreCase(statement.toString(), "log") ||
						containsIgnoreCase(statement.toString(), "logger"))) {
						logflag = true;
				}
			}
		}
		
		if (logflag || printstacktraceflag || printflag) {
			if (throwflag) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean containsIgnoreCase(String str, String searchStr) {
		 if(str == null || searchStr == null) return false;

		    final int length = searchStr.length();
		    if (length == 0)
		        return true;

		    for (int i = str.length() - length; i >= 0; i--) {
		        if (str.regionMatches(true, i, searchStr, 0, length))
		            return true;
		    }
		    return false;
	}
}
