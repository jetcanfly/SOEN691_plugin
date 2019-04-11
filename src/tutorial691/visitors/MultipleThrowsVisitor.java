package tutorial691.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;

public class MultipleThrowsVisitor extends ASTVisitor {
	List<MethodDeclaration> methodName = new ArrayList<>();
	static Map<String,Map<String,Integer>> output = new HashMap<>();
	Map<String,Integer> output2 = new HashMap<>();
	int methodsNumber;
	@Override
	public boolean visit(MethodDeclaration node) {
		List<Type> temp = node.thrownExceptionTypes();
		if(temp.size()>1) {
			methodName.add(node);
			methodsNumber++;
		}
		return super.visit(node);
	}
	
	public List<MethodDeclaration> getMethodName(){
		return methodName;
	}
	
	public Integer getMethodsNumber() {
		return methodsNumber;
	}
	
	public Map<String, Integer> getOuput2(){
		output2.put("ThrowKitchenAndSink", methodsNumber);
		return output2;
	}
	
	public Map<String, Map<String, Integer>> getOutput(){
		return output;
	}
}
