package tutorial691.visitors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;


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
	    
		String body = node.getBody().toString();		
		
		boolean throwflag = false;
		boolean printstacktraceflag = false;
		boolean logflag = false;
		boolean printflag = false;

		if(containsIgnoreCase(body, "throw ")){
			throwflag = true;
		}
		if(containsIgnoreCase(body, "printStackTrace")) {
			printstacktraceflag = true;
		}
		if(containsIgnoreCase(body, "print")){
			printflag = true;
		}
		if( (containsIgnoreCase(body, "log") ||
				containsIgnoreCase(body, "logger"))) {
				logflag = true;
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
