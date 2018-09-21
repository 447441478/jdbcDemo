package cn.hncu.proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;

/*
 * 演示动态代理
 */
public class ProxyDemo {
	
	//回顾：类反射调用一个方法
	@Test
	public void demo1() throws Exception {
		
		/*
		 * 实现:
		 * IPerson p = new Person();
		 * int sum = p.sum(10,20);
		 */
		//1.获取类模版
		Class<Person> clazz = Person.class;
		//2.获取想要调用的方法对象
		//第一个参数：方法名，第二个参数：该方法的参数类型
		Method method = clazz.getMethod("sum", new Class[]{ int.class,int.class});
		//3.获取调用该方法的对象
		//3.1获取空参构造器
		Constructor<Person> constructor = clazz.getConstructor(new Class[]{});
		//3.2 通过空参构造器创建一个空参实例
		Person obj = constructor.newInstance(new Object[]{});
		//4.调用该方法
		Object[] args = {10,20}; //参数
		Object returnValue = method.invoke(obj, args);
		System.out.println("a+b="+returnValue); 
	}
	
	//演示动态代理
	@Test
	public void dynamicProxy() {
		final Person p = new Person();
		//技术入口： java.lang.reflect.Proxy类的newProxyInstance方法
		Object proxiedObj = Proxy.newProxyInstance(
								ProxyDemo.class.getClassLoader(), 
								new Class[]{IPerson.class}, 
								new InvocationHandler() {
									//参数1：代理对象，与proxiedObj相同，参数2：被调用的方法对象，参数3：被调用的方法的参数
									@Override
									public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
										/* 当 person 代理对象调用sayHello()方法时，
										 * 就会调用到 invoke 这个方法
										 */
										
										System.out.println("代理对象，在前面做了一些功能");
										
										//如果只有下面这一句，则采用的原型对象 p 的 sayHello()方法
										Object returnValue = method.invoke(p, args); 
										
										//如果不满意的花可以补充些东西或者不调用 原型对象的方法
										
										System.out.println("代理对象，在后面做了一些功能");
										
										return returnValue;
									}
								});
		IPerson person = (IPerson) proxiedObj;
		person.sayHello();
	}
	
}
