package cn.hncu.dbMate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.junit.Test;

import cn.hncu.utils.MyConnPool3;

/**
 * 演示：获取并使用数据库元数据
 * CreateTime: 2018年9月21日 上午7:56:53	
 * @author 宋进宇  Email:447441478@qq.com
 */
public class DatabaseMateDemo {
	/* 玩转数据库元数据主要有两个类：
	 *    java.sql.DatabaseMetaData 和 java.sql.ResultSetMetaData
	 *    DatabaseMetaData：可以获得驱动信息、所有数据库名，所有表格名(视图、存储过程等)
	 *    ResultSetMetaData：可以获得查询结果的表结构，如列(字段)名、字段类型等
	 * 获取：
	 *    DatabaseMetaData dbmd = con.getMetaData(); //con 是Connection类对象
	 *    ResultSetMetaData rsmd = rs.getMetaData(); //rs 是ResultSet类对象
	 */
	@Test
	public void hello() throws Exception {
		Connection con = MyConnPool3.getConnection();
		DatabaseMetaData dm = con.getMetaData();
		
		System.out.println(dm.getDriverName());
		System.out.println(dm.getDriverVersion());
		System.out.println(dm.getDriverMajorVersion());
		System.out.println(dm.getDriverMinorVersion());
		System.out.println(dm.getMaxStatements());
		System.out.println(dm.getJDBCMajorVersion());
	}
	
	@Test
	public void demo1() throws Exception {
		Connection con = MyConnPool3.getConnection();
		//技术入口：
		DatabaseMetaData dbmd = con.getMetaData();
		
		//获取所有数据库的信息
		ResultSet dbRs = dbmd.getCatalogs();
		while( dbRs.next() ) {
			//查询结果的表头为：TABLE_CAT
			String dbName = dbRs.getString("TABLE_CAT");
			System.out.println(dbName);
		}
		System.out.println("---------------db----------------");
		
		//获取某个数据库中所有表信息
		ResultSet tbRs = dbmd.getTables("hncu", "hncu", null, new String[]{"TABLE"}); 
		while( tbRs.next() ) {
			//查询结果的表头: TABLE_CAT TABLE_SCHEM TABLE_NAME 等，具体信息见API
			String tbName = tbRs.getString("TABLE_NAME");
			System.out.println(tbName);
		}
		System.out.println("---------------tb----------------");
		
		
		//获取查询结果的表结构信息
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("select * from hncu.person");
		//技术入口：
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount(); //获取查询结果的列(字段)的个数
		//输出表头
		System.out.println("-------------------------------");
		for (int i = 0; i < cols ; i++) {
			//注意 getColumnLabel参数 从1开始
			String columnLabel = rsmd.getColumnLabel(i+1); //别名，在没有使用as的情况下同表结构字段名
			//String columnName = rsmd.getColumnName(i+1); //表结构字段名
			System.out.print(columnLabel+"\t");
		}
		System.out.println("\n-------------------------------");
		//输出表数据
		while(rs.next()) {
			for (int i = 0; i < cols ; i++) {
				//注意 getObject参数 从1开始
				Object obj = rs.getObject( i+1 );
				System.out.print(obj+"\t");
			}
			System.out.println("\n-------------------------------");
		}
	}
	
	//演示查看 getTables获得的查询结果的所有字段(列)
	@Test
	public void demo2() throws Exception {
		Connection con = MyConnPool3.getConnection();
		//技术入口：
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet tbsRs = dbmd.getTables("hncu", "hncu", null, new String[]{"TABLE"}); 
		ResultSetMetaData tbMd = tbsRs.getMetaData();
		int cols = tbMd.getColumnCount();
		System.out.println( cols );
		for (int i = 0; i < cols; i++) {
			System.out.print( tbMd.getColumnLabel( i+1 ) +"\t" );
		}
		System.out.println("\n-----------------------------");
		while( tbsRs.next() ) {
			for (int i = 0; i < cols; i++) {
				System.out.print( tbsRs.getObject( i+1 ) +"\t" );
			}
			System.out.println();
		}
	}
	
}
