package tutorial691.visitors;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.Statement;


public class LogAndThrowVisitor extends ASTVisitor{
	static public HashMap<String, HashMap<String, Integer>> metricMap = new HashMap<String, HashMap<String,Integer>>();
	public int numberOfLogAndThrow = 0;
	public int numberOfCatch = 0;
	public int numberOfCatchAndDoNothing = 0;
	public int numberOfCatchAndReturnNull = 0;
	
	
	
	
	
	
	
	
	

	public HashSet<CatchClause> logAndThrowCathesCatchClauses = new HashSet<>();
		
	@Override
	public boolean visit(CatchClause node) {
		// *** Catch Quantity ***
		this.numberOfCatch++;
		
		
		List<Statement> statements = node.getBody().statements();		
		// *** Catch and Do Nothing ***
		if (statements.size() == 0) {
			this.numberOfCatchAndDoNothing++;
		}
		
		// *** Catch and Return Null ***
		for (Statement statement : statements) {
			if (statement.getNodeType() == ASTNode.RETURN_STATEMENT) {
				if (containsIgnoreCase(statement.toString(), "null")) {
					numberOfCatchAndReturnNull++;
				}
			}
		}
		
		// *** Catch generic ***
//		if (isCatchGeneric(node)) {
//			catchGeneric.add(node);
//		}
		
		// *** Destructive Wrapping ***
		
		
		// *** Dummy Handle ***
		
		
		// *** Ignoring InterruptedException ***
		
		
		//  *** Incomplete Implementation ***
		
		// *** Log and Return Null ***
		
//		try {
//				
//		} catch (SQLClientInfoException | IOException e2) {
//			
//		}
		
		
		// *** log and throw ***
		if (isLogAndThrow(node)) {
			logAndThrowCathesCatchClauses.add(node);
			this.numberOfLogAndThrow++;
		}
		
		// *** Multi-Line Log ***
		
		// *** Nested Try ***
		
		// *** Relying on getCause() ***
		
		// *** Throw within Finally ***
		
		// *** Throws Generic ***
		
		// *** Throws Kitchen Sink ***
		
		return super.visit(node);
	}
	
	private boolean isLogAndThrow(CatchClause node) {
		boolean throwflag = false;
		boolean printstacktraceflag = false;
		boolean logflag = false;
		boolean printflag = false;
		
		String body = node.getBody().toString();
		if (this.containsIgnoreCase(body, "throw")) {
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
