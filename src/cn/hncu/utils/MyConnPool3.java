package cn.hncu.utils;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

/*
 * 采用动态代理的方式实现线程池
 */
public class MyConnPool3 {
	private static Queue<Connection> pool = new LinkedList<Connection>();
	private static int size = 3;
	static {
		Properties p = new Properties();
		try {
			//加载配置文件
			p.load( MyConnPool3.class.getClassLoader().getResourceAsStream("myConnPool.properties"));
			//读取配置信息
			String driver = p.getProperty("driver");
			String url = p.getProperty("url");
			String username = p.getProperty("username");
			String password = p.getProperty("password");
			String strSize = p.getProperty("size");
			size = Integer.valueOf( strSize );
			//加载驱动
			Class.forName( driver );
			for( int i = 0; i < size; i++ ) {
				//因为匿名内部类需要调用该对象，所以用final修饰
				final Connection con = DriverManager.getConnection(url, username, password);
				//关键点
				//这是一个 Connection 接口的代理对象
				Object proxiedObj = Proxy.newProxyInstance(
										MyConnPool3.class.getClassLoader(), 
										new Class[] {Connection.class},
										new InvocationHandler() { //这个才是关键点
											//参数 proxy对象 就是proxiedObj对象, method就是被调用的方法对象 , args方法参数
											@Override
											public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
												//判断被调用的方法是否是close()方法
												if( "close".equals( method.getName() ) ) {
													//修改close()方法
													pool.add( (Connection) proxy );
													System.out.println("还回来一个conn...");
													return null;
												}
												return method.invoke(con, args);
											}
										});
				Connection con2 = (Connection) proxiedObj;
				pool.add(con2);
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	/**
	 * 获取数据库连接对象
	 * @return 数据库连接对象
	 */
	public synchronized static Connection getConnection() {
		if( pool.size() <= 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return getConnection();
		}
		return pool.poll();
	}
	
}