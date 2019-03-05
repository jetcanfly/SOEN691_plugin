package tutorial691.visitors;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;


public class MethodDeclarationVisitor extends ASTVisitor {
	public HashSet<String> exceptionSet = new HashSet<String>();
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	private String className = null;
	private String methodName = null;
	private String parameterName = null;
	
	public boolean visit(MethodDeclaration node) {
		TypeDeclaration classDeclaration = findClassForMethod(node);
		String thisClassName = classDeclaration.getName().getFullyQualifiedName();
		String thisMethodName = node.getName().getFullyQualifiedName();
		String thisParaName = "";
		for(Object dec: node.parameters()) {
			thisParaName += ((SingleVariableDeclaration)dec).getType().toString();
		}
		
		if(!className.contains(thisClassName)
				// sometimes we cannot get QualifiedName but only simple name for
				|| !thisMethodName.equals(methodName) 
				|| !thisParaName.equals(parameterName)) {
			return super.visit(node);
		}
		
		for(Object thrownException: node.thrownExceptionTypes()) {
			exceptionSet.add(((Type)thrownException).toString());
		}
		
		Javadoc doc = node.getJavadoc();
		if(doc != null) {	// If method throws exception in JavaDoc.
			List<TagElement> tagList = doc.tags();
			for(TagElement tag: tagList) {
				// The tags @throws and @exception are synonyms.
				if(tag.getTagName() == TagElement.TAG_THROWS || 
						tag.getTagName() == TagElement.TAG_EXCEPTION) {
					Object docName = tag.fragments().get(0);
//					for(Object docName: tag.fragments()) {
					exceptionSet.add(((SimpleName)docName).getFullyQualifiedName());
//					}
				}
			}
		}
		
		return false;  // don't need to visit more in this tree
	}
	
	private ASTNode findParentMethodDeclaration(ASTNode node) {
		if(node.getParent().getNodeType() == ASTNode.TYPE_DECLARATION) {
			return node.getParent();
		} else {
			return findParentMethodDeclaration(node.getParent());
		}
	}
	
	private TypeDeclaration findClassForMethod(MethodDeclaration node) {
		return (TypeDeclaration) findParentMethodDeclaration(node);
	}
}
