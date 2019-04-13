package tutorial691.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;

public class MultipleThrowsVisitor extends ASTVisitor {
	public int numberOfThrowsGeneric = 0;
	public int numberOfMultipleThrows = 0;
	
	
	@Override
	public boolean visit(MethodDeclaration node) {
		List<Type> temp = node.thrownExceptionTypes();
		if (temp.size() > 0) {
			for (Type type : temp) {
				if (type.toString().equals("Exception")) {
					numberOfThrowsGeneric++;
				}
			}
		}
		
		if(temp.size()>1) {
			numberOfMultipleThrows++;
		}
		return super.visit(node);
	}
}
