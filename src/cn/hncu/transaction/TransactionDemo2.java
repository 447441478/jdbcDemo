package cn.hncu.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import cn.hncu.utils.ConnUtil;

/**
 * 演示多线程下的事务
 * CreateTime: 2018年9月19日 下午12:44:24	
 * @author 宋进宇  Email:447441478@qq.com
 */
public class TransactionDemo2 {
	
	/* 经过多次测试 发现在多线程下，会发生混乱
	 */
	@Test
	public void demo1(){
		Connection con = ConnUtil.getConnection();
		try {
			con.setAutoCommit( false ); //开启事务
			
			Statement st = con.createStatement();
			
			
			String sql = "insert into tb_user(username,password) value('Rose','123')";
			st.executeUpdate(sql); 
			
			//处理其他业务
			new MyThread(1).start();
			new MyThread(2).start();
			
			con.commit(); //提交事务
			System.out.println("主线程：事务完成...");
		} catch (SQLException e) {
			//e.printStackTrace();
			try {
				con.rollback(); //事务回滚
				System.out.println("主线程：事务回滚...");
			} catch (SQLException e1) {
				throw new RuntimeException( e1.getMessage(), e1);
			}
		} finally {
			if( con != null ) {
				try {
					//con.setAutoCommit( true ); //还原设置
					
					con.close();
				} catch (SQLException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}
	
	class MyThread extends Thread{
		int num ;

		public MyThread(int num) {
			this.num = num;
		}

		@Override
		public void run() {

			Connection con = ConnUtil.getConnection();
			try {
				con.setAutoCommit( false ); //开启事务
				
				Statement st = con.createStatement();
				
				String sql = "insert into tb_user(username,password) value('Rose"+num+"','123')";
				st.executeUpdate(sql); 
				
				con.commit(); //提交事务
				System.out.println( "子线程"+num+":事务完成...");
			} catch (SQLException e) {
				//e.printStackTrace();
				try {
					con.rollback(); //事务回滚
					System.out.println("子线程"+num+":事务回滚...");
				} catch (SQLException e1) {
					throw new RuntimeException( e1.getMessage(), e1);
				}
			} finally {
				if( con != null ) {
					try {
						//con.setAutoCommit( true ); //还原设置
						
						con.close();
					} catch (SQLException e) {
						throw new RuntimeException(e.getMessage(), e);
					}
				}
			}
		
		}
		
	}
}
