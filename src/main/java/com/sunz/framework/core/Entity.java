package com.sunz.framework.core;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

/**
 * 	具有32位字符串作id属性的实体
 *		统一对id及其注解定义
 * 
 * @author Xingzhe
 *
 */
@MappedSuperclass
public abstract class Entity implements IEntity {
	/**
	 * id
	 */
	protected String id;

	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
