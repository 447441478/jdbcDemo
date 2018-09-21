package cn.hncu.jdbc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;

import cn.hncu.utils.ConnUtil;

/**
 * 有关大数据量的字符数据 和 二进制数据的 存储 和 读取
 * CreateTime: 2018年9月18日 下午11:26:46	
 * @author 宋进宇  Email:447441478@qq.com
 */
public class JDBC_TEXT_BLOB_Demo {
/*
 大数据量的字符数据类型有四种：TinyText 、Text 、MediumText 、LongText
 对应的最大数据长度  255个字节  64k个字节    16M个字节    4G个字节 
#建表语句
CREATE TABLE note(
	id INT PRIMARY KEY AUTO_INCREMENT,
	note TEXT
);

*/
	
	////////演示text存储和读取////////
	// 注意 存储的时候 只能使用 PreparedStatemnet
	@Test
	public void saveText() throws Exception {
		Connection con = ConnUtil.getConnection();
		String sql = " insert into note(note) values(?)";
		PreparedStatement pst = con.prepareStatement(sql);
		//当前项目中源文件：JDBCDemo.java
		File file = new File("./src/cn/hncu/jdbc/JDBCDemo.java");
		InputStream in = new FileInputStream(file);
		//关键点
		pst.setAsciiStream(1, in); //技术入口！！！
		
		pst.executeUpdate();
		
		con.close();
	}
	//读取的时候 PreparedStatemnet 和 Statemnet 都可以
	// 所以读取时就用 Statemnet 演示
	@Test
	public void readText() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		ResultSet resultSet = st.executeQuery("select * from note where id = 1");
		while( resultSet.next() ) {
			int id = resultSet.getInt("id");
			System.out.println("id : "+id);
			//关键点！！！
			InputStream in = resultSet.getBinaryStream("note");
			BufferedReader br = new BufferedReader( 
									new InputStreamReader( in, "utf-8" ) );
			String str = br.readLine();
			while( str != null ) {
				System.out.println( str );
				str = br.readLine();
			}
			//防止内存泄漏 关流
			in.close();
		}
		//关闭连接---释放资源。
		con.close();
	}
	////////////////////////////////////
	
	////////演示二进制数据的存储和读取////////
/*
大数据量的二进制数据同样类型有四种：TinyBlob 、Blob 、 MediumBlob 、 LongBlob
对应的最大数据长度  255B  64kB   16MB   4GB
#建表语句
CREATE TABLE img(
	id INT PRIMARY KEY AUTO_INCREMENT,
	img MEDIUMBLOB
);
*/	
	// 注意 存储的时候 只能使用 PreparedStatemnet
	@Test
	public void saveBLOB() throws Exception {
		Connection con = ConnUtil.getConnection();
		String sql = " insert into img(img) values(?)";
		PreparedStatement pst = con.prepareStatement(sql);
		//d盘中a目录下的图片
		File file = new File("d:/a/1.jpg");
		InputStream in = new FileInputStream(file);
		//关键点
		pst.setBinaryStream(1, in); //技术入口！！！
		
		pst.executeUpdate();
		
		con.close();
	}
	//读取的时候 PreparedStatemnet 和 Statemnet 都可以
	// 所以读取时就用 Statemnet 演示
	@Test
	public void readBLOB() throws Exception {
		Connection con = ConnUtil.getConnection();
		Statement st = con.createStatement();
		ResultSet resultSet = st.executeQuery("select * from img where id = 1");
		while( resultSet.next() ) {
			int id = resultSet.getInt("id");
			System.out.println("id : "+id);
			//关键点！！！
			InputStream in = resultSet.getBinaryStream("img");
			//创建存放位置 
			File file = new File("d:/a/blob.jpg");
			FileOutputStream out = new FileOutputStream(file);
			//进行流对拷
			byte[] b = new byte[1024]; //1KB
			int len = in.read(b);
			while ( len != -1 ) {
				out.write(b, 0, len);
				len = in.read(b);
			}
			//关流，防止内存泄漏
			in.close();
			out.close();
		}
		//关闭连接---释放资源。
		con.close();
	}
	
}
