package test;

import tryCatchPolymorphism.Super;
import tryCatchPolymorphism.SuperInterface;

public class MultipleException {
	
	public void multipleExceptionCatch() { 
		try {
//			int a = 1;
//			System.out.printf("%d", a);
//			System.out.println(1);
//			JavaDoc javaDoc = new JavaDoc();
//			javaDoc.javaDoc1();
//			Object o = new Object();
//			boolean b = javaDoc.equals(o);
//			Test t = new Test();
//			t.testMethodCall();
			SuperInterface sup = new Super();
			int a = sup.test(2);
		}
		catch(NullPointerException ex){
			// M-1
		}
		catch(ClassCastException ex) {
			// M-2
		}
		catch(ArithmeticException | NumberFormatException ex) {
			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void twoException() {
		

	}

}
