package tutorial691.patterns;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import tutorial691.handlers.DetectException;
import tutorial691.handlers.SampleHandler;
import tutorial691.visitors.ExceptionVisitor;
import tutorial691.visitors.LogAndThrowVisitor;
import tutorial691.visitors.MultipleThrowsVisitor;

public class ExceptionFinder {
	
	HashMap<String, HashSet<String>> methodException = new HashMap<String, HashSet<String>>();
	IProject project = null;
	IPackageFragment packageFragment = null;
	static public IJavaProject jProject = null;
	
	public void findExceptions(IProject project) throws JavaModelException {
		this.project = project;
		jProject = JavaCore.create(project);
		IPackageFragment[] packages = jProject.getPackageFragments();
		
		for(IPackageFragment mypackage : packages){
			this.packageFragment = mypackage;
			SampleHandler.printMessage("**********checking package: " + mypackage.getElementName());
			System.out.println("**********checking package: " + mypackage.getElementName());
			checkExceptions(mypackage);
		}
}
	
	private void checkExceptions(IPackageFragment packageFragment) throws JavaModelException {
		for (ICompilationUnit unit : packageFragment.getCompilationUnits()) {
			CompilationUnit parsedCompilationUnit = parse(unit);
			
			// We should build 3 Visitors here and use them one by one.

//			ExceptionVisitor exceptionVisitor = new ExceptionVisitor(this.methodException, project);
//			parsedCompilationUnit.accept(exceptionVisitor);
//			printOverCatchExceptions(exceptionVisitor, parsedCompilationUnit);

//			LogAndThrowVisitor logAndThrowVistor = new LogAndThrowVisitor();
//			parsedCompilationUnit.accept(logAndThrowVistor);
//			printLogAndThrowExceptions(logAndThrowVistor);
//			
			MultipleThrowsVisitor multipleException  = new MultipleThrowsVisitor();
			parsedCompilationUnit.accept(multipleException);
			printMultipleExceptions(multipleException,parsedCompilationUnit);
		} 
	}
	
	private void printMultipleExceptions(MultipleThrowsVisitor visitor, CompilationUnit compilationunit) {

		if(visitor.getMethodName().size() != 0) {
			SampleHandler.printMessage("+++++++++Find multiple exception throw+++++++++");
			System.out.println("+++++++++Find multiple exception throw+++++++++");
			SampleHandler.printMessage("find multiple exception throw in project: \n" + this.project.getName());
			System.out.println("find multiple exception throw in project: \n" + this.project.getName());
			SampleHandler.printMessage("find multiple exception throw in package: \n" + this.packageFragment.getElementName());
			System.out.println("find multiple exception throw in package: \n" + this.packageFragment.getElementName());
		}
		for(MethodDeclaration method:visitor.getMethodName()) {
			SampleHandler.printMessage("find in class: \n" + ((TypeDeclaration)(method.getParent())).getName());
			System.out.println("find in class: \n" + ((TypeDeclaration)(method.getParent())).getName());
			SampleHandler.printMessage("Method: "+method.getName());
			System.out.println("Method: "+method.getName());
			SampleHandler.printMessage("Exception Names: ");
			System.out.println("Exception Names: ");
			for(Object type:method.thrownExceptionTypes()) {
				SampleHandler.printMessage(type.toString());
				System.out.println(type.toString());
			}
		}
		if(visitor.getMethodName().size() != 0) {
			System.out.print("+++++++++++++++++++++++++\n\n");
			SampleHandler.printMessage("+++++++++++++++++++++++++\n\n");
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
	
	private void printOverCatchExceptions(ExceptionVisitor visitor, CompilationUnit parsedCompilationUnit) {
		if(visitor.getTryStatements().size() != 0) {
			SampleHandler.printMessage("==============================================");
			System.out.println("==============================================");
			SampleHandler.printMessage("find overCatch in project: \n" + this.project.getName());
			System.out.println("find overCatch in project: \n" + this.project.getName());
			SampleHandler.printMessage("find overCatch in package: \n" + this.packageFragment.getElementName());
			System.out.println("find overCatch in package: \n" + this.packageFragment.getElementName());
		}
		for(Map.Entry<TryStatement, String> entry: visitor.getTryStatements().entrySet()) {
			TryStatement statement = entry.getKey();
			String tryException = entry.getValue();
			MethodDeclaration methodDeclaration = findMethodForCatch(statement);
			SampleHandler.printMessage("find in class: \n" + ((TypeDeclaration)(methodDeclaration.getParent())).getName());
			System.out.println("find in class: \n" + ((TypeDeclaration)(methodDeclaration.getParent())).getName());
			SampleHandler.printMessage("find in method: \n" + methodDeclaration.getName());
			System.out.println("find in method: \n" + methodDeclaration.getName());
			SampleHandler.printMessage("exceptions in try clause: \n" + tryException);
			System.out.println("exceptions in try clause: \n" + tryException);
			SampleHandler.printMessage("try-catch statement: \n");
			System.out.println("try-catch statement: \n");
			SampleHandler.printMessage(statement.toString());
			System.out.println(statement.toString());
		}
		if(visitor.getTryStatements().size() != 0) {
			SampleHandler.printMessage("==============================================\n\n");
			System.out.println("==============================================\n\n");
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
	
	public static CompilationUnit parse(String classfile) {
		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(classfile.toCharArray());
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
	
	/**
	 * @param project
	 * @param superName
	 * @return
	 * 
	 * There is a serious bug in method: iType.newTypeHierarchy(jProject, new NullProgressMonitor())
	 * and getAllTypes() on this Hierarchy. It always returns incomplete subTypes.
	 * So I traverse every Package that is actually in target workspace to get a complete set of subTypes.
	 */
	static public HashSet<IType> findSubTypes(IProject project, String superName) {
		if(jProject == null) {
			jProject = JavaCore.create(project);
		}
		HashSet<IType> iTypesSet = new HashSet<IType>();
		try {
			IType iType = jProject.findType(superName);
			IPackageFragment[] packages = jProject.getPackageFragments();
			for(IPackageFragment mypackage : packages){
				if(mypackage.getCompilationUnits().length != 0) {
					ITypeHierarchy ih = iType.newTypeHierarchy(mypackage.getCompilationUnits(), DetectException.nullProgressMonitor);
					if(ih != null) {
						IType[] iTypes = ih.getAllSubtypes(iType);
						for(IType t: iTypes) {
							iTypesSet.add(t);
						}
					}
				}
			}
			
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
		return iTypesSet;
	}
}
