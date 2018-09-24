package cn.hncu.dbPool.dbcp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.junit.Test;

/* sun公司提供了javax.sql.DataSource接口，
 * 所有第三方的数据库连接池都必须实现该接口。
 * 需要jar包：apache.commons.pool-1.5.3.jar 和 commons-dbcp-1.4.jar，
 * 			还有最基本的 mysql-connector-java-5.1.35-bin.jar
 */
public class DbcpHello {
	
	//纯java访问dbcp连接池
	@Test //技术入口: org.apache.commons.dbcp.BasicDataSource 
	public void demo1() throws SQLException {
		//下面这一句相当于创建了一个池
		BasicDataSource bds = new BasicDataSource();
		//下面开始给这个池配置信息。
		bds.setDriverClassName("com.mysql.jdbc.Driver");
		bds.setUrl("jdbc:mysql://127.0.0.1:3306/hncu?useUnicode=true&characterEncoding=utf-8");
		bds.setUsername("root");
		bds.setPassword("1234");
		//接下来就可以获取连接了,进行查询了。
		Connection con = bds.getConnection();
		Statement st = con.createStatement();
		ResultSet resultSet = st.executeQuery("show databases");
		while ( resultSet.next() ) {
			System.out.println( resultSet.getString(1) );
		}
		System.out.println("------------------------------");
		//通过bds还可获取连接池的详细信息。
		System.out.println("连接池最多有多少个连接："+ bds.getMaxActive() );
		System.out.println("连接池最多有多少个连接处于空闲："+ bds.getMaxIdle() );
		System.out.println("连接池初始化大小："+ bds.getInitialSize() );
		System.out.println("连接的等待时间的最大值："+ bds.getMaxWait() );
		//还有其他信息。。。
		
	}
	
	//演示通过 配置文件 初始化连接池，该方式面向接口编程，扩展性较好
	 //技术入口：BasicDataSourceFactory.createDataSource(p);
	public static void main( String[] args) throws Exception {
		Properties p = new Properties();
		//加载配置文件方式1：该方式可以加载与当前类所在同一个包中的配置文件
		//p.load(DbcpHello.class.getResourceAsStream("dbcp.properties"));
		//加载配置文件方式2：该方式可以加载classPath下配置文件
		p.load( DbcpHello.class.getClassLoader().getResourceAsStream("cn/hncu/dbPool/dbcp/dbcp.properties"));
		
		DataSource ds = BasicDataSourceFactory.createDataSource(p);
		//接下来就可以获取连接了,进行查询了。
		Connection con = ds.getConnection();
		Statement st = con.createStatement();
		ResultSet resultSet = st.executeQuery("show databases");
		while ( resultSet.next() ) {
			System.out.println( resultSet.getString(1) );
		}
		System.out.println("-----------------");
		for(int i = 0; i<20;i++) {
			try {
				Connection con2 = ds.getConnection();
				System.out.println(con2.hashCode());
				if( i%2 == 0) {
					con2.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
