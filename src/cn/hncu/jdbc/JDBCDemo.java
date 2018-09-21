package cn.hncu.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

import org.junit.Test;

import cn.hncu.utils.ConnUtil;
/*
 * 演示前准备：数据库脚本
CREATE DATABASE hncu CHARACTER SET utf8;
USE hncu;
CREATE TABLE tb_user(
	id INT PRIMARY KEY AUTO_INCREMENT,
	username VARCHAR(10),
	PASSWORD VARCHAR(10)
);

INSERT INTO tb_user(username,PASSWORD) VALUES('jack','1234');
INSERT INTO tb_user(username,PASSWORD) VALUES('张三','333');
INSERT INTO tb_user(username,PASSWORD) VALUES('Alice','1111');
 */
public class JDBCDemo {
	
	@Test
	public void testGetConnection() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		st.execute("show tables");
		con.close();
	}
	/**
	 * 演示跨库查询
	 * 很简单：表名加个前缀，如 test.aa 就是 数据库test 中的表 aa
	 * @throws Exception 
	 */
	@Test
	public void demo() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		String sql = "select * from test.aa";
		st.executeQuery(sql);
	}
	
	/**
	 * 演示 ResultSet.getXXX()
	 * @throws Exception 为演示方便直接抛异常
	 */
	@Test
	public void demo1() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		ResultSet resultSet = st.executeQuery(" select * from tb_user ");
		System.out.println( "id\t用户名\t密码" );
		while(resultSet.next()) {
			//方式1：通过字段名获取相应的数据
			int id = resultSet.getInt("id");
			//方式2：通过字段的顺序获取对应的数据(以1开始)
			String username = resultSet.getString(2);
			//方式3：通过getObject()获取数据类型未知的数据，参数同上面两种
			Object obj = resultSet.getObject(3);
			System.out.println( id+"\t"+username+"\t"+obj);
		}
	}
	
	
	/////////////演示Statement的三种执行SQL语句的方法//////////////////
	/* 第一种: Statement.execute(sql)
	 *       该方式通吃所有SQL语句，包括：增、删、改、查,通过 st.execute(sql)的返回值判断
	 *       执行的sql语句是属于 查询 还是增、删、改,
	 *       true：查询 ，false:增、删、改或其他
	 */
	@Test
	public void demo2_1() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		String sql = "";
		//增
		//sql = " insert into tb_user(username,password) values('Rose','4321') ";
		//删
		//sql = " delete from tb_user where id = 4 ";
		//改
		//sql = " update tb_user set password='hncu' where username='张三' ";
		//查
		sql = " select * from tb_user ";
		boolean boo = st.execute(sql);
		if( boo ) {
			ResultSet resultSet = st.getResultSet();
			System.out.println( "id\t用户名\t密码" );
			while(resultSet.next()) {
				int id = resultSet.getInt("id");
				String username = resultSet.getString("username");
				String password = resultSet.getString("password");
				System.out.println( id+"\t"+username+"\t"+password);
			}
		}
		con.close();
	}
	/* 第二种: Statement.executeUpdate(sql)
	 * 	      该种方式只能执行 insert、delete、update,
	 *	      不能执行 select
	 */
	@Test
	public void demo2_2() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		String sql = "";
		//增
		sql = " insert into tb_user(username,password) values('Rose','4321') ";
		//删
		//sql = " delete from tb_user where id = 5 ";
		//改
		//sql = " update tb_user set password='hncu' where username='张三' ";
		int update = st.executeUpdate(sql); //返回值是执行后，影响表的行数
		System.out.println( update );
		con.close();
	}
	/* 第三种: Statement.executeQuery(sql)
	 * 	      该种方式只能执行 select
	 * 	      不能执行 insert、delete、update
	 */
	@Test
	public void demo2_3() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		String sql = " select * from tb_user ";
		ResultSet resultSet = st.executeQuery(sql);
		System.out.println( "id\t用户名\t密码" );
		while(resultSet.next()) {
			int id = resultSet.getInt("id");
			String username = resultSet.getString("username");
			String password = resultSet.getString("password");
			System.out.println( id+"\t"+username+"\t"+password);
		}
		con.close();
	}
	/////////////演示Statement的三种执行SQL语句的方法//////////////////
	
	//模拟用户注册
	@Test //黑：用户名或密码出现 '字符就挂，如 aa'bb
	public void regDemo() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		Scanner in = new Scanner( System.in );
		System.out.println("请输入用户名：");
		String username = in.nextLine();
		System.out.println("请输入密码：");
		String password = in.nextLine();
		in.close();
		//写死的sql语句
		//String sql = " insert into tb_user(username,password) values('Bob','666666')";
		//写活
		String sql = " insert into tb_user(username,password) values('"+username+"','"+password+"')";
		int update = st.executeUpdate(sql);
		if( update>0 ) {
			System.out.println("注册成功！！！");
		}
		con.close();
	}
	//模拟用户登录
	@Test //黑：' or '1'='1
	public void loginDemo() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		Scanner in = new Scanner( System.in );
		System.out.println("请输入用户名：");
		String username = in.nextLine();
		System.out.println("请输入密码：");
		String password = in.nextLine();
		in.close();
		String sql = " select count(1) from tb_user where username='"+username+"' and password='"+password+"' " ;
		System.out.println(sql);
		ResultSet resultSet = st.executeQuery(sql);
		if( resultSet.next() ) {
			int i = resultSet.getInt(1);
			if( i > 0 ) {
				System.out.println("登录成功");
			}else {
				System.out.println("登录失败");
			}
		}
		con.close();
	}
	
	/* 由于使用Statement 会出现上述BUG
	 * 所以，这时候应该采用PreparedStatement类，
	 * 该对象可以通过'占位符'的方式，先预编译sql语句，
	 * 等接收到相应的数据时在通过 setxxx()来补充sql语句
	 * 最后再执行。
	 */ 
	@Test //防黑：' or '1'='1
	public void loginDemo2() throws Exception {
		Connection con = ConnUtil.getConnection();
		//使用'占位符'：? 来代替真正的变化的数据的位置。
		String sql = " select count(1) from tb_user where username=? and password=?";
		PreparedStatement pst = con.prepareStatement(sql);
		//接收用户输入
		Scanner in = new Scanner( System.in );
		System.out.println("请输入用户名：");
		String username = in.nextLine();
		System.out.println("请输入密码：");
		String password = in.nextLine();
		in.close();
		//把用户输入的数据补充到原先占位符的位置，注意：从1开始。
		pst.setString(1, username); //该方法内部把敏感字符进行转义了。
		pst.setString(2, password);
		ResultSet resultSet = pst.executeQuery();
		if( resultSet.next() ) {
			int i = resultSet.getInt(1);
			if( i > 0 ) {
				System.out.println("登录成功");
			}else {
				System.out.println("登录失败");
			}
		}
		con.close();
	}
	
}
