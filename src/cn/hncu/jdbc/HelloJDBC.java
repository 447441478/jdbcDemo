package cn.hncu.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HelloJDBC {
	
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
			//3 通过 con 创建 statement 对象
			Statement st = con.createStatement();
			//4 通过 st 执行 sql 语句
			String sql = "select * from stud";
			//增、改、删 调用 execute(sql) 方法
			//st.execute(" insert into stud values('s1015','石五',15,'石门') ");
			//st.execute(" update stud set sname='十五' where sno='s1015' ");
			//st.execute(" delete from stud where sno='s1015' ");
			
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
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			//知识点： 在try-catch-finally 语句中，无论是在 try中return 还是在 catch 中return
			//       都会执行 finally 中语句！！！。
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
