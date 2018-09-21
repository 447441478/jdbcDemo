package cn.hncu.proxy;

public class Person implements IPerson {
	
	@Override
	public void sayHello() {
		System.out.println("Hello world");
		double d =-1.0/0;
		System.out.println( d );
	}
	
	public int sum(int a,int b) {
		return a+b;
	}
	
}
