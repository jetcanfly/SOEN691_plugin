package tutorial691.patterns;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.osgi.framework.util.FilePath;

import tutorial691.handlers.DetectException;
import tutorial691.handlers.SampleHandler;
import tutorial691.visitors.ExceptionVisitor;
import tutorial691.visitors.LogAndThrowVisitor;
import tutorial691.visitors.MultipleThrowsVisitor;

public class ExceptionFinder {

	HashMap<String, HashSet<String>> methodException = new HashMap<String, HashSet<String>>();
	IProject project = null;
	IPackageFragment packageFragment = null;
	ICompilationUnit iCompilationUnit = null;
	String filePath = null;
	static public IJavaProject jProject = null;
	static public HashMap<String, HashSet<IType>> hierachyMap = new HashMap<String, HashSet<IType>>();

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
	
	static public void serializeMap() {
		// Use your map and whatever file path you want.
		try {
			FileOutputStream outStream = new FileOutputStream("G:/OverCatch.txt");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
			objectOutputStream.writeObject(LogAndThrowVisitor.metricMap);  // Hard-code, change to your metrics.
			outStream.close();
			System.out.println("successful");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkExceptions(IPackageFragment packageFragment) throws JavaModelException {
		for (ICompilationUnit unit : packageFragment.getCompilationUnits()) {
			this.iCompilationUnit = unit;
			// get source path
			IResource resource = null;
			try {
				resource = this.iCompilationUnit.getUnderlyingResource();
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				System.out.println("Something wrong1");
			}
			if(resource != null && resource.getType() == IResource.FILE) {
				IFile ifile = (IFile) resource;
				String path = ifile.getRawLocation().toString();
				if(path.contains("src\test") || path.contains("src/test") || !path.contains(".java")) {
					continue;
				}
				if(path.contains("kylin")) {
					this.filePath = path.substring(path.indexOf("kylin") + 6);
					SampleHandler.printMessage("**********checking file: \n" + path);
					System.out.println("**********checking file: \n" + path);
				}
				else {
					System.out.println("Something wrong2");
				}
			}
			CompilationUnit parsedCompilationUnit = parse(unit);

			// We should build 3 Visitors here and use them one by one.
			
//			ExceptionVisitor exceptionVisitor = new ExceptionVisitor(this.methodException, project);
//			parsedCompilationUnit.accept(exceptionVisitor);
//			printOverCatchExceptions(exceptionVisitor, parsedCompilationUnit);

						LogAndThrowVisitor logAndThrowVistor = new LogAndThrowVisitor();
						parsedCompilationUnit.accept(logAndThrowVistor);
						printLogAndThrowExceptions(logAndThrowVistor);
						
//						MultipleThrowsVisitor multipleException  = new MultipleThrowsVisitor();
//						parsedCompilationUnit.accept(multipleException);
//						printMultipleExceptions(multipleException);
		} 
	}

	private void printMultipleExceptions(MultipleThrowsVisitor visitor) {
		HashMap<String, Integer> fileMap = new HashMap<String, Integer>();
		fileMap.put("thorwsGeneric", visitor.numberOfThrowsGeneric);
		fileMap.put("multipleThrows", visitor.numberOfMultipleThrows);
		
		LogAndThrowVisitor.metricMap.put(this.filePath, fileMap);
	}

	private void printLogAndThrowExceptions(LogAndThrowVisitor visitor) {
		HashMap<String, Integer> fileMap = new HashMap<String, Integer>();
		fileMap.put("logAndThrow", visitor.numberOfLogAndThrow);
		fileMap.put("catchQuantity", visitor.numberOfCatch);
		fileMap.put("catchAndDoNothing", visitor.numberOfCatchAndDoNothing);
		fileMap.put("catchAndReturnNull", visitor.numberOfCatchAndReturnNull);
		fileMap.put("catchGeneric", visitor.numberOfCatchGeneric);
		fileMap.put("dummyHandle", visitor.numberOfDummyHandle);
		fileMap.put("destructiveWrapping", visitor.numberOfDestructiveWrapping);
		fileMap.put("logAndReturnNull", visitor.numberOfLogAndReturnNull);
		fileMap.put("multiLineLog", visitor.numberOfMultiLineLog);
		fileMap.put("relyingOnGetCause", visitor.numberOfRelyingOnGetCause);
		
		
		LogAndThrowVisitor.metricMap.put(this.filePath, fileMap);	
	}

	private void printOverCatchExceptions(ExceptionVisitor visitor, CompilationUnit parsedCompilationUnit) {
		if(visitor.getTryStatements().size() != 0) {
			SampleHandler.printMessage("==============================================");
			System.out.println("==============================================");
			SampleHandler.printMessage("find overCatch in project: \n" + this.project.getName());
			System.out.println("find overCatch in project: \n" + this.project.getName());
			SampleHandler.printMessage("find overCatch in package: \n" + this.packageFragment.getElementName());
			System.out.println("find overCatch in package: \n" + this.packageFragment.getElementName());
			SampleHandler.printMessage("find overCatch in file: \n" + this.filePath);
			System.out.println("find overCatch in file: \n" + this.filePath);
			HashMap<String, Integer> fileMap = new HashMap<String, Integer>();
			fileMap.put("overCatch", visitor.countOverCatchPerFile);
			fileMap.put("overCatchExit", visitor.countOverCatchExitPerFile);
			ExceptionVisitor.metricMap.put(this.filePath, fileMap);
		}
		for(Map.Entry<TryStatement, String> entry: visitor.getTryStatements().entrySet()) {
			TryStatement statement = entry.getKey();
			String tryException = entry.getValue();

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
		if(node.getParent() == null) {
			return null;
		}
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
		System.out.println("	findSubTypes " + superName);
		if(hierachyMap.containsKey(superName)) {
			return hierachyMap.get(superName);
		}

		if(jProject == null) {
			jProject = JavaCore.create(project);
		}
		HashSet<IType> iTypesSet = new HashSet<IType>();
		if(!superName.startsWith("io.bootique.")) {  // a hardcode for efficiency.
			return iTypesSet;
		}
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
		System.out.println("	over findSubTypes " + superName);
		hierachyMap.put(superName, iTypesSet);
		return iTypesSet;
	}
}
