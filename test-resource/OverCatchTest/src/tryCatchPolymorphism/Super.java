package tryCatchPolymorphism;

import test.Test;

public class Super implements SuperInterface{
	
	public int test(int a) throws ExceptionTest1{
		return 1;
	}
	
	public int test(int a, Test b) throws ExceptionTest2{
		return 1;
	}

}
