package test;
//
//
//class Point{
//	public boolean equals(final Object anObject) {
//		System.out.println("One");
//		return false;
//	}
//	public boolean equals(final Point aPoint) {
//		System.out.println("Another");
//		return false;
//	}
//}
//
//class SuperClass {
//	Object getSomething() {
//		return new Object();
//	}
//}
//class SubClass extends SuperClass {
//	String getSomething() {
//		return new String();
//	}
//}
//
public class Test {
	
//	static public void main(String[] args) {
//		SuperClass sc = new SubClass();
//		sc.getSomething();
//		
//		int[] a = {1, 2, 3};
//		try {
//			int b = a[3];
//		} catch (Exception e) {
//			System.out.println("Got it");
//		}
//		
//		final Point p1 = new Point();
//		final Point p2 = new Point();
//		final Object o = p1;
////		System.out.println(o.equals(p2));
//		System.out.println(((Point)o).equals(p2));
//			
//		
//	}
	
	/**
	 * @throws NullPointerException2
	 */
	public void testMethodCall() throws ClassCastException{
		Test2 test2 = new Test2();
		test2.testMethodCall2();
	}

}
