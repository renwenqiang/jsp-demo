/**
 * 
 */
smart.toDataOptions=(ptFun="\"function",lenFun=ptFun.length,ptClose="}\"",lenClose=ptClose.length,function(def){
	def=$.extend({},def);
	$.extend(def,parseOtherSetting(def.otherSetting));
	delete def.otherSetting;
	delete def.children;
	delete def.define;
	delete def.element;
	
	var ele=document.createElement("div");
	sunz.applyAttributes(ele, def);
	delete def.attrs;
	delete def.attributes;
	delete def.styles;
	
	var str= $.encode(def);
	str=str.substr(1,str.length-2);
	// 计算function 
	var ptIndex=str.indexOf(ptFun);
	while(ptIndex>-1){
		var pre=str.substr(0,ptIndex)+"function";
		str=str.substr(ptIndex+lenFun);
		var cIndex=str.indexOf(ptClose);
		pre=pre+str.substring(0, cIndex).replace(/\\\"/g,"\"")+"}";
		str=str.substr(cIndex+lenClose);
		
		ptIndex=str.indexOf(ptFun);
		// 
		str=pre+str;
	}
	return str.replace(/'/g,"&quot;").replace(/\"/g,"'")+"\" "+ele.outerHTML.substr(4,ele.outerHTML.length-11);
});
smart.toTplParam=function(def){
	return {
		id:def.id,
		name:def.name,
		options:smart.toDataOptions(def),
		children:"children"
	};
};
smart.toHtml=function(designs){
	var html=tplReplace('<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>\
				<html>\
				<head>\
				<%@include file="/sunzbase.jsp"%>\
				<z:resource items="jquery,easyui,sunzui"></z:resource>\
				<z:dict items="{DICTS}"></z:dict>\
				<script type="text/javascript">\
					var id="${{*}param.id}",jobid="${{*}param.jobid}";\
					$(function(){\
						if(id||jobid){\
							var params={};if(id)params.id=id;if(jobid)params.jobid=jobid;\
							$.post("{QUERYURL}",params,function(jr){{*}\
								if(jr.success && jr.data){{*}\
									if(jr.data.length==0){{*}\
										if(jobid&&!id){{*}\
											save(); \
										}else\
											$.messager.alert("加载出错了","数据不存在！");\
									}else{{*}\
										var form=$("[type=smartForm]").asForm();\
										form.load(jr.data[0]);\
									}\
								}else{{*}\
									$.messager.alert("数据加载出错了",jr.msg);\
								}\
							});\
						}\
					});\
					function save(){\
						var form=$("[type=smartForm]").asForm();\
						if(!form.validate()){\
							$.messager.show({{*}title:"友情提示",msg:"验证未通过，请修改数据后再提交！" });\
							return;\
						}\
						var params=form.getFieldValues();if(id)params.id=id;if(jobid)params.jobid=jobid;\
						$.post(id?"{SAVEURL}":"{ADDURL}",params,function(jr){{*}if(jr.success){{*}id=jr.data.ID;jobid=jr.data.JOBID;$.messager.show({{*}title:"恭喜您",msg:"操作成功" });}else{{*}$.messager.alert("操作失败",jr.msg); }});	\
					}</script>\
				</head>\
				<body><form type="smartForm" style="width:{FORMWIDTH};height:{FORMHEIGHT}">',$.extend({FORMWIDTH:"100%",FORMHEIGHT:"100%"},window.smartForm));
	var itemToHtml=function(def){
		var define=defines[def.xtype],self=define.htmlize(def),subs='';
		$.each(def.children,function(i,sub){
			subs +=itemToHtml(sub);
		});
		return self.replace("{children}",subs);
	};
	$.each(designs,function(i,def){
		html+=itemToHtml(def);
	});
	return html+'</form></body></html>';
};
smart.toFormHtml=function(formDef,globalSetting){
	
};
smart.toListHtml=function(listDef,formDef,globalSetting){
	
};
smart.extend("Form",{
	htmlize:function(def){
		return tplReplace('<form id="{id}" class="easyui-form" data-options="{options}>{{children}}</form>'
				,smart.toTplParam(def));
	}
});
smart.extend("Panel",{
	htmlize:function(def){
		return tplReplace('<div id="{id}" class="easyui-panel" data-options="{options}>{{children}}</div>'
				,smart.toTplParam(def));
	}
});
smart.extend("Layout",{
	htmlize:function(def){
		return tplReplace('<div id="{id}" class="easyui-layout" data-options="{options}>{{children}}</div>'
				,smart.toTplParam(def));
	}
});
smart.extend("Sublayout",{
	htmlize:function(def){
		return tplReplace('<div id="{id}" data-options="{options}>{{children}}</div>'
				,smart.toTplParam(def));
	}
});
smart.extend("Tabs",{
	htmlize:function(def){
		return tplReplace('<div id="{id}" class="easyui-tabs" data-options="{options}>{{children}}</div>'
				,smart.toTplParam(def));
	}
});
smart.extend("Subtab",{
	htmlize:function(def){
		return tplReplace('<div id="{id}" data-options="{options}>{{children}}</div>'
				,smart.toTplParam(def));
	}
});
smart.extend("Tree",{
	htmlize:function(def){
		return tplReplace('<ul id="{id}" class="easyui-tree" data-options="'+(def.loadFilter?"":("loadFilter:"+this.defaults.loadFilter.toString()+","))+'{options}>{{children}}</ul>'
				,smart.toTplParam(def));
	}
});
smart.extend("Accordion",{
	htmlize:function(def){
		return tplReplace('<div id="{id}" class="easyui-accordion" data-options="{options}>{{children}}</div>'
				,smart.toTplParam(def));
	}
});
smart.extend("Subaccordion",{
	htmlize:function(def){
		return tplReplace('<div id="{id}" data-options="{options}>{{children}}</div>'
				,smart.toTplParam(def));
	}
});
smart.extend("Datagrid",{
	htmlize:function(def){
		return tplReplace('<table id="{id}" name="{name}" class="easyui-datagrid" data-options="{options}>{{children}}</table>'
				,smart.toTplParam(def));
	}
});
smart.extend("Textbox",{
	htmlize:function(def){
		return tplReplace('<input id="{id}" name="{name}"  class="easyui-textbox" data-options="{options}></input>'
				,smart.toTplParam(def));
	}
});
smart.extend("Numberbox",{
	htmlize:function(def){
		return tplReplace('<input id="{id}" name="{name}"  class="easyui-numberbox" data-options="{options}></input>'
				,smart.toTplParam(def));
	}
});
smart.extend("Numberspinner",{
	htmlize:function(def){
		return tplReplace('<input id="{id}" name="{name}"  class="easyui-numberspinner" data-options="{options}></input>'
				,smart.toTplParam(def));
	}
});
smart.extend("Calendar",{
	htmlize:function(def){
		return tplReplace('<input id="{id}" name="{name}"  class="easyui-calendar" data-options="{options}></input>'
				,smart.toTplParam(def));
	}
});
smart.extend("Datebox",{
	htmlize:function(def){
		return tplReplace('<input id="{id}" name="{name}"  class="easyui-datebox" data-options="{options}></input>'
				,smart.toTplParam(def));
	}
});
smart.extend("Datetimebox",{
	htmlize:function(def){
		return tplReplace('<input id="{id}"  name="{name}" class="easyui-datetimebox" data-options="{options}></input>'
				,smart.toTplParam(def));
	}
});
smart.extend("Datetimespinner",{
	htmlize:function(def){
		return tplReplace('<input id="{id}"  name="{name}" class="easyui-datetimespinner" data-options="{options}></input>'
				,smart.toTplParam(def));
	}
});
smart.extend("Timespinner",{
	htmlize:function(def){
		return tplReplace('<input id="{id}"  name="{name}" class="easyui-timespinner" data-options="{options}></input>'
				,smart.toTplParam(def));
	}
});
smart.extend("Dictcombo",{
	htmlize:function(def){
		return tplReplace('<input id="{id}"  name="{name}" class="easyui-dictcombo" data-options="{options}></input>'
				,smart.toTplParam(def));
	}
});
smart.extend("Slider",{
	htmlize:function(def){
		return tplReplace('<input id="{id}"  name="{name}" class="easyui-slider" data-options="{options}></input>'
				,smart.toTplParam(def));
	}
});
smart.extend("Linkbutton",{
	htmlize:function(def){
		return tplReplace('<a id="{id}"  name="{name}" class="easyui-linkbutton" data-options="{options}></a>'
				,smart.toTplParam(def));
	}
});
smart.extend("Filebox",{
	htmlize:function(def){
		return tplReplace('<input id="{id}"  name="{name}" class="easyui-filebox" data-options="{options}></input>'
				,smart.toTplParam(def));
	}
});
smart.extend("Combo",{
	htmlize:function(def){
		return tplReplace('<input id="{id}"  name="{name}" class="easyui-combo" data-options="{options}></input>'
				,smart.toTplParam(def));
	}
});
smart.extend("Combotree",{
	htmlize:function(def){
		return tplReplace('<select id="{id}"  name="{name}" class="easyui-combotree" data-options="{options}></select>'
				,smart.toTplParam(def));
	}
});
smart.extend("Combogrid",{
	htmlize:function(def){
		return tplReplace('<select id="{id}"  name="{name}" class="easyui-combogrid" data-options="{options}></select>'
				,smart.toTplParam(def));
	}
});
smart.extend("Datalist",{
	htmlize:function(def){
		return tplReplace('<ul id="{id}"  name="{name}" class="easyui-datalist" data-options="{options}></ul>'
				,smart.toTplParam(def));
	}
});
smart.extend("Propertygrid",{
	htmlize:function(def){
		return tplReplace('<table id="{id}"  name="{name}" class="easyui-propertygrid" data-options="{options}></table>'
				,smart.toTplParam(def));
	}
});
smart.extend("Treegrid",{
	htmlize:function(def){
		return tplReplace('<table id="{id}"  name="{name}" class="easyui-treegrid" data-options="{options}></table>'
				,smart.toTplParam(def));
	}
});

//
smart.toTagHtml=function(def,tagName,leaf){
	var ele=document.createElement(tagName);
	if(!(leaf==true))
		ele.innerHTML="{children}";
	smart.applyToElement(ele, def, this.attrs, this.styles);
	return ele.outerHTML;
};
smart.extend("Table",{
	htmlize:function(def){
		return smart.toTagHtml.call(this,def, "table");
	}
});
smart.extend("Tablerow",{
	htmlize:function(def){
		return smart.toTagHtml.call(this,def, "tr");
	}
});
smart.extend("Tablecell",{
	htmlize:function(def){
		return smart.toTagHtml.call(this,def, "td");
	}
});
smart.extend("Label",{
	htmlize:function(def){
		return smart.toTagHtml.call(this,def, "span",true);
	}
});
smart.extend("Div",{
	htmlize:function(def){
		return smart.toTagHtml.call(this,def, "div");
	}
});
smart.extend("Input",{
	htmlize:function(def){
		return smart.toTagHtml.call(this,def, "input",true);
	}
});
smart.extend("Textarea",{
	htmlize:function(def){
		return smart.toTagHtml.call(this,def, "textarea",true);
	}
});


/** **/
smart.extend("Smartform",{
	htmlize:function(def){
		return tplReplace(smart.toTagHtml.call(this,def, "div"),
				{children:tplReplace('<iframe width="100%" height="100%" src="framework/smartform.do?parse&smartid={smartid}&smartpart={part}&relationField={relationField}&relationid=${param.id}"></iframe>',def)}
				);
	}
});