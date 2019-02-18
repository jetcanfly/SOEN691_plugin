package tutorial691.patterns;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;

import tutorial691.handlers.SampleHandler;
import tutorial691.visitors.ExceptionVisitor;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;

public class ExceptionFinder {
	
	HashMap<String, HashSet<String>> methodException = new HashMap<String, HashSet<String>>();
	IProject project = null;
	
	public void findExceptions(IProject project) throws JavaModelException {
		this.project = project;
		IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();

		for(IPackageFragment mypackage : packages){
			checkExceptions(mypackage);
		}
}
	
	private void checkExceptions(IPackageFragment packageFragment) throws JavaModelException {
		for (ICompilationUnit unit : packageFragment.getCompilationUnits()) {
			CompilationUnit parsedCompilationUnit = parse(unit);
			
			// We should build 3 Visitors here and use them one by one.
//			ExceptionVisitor exceptionVisitor = new ExceptionVisitor(this.methodException, project);
//			parsedCompilationUnit.accept(exceptionVisitor);
//			printOverCatchExceptions(exceptionVisitor);
		}
	}
	
	private void printOverCatchExceptions(ExceptionVisitor visitor) {
//		if(visitor.getTryStatements().size() != 0) {
//			findMethodForCatch()
//		}
		for(TryStatement statement: visitor.getTryStatements()) {
			MethodDeclaration methodDeclaration = findMethodForCatch(statement);
			SampleHandler.printMessage("find in method: \n" + methodDeclaration.toString());
			System.out.println("find in method: \n" + methodDeclaration.toString());
			SampleHandler.printMessage(statement.toString());
			System.out.println(statement.toString());
		}
	}
	
	private ASTNode findParentMethodDeclaration(ASTNode node) {
		if(node.getParent().getNodeType() == ASTNode.METHOD_DECLARATION) {
			return node.getParent();
		} else {
			return findParentMethodDeclaration(node.getParent());
		}
	}
	
	private MethodDeclaration findMethodForCatch(TryStatement catchClause) {
		return (MethodDeclaration) findParentMethodDeclaration(catchClause);
	}
	
	public static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
}
