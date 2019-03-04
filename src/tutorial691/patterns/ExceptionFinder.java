package tutorial691.patterns;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;

import tutorial691.handlers.SampleHandler;
import tutorial691.visitors.ExceptionVisitor;
import tutorial691.visitors.LogAndThrowVisitor;
import tutorial691.visitors.MultipleThrowsVisitor;

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
			LogAndThrowVisitor logAndThrowVistor = new LogAndThrowVisitor();
			MultipleThrowsVisitor multipleException  = new MultipleThrowsVisitor();
//			parsedCompilationUnit.accept(exceptionVisitor);
//			printOverCatchExceptions(exceptionVisitor);
			parsedCompilationUnit.accept(logAndThrowVistor);
			printLogAndThrowExceptions(logAndThrowVistor);
			parsedCompilationUnit.accept(multipleException);
//			printInformation(packageFragment, unit);
			printMultipleExceptions(multipleException);
		}
	}
	
	private void printLogAndThrowExceptions(LogAndThrowVisitor visitor) {
		for (CatchClause catchClause : visitor.getLogAndThrowCathesCatchClauses()) {
			MethodDeclaration methodDeclaration = findMethodForCatch(catchClause);
			
			SampleHandler.printMessage("find method suffers from Log and Throw: \n" + methodDeclaration.toString());
			System.out.println("find method suffers from Log and Throw: \n" + methodDeclaration.toString());
			SampleHandler.printMessage(catchClause.toString());
			System.out.println(catchClause.toString());
		}
	}
	
	
	private void printInformation(IPackageFragment packageName, ICompilationUnit unit) {
		System.out.println("Package: " + packageName.getElementName());
		SampleHandler.printMessage("Package: " + packageName.getElementName());
		System.out.println("Class: " + unit.getElementName());
		SampleHandler.printMessage("Class: " + (unit.getElementName()));
	}
	
	
	private void printMultipleExceptions(MultipleThrowsVisitor visitor) {
		List<List<Type>> exceptionNames = visitor.getMultipleException();
		List<SimpleName> methodNames = visitor.getMethodName();
		for(int i=0; i<methodNames.size();i++) {
			SampleHandler.printMessage("Method: "+methodNames.get(i).toString());
			System.out.println("Method: "+methodNames.get(i).toString());
			SampleHandler.printMessage("Exception Names: ");
			System.out.println("Exception Names: ");
			for(Object type:exceptionNames.get(i)) {
				SampleHandler.printMessage(type.toString());
				System.out.println(type.toString());
			}
			System.out.print("\n");
			SampleHandler.printMessage("");
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
	
	private MethodDeclaration findMethodForCatch(CatchClause catchClause) {
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
