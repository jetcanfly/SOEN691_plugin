package tutorial691.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TryStatement;

import tutorial691.handlers.DetectException;
import tutorial691.patterns.ExceptionFinder;

public class MethodInvocationVisitor extends ASTVisitor {
	
	public HashMap<String, HashSet<String>> methodException;
	public HashSet<String> exceptionTryHashSet = new HashSet<String>();
	public CompilationUnit sourceFileAST;
	private IProject project;
	public int recursionLevel = 0;  // set a recursion threshold in case too much recursion leads to memory running-out
	public TryStatement tryStatement = null;
	
	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public MethodInvocationVisitor(HashMap<String, HashSet<String>> methodException, IProject project) {
		this.methodException = methodException;
		this.project = project;
	}
	
	public MethodInvocationVisitor(HashMap<String, HashSet<String>> methodException, HashSet<String> exceptionSet) {
		this.methodException = methodException;
		this.exceptionTryHashSet = exceptionTryHashSet;
	}
	
	public MethodInvocationVisitor(HashMap<String, HashSet<String>> methodException, CompilationUnit sourceFileAST, HashSet<String> exceptionTryHashSet) {
		this.methodException = methodException;
		this.exceptionTryHashSet = exceptionTryHashSet;
		this.sourceFileAST = sourceFileAST;
	}
	
	public boolean visit(MethodInvocation node) {
		if(findTryStatementForMethodInvocation(node) != null && 
				this.tryStatement != findTryStatementForMethodInvocation(node)){
			return super.visit(node);
		}
		System.out.println("		...." + node.getName());
		IMethodBinding iMethodBinding = node.resolveMethodBinding();
		IMethodBinding iMethodDec = iMethodBinding.getMethodDeclaration();
		String classNameString = iMethodDec.getDeclaringClass().getQualifiedName();
		String formalParameter = "";
		for(ITypeBinding type: iMethodDec.getParameterTypes()) {
			formalParameter += type.getName();
		}
		String methodName = node.getName().getFullyQualifiedName();
		String qualifiedName = classNameString + "-" + methodName + "-" + formalParameter;  // to make sure it's unique
		if(methodException.containsKey(qualifiedName) && methodException.get(qualifiedName) != null) {
			return super.visit(node);  // this method has been traversed.
		}
		if(methodException.containsKey(qualifiedName) && methodException.get(qualifiedName) == null) {
			return super.visit(node);  // this method is being traversed.
		}
		methodException.put(qualifiedName, null);
		
		HashSet<String> exceptionSet = new HashSet<String>();
		for(ITypeBinding type: iMethodBinding.getExceptionTypes()) {  // If method throws exception
			String exception = type.getName();
			exceptionSet.add(exception);
		}
		
		ICompilationUnit unit = (ICompilationUnit) iMethodBinding.getJavaElement().getAncestor( IJavaElement.COMPILATION_UNIT );
		if(unit != null) {
			// if not find source code. skip Javadoc, skip traverse
			// We mostly care methods in target project. Third party without source code wouldn't be handled.
			MethodDeclaration decl = FindDeclarationInSource(unit, iMethodBinding);
			if(decl != null) {
				Javadoc doc = decl.getJavadoc();
				if(doc != null) {	// If method throws exception in JavaDoc.
					List<TagElement> tagList = doc.tags();
					for(TagElement tag: tagList) {
						// The tags @throws and @exception are synonyms.
						if(tag.getTagName() == TagElement.TAG_THROWS || 
								tag.getTagName() == TagElement.TAG_EXCEPTION) {
							Object docName = tag.fragments().get(0);
							exceptionSet.add(((SimpleName)docName).getFullyQualifiedName());
						}
					}
				}

				// go into the method declaration body to find exception.
				Block mBody = decl.getBody();
				if(null != mBody && this.recursionLevel < 3) {
					MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor(methodException, this.project);
					methodInvocationVisitor.recursionLevel = this.recursionLevel + 1;
					mBody.accept(methodInvocationVisitor);
					exceptionSet.addAll(methodInvocationVisitor.exceptionTryHashSet);
				}
			}
		}
		
		// for methodcall that only has classfile not source code.
		IClassFile classFile = (IClassFile)iMethodBinding.getJavaElement().getAncestor( IJavaElement.CLASS_FILE );
		if(classFile != null) {
			CompilationUnit unit2 = null;
			try {
				unit2 = ExceptionFinder.parse(classFile.getSource());
				MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
				visitor.setClassName(classNameString);
				visitor.setMethodName(methodName);
				visitor.setParameterName(formalParameter);
				unit2.accept(visitor);
				exceptionSet.addAll(visitor.exceptionSet);
			}
			catch (Exception e) {
				System.out.println("cannot parse classfile " + classFile.getElementName());
			}
		}
		
		exceptionTryHashSet.addAll(exceptionSet);
		methodException.put(qualifiedName, exceptionSet);
		
		addExceptionThroughPolymorphism(classNameString, iMethodDec, iMethodBinding);
		
		return super.visit(node);
	}
	
	public MethodDeclaration FindDeclarationInSource(ICompilationUnit unit, IMethodBinding binding) {
		CompilationUnit cUnit = ExceptionFinder.parse(unit);
		return (MethodDeclaration)cUnit.findDeclaringNode(binding.getKey());
	}
	
	public void addExceptionThroughPolymorphism(String classNameString, IMethodBinding iMethodDec, 
			IMethodBinding iMethodBinding) {
		
		/* For the sake of polymorphism
		 * For Example:
		SomeInterface a = getfromSomeMethod();
		or:
		SomeSubClass a = getfromSomeMethod();
		Try{
			a.test();
		}
		According to Prof. Shang, we should take polymorphsim into consideration
		no matter if the program really uses polymorphsim in the runtime.
		That means: all the subtypes(including a's sub interface, sub class, 
		descendant interface, descendant class) should be considered.
		So we go through all the methods which has same method signature of a's subTypes.
		*/
		
		HashSet<IType> iTypesSet = ExceptionFinder.findSubTypes(project, classNameString);
		if(iTypesSet.size() != 0) {
			ArrayList<String> parameters = new ArrayList<String>();
			for(ITypeBinding type: iMethodDec.getParameterTypes()) {
				parameters.add(type.getBinaryName());
			}
			for(IType type : iTypesSet) {
				/*
				 * get method which has same name and parameters because 
				 * there might be same name method with different parameters.
				 */
				IMethod overrideMethod = type.getMethod(iMethodBinding.getName(), parameters.toArray(new String[] {}));
				/*
				 * if it throws any exception, directly add it to TrySet.
				 * 
				 */
				try {
					for(String ex: overrideMethod.getExceptionTypes()) { 
						
						// It's from source code. For UnresolvedClassTypeSignature, will be 
						// start with a "Q"
						if(ex.indexOf(";") != -1) {
							ex = ex.substring(0, ex.length()-1);
						}
						exceptionTryHashSet.add(ex.substring(1));  
					}
				} catch (JavaModelException e) {
					// do nothing
				}
			}
		}
		
	}
	
	private ASTNode findTryStatementForMethodInvocation(ASTNode node) {
		if(node == null || node.getParent() == null) {
			return null;
		}
		if(node.getParent().getNodeType() == ASTNode.TRY_STATEMENT) {
			if(((TryStatement)node.getParent()).getFinally() == node) {
				return null;
			}
			return node.getParent();
		} else {
			return findTryStatementForMethodInvocation(node.getParent());
		}
	}

}
