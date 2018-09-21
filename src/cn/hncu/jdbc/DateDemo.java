package cn.hncu.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;

import cn.hncu.utils.ConnUtil;

/**
 * 演示Java与MySQL之间日期的转换
 * CreateTime: 2018年9月20日 下午4:50:33	
 * @author 宋进宇  Email:447441478@qq.com
 */
public class DateDemo {

/*
USE hncu;

创建表
CREATE TABLE birthInfo(
	id INT PRIMARY KEY AUTO_INCREMENT,
	birthDate DATE,
	birthTime TIME,
	birthDateTime DATETIME
);

插入一些数据
INSERT INTO birthInfo(birthDate,birthTime,birthDateTime) VALUES(NOW(),NOW(),NOW());
INSERT INTO birthInfo(birthDate,birthTime,birthDateTime) VALUES(CURDATE(),CURTIME(),NOW());

*/
	
	@Test
	public void readDemo() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		String sql = "select * from birthInfo";
		ResultSet resultSet = st.executeQuery(sql);
		//根据表结构可知字段类型分别是 int 、date 、 time 、datetime
		while( resultSet.next() ) {
			Integer id = resultSet.getInt("id");
			//date 和 time 都有响应的获取方法
			java.sql.Date birthDate = resultSet.getDate("birthDate");
			java.sql.Time birthTime = resultSet.getTime("birthTime");
			//datetime 没有，但是datetime可以 通过 date 和 time 拼装
			java.sql.Date birthDateTime1 = resultSet.getDate("birthDateTime");
			java.sql.Time birthDateTime2 = resultSet.getTime("birthDateTime");
			System.out.println("id:"+id);
			System.out.println("birthDate:"+birthDate);
			System.out.println("birthTime:"+birthTime);
			System.out.println("birthDateTime:"+birthDateTime1+" "+birthDateTime2);
			System.out.println("----------------------");
		}
		
		con.close();
	}
	
	@Test
	public void saveDemo() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		String sql = "";
		//sql = "insert into birthinfo(birthDate,birthTime,birthDateTime) values(now(),now(),now())";
		sql = "insert into birthinfo(birthDate,birthTime,birthDateTime) values('1996-8-10','17:15:20','1996-8-10 17:15:20')";
		st.executeUpdate(sql);
		sql = "insert into birthinfo(birthDate,birthTime,birthDateTime) values(?,?,?)";
		PreparedStatement pst = con.prepareStatement(sql);
		pst.setDate(1, new java.sql.Date( System.currentTimeMillis() ));
		pst.setTime(2, new java.sql.Time( System.currentTimeMillis() ));
		pst.setString(3, "2018-09-20 22:23:30");
		pst.executeUpdate();
		con.close();
	}
	
}
