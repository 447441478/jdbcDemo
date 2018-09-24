package cn.hncu.domain;

import org.apache.commons.dbutils.ext.Column;
import org.apache.commons.dbutils.ext.Table;

/*
建表语句
CREATE TABLE pet(
	id INT PRIMARY KEY AUTO_INCREMENT,
	NAME VARCHAR(20),
	color VARCHAR(10)
); 
#初始化数据
INSERT INTO pet(NAME,color) VALUES('小黄','黄色');
INSERT INTO pet(NAME,color) VALUES('小花','花色');
INSERT INTO pet(NAME,color) VALUES('小红','红色');
 */
@Table(value="pet") //注意这是DBUtils 扩展包的功能
public class Pet {
	@Column
	private Integer id; //id 主键
	
	//***属性名与表字段名不一致时需要给value***,同事还要把set和get方法修改
	@Column(value="name") 
	private String petName; //宠物名称,为演示DBUtils功能特意搞成不一致，开发时尽量一致！！！
	
	@Column
	private String color; //宠物颜色

	public Pet() {
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return petName;
	}
	public void setName(String petName) {
		this.petName = petName;
	}

	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "Pet [id=" + id + ", petName=" + petName + ", color=" + color + "]";
	}
	
}
