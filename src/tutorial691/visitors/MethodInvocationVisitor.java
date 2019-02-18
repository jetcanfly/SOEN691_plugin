package tutorial691.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IProject;
//import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
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

import tutorial691.handlers.DetectException;
import tutorial691.patterns.ExceptionFinder;

public class MethodInvocationVisitor extends ASTVisitor {
	
	public HashMap<String, HashSet<String>> methodException;
//	public HashSet<String> exceptionSet = new HashSet<String>();
	public HashSet<String> exceptionTryHashSet = new HashSet<String>();
	public CompilationUnit sourceFileAST;
	private IProject project;
	
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
		
//		SimpleNameVisitor sn = new SimpleNameVisitor();  
//		node.accept(sn);
		
		IMethodBinding iMethodBinding = node.resolveMethodBinding();
		IMethodBinding iMethodDec = iMethodBinding.getMethodDeclaration();
		String classNameString = iMethodDec.getDeclaringClass().getQualifiedName();
		String formalParameter = "";
		for(ITypeBinding type: iMethodDec.getParameterTypes()) {
			formalParameter += type.getQualifiedName();
		}
		String methodName = node.getName().getFullyQualifiedName();
//		String qualifiedName = iMethodBinding.getKey();
		String qualifiedName = classNameString + "-" + methodName + "-" + formalParameter;  // to make sure it's unique
		if(methodException.containsKey(qualifiedName) && methodException.get(qualifiedName) != null) {
			return super.visit(node);  // this method has been traversed.
		}
		if(methodException.containsKey(qualifiedName) && methodException.get(qualifiedName) == null) {
			return super.visit(node);  // this method is being traversed.
		}
		methodException.put(qualifiedName, null);
		
		HashSet<String> exceptionSet = new HashSet<String>();
//		iMethodBinding.getName();
		for(ITypeBinding type: iMethodBinding.getExceptionTypes()) {  // If method throws exception
			String exception = type.getName();
			exceptionSet.add(exception);
		}
		
//		IMethodBinding binding = (IMethodBinding) node.getName().resolveBinding();
		ICompilationUnit unit = (ICompilationUnit) iMethodBinding.getJavaElement().getAncestor( IJavaElement.COMPILATION_UNIT );
//		IMethod iMethod = (IMethod) iMethodBinding.getJavaElement();
		if(unit != null) {
			// if not find source code. skip Javadoc, skip traverse
			// We mostly care methods in target project. Third party without source code wouldn't be handled.
			MethodDeclaration decl = FindDeclarationInSource(unit, iMethodBinding);
			Javadoc doc = decl.getJavadoc();
			if(doc != null) {	// If method throws exception in JavaDoc.
				List<TagElement> tagList = doc.tags();
				for(TagElement tag: tagList) {
					// The tags @throws and @exception are synonyms.
					if(tag.getTagName() == TagElement.TAG_THROWS || 
							tag.getTagName() == TagElement.TAG_EXCEPTION) {
						Object docName = tag.fragments().get(0);
//						for(Object docName: tag.fragments()) {
						exceptionSet.add(((SimpleName)docName).getFullyQualifiedName());
//						}
					}
				}
			}
			// go into the method declaration body to find exception.
			Block mBody = decl.getBody();
			if(null != mBody) {
				MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor(methodException, this.project);
				mBody.accept(methodInvocationVisitor);
				exceptionSet.addAll(methodInvocationVisitor.exceptionTryHashSet);
			}
		}
		
		exceptionTryHashSet.addAll(exceptionSet);
		methodException.put(qualifiedName, exceptionSet);
		
		addExceptionThroughPolymorphism(classNameString, iMethodDec, iMethodBinding);
		
		return super.visit(node);
	}
	
	public MethodDeclaration FindDeclarationInSource(ICompilationUnit unit, IMethodBinding binding) {
//		ASTParser parser = ASTParser.newParser(AST.JLS11);
//		parser.setKind(ASTParser.K_COMPILATION_UNIT);
//		parser.setSource(unit);
//		parser.setResolveBindings(true);
//		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
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
		
		HashSet<IType> iTypesSet = DetectException.findSubTypes(project, classNameString);
		if(iTypesSet.size() != 0) {
			ArrayList<String> parameters = new ArrayList<String>();
			for(ITypeBinding type: iMethodDec.getParameterTypes()) {
				parameters.add(type.getBinaryName());
			}
			for(IType type : iTypesSet) {
				IMethod overrideMethod = type.getMethod(iMethodBinding.getName(), parameters.toArray(new String[] {}));
				/*
				 * if it throws any exception, directly add it to TrySet.
				 * 
				 */
				try {
					for(String ex: overrideMethod.getExceptionTypes()) { 
//						String[] tmp = Signature.getTypeParameters(ex);
						
						// It's from source code. For UnresolvedClassTypeSignature, will be 
						// start with a "Q"
						if(ex.indexOf(";") != -1) {
							ex = ex.substring(0, ex.length());
						}
						exceptionTryHashSet.add(ex.substring(1));  
					}
//					overrideMethod.getAttachedJavadoc(null);  
//					This should be used only for binary elements. 
//					Source elements will always return null.
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
			}
		}
		
	}

}
