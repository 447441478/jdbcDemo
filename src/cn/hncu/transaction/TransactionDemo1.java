package cn.hncu.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import cn.hncu.utils.ConnUtil;

/**
 * 演示事务
 * CreateTime: 2018年9月19日 下午12:44:24	
 * @author 宋进宇  Email:447441478@qq.com
 */
public class TransactionDemo1 {
	
	//演示没有事务下执行 多条SQL语句 会出现什么状况
	@Test
	public void noTransaction() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		//SQL 语句正确
		String sql = "insert into tb_user(username,password) value('Rose','123')";
		st.executeUpdate(sql); //正常执行
		//SQL 语句错误：id是INT型，并非 VARCHAR
		sql = "insert into tb_uservalue('u123','Rose','123')";
		st.executeUpdate(sql); //这里会出现异常
		//SQL 语句正确
		sql = "insert into tb_user(username,password) value('Java','1234')";
		st.executeUpdate(sql); //由于上一句出现异常，这一句就不会执行。
		con.close();
	}
	//演示没有事务下执行 批量处理 会出现什么状况
	@Test
	public void noTransaction2() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		//SQL 语句正确
		String sql = "insert into tb_user(username,password) value('Rose','123')";
		st.addBatch(sql);
		//SQL 语句错误：id是INT型，并非 VARCHAR
		sql = "insert into tb_uservalue('u123','Rose','123')";
		st.addBatch(sql);
		//SQL 语句正确
		sql = "insert into tb_user(username,password) value('Java','1234')";
		st.addBatch(sql);
		/* 运行时下面这一句会出现异常，因为批量处理是只有有一条语句出现问题，就会抛出异常
		 * 但是语法正确的sql语句会照样执行，不同于上面演示！！！
		 * 因为上面演示是 每条sql语句都是要进行一次通讯，在出现异常后，与mysql的连接就断开了，
		 * 而 批量处理 则是一次性把所有sql语句发送过去，即使异常也只是影响下一次的通讯。
		 */
		st.executeBatch();
		con.close();
	}
	
	//演示有事务下执行 多条SQL语句 会出现什么状况
	@Test
	public void transaction(){
		Connection con = ConnUtil.getConnection();
		try {
			con.setAutoCommit( false ); //开启事务
			
			Statement st = con.createStatement();
			//SQL 语句正确
			String sql = "insert into tb_user(username,password) value('Rose','123')";
			st.executeUpdate(sql); //正常执行
			//SQL 语句错误：id是INT型，并非 VARCHAR
//			sql = "insert into tb_uservalue('u123','Rose','123')";
//			st.executeUpdate(sql); //这里会出现异常
			//SQL 语句正确
			sql = "insert into tb_user(username,password) value('Java','1234')";
			st.executeUpdate(sql); 
			
			con.commit(); //提交事务
			System.out.println("事务完成...");
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				con.rollback();
				System.out.println("事务回滚...");
			} catch (SQLException e1) {
				throw new RuntimeException( e1.getMessage(), e1);
			}
		} finally {
			if( con != null ) {
				try {
					//如果后序还有其他业务需要访问数据库的话,应该还原成事务自动提交
					//con.setAutoCommit( true );
					
					//如果没有后序操作应该关闭连接
					con.close();
				} catch (SQLException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
		
	}
}
