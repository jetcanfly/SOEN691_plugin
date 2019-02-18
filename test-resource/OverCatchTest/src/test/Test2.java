package test;
//
public class Test2 {
//	
//	static public void main(String[] args) {
//		int[] a = {1, 2, 3};
//		try {
//			int b = a[2];
//			try {
//				System.out.println(1);
//			}
//			finally {
//				// we have two try
//				
//			}
//		} catch (Exception e) {
//			// check if comments recorded. 
//			// FIXME
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
//	
//	public void testEmptyCatch() {
//		try {
//			int a = 0;
//		}
//		catch (IndexOutOfBoundsException e) {
//			// handle exception
//		}
//	}
//	
	public void testMethodCall2() throws IndexOutOfBoundsException {
		Test test = new Test();
		test.testMethodCall();
		
	}
	

}
