package cn.hncu.proxy.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理工具
 * 
 * CreateTime: 2018年9月20日 下午3:03:15	
 * @author 宋进宇  Email:447441478@qq.com
 */
public class ProxyUtil {

	//私有化构造函数
	private ProxyUtil() {}
	
	/**
	 * 给一个对象 t,通过该方法可以返回一个该对象的代理对象
	 * @param t 被代理的对象
	 * @return 代理对象
	 */
	@SuppressWarnings("unchecked")
	public static<T> T getProxiedObject(T t) {
		Object proxiedObj = Proxy.newProxyInstance(
						ProxyUtil.class.getClassLoader(),
						t.getClass().getInterfaces(), 
						new TransactionInvocationHandlerImpl(t));
		
		return (T) proxiedObj;
	}
	
	private static class TransactionInvocationHandlerImpl implements InvocationHandler{
		Object srcObj;
		
		public TransactionInvocationHandlerImpl(Object srcObj) {
			this.srcObj = srcObj;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object returnValue = null;
			try {
				System.out.println("开始事务...");
				System.out.println("con.setAutoCommit(fale);");
				returnValue = method.invoke(srcObj, args);
				System.out.println("事务提交...");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("事务回滚...");
			} finally {
				System.out.println("con.setAutoCommit(true);");
				System.out.println("con.close()");
			}
			return returnValue;
		}
		
	}
	
}
