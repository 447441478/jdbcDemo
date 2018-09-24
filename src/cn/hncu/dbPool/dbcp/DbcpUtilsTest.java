package cn.hncu.dbPool.dbcp;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/* 经过测试发现 dbcp连接池是采用栈的数据结构来做的，而且内存是固定的，
 * 当一个连接con是closed的状态时,dbcp会清空con所指向的内存的数据，并且把con返回栈中。
 * 当调用getConnection时，dbcp会从栈顶取出一个con,如果con所指向的内存的数据为空,
 * 就会在con所指向的内存区域重新初始化一个con对象。
 */
public class DbcpUtilsTest {
	
	public static void main(String[] args) {
		Connection con = null;
		try {
			con = DbcpUtils.getConnection();
			System.out.println(Thread.currentThread().getName()+">>"+con.hashCode());
			con.setAutoCommit(false); //开启事务
			
			save1(1);
			
			new MyThread(1).start();
			
			save2(1);
			
			con.commit(); //事务提交
			System.out.println("m1事务提交了...");
		} catch (SQLException e) {
			//e.printStackTrace();
			try {
				con.rollback();
				System.out.println("m1事务回滚了...");
			} catch (SQLException e1) {
				throw new RuntimeException(e.getMessage(),e);
			}
		} finally {
			if( con != null ) {
				try {
					//还原
					con.setAutoCommit(true);
					//关闭
					con.close();
				} catch (SQLException e) {
					throw new RuntimeException(e.getMessage(),e);
				}
			}
		}
		new MyThread(2).start(); 
		try {
			Thread.sleep(1);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		/* 如果下面的注释掉的话，上面这个线程中con的hashcode与Main线程的hashcode一样。
		 */
		//*
		try {
			con = DbcpUtils.getConnection();
			System.out.println(Thread.currentThread().getName()+">>"+con.hashCode());
			con.setAutoCommit(false); //开启事务
			save1(2);
			Statement st = con.createStatement();
			//使出现错误
			st.executeQuery("select");
			con.commit(); //事务提交
			System.out.println("m2事务提交了...");
		} catch (Exception e) {
			//e.printStackTrace();
			try {
				con.rollback();
				System.out.println("m2事务回滚了...");
			} catch (SQLException e1) {
				throw new RuntimeException(e.getMessage(),e);
			}
		} finally {
			if( con != null ) {
				try {
					//还原
					con.setAutoCommit(true);
					//关闭
					con.close();
				} catch (SQLException e) {
					throw new RuntimeException(e.getMessage(),e);
				}
			}
		}
		//*/
	}
	
	public static void save1(int i) throws SQLException {
		Connection con = DbcpUtils.getConnection();
		System.out.println(Thread.currentThread().getName()+">save1>"+con.hashCode());
		Statement st = con.createStatement();
		st.executeUpdate("insert into student values('A001"+Thread.currentThread().getName()+i+"','Jack')");
		st.executeUpdate("insert into student values('A002"+Thread.currentThread().getName()+i+"','Tom"+i+"')");
		st.close();
	}
	
	public static void save2(int i)throws SQLException {
		Connection con = DbcpUtils.getConnection();
		System.out.println(Thread.currentThread().getName()+">save2>"+con.hashCode());
		Statement st = con.createStatement();
		st.executeUpdate("insert into student values('B001"+Thread.currentThread().getName()+i+"','Rose')");
		st.executeUpdate("insert into student values('B002"+Thread.currentThread().getName()+i+"','Alice')");
		st.close();
	}
	
	static class MyThread extends Thread{
		int num;
		protected MyThread(int num) {
			this.num = num;
		}

		@Override
		public void run() {
			Connection con = null;
			try {
				con = DbcpUtils.getConnection();
				System.out.println(Thread.currentThread().getName()+">>"+con.hashCode());
				con.setAutoCommit(false); //开启事务
				
				save1(num);
				save2(num);
//				if( num % 2 == 0) {
//					Statement st = con.createStatement();
//					//使出现错误
//					st.executeQuery("11111111");
//				}
				
				con.commit(); //事务提交
				System.out.println(num+">>事务提交了...");
			} catch (SQLException e) {
				//e.printStackTrace();
				try {
					con.rollback();
					System.out.println(num+">>事务回滚了...");
				} catch (SQLException e1) {
					throw new RuntimeException(e.getMessage(),e);
				}
			} finally {
				if( con != null ) {
					try {
						//还原
						con.setAutoCommit(true);
						//关闭
						con.close();
					} catch (SQLException e) {
						throw new RuntimeException(e.getMessage(),e);
					}
				}
			}
		};
	}
}
