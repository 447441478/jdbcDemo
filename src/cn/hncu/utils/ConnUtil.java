package cn.hncu.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * 产生数据库连接工具
 * CreateTime: 2018年9月17日 下午10:30:37	
 * @author 宋进宇  Email:447441478@qq.com
 */
public class ConnUtil {
	private static Connection con;
	static {
		try {
			//创建Properties对象
			Properties p = new Properties();
			// 通过类加载器  加载配置文件
			p.load( ConnUtil.class.getClassLoader().getResourceAsStream( "jdbc.properties" ) );
			//获取配置信息
			String driver = p.getProperty("driver");
			String url = p.getProperty("url");
			String user = p.getProperty("username");
			String password = p.getProperty("password");
			//加载驱动
			Class.forName( driver );
			//获取连接
			con = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			throw new RuntimeException( e.getMessage(), e);
		}
	}
	
	private ConnUtil() {}
	/**
	 * 获取数据库了连接对象
	 * @return 数据库连接对象
	 */
	public static Connection getConnection() {
		return con;
	}
}
