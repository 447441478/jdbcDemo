package cn.hncu.dbPool.dbcp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;

/**
 * dbcp连接池工具
 * <br/><br/><b>CreateTime:</b><br/>&emsp;&emsp;&emsp;&ensp; 2018年9月23日 上午8:40:41	
 * @author 宋进宇  Email:447441478@qq.com
 */
public class DbcpUtils {
	//数据库连接池
	private static DataSource ds; 
	//线程局部变量池---为实现一个线程最多只能拥有一个连接
	private static ThreadLocal<Connection> tlPool = new ThreadLocal<Connection>();
	//私有化构造函数
	private DbcpUtils() {
	}
	//初始化dbcp连接池
	static {
		try {
			//创建配置文件对象
			Properties p = new Properties();
			//加载配置文件信息
			p.load( DbcpHello.class.getClassLoader().getResourceAsStream("cn/hncu/dbPool/dbcp/dbcp.properties"));
			
			//通过工厂生产连接池
			ds = BasicDataSourceFactory.createDataSource(p);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	/**
	 * 向外提供连接池
	 * @return 连接池对象
	 */
	public static DataSource getDataSource() {
		return ds;
	}
	
	public static Connection getConnection() throws SQLException {
		//先从线程局部变量池中获取当前线程拥有数据库连接
		Connection con = tlPool.get();
		//如果当前线程拥有的数据库连接为null或者是Closed状态,那么从连接池中获取一个连接
		if( con == null || con.isClosed() ) {
			//从连接池中获取一个连接
			con = ds.getConnection();
			//把获取到的连接放到线程局部变量池中，以便同一个线程共享一个数据库连接。
			tlPool.set(con);
		}
		return con;
	}
}
