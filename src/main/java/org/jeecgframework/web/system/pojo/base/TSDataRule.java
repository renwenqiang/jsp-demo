package org.jeecgframework.web.system.pojo.base;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.jeecgframework.core.common.entity.IdEntity;
/**
 * 
  * @ClassName: TSDataRule 数据规则权限表
  *  TODO
  * @author Comsys-skyCc cmzcheng@gmail.com
  *  2014-8-19 下午2:19:29
 */
@Entity
@Table(name = "t_s_data_rule")
public class TSDataRule extends IdEntity implements Serializable {

	/**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 1L;
	/*
	 * 规则名称
	 */
	private String ruleName;
	/*
	 * 规则字段
	 */
	private String ruleColumn;
	/*
	 * 规则条件
	 */
	private String ruleConditions;
	/*
	 * 规则值
	 */
	private String ruleValue;
	
	
	private TSFunction TSFunction = new TSFunction();	
	@Column(name ="rule_name",nullable=false,length=32)
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	@Column(name ="rule_column",nullable=false,length=100)
	public String getRuleColumn() {
		return ruleColumn;
	}
	public void setRuleColumn(String ruleColumn) {
		this.ruleColumn = ruleColumn;
	}
	@Column(name ="rule_conditions",nullable=false,length=100)
	public String getRuleConditions() {
		return ruleConditions;
	}
	public void setRuleConditions(String ruleConditions) {
		this.ruleConditions = ruleConditions;
	}
	
	@Column(name ="rule_value",nullable=false,length=100)
	public String getRuleValue() {
		return ruleValue;
	}
	public void setRuleValue(String ruleValue) {
		this.ruleValue = ruleValue;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "functionId")
	public TSFunction getTSFunction() {
		return TSFunction;
	}

	public void setTSFunction(TSFunction tSFunction) {
		TSFunction = tSFunction;
	}
}
