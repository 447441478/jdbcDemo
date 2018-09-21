package cn.hncu.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;

import cn.hncu.utils.ConnUtil;

/**
 * 演示获取自动增长字段值和批量处理
 * CreateTime: 2018年9月18日 下午10:32:39	
 * @author 宋进宇  Email:447441478@qq.com
 */
public class JDBCDemo2 {

	/////////获取自动增长字段的值///////////
	//演示 Statement 获取
	@Test //为演示直观 异常直接抛了
	public void saveAutoIncrement1() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		
		String sql = "insert into tb_user(username,password) values('刘备','8888')";
		/*
		 * 采用 Statement.RETURN_GENERATED_KEYS 参数
		 * 配合getGeneratedKeys()方法即可获取自动增长字段的值
		 */
		//关键 1
		st.execute(sql, Statement.RETURN_GENERATED_KEYS);
		//关键 2
		ResultSet keys = st.getGeneratedKeys();
		while(keys.next()) {
			int id = keys.getInt(1); 
			System.out.println( "id:" + id );
		}
		
		con.close();
	}
	
	//演示 PreparedStatement 获取
	@Test //为演示直观 异常直接抛了
	public void saveAutoIncrement2() throws Exception {
		Connection con = ConnUtil.getConnection();
		
		String sql = "insert into tb_user(username,password) values(?,?)";
		
		//关键 1
		PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		// 补全
		pst.setString(1,"admin");
		pst.setString(2,"pwd");
		
		pst.executeUpdate(); //注意这里是空参方法
		//关键 2
		ResultSet keys = pst.getGeneratedKeys();
		while(keys.next()) {
			int id = keys.getInt(1); 
			System.out.println( "id:" + id );
		}
		
		con.close();
	}
	////////////////////////////////////
	
	
	
	/////////////演示批量处理/////////////
	//演示 Statement 批量处理
	@Test //为演示直观 异常直接抛了
	public void batchDemo1() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		
		for (int i = 1; i <= 5; i++) {
			String sql = "insert into tb_user(username,password) values('刘备"+i+"','8888')";
			// 批量处理关键 1
			st.addBatch(sql);
		}
		// 把所有id都+100
		String sql = "update tb_user set id = id+100";
		st.addBatch(sql);
		
		// 批量处理关键 2
		int[] executeBatch = st.executeBatch();
		//打印批量处理后 返回的受影响的行数
		for (int i : executeBatch) {
			System.out.print(i+" ");
		}
		
		con.close();
	}
	
	//演示 PreparedStatement 批量处理
	@Test //为演示直观 异常直接抛了
	public void batchDemo2() throws Exception {
		Connection con = ConnUtil.getConnection();
		String sql = "insert into tb_user(username,password) values(?,?)";
		PreparedStatement pst = con.prepareStatement(sql);
		for( int i = 1; i <= 5; i++ ) {
			pst.setString(1, "Jack"+i);
			pst.setString(2, "pwd"+i);
			//关键步骤 1
			pst.addBatch(); //注意 与上面不同 这里是空参！！！
		}
		// PreparedStatement 也可以这样添加一条sql语句到批量处理中
		sql = "delete from tb_user where id%2=1"; // 删除id值为偶数的记录
		// 但是要注意的是: 以下面这种方式 sql 语句必须是完整的 不能有 '占位符' ！！！
		pst.addBatch(sql);
		
		// 关键步骤2
		int[] executeBatch = pst.executeBatch();
		//打印批量处理后 返回的受影响的行数
		for (int i : executeBatch) {
			System.out.print(i+" ");
		}
	}
}
