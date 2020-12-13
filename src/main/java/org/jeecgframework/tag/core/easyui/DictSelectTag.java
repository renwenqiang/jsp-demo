package org.jeecgframework.tag.core.easyui;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.jeecgframework.web.system.pojo.base.TSType;
import org.jeecgframework.web.system.pojo.base.TSTypegroup;
import org.jeecgframework.web.system.service.SystemService;

import com.sunz.framework.core.JsonHelper;
import com.sunz.framework.util.StringUtil;
import org.jeecgframework.core.util.ApplicationContextUtil;
import org.jeecgframework.core.util.MutiLangUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * 选择下拉框
 * 
 *  日期：2013-04-18
 * @version 1.0
 */
public class DictSelectTag extends TagSupport {

	private static final long serialVersionUID = 1;
	private String typeGroupCode; // 数据字典类型
	private String field; // 选择表单的Name EAMPLE:<select name="selectName" id = ""
							// />
	private String id; // 选择表单ID EAMPLE:<select name="selectName" id = "" />
	private String defaultVal; // 默认值
	private String divClass; // DIV样式
	private String labelClass; // Label样式
	private String title; // label显示值
	private boolean hasLabel = true; // 是否显示label
	private String type;// 控件类型select|radio|checkbox
	private String dictTable;// 自定义字典表
	private String dictField;// 自定义字典表的匹配字段-字典的编码值
	private String dictText;// 自定义字典表的显示文本-字典的显示值
	private String extendJson;//扩展参数
    
	//update-begin--Author:jg_xugj  许国杰  Date:20151209 for：#775 增加只读属性
	private String readonly;// 只读属性
    public String getReadonly() {
		return readonly;
	}
	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}
    //update-end--Author:jg_xugj 许国杰  Date:20151209 for：#775 增加只读属性

	//  update-begin--Author:jg_longjb龙金波  Date:20150313 for：增加查询条件属性
	private String dictCondition;
	public String getDictCondition() {
		return dictCondition;
	}

	public void setDictCondition(String dicCondition) {
		this.dictCondition = dicCondition;
	}
//  update-end--Author:jg_longjb龙金波  Date:20150313 for：增加查询条件属性
//	update-start--Author:jg_huangxg  Date:20151116 for：增加datatype属性
	private String datatype;
	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
//	update-end--Author:jg_longjb龙金波  Date:20150313 for：增加datatype属性	
	@Autowired
	private static SystemService systemService;

	public int doStartTag() throws JspTagException {
		return EVAL_PAGE;
	}

	public int doEndTag() throws JspTagException {
		try {
			JspWriter out = this.pageContext.getOut();
			out.print(end().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

	public StringBuffer end() {
		
		StringBuffer sb = new StringBuffer();
		if (StringUtil.isEmpty(divClass)) {
			divClass = "form"; // 默认form样式
		}
		if (StringUtil.isEmpty(labelClass)) {
			labelClass = "Validform_label"; // 默认label样式
		}
		if (dictTable != null) {
			List<Map<String, Object>> list = queryDic();
			if ("radio".equals(type)) {
				for (Map<String, Object> map : list) {
					radio(map.get("text").toString(), map.get("field")
							.toString(), sb);
				}
			} else if ("checkbox".equals(type)) {
				for (Map<String, Object> map : list) {
					checkbox(map.get("text").toString(), map.get("field")
							.toString(), sb);
				}
			} else if("text".equals(type)){
				for (Map<String, Object> map : list) {
					text(map.get("text").toString(), map.get("field")
							.toString(), sb);
				}
			}else {
				sb.append("<select name=\"" + field + "\"");
				
				this.readonly(sb);
				
				//增加扩展属性
				if (!StringUtil.isEmpty(this.extendJson)) {
					Map<String, String> mp = JsonHelper.toBean(extendJson, Map.class);
					for(Map.Entry<String, String> entry: mp.entrySet()) { 
						sb.append(entry.getKey()+"=\"" + entry.getValue() + "\"");
						} 
				}
				if (!StringUtil.isEmpty(this.id)) {
					sb.append(" id=\"" + id + "\"");
				}
				sb.append(">");
				//update-begin--Author:zhangdaihao  Date:20140724 for：[bugfree号]默认选择项目--------------------
				select("common.please.select", "", sb);
				//update-end--Author:zhangdaihao  Date:20140724 for：[bugfree号]默认选择项目----------------------
				for (Map<String, Object> map : list) {
					select(map.get("text").toString(), map.get("field").toString(), sb);
				}
				sb.append("</select>");
			}
		} else {
			TSTypegroup typeGroup = TSTypegroup.allTypeGroups.get(this.typeGroupCode.toLowerCase());
			List<TSType> types = TSTypegroup.allTypes.get(this.typeGroupCode.toLowerCase());
			if (hasLabel) {
				sb.append("<div class=\"" + divClass + "\">");
				sb.append("<label class=\"" + labelClass + "\" >");
			}
			if (typeGroup != null) {
				if (hasLabel) {
					if (StringUtil.isEmpty(this.title)) {
						this.title = MutiLangUtil.getMutiLangInstance().getLang(typeGroup.getTypegroupname());
					}
					sb.append(this.title + ":");
					sb.append("</label>");
				}
				if ("radio".equals(type)) {
					for (TSType type : types) {
						radio(type.getTypename(), type.getTypecode(), sb);
					}
				} else if ("checkbox".equals(type)) {
					for (TSType type : types) {
						checkbox(type.getTypename(), type.getTypecode(), sb);
					}
				}else if ("text".equals(type)) {
					for (TSType type : types) {
						text(type.getTypename(), type.getTypecode(), sb);
					}
				} else {
					sb.append("<select name=\"" + field + "\"");
					//update-begin--Author:jg_xugj  许国杰  Date:20151209 for：#775 增加只读属性
					this.readonly(sb);
				    //update-end--Author:jg_xugj 许国杰  Date:20151209 for：#775 增加只读属性
					
					//增加扩展属性
					if (!StringUtil.isEmpty(this.extendJson)) {
						Map<String, String> mp = JsonHelper.toBean(extendJson, Map.class);
						for(Map.Entry<String, String> entry: mp.entrySet()) { 
							sb.append(" "+entry.getKey()+"=\"" + entry.getValue() + "\"");
							} 
					}
					if (!StringUtil.isEmpty(this.id)) {
						sb.append(" id=\"" + id + "\"");
					}
					this.datatype(sb);
					sb.append(">");
					//update-begin--Author:zhangdaihao  Date:20140724 for：[bugfree号]默认选择项目--------------------
					select("common.please.select", "", sb);
					//update-end--Author:zhangdaihao  Date:20140724 for：[bugfree号]默认选择项目----------------------
					for (TSType type : types) {
						select(type.getTypename(), type.getTypecode(), sb);
					}
					sb.append("</select>");
				}
				if (hasLabel) {
					sb.append("</div>");
				}
			}
		}

		return sb;
	}
	/**
	 * 文本框方法
	 * @param name
	 * @param code
	 * @param sb
	 */
	private void text(String name, String code, StringBuffer sb) {
		if (code.equals(this.defaultVal)) {
			sb.append("<input name='"+field+"'"+" id='"+id+"' value='" + MutiLangUtil.getMutiLangInstance().getLang(name) + "' readOnly = 'readOnly' />");
		} else {
		}
	}


	/**
	 * 单选框方法
	 * 
	 * @作者：Alexander
	 * 
	 * @param name
	 * @param code
	 * @param sb
	 */
	private void radio(String name, String code, StringBuffer sb) {
		if (code.equals(this.defaultVal)) {
			sb.append("<input type=\"radio\" name=\"" + field
					+ "\" checked=\"checked\" value=\"" + code + "\"");
			if (!StringUtil.isEmpty(this.id)) {
				sb.append(" id=\"" + id + "\"");
			}
			//update-begin--Author:jg_xugj  许国杰  Date:20151209 for：#775 增加只读属性
			this.readonly(sb);
		    //update-end--Author:jg_xugj 许国杰  Date:20151209 for：#775 增加只读属性

			this.datatype(sb);
			sb.append(" />");
		} else {
			sb.append("<input type=\"radio\" name=\"" + field + "\" value=\""
					+ code + "\"");
			if (!StringUtil.isEmpty(this.id)) {
				sb.append(" id=\"" + id + "\"");
			}
			//update-begin--Author:jg_xugj  许国杰  Date:20151209 for：#775 增加只读属性
			this.readonly(sb);
		    //update-end--Author:jg_xugj 许国杰  Date:20151209 for：#775 增加只读属性
			this.datatype(sb);
			sb.append(" />");
		}
		sb.append(MutiLangUtil.getMutiLangInstance().getLang(name));
	}

	/**
	 * 复选框方法
	 * 
	 * @作者：Alexander
	 * 
	 * @param name
	 * @param code
	 * @param sb
	 */
	private void checkbox(String name, String code, StringBuffer sb) {
		String[] values = this.defaultVal.split(",");
		Boolean checked = false;
		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			if (code.equals(value)) {
				checked = true;
				break;
			}
			checked = false;
		}
		if(checked){
			sb.append("<input type=\"checkbox\" name=\"" + field
					+ "\" checked=\"checked\" value=\"" + code + "\"");
			if (!StringUtil.isEmpty(this.id)) {
				sb.append(" id=\"" + id + "\"");
			}
			//update-begin--Author:jg_xugj  许国杰  Date:20151209 for：#775 增加只读属性
			this.readonly(sb);
		    //update-end--Author:jg_xugj 许国杰  Date:20151209 for：#775 增加只读属性
			this.datatype(sb);
			sb.append(" />");
		} else {
			sb.append("<input type=\"checkbox\" name=\"" + field
					+ "\" value=\"" + code + "\"");
			if (!StringUtil.isEmpty(this.id)) {
				sb.append(" id=\"" + id + "\"");
			}
			//update-begin--Author:jg_xugj  许国杰  Date:20151209 for：#775 增加只读属性
			this.readonly(sb);
		    //update-end--Author:jg_xugj 许国杰  Date:20151209 for：#775 增加只读属性
			this.datatype(sb);
			sb.append(" />");
		}
		sb.append(MutiLangUtil.getMutiLangInstance().getLang(name));
	}

	/**
	 * 选择框方法
	 * 
	 * @作者：Alexander
	 * 
	 * @param name
	 * @param code
	 * @param sb
	 */
	private void select(String name, String code, StringBuffer sb) {
		if (code.equals(this.defaultVal)) {
			sb.append(" <option value=\"" + code + "\" selected=\"selected\">");
		} else {
			sb.append(" <option value=\"" + code + "\">");
		}
		sb.append(MutiLangUtil.getMutiLangInstance().getLang(name));
		sb.append(" </option>");
	}

	/**
	 * 查询自定义数据字典
	 * 
	 * @作者：Alexander
	 */
	private List<Map<String, Object>> queryDic() {
		String sql = "select " + dictField + " as field," + dictText
				+ " as text from " + dictTable;
//  update-begin--Author:jg_longjb龙金波  Date:20150313 for：增加查询条件属性 例如：where type='xxx'
	       if(dictCondition!=null){
	           sql+=dictCondition;
	       }
//  update--end--Author:jg_longjb龙金波  Date:20150313 for：增加查询条件属性
		systemService = ApplicationContextUtil.getContext().getBean(
				SystemService.class);
		List<Map<String, Object>> list = systemService.findForJdbc(sql);
		return list;
	}
	
	/**
	 * 加入datatype属性,并加入非空验证作为默认值
	 * @param sb
	 * @return
	 */
	private StringBuffer datatype(StringBuffer sb){
		if (!StringUtil.isEmpty(this.datatype)) {
			sb.append(" datatype=\"" + datatype + "\"");
		}
		return sb;
	}
	
	/**
	 * 加入readonly 属性,当此属性值为 readonly时，设置控件只读
	 * @author jg_xugj
	 * @param sb
	 * @return sb
	 */
	private StringBuffer readonly(StringBuffer sb){
		if(!StringUtil.isEmpty(readonly) &&readonly.equals("readonly")){
			if ("radio".equals(type)) {
				sb.append(" disable= \"disabled\" disabled=\"disabled\" ");
			}
			else if ("checkbox".equals(type)) {
				sb.append(" disable= \"disabled\" disabled=\"disabled\" ");
			}
			else if ("text".equals(type)) {
				
			} 
			else {
				sb.append(" disable= \"disabled\" disabled=\"disabled\" ");
			}
		}
		return sb;
	}

	public String getTypeGroupCode() {
		return typeGroupCode;
	}

	public void setTypeGroupCode(String typeGroupCode) {
		this.typeGroupCode = typeGroupCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDefaultVal() {
		return defaultVal;
	}

	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}

	public String getDivClass() {
		return divClass;
	}

	public void setDivClass(String divClass) {
		this.divClass = divClass;
	}

	public String getLabelClass() {
		return labelClass;
	}

	public void setLabelClass(String labelClass) {
		this.labelClass = labelClass;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isHasLabel() {
		return hasLabel;
	}

	public void setHasLabel(boolean hasLabel) {
		this.hasLabel = hasLabel;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDictTable() {
		return dictTable;
	}

	public void setDictTable(String dictTable) {
		this.dictTable = dictTable;
	}

	public String getDictField() {
		return dictField;
	}

	public void setDictField(String dictField) {
		this.dictField = dictField;
	}

	public String getDictText() {
		return dictText;
	}

	public void setDictText(String dictText) {
		this.dictText = dictText;
	}
	public String getExtendJson() {
		return extendJson;
	}

	public void setExtendJson(String extendJson) {
		this.extendJson = extendJson;
	}
}