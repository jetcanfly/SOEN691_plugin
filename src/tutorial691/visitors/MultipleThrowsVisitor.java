package tutorial691.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;

public class MultipleThrowsVisitor extends ASTVisitor {
	List<List<Type>> multipleException = new ArrayList<>();
	List<SimpleName> methodName = new ArrayList<>();
	@Override
	public boolean visit(MethodDeclaration node) {
		List<Type> temp = node.thrownExceptionTypes();
		if(temp.size()>1) {
			multipleException.add(node.thrownExceptionTypes());
			methodName.add(node.getName());
		}
		return super.visit(node);
	}
	
	public List<List<Type>> getMultipleException(){
		return multipleException;
	}
	
	public List<SimpleName> getMethodName(){
		return methodName;
	}
}
