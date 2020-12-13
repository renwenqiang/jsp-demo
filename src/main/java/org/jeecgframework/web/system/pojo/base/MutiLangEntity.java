package org.jeecgframework.web.system.pojo.base;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.jeecgframework.core.common.entity.IdEntity;

/**   
 * : Entity
 *  多语言
 * @author Rocky
 *  2014-06-28 00:09:31
 * @version V1.0   
 *
 */
@Entity
@Table(name = "t_s_muti_lang", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class MutiLangEntity extends IdEntity implements java.io.Serializable {
	/**语言主键*/
	private java.lang.String langKey;
	/**内容*/
	private java.lang.String langContext;
	/**语言*/
	private java.lang.String langCode;
	
	/**key:common.dash_board_en_us, value:Dashboard **/
	public static Map<String, String> mutiLangMap = new HashMap<String, String>(); 
	
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  语言主键
	 */
	@Column(name ="LANG_KEY",nullable=false,length=50)
	public java.lang.String getLangKey(){
		return this.langKey;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  语言主键
	 */
	public void setLangKey(java.lang.String langKey){
		this.langKey = langKey;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  内容
	 */
	@Column(name ="LANG_CONTEXT",nullable=false,length=500)
	public java.lang.String getLangContext(){
		return this.langContext;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  内容
	 */
	public void setLangContext(java.lang.String langContext){
		this.langContext = langContext;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  语言
	 */
	@Column(name ="LANG_CODE",nullable=false,length=50)
	public java.lang.String getLangCode(){
		return this.langCode;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  语言
	 */
	public void setLangCode(java.lang.String langCode){
		this.langCode = langCode;
	}	
}
