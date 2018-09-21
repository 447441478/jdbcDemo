package cn.hncu.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HelloTransaction {
	public static void main(String[] args) {

		Connection con = null;
		try {
			//1 加载驱动
			Class.forName("com.mysql.jdbc.Driver");
			//2 通过驱动管理器获取连接
			//2.1 声明连接信息  
			//?后面可省略，但是为了防止中文乱码，加上更好。
			//端口默认为3306,但是我有两个mysql服务所以 采用 3307
			String url = "jdbc:mysql://127.0.0.1:3307/hncu?useUnicode=true&CharacterEncoding=utf-8";
			String user = "root";
			String password = "1234";
			//2.2 获取连接
			con = DriverManager.getConnection(url, user, password);
			
			/* 设置事务隔离等级
			 * 隔离等级最低：READ_UNCOMMITTED 会出现：脏读、不可重复度、幻读  --- 不安全，但是数据实时性最好 ，效率最好
			 * 		  次级：READ_COMMITTED 会出现：不可重复度、幻读  --- 不安全，但是数据实时性相对较好，效率较好
			 *       中级：REPEATABLE_READ  MySQL默认隔离等级  会出现：幻读--- 相对安全，效率一般
			 *      最高级：SERIALIZABLE 串行化 --- 最安全，但是由于对表加锁，效率非常低
			 */
			con.setTransactionIsolation( Connection.TRANSACTION_REPEATABLE_READ );
			
			//3 开启事务
			con.setAutoCommit( false ); //等价于 SQL：start transaction;
			
			//4 通过 con 创建 statement 对象
			Statement st = con.createStatement();
			//5 通过 st 执行 sql 语句
			
			String sql = " delete from stud where sno='s1015' ";
			st.execute( sql );
			
			sql = "select * from stud ";
			// 查询 调用  executeQuery(sql) 方法
			ResultSet resultSet = st.executeQuery(sql);
			//遍历结果集
			while ( resultSet.next() ) {
				//有两种方式从结果集中获取数据
				//方式1 通过columnIndex  不推荐 ，还需注意： columnIndex是从1开始的！！！
				String sno = resultSet.getString(1);
				//方式2 通过columnLabel  推荐
				String sname = resultSet.getString("sname");
				Integer age = resultSet.getInt("age");
				String addr = resultSet.getString("addr");
				System.out.println( sno+"\t"+sname+"\t"+age+"\t"+addr );
			}
			//因为表结构中没有 sex 这一字，段所以会出现异常，即事务会发生回滚。
			sql = " insert into stud set sex='0' where sno='s1015' ";
			st.execute( sql );
			
			con.commit(); //等价于MySQL: commit;
			System.out.println("事务完成...");
			return;
		} catch (Exception e) {
			try {
				con.rollback(); //等价于MySQL: rollback;
				System.out.println("事务回滚...");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return;
		} finally {
			System.out.println("关闭连接！！！");
			//最后关闭连接
			if( con != null ) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	
	}
}
