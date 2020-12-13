package org.jeecgframework.core.extend.hqlsearch;

import org.jeecgframework.core.extend.hqlsearch.parse.vo.HqlRuleEnum;
import org.jeecgframework.core.util.ResourceUtil;
import org.jeecgframework.web.system.pojo.base.TSDataRule;

/**
 * 数据库列表序列化转换sql
 * 
 * @ClassName: SqlJsonConvert
 *  TODO
 * @author Comsys-skyCc cmzcheng@gmail.com
 *  2014-8-25 下午7:12:41
 * 
 */
public class SysContextSqlConvert {

	enum Signal {
		GREEN, YELLOW, RED
	}

	/**
	 * setSqlModel sql行列转换
	 * @param dataRule
	 * @return
	 */
	public static String setSqlModel(TSDataRule dataRule){
		if(dataRule == null) 
		return "";
		String sqlValue="";
		HqlRuleEnum ruleEnum=HqlRuleEnum.getByValue(dataRule.getRuleConditions());

		//-----------------------------------------------------------------------
		//#{sys_user_code}%
		String ValueTemp = dataRule.getRuleValue();
		String moshi = "";
		if(ValueTemp.indexOf("}")!=-1){
			 moshi = ValueTemp.substring(ValueTemp.indexOf("}")+1);
		}
		String returnValue = null;
		//针对特殊标示处理#{sysOrgCode}，判断替换
		if (ValueTemp.contains("#{")) {
			ValueTemp = ValueTemp.substring(2,ValueTemp.indexOf("}"));
		} else {
			ValueTemp = ValueTemp;
		}
		//-----------------------------------------------------------------------
		String tempValue = null;
		//---author:jg_xugj----start-----date:20151226--------for：#814 【数据权限】扩展支持写表达式，通过session取值
		tempValue = ResourceUtil.converRuleValue(ValueTemp);
		//---author:jg_xugj----end-----date:20151226--------for：#814 【数据权限】扩展支持写表达式，通过session取值

		if(tempValue!=null){
			tempValue = tempValue + moshi;
		}else{
			tempValue = ValueTemp;
		}
		switch (ruleEnum) {
		//------------------update-begin-------20150730----author:zhoujf------for:-大于号控制修正------------
		case GT:
			sqlValue+=" and "+dataRule.getRuleColumn()+" >'"+tempValue+"'";
			break;
		//------------------update-end-------20150730----author:zhoujf------for:-大于号控制修正------------
		case GE:
			sqlValue+=" and "+dataRule.getRuleColumn()+" >='"+tempValue+"'";
			break;
		case LT:
			sqlValue+=" and "+dataRule.getRuleColumn()+" <'"+tempValue+"'";
			break;
		case LE:
			sqlValue+=" and "+dataRule.getRuleColumn()+" <= '"+tempValue+"'";
			break;
		case  EQ:
			sqlValue+=" and "+dataRule.getRuleColumn()+" ='"+tempValue+"'";
			break;
		case LIKE:
			sqlValue+=" and "+dataRule.getRuleColumn()+" like '"+tempValue+"'";
			break;
		case NE:
			sqlValue+=" and "+dataRule.getRuleColumn()+" !='"+tempValue+"'";
			break;
		case IN:
			sqlValue+=" and "+dataRule.getRuleColumn()+" IN('"+tempValue+"')";
		default:
			break;
		}
		
		
		return sqlValue;
	}
}
