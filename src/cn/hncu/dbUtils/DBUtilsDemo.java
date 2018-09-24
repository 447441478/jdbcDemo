package cn.hncu.dbUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ext.ExtQueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;

import cn.hncu.dbPool.c3p0.C3p0Utils;
import cn.hncu.domain.Pet;
import cn.hncu.domain.Student;

/**
 * &emsp;&emsp;演示Apache的工具包：commons-dbutils-1.4.jar<hr/>
 * <b>玩转DBUtils工具，主要是一个类和一个接口：</b><br/>
 * &emsp;&emsp;1. QueryRunner类---用于执行sql，且主要使用它内部的两个方法: query(), update(), 还有一个不常用的batch()方法。<br/>
 * &emsp;&emsp;2. ResultSetHandler接口 ----用于帮助我们封装结果数据的。
 * <br/><br/><b>CreateTime:</b><br/>&emsp;&emsp;&emsp; 2018年9月23日 下午2:52:16	
 * @author 宋进宇&emsp;<a href='mailto:447441478@qq.com'>447441478@qq.com</a>
 */
public class DBUtilsDemo {
	
	//查询并封装成 List<Map<String, Object>> 形式
	@Test
	public void query1() throws SQLException {
		//获取连接池
		DataSource ds = C3p0Utils.getDataSource();
		//获取查询运行器
		QueryRunner qr = new QueryRunner(ds);
		List<Map<String, Object>> list = qr.query("select * from student", new MapListHandler());
		System.out.println(list);
	}
	
	//查询并封装成 List<Bean> 形式
	@Test
	public void query2() throws SQLException {
		//获取连接池
		DataSource ds = C3p0Utils.getDataSource();
		//获取查询运行器
		QueryRunner qr = new QueryRunner(ds);
		List<Student> list = qr.query( "select * from student", new BeanListHandler<Student>(Student.class) );
		System.out.println(list);
	}
	
	//查询并封装单值，如count(1)、max(age)等
	@Test
	public void query3() throws SQLException {
		//获取连接池
		DataSource ds = C3p0Utils.getDataSource();
		//获取查询运行器
		QueryRunner qr = new QueryRunner(ds);
		Object count = qr.query("select count(1) from student", new ScalarHandler());
		System.out.println( count );
	}
	
	//PreparedStatement形式查询
	@Test
	public void query4() throws SQLException {
		//获取连接池
		DataSource ds = C3p0Utils.getDataSource();
		//获取查询运行器
		QueryRunner qr = new QueryRunner(ds);
		List<Map<String, Object>> query = qr.query("select * from student where name=?", new MapListHandler(),"Alice");
		System.out.println( query );
	}
	
	//模糊查询
	@Test
	public void query5() throws SQLException {
		//获取连接池
		DataSource ds = C3p0Utils.getDataSource();
		//获取查询运行器
		QueryRunner qr = new QueryRunner(ds);
		List<Map<String, Object>> query = qr.query( "select * from student where name like '%a%'", new MapListHandler() );
		System.out.println( query );
	}
	
	///////////下面演示增、删、改---update//////////////
	@Test//增加 1
	public void add() throws SQLException {
		//获取连接池
		DataSource ds = C3p0Utils.getDataSource();
		//获取查询运行器
		QueryRunner qr = new QueryRunner(ds);
		qr.update("insert into student(id,name) values('A001','Rose')");
	}
	@Test//增加 2
	public void add2() throws SQLException {
		//获取连接池
		DataSource ds = C3p0Utils.getDataSource();
		//获取查询运行器
		QueryRunner qr = new QueryRunner(ds);
		qr.update("insert into student(id,name) values(?,?)","A002","Bob");
	}
	@Test//删除
	public void del() throws SQLException {
		//获取连接池
		DataSource ds = C3p0Utils.getDataSource();
		//获取查询运行器
		QueryRunner qr = new QueryRunner(ds);
		qr.update("delete from student where id=? ","A002");
	}
	@Test//修改
	public void update() throws SQLException {
		//获取连接池
		DataSource ds = C3p0Utils.getDataSource();
		//获取查询运行器
		QueryRunner qr = new QueryRunner(ds);
		qr.update("update student set name=? where id=? ","张飞","A001");
	}
	
	////////////下面演示事务---针对于update，因为查询没有什么事务可言/////////////
	@Test
	public void transaction(){
		//注意1：采用空参的构造方法
		QueryRunner qr = new QueryRunner();
		Connection con = null;
		try {
			//注意2：获取一个连接
			con = C3p0Utils.getConnection();
			con.setAutoCommit( false ); //开启事务
			
			//注意3：把获取的连接做为参数，让qr通过 con 去执行sql语句。
			qr.update(con, "insert into student(id,name) values(?,?)", "B001","Mike");
			//下面这一句故意让id为'A001'就是为了演示事务回滚
			qr.update(con, "insert into student(id,name) values(?,?)", "A001","刘备");
			
			con.commit(); //提交事务
			System.out.println("事务提交了...");
		} catch (SQLException e) {
			try {
				con.rollback(); //事务回滚
				System.out.println("事务回滚了...");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			if( con != null ) {
				try {
					//还原设置
					con.setAutoCommit(true);
					//关闭连接
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//演示批量处理
	@Test
	public void batch() throws SQLException {
		//获取连接池
		DataSource ds = C3p0Utils.getDataSource();
		//获取查询运行器
		QueryRunner qr = new QueryRunner(ds);
		for ( int i = 1; i<=100; i++ ) {
			Object[][] params = {
								{"小白"+i,"黑色"+i},
								{"小黄"+i,"黄色"+i},
								{"小狼"+i,"蓝色"+i}
								};
			//一次批量处理，处理3条，总共执行了100次批量处理
			qr.batch("insert into pet(name,color) values(?,?)",params );
		}
			
	}
	
	
	///////下面演示DBUtils扩展包功能，需要jar包：commons-dbutils-ext.jar////////
	//查询
	@Test //技术入口：ExtQueryRunner类
	public void extQuery() {
		//获取扩展的查询运行器
		ExtQueryRunner eqr = new ExtQueryRunner(C3p0Utils.getDataSource());
		List<Pet> list = eqr.query(Pet.class);
		System.out.println( list );
	}
	//增、删、改
	@Test
	public void extUpdate() {
		//获取扩展的查询运行器
		ExtQueryRunner eqr = new ExtQueryRunner(C3p0Utils.getDataSource());
		Pet pet = new Pet();
		pet.setName("小黑");
		pet.setColor("白色");
		eqr.save(pet);
	}
	
}
