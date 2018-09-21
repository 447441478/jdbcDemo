package cn.hncu.exportOffice;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import cn.hncu.utils.MyConnPool3;

/**
 * 演示java生成Excel文件
 * CreateTime: 2018年9月20日 下午10:40:29	
 * @author 宋进宇  Email:447441478@qq.com
 */
public class ExportExcelDemo {

	/* 
	 * 技术入口：使用apache公司的poi工具包
	 * 该插件可以生成许多office文件，如Excel、Word、PPT等
	 * 而office文件又分两个版本：2007以前的是xls、doc、ppt等后缀名
	 * 2007以后则是：xlsx、docx、pptx
	 * xls使用 org.apache.poi.hssf.usermodel.HSSFWorkbook 类入口 
	 * 使用poi-xxx.jar包
	 * 
	 * xlsx使用 org.apache.poi.xssf.usermodel.XSSFWorkbook 类入口 
	 * 使用poi-xxx.jar、poi-ooxml-xxx.jar、poi-ooxml-schemas-xxx.jar 、xmlbeans-xxx.jar等包，不同版本所需jar包不同。
	 */
	//演示生成 xls Excel
	@Test
	public void simpleXlsDemo() throws Exception {
		//1 创建 XSSFWorkbook对象，就是一个Excel文件对象
		HSSFWorkbook workbook = new HSSFWorkbook();
		//2 创建表sheet
		HSSFSheet sheet = workbook.createSheet("测试");
		//3 创建一行, 0表示为第一行
		HSSFRow row = sheet.createRow(4);
		//4 创建单元格, 0表示为第一行
		HSSFCell cell = row.createCell(3);
		cell.setCellValue("湖南城市学院");
		
		//序列化
		workbook.write( new FileOutputStream("d:/a/test.xls"));
	}
	
	//演示生成 xlsx Excel
	@Test
	public void simpleXlsxDemo() throws Exception {
		//1 创建 XSSFWorkbook对象，就是一个Excel文件对象
		XSSFWorkbook workBook = new XSSFWorkbook();
		//2 创建表sheet
		XSSFSheet sheet = workBook.createSheet("测试");
		//3 创建一行, 0表示为第一行
		XSSFRow row = sheet.createRow(4);
		//4 创建单元格, 0表示为第一行
		XSSFCell cell = row.createCell(3);
		cell.setCellValue("湖南城市学院");
		
		//序列化
		workBook.write( new FileOutputStream("d:/a/t.xlsx"));
	}
	
	//演示把某个数据库中的所有表数据导出
	@Test
	public void export() throws Exception {
		exportDB("hncu");
	}
	/**
	 * 把dbName中的所有表数据导出
	 * @param dbName 数据库名
	 * @throws Exception 
	 */
	public void exportDB(String dbName) throws Exception {
		Connection con = MyConnPool3.getConnection();
		Statement st = con.createStatement();
		//获取数据库元数据对象
		DatabaseMetaData dbmd = con.getMetaData();
		//通过 dbmd 获取 dbName 中的所有表名
		ResultSet tables = dbmd.getTables(dbName, dbName, null, new String[]{"TABLE"});
		/* 下面这种做法逻辑上是没有问题的，
		 * 但是 每个 ResultSet类对象 就相当于 与 mysql数据库进行通讯，
		 * 如果一个 用户 发生了同时发生了两次通讯，即很有可能会发生通讯混乱。
		 * 所以，严禁使用 两个 ResultSet类对象进行嵌套遍历！！！
		while( tables.next() ) {
			String tableName = tables.getString("TABLE_NAME");
			ResultSet resultSet = st.executeQuery( "select * from "+dbName+"."+tableName );
			while( resultSet.next() ) {
				//...
			}
		}
		*/
		//正确做法
		//先把所有表名查出并存起来
		List<String> tableNames = new ArrayList<String>();
		while( tables.next() ) {
			String tableName = tables.getString("TABLE_NAME");
			tableNames.add(tableName);
		}
		//再依次查询每一个表
		//因为要把数据以Excel格式导出，所有要生成Excel类对象
		//1 生成Excel类对象
		XSSFWorkbook workbook = new XSSFWorkbook();
		//2 遍历tableNames
		for (String tableName : tableNames) {
			//3  每一张表对应 一个sheet
			XSSFSheet sheet = workbook.createSheet(tableName);
			//4 查询数据库表数据
			ResultSet resultSet = st.executeQuery( "select * from "+dbName+"."+tableName );
			//5 获取查询结果的表头
			ResultSetMetaData tbMd = resultSet.getMetaData();
			//6 获取查询结果总共有几列
			int cols = tbMd.getColumnCount();
			//7 把表头数据存储到Excel中
			int idx = 0;
			XSSFRow row = sheet.createRow(idx++);
			for (int i = 0; i < cols; i++) {
				//从tbMd获取表头信息
				//注意：java.sql包中大多数getxxx方法的int型参数都是 以 1 为起始
				String label = tbMd.getColumnLabel( i+1 ); 
				//创建一个单元格
				XSSFCell cell = row.createCell(i);
				//给单元格存入数据
				cell.setCellValue(label);
				//打印 数据类型
				System.out.print(tbMd.getColumnTypeName(i+1)+" ");
			}
			System.out.println();
			//8 把表数据存储到Excel中
			while( resultSet.next() ) {
				row = sheet.createRow(idx++);
				for (int i = 0; i < cols; i++) {
					XSSFCell cell = row.createCell(i);
					Object data = null;
					if( tbMd.getColumnTypeName(i+1).contains("LOB") ) {
						data = "图片："+ tbMd.getColumnTypeName(i+1);
					}else {
						data = resultSet.getObject( i+1 );
					}
					cell.setCellValue( data.toString() );
				}
			}
		}
		//9 序列化---把Excel树存储到磁盘中
		workbook.write( new FileOutputStream("d:/a/"+dbName+".xlsx"));
		
	}
	
}
