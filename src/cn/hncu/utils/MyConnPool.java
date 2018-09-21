package cn.hncu.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

/* MyConnPool实现了线程池，但是存在缺点：
 * 根据编程习惯使用完Connection，应该是调用 con.close(),
 * 而不是 MyConnPool.back(con),该方式只是针对于熟悉该工具的开发者使用。
 * 所以需要进一步改进MyConnPool
 */
public class MyConnPool {
	private static Queue<Connection> pool = new LinkedList<Connection>();
	private static int size = 3;
	static {
		Properties p = new Properties();
		try {
			p.load( MyConnPool.class.getClassLoader().getResourceAsStream("myConnPool.properties"));
			String driver = p.getProperty("driver");
			String url = p.getProperty("url");
			String username = p.getProperty("username");
			String password = p.getProperty("password");
			String strSize = p.getProperty("size");
			size = Integer.valueOf( strSize );
			Class.forName( driver );
			for( int i = 0; i < size; i++ ) {
				Connection con = DriverManager.getConnection(url, username, password);
				pool.add(con);
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
	 * 获取一个数据库连接对象
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
	/**
	 * 把con还回来
	 * @param con 数据库连接对象
	 */
	public static void back( Connection con ) {
		System.out.println("还回来一个连接...");
		pool.add(con);
	}
	 
}
