package cn.hncu.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import cn.hncu.utils.MyConnPool;

/* MyConnPool实现了线程池，但是存在缺点：
 * 根据编程习惯使用完Connection，应该是调用 con.close(),
 * 而不是 MyConnPool.back(con),该方式只是针对于熟悉该工具的开发者使用。
 * 所以需要进一步改进MyConnPool
 */
public class TestMyConnPool {
	
	public static void main(String[] args) {
		Connection con = MyConnPool.getConnection();
		try {
			con.setAutoCommit( false );
			
			Statement st = con.createStatement();
			
			new MyThread(1).start();
			new MyThread(2).start();
			new MyThread(3).start();
			new MyThread(4).start();
			new MyThread(5).start();
			
			String sql = "insert into tb_user(username,password) value('Jack','4312')";
			st.executeUpdate(sql); 
			
			con.commit(); //提交事务
			System.out.println("主线程事务提交了...");
		} catch (SQLException e) {
			try {
				con.rollback();
				System.out.println("主线程事务回滚...");
			} catch (SQLException e1) {
				throw new RuntimeException( e1.getMessage(), e1 );
			}
			e.printStackTrace();
		} finally {
			try {
				con.setAutoCommit( true );
				MyConnPool.back(con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	static class MyThread extends Thread{
		int num ;

		public MyThread(int num) {
			this.num = num;
		}

		@Override
		public void run() {

			Connection con = MyConnPool.getConnection();
			try {
				con.setAutoCommit( false ); //开启事务
				
				Statement st = con.createStatement();
				
				String sql = "insert into tb_user(username,password) value('Rose"+num+"','123')";
				//搞破坏，测试多个线程之间是否存在干扰。
				if( num % 2 == 0 ) {
					sql += "select"; 
				}
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
						con.setAutoCommit( true ); //还原设置
						MyConnPool.back(con);
					} catch (SQLException e) {
						throw new RuntimeException(e.getMessage(), e);
					}
				}
			}
		
		}
		
	}
}
