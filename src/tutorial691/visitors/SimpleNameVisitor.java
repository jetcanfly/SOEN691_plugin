package tutorial691.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SimpleName;

public class SimpleNameVisitor extends ASTVisitor{
	
	public SimpleName simpleName;
	
	@Override
	public boolean visit(SimpleName node) {
		this.simpleName = node;
		return super.visit(node);
	}

}
