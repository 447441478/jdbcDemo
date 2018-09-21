package cn.hncu.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;

import org.junit.Test;

import cn.hncu.utils.ConnUtil;

/**
 * 演示存储过程的调用
 * CreateTime: 2018年9月19日 下午12:11:44	
 * @author 宋进宇  Email:447441478@qq.com
 */
public class JDBC_CallableDemo {
	//演示调用空参存储过程
/*
MySQL下
#创建存储过程
DELIMITER $$
CREATE PROCEDURE p1()
BEGIN 
	INSERT INTO tb_user(username,PASSWORD) VALUES('张飞','pwd777');
	SELECT * FROM tb_user;
END $$
DELIMITER ;
#调用存储过程
CALL p1();
 */
	@Test
	public void demo1() throws Exception {
		Connection con = ConnUtil.getConnection();
		String sql = "call p1()";
		CallableStatement cst = con.prepareCall(sql);
		//因为存储过程中有查询语句，所以调用executeQuery()相对较好
		ResultSet resultSet = cst.executeQuery();
		while( resultSet.next() ) {
			int id = resultSet.getInt("id");
			String username = resultSet.getString("username");
			String password = resultSet.getString("password");
			System.out.println("id:"+id+",username:"+username+",password:"+password);
		}
		con.close();
	}
	
	//演示调用带参的存储过程
/*
#创建带参存储过程
DELIMITER $$
CREATE PROCEDURE p2(IN Uname VARCHAR(10),IN pwd VARCHAR(10))
BEGIN 
	INSERT INTO tb_user(username,PASSWORD) VALUES(Uname,pwd);
	SELECT * FROM tb_user;
END $$
DELIMITER ;

#调用带参数存储过程
CALL p2('关羽','sss666');
*/
	@Test
	public void demo2() throws Exception {
		Connection con = ConnUtil.getConnection();
		String sql = "call p2(?,?)";
		CallableStatement cst = con.prepareCall(sql);
		
		//补充完整SQL语句
		cst.setString(1, "赵云");
		cst.setString(2, "zy111");
		
		//因为存储过程中有查询语句，所以调用executeQuery()相对较好
		ResultSet resultSet = cst.executeQuery();
		while( resultSet.next() ) {
			int id = resultSet.getInt("id");
			String username = resultSet.getString("username");
			String password = resultSet.getString("password");
			System.out.println("id:"+id+",username:"+username+",password:"+password);
		}
		con.close();
	}
	
	//演示调用带参带返回值的存储过程
/*
#创建带参带返回值的存储过程
DELIMITER $$
CREATE PROCEDURE p3(IN Uname VARCHAR(10),IN pwd VARCHAR(10),OUT num INT)
BEGIN 
	INSERT INTO tb_user(username,PASSWORD) VALUES(Uname,pwd);
	SELECT * FROM tb_user;
	SELECT COUNT(*) INTO num FROM tb_user;
END $$
DELIMITER ;

#调用带参带返回值的存储过程
CALL p3('马超','mc666',@abc);
#读取返回值
SELECT @abc;
*/	
	@Test
	public void demo3() throws Exception {
		Connection con = ConnUtil.getConnection();
		
		//注意1！！！
		String sql = "call p3(?,?,?)"; //最后一个用于存放返回值
		
		CallableStatement cst = con.prepareCall(sql);
		
		//补充完整SQL语句
		cst.setString(1, "赵云");
		cst.setString(2, "zy111");
		
		//注意2！！！
		//注册一个返回参数类型
		cst.registerOutParameter(3, Types.INTEGER);
		
		//因为存储过程中有查询语句，所以调用executeQuery()相对较好
		ResultSet resultSet = cst.executeQuery();
		while( resultSet.next() ) {
			int id = resultSet.getInt("id");
			String username = resultSet.getString("username");
			String password = resultSet.getString("password");
			System.out.println("id:"+id+",username:"+username+",password:"+password);
		}
		//注意3！！！
		//通过 cst获取存储过程的返回值，不是通过resultSet！！！
		int count = cst.getInt(3); 
		System.out.println("总共："+count);
		con.close();
	}
}
