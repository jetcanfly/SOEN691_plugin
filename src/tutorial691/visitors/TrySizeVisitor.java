package tutorial691.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
	int tryCondition =0;
	int tryLoop =0;
//	List<MethodDeclaration> methodName = new ArrayList<>();
	public static Map<String,Map<String,Integer>> output = new HashMap<>();
	Map<String,Integer> output2 = new HashMap<>();
	
	public boolean visit(TryStatement node) {
		this.tryQuantity++;
		this.getLOC(node);
		this.getConditionInTry(node);
		this.getLoopInTry(node);
		return super.visit(node);
	}

	public  void getConditionInTry(TryStatement node) {
		
		getIfInTry(node);
		getSwitchInTry(node);
	}

	public  void getLoopInTry(TryStatement node) {
		getWhileInTry(node);
		getForInTry(node);
	}
	
	public void getWhileInTry(TryStatement node) {
		String a = node.getBody().toString();
		for(int i=0; i<a.length()-5;i++) {
			String tempWhile = a.substring(0+i,6+i);
			if(tempWhile.equals("while ")) {
				tryLoop++;
			}
		}
	}
	
	public void getForInTry(TryStatement node) {
		String a = node.getBody().toString();
		for(int i=0; i<a.length()-3;i++) {
			String tempFor = a.substring(0+i,4+i);
			if(tempFor.equals("for ")) {
				tryLoop++;
			}
		}
	}
	
	public void getIfInTry(TryStatement node) {
		String a = node.getBody().toString();
		for(int i=0; i<a.length()-2;i++) {
			String tempif = a.substring(0+i,3+i);
			if(tempif.equals("if ")) {
				tryCondition++;
			}
		}
	}
	
	public void getSwitchInTry(TryStatement node) {
		String a = node.getBody().toString();
		for(int i=0; i<a.length()-6;i++) {
			String tempSwitch = a.substring(0+i,7+i);
			if(tempSwitch.equals("switch ")) {
				tryCondition++;
			}
		}
	}
	public  void getLOC(TryStatement node) {
		String a = node.getBody().toString();
		for(int i=0; i<a.length();i++) {
			if(a.charAt(i)==('\n')) {
				tryLOC++;
			}	
		}
	}

	public int getTryCondition() {
		return tryCondition;
	}
	
	public int getTrySize() {
		return tryLOC;
	}
	
	public int getTryQuantity() {
		return tryQuantity;
	}

	public int getTryLoop() {
		return tryLoop;
	}
	public Map<String, Integer> getOutput2(){
		output2.put("TryQuantity", tryQuantity);
		output2.put("TryLOC", tryLOC);
		output2.put("TryCondition", tryCondition);
		output2.put("TryLoop", tryLoop);
		return output2;
	}
	public Map<String, Map<String, Integer>> getOutput(){
		return output;
	}
}
