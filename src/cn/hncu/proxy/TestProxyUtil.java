package cn.hncu.proxy;

import org.junit.Test;

import cn.hncu.proxy.utils.ProxyUtil;

public class TestProxyUtil {
	
	@Test
	public void demo() {
		IPerson person = ProxyUtil.getProxiedObject( new Person() );
		person.sayHello();
	}
}
