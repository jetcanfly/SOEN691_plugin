package tutorial691.visitors;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;


public class LogAndThrowVisitor extends ASTVisitor{
	static public HashMap<String, HashMap<String, Integer>> metricMap = new HashMap<String, HashMap<String,Integer>>();
	public int numberOfLogAndThrow = 0;
	public int numberOfCatch = 0;
	public int numberOfCatchAndDoNothing = 0;
	public int numberOfCatchAndReturnNull = 0;
	public int numberOfCatchGeneric = 0;
	public int numberOfDummyHandle = 0;
	public int numberOfDestructiveWrapping = 0;
	public int numberOfLogAndReturnNull = 0;	
	
	
	public HashSet<CatchClause> logAndThrowCathesCatchClauses = new HashSet<>();
		
	@Override
	public boolean visit(final CatchClause node) {
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
					this.numberOfCatchAndReturnNull++;
				}
			}
		}
		
		List<String> exceptionList = new ArrayList<String>();
		// *** Catch generic ***
		Type exceptionType = node.getException().getType();
		if(exceptionType.isUnionType()) {
			for(Object eachType: ((UnionType)exceptionType).types()) {
				String exceptionName = eachType.toString();
				exceptionList.add(exceptionName);
				if (exceptionName.equals("Exception")) {
					this.numberOfCatchGeneric++;
				}
			}	
		} else {
			String exceptionName = exceptionType.toString();
			exceptionList.add(exceptionName);
			if (exceptionName.equals("Exception")) {
				this.numberOfCatchGeneric++;
			}
		}
		
		// *** Destructive Wrapping ***
		String arumentsName = node.getException().resolveBinding().getName();
		for (Statement statement : statements) {
			if (statement.getNodeType() == ASTNode.THROW_STATEMENT) {
				Expression expression = (Expression) ((ThrowStatement) statement).getExpression();
				if (expression instanceof ClassInstanceCreation) {
					List<Expression> argumentList = ((ClassInstanceCreation) expression).arguments();
					for (Expression argument : argumentList) {
						if (argument.toString().equals(arumentsName)) {
							this.numberOfDestructiveWrapping++;
						}
					}
				}
				
			}
		}
		
		// *** Dummy Handle ***
		for (Statement statement : statements) {
			if (this.containsIgnoreCase(statement.toString(), "log") || containsIgnoreCase(statement.toString(), "logger")
					|| containsIgnoreCase(statement.toString(), "print") || containsIgnoreCase(statement.toString(), "printStackTrace")) {
				continue;
			} else {
				this.numberOfDummyHandle++;
				break;
			}
		}
		
		// *** Ignoring InterruptedException ***
		
		
		//  *** Incomplete Implementation ***
		
		
		// *** Log and Return Null ***
		if (isLogAndReturnNull(node)) {
			this.numberOfLogAndReturnNull++;
		}
		
		
//		try {
//				
//		} catch (IOException | SQLException e2) {
//			return null;
//		}

		
		// *** log and throw ***
		if (isLogAndThrow(node)) {
			logAndThrowCathesCatchClauses.add(node);
			this.numberOfLogAndThrow++;
		}
		
		// *** Multi-Line Log ***
		
		
		// *** Nested Try ***
		
		
		// *** Relying on getCause() ***
		
		
		
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
	
	private boolean isLogAndReturnNull(CatchClause node) {
		boolean returnNullflag = false;
		boolean printstacktraceflag = false;
		boolean logflag = false;
		boolean printflag = false;
		
		String body = node.getBody().toString();
	
		if (this.containsIgnoreCase(body, "return null;")) {
			returnNullflag = true;
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
			if (returnNullflag) {
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
