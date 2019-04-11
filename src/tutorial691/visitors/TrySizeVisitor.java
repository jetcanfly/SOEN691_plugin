package tutorial691.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.LineComment;
import java.io.LineNumberReader;

public class TrySizeVisitor extends ASTVisitor{
	int tryQuantity = 0;
	int tryLOC =0;



	
	public boolean visit(TryStatement node) {
		this.tryQuantity++;
		this.getLOC(node);

		return super.visit(node);
	}

	public  void getLOC(TryStatement node) {
		String a = node.getBody().toString();
		for(int i=0; i<a.length();i++) {
			if(a.charAt(i)==('\n')) {
				tryLOC++;
			}	
		}
	}

	public int getTrySize() {
		return tryLOC;
	}
	
	public int getTryQuantity() {
		return tryQuantity;
	}

}
