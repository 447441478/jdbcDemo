package cn.hncu.utils;

import java.io.IOException;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.Executor;

/* 虽然已经弥补了上个版本的缺点，
 * 但是发现只是为了修改一个方法的功能，却增加了这么多的代码量，
 * 看似工作量很大，但不符合开发原则。
 * 那怎么办？
 * 简单，采用动态代理的方式，动态代理同样可以实现该功能，而且代码量少。
 */
public class MyConnPool2 {
	private static Queue<Connection> pool = new LinkedList<Connection>();
	private static int size = 3;
	static {
		Properties p = new Properties();
		try {
			p.load( MyConnPool2.class.getClassLoader().getResourceAsStream("myConnPool.properties"));
			String driver = p.getProperty("driver");
			String url = p.getProperty("url");
			String username = p.getProperty("username");
			String password = p.getProperty("password");
			String strSize = p.getProperty("size");
			size = Integer.valueOf( strSize );
			Class.forName( driver );
			for( int i = 0; i < size; i++ ) {
				Connection con = DriverManager.getConnection(url, username, password);
				//把 Connection 包装成 MyConnection
				con = new MyConnection(con);
				pool.add(con);
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public synchronized static Connection getConnection() {
		if( pool.size() <= 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return getConnection();
		}
		return pool.poll();
	}
	
	
	static class MyConnection implements Connection{
		private Connection con=null;
		public MyConnection(Connection con) {
			this.con = con;
		}
		public void close(){
			//还回池中
			System.out.println("还回来一个con...");
			pool.add(this);
		}
		
		/////////////以下方法的功能全是用原Connection默认的，即直接用包装的con对象去执行/////////////////////
		
		@Override
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return con.unwrap(iface);
		}
		@Override
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return con.isWrapperFor(iface);
		}
		@Override
		public Statement createStatement() throws SQLException {
			return con.createStatement();
		}
		@Override
		public PreparedStatement prepareStatement(String sql)
				throws SQLException {
			return con.prepareStatement(sql);
		}
		@Override
		public CallableStatement prepareCall(String sql) throws SQLException {
			return con.prepareCall(sql);
		}
		@Override
		public String nativeSQL(String sql) throws SQLException {
			return con.nativeSQL(sql);
		}
		@Override
		public void setAutoCommit(boolean autoCommit) throws SQLException {
			con.setAutoCommit(autoCommit);
		}
		@Override
		public boolean getAutoCommit() throws SQLException {
			return con.getAutoCommit();
		}
		@Override
		public void commit() throws SQLException {
			con.commit();
		}
		@Override
		public void rollback() throws SQLException {
			con.rollback();
		}
		@Override
		public boolean isClosed() throws SQLException {
			return con.isClosed();
		}
		@Override
		public DatabaseMetaData getMetaData() throws SQLException {
			return con.getMetaData();
		}
		@Override
		public void setReadOnly(boolean readOnly) throws SQLException {
			con.setReadOnly(readOnly);
		}
		@Override
		public boolean isReadOnly() throws SQLException {
			return con.isReadOnly();
		}
		@Override
		public void setCatalog(String catalog) throws SQLException {
			con.setCatalog(catalog);
		}
		@Override
		public String getCatalog() throws SQLException {
			return con.getCatalog();
		}
		@Override
		public void setTransactionIsolation(int level) throws SQLException {
			con.setTransactionIsolation(level);
		}
		@Override
		public int getTransactionIsolation() throws SQLException {
			return con.getTransactionIsolation();
		}
		@Override
		public SQLWarning getWarnings() throws SQLException {
			return con.getWarnings();
		}
		@Override
		public void clearWarnings() throws SQLException {
			con.clearWarnings();
		}
		@Override
		public Statement createStatement(int resultSetType,
				int resultSetConcurrency) throws SQLException {
			return con.createStatement(resultSetType, resultSetConcurrency);
		}
		@Override
		public PreparedStatement prepareStatement(String sql,
				int resultSetType, int resultSetConcurrency)
				throws SQLException {
			return con.prepareStatement(sql, resultSetType, resultSetConcurrency);
		}
		@Override
		public CallableStatement prepareCall(String sql, int resultSetType,
				int resultSetConcurrency) throws SQLException {
			return con.prepareCall(sql, resultSetType, resultSetConcurrency);
		}
		@Override
		public Map<String, Class<?>> getTypeMap() throws SQLException {
			return con.getTypeMap();
		}
		@Override
		public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
			con.setTypeMap(map);
		}
		@Override
		public void setHoldability(int holdability) throws SQLException {
			con.setHoldability(holdability);
		}
		@Override
		public int getHoldability() throws SQLException {
			return con.getHoldability();
		}
		@Override
		public Savepoint setSavepoint() throws SQLException {
			return con.setSavepoint();
		}
		@Override
		public Savepoint setSavepoint(String name) throws SQLException {
			return con.setSavepoint(name);
		}
		@Override
		public void rollback(Savepoint savepoint) throws SQLException {
			con.rollback(savepoint);
		}
		@Override
		public void releaseSavepoint(Savepoint savepoint) throws SQLException {
			con.releaseSavepoint(savepoint);
		}
		@Override
		public Statement createStatement(int resultSetType,
				int resultSetConcurrency, int resultSetHoldability)
				throws SQLException {
			return con.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		}
		@Override
		public PreparedStatement prepareStatement(String sql,
				int resultSetType, int resultSetConcurrency,
				int resultSetHoldability) throws SQLException {
			return con.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}
		@Override
		public CallableStatement prepareCall(String sql, int resultSetType,
				int resultSetConcurrency, int resultSetHoldability)
				throws SQLException {
			return con.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}
		@Override
		public PreparedStatement prepareStatement(String sql,
				int autoGeneratedKeys) throws SQLException {
			return con.prepareStatement(sql, autoGeneratedKeys);
		}
		@Override
		public PreparedStatement prepareStatement(String sql,
				int[] columnIndexes) throws SQLException {
			return con.prepareStatement(sql, columnIndexes);
		}
		@Override
		public PreparedStatement prepareStatement(String sql,
				String[] columnNames) throws SQLException {
			return con.prepareStatement(sql, columnNames);
		}
		@Override
		public Clob createClob() throws SQLException {
			return con.createClob();
		}
		@Override
		public Blob createBlob() throws SQLException {
			return con.createBlob();
		}
		@Override
		public NClob createNClob() throws SQLException {
			return con.createNClob();
		}
		@Override
		public SQLXML createSQLXML() throws SQLException {
			return con.createSQLXML();
		}
		@Override
		public boolean isValid(int timeout) throws SQLException {
			return con.isValid(timeout);
		}
		@Override
		public void setClientInfo(String name, String value)
				throws SQLClientInfoException {
			con.setClientInfo(name,value);
		}
		@Override
		public void setClientInfo(Properties properties)
				throws SQLClientInfoException {
			con.setClientInfo(properties);
		}
		@Override
		public String getClientInfo(String name) throws SQLException {
			return con.getClientInfo(name);
		}
		@Override
		public Properties getClientInfo() throws SQLException {
			return con.getClientInfo();
		}
		@Override
		public Array createArrayOf(String typeName, Object[] elements)
				throws SQLException {
			return con.createArrayOf(typeName, elements);
		}
		@Override
		public Struct createStruct(String typeName, Object[] attributes)
				throws SQLException {
			return con.createStruct(typeName, attributes);
		}
		@Override
		public void setSchema(String schema) throws SQLException {
			con.setSchema(schema);
		}
		@Override
		public String getSchema() throws SQLException {
			return con.getSchema();
		}
		@Override
		public void abort(Executor executor) throws SQLException {
			con.abort(executor);
		}
		@Override
		public void setNetworkTimeout(Executor executor, int milliseconds)
				throws SQLException {
			con.setNetworkTimeout(executor, milliseconds);
		}
		@Override
		public int getNetworkTimeout() throws SQLException {
			return con.getNetworkTimeout();
		}
	}
}