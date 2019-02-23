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
		//System.out.println("CATCH CLAUSE BODY: " + body);
		//System.out.println("CATCH CLAUSE : " + node);
		
		boolean throwflag = false;
		boolean printstacktraceflag = false;
		boolean logflag = false;
		boolean sysoutflag = false;

		if(body.contains("throw ")){
			//System.out.println("****** CONTAINS THROW ******");
			throwflag = true;
		}
		if(body.contains("printStackTrace")){
			//System.out.println("****** CONTAINS PRINT STACK TRACE ******");
			printstacktraceflag = true;
		}
		if(body.contains("System.out.print")){
			//System.out.println("****** CONTAINS PRINT System.out.print ******");
			sysoutflag = true;
		}
		if(throwflag && printstacktraceflag){
			System.out.println("****** CONTAINS PRINT STACK TRACE and throw ******");
		}
		if( (body.contains("log.error")) ||
			    (body.contains("log.info")) ||
			    (body.contains("log.warn")) ||
			    (body.contains("log.debug")) ||
			    (body.contains("log.trace")) ||
			    (body.contains("log.fatal")) ||
			    (body.contains("logger.trace")) ||
			    (body.contains("logger.fatal")) ||
			    (body.contains("logger.error")) ||
			    (body.contains("logger.info")) ||
			    (body.contains("logger.warn")) ||
			    (body.contains("logger.debug")) ) {
				//System.out.println("****** CONTAINS LOG ******");
				logflag = true;
		}
		if(throwflag && logflag){
			System.out.println("****** CONTAINS Log and throw ******");
		}
		if (throwflag && sysoutflag) {
			System.out.println("****** CONTAINS System.out.print and throw ******");
		}
		
		if (logflag || printstacktraceflag || sysoutflag) {
			if (throwflag) {
				return true;
			}
		}
		
		return false;
	}
}
