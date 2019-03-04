package tutorial691.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;

public class MultipleThrowsVisitor extends ASTVisitor {
	List<MethodDeclaration> methodName = new ArrayList<>();
	@Override
	public boolean visit(MethodDeclaration node) {
		List<Type> temp = node.thrownExceptionTypes();
		if(temp.size()>1) {
			methodName.add(node);
		}
		return super.visit(node);
	}
	
	public List<MethodDeclaration> getMethodName(){
		return methodName;
	}
}
