package cn.hncu.dbPool.c3p0;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.Test;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/* sun公司提供了javax.sql.DataSource接口，
 * 所有第三方的数据库连接池都必须实现该接口。
 * 需要jar包：c3p0-0.9.1.2.jar 还有最基本的 mysql-connector-java-5.1.35-bin.jar
 */
public class C3p0Hello {
	//纯java访问c3p0连接池
	@Test //技术入口: com.mchange.v2.c3p0.ComboPooledDataSource
	public void demo1() throws SQLException, PropertyVetoException {
		//下面这一句相当于创建了一个池
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		//下面开始给这个池配置信息。
		cpds.setDriverClass("com.mysql.jdbc.Driver");
		cpds.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/hncu?useUnicode=true&characterEncoding=utf-8");
		cpds.setUser("root");
		cpds.setPassword("1234");
		//接下来就可以获取连接了,进行查询了。
		Connection con = cpds.getConnection();
		Statement st = con.createStatement();
		ResultSet resultSet = st.executeQuery("show databases");
		while ( resultSet.next() ) {
			System.out.println( resultSet.getString(1) );
		}
		System.out.println("------------------------------");
		System.out.println( cpds.getMaxConnectionAge() );
		System.out.println( cpds.getMaxIdleTime() );
		System.out.println( cpds.getAcquireIncrement());
		System.out.println( cpds.getAcquireRetryAttempts());
		System.out.println( cpds.getAcquireRetryDelay());
		System.out.println( cpds.getInitialPoolSize());
		System.out.println( cpds.getThreadPoolSize());
		System.out.println( cpds.getMaxPoolSize());
		System.out.println( cpds.getMinPoolSize());
		System.out.println(cpds.getThreadPoolNumActiveThreads());
	}
	
	//演示通过 配置文件 初始化连接池，该方式面向接口编程，扩展性较好
	@Test
	public void demo2() throws SQLException {
		//配置文件必须在src目录下，c3p0写死了！！！
		DataSource ds = new ComboPooledDataSource();//不带参数时：使用的是<default-config>中的配置信息
		//DataSource cpds = new ComboPooledDataSource("hncu"); //带参数时：使用的是配置文件中 <named-config name="hncu">中的配置信息
		//接下来就可以获取连接了,进行查询了。
		Connection con = ds.getConnection();
		Statement st = con.createStatement();
		ResultSet resultSet = st.executeQuery("show databases");
		while ( resultSet.next() ) {
			System.out.println( resultSet.getString(1) );
		}
		//强转为查看信息
		ComboPooledDataSource cpds = (ComboPooledDataSource) ds;
		System.out.println("------------------------------");
		System.out.println( "MaxConnectionAge:"+cpds.getMaxConnectionAge() );
		System.out.println( "MaxIdleTime:"+cpds.getMaxIdleTime() );
		System.out.println( "AcquireIncrement:"+cpds.getAcquireIncrement());
		System.out.println( "AcquireRetryAttempts:"+cpds.getAcquireRetryAttempts());
		System.out.println( "AcquireRetryDelay:"+cpds.getAcquireRetryDelay());
		System.out.println( "InitialPoolSize:"+cpds.getInitialPoolSize());
		System.out.println( "ThreadPoolSize:"+cpds.getThreadPoolSize());
		System.out.println( "MaxPoolSize:"+cpds.getMaxPoolSize());
		System.out.println( "MinPoolSize:"+cpds.getMinPoolSize());
		System.out.println( "ThreadPoolNumActiveThreads:"+cpds.getThreadPoolNumActiveThreads());
		System.out.println("------------------------------");
		for( int i = 0; i < 18; i++ ) {
			Connection con2 = ds.getConnection();
			System.out.println(con2.hashCode());
			System.out.println( "ThreadPoolNumActiveThreads:"+cpds.getThreadPoolNumActiveThreads());
			if( i%2==0 ) {
				con2.close();
			}
		}
	}
	
}
