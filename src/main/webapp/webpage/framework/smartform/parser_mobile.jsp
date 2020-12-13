<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<z:dict items="${setting.DICTS}"></z:dict>
<style type="text/css">
<!--
body{margin:2px;}
div.proxy-container{width:100%;height:100%;}
.autoSize,div.proxy-select {display:inline-block;*display:inline;*zoom:1; }
.fit{width:100% !important;height:100% !important;}
-->
</style>
</head>
<body></body>
</html>
<script type="text/javascript" src="webpage/framework/smartform/interface.js"></script>
<script type="text/javascript" src="webpage/framework/smartform/components/defines.js"></script>
<c:forEach items="${exdefines}" var="exdefine"><script type="text/javascript" src="webpage/framework/smartform/components/exdefines/${exdefine}"></script>
</c:forEach>
<script type="text/javascript" src="webpage/framework/smartform/parse.js"></script>

<script type="text/javascript">
    var formCode="${setting.CODE}",formName="${setting.NAME}",formWidth="100%",formHeight="100%";
	var queryUrl="${setting.QUERYURL}",addUrl="${setting.ADDURL}",saveUrl="${setting.SAVEURL}",deleteUrl="${setting.DELETEURL}";
	var formRoot='<form style="width:'+formWidth+';height:'+formHeight+';"></form>';
	var id="${param.id}",jobid="${param.jobid}";
	var relationField="${param.relationField}",relationid="${param.relationid}";
	var jobkey='${param.jobkey}',stepkey='${param.stepkey}';
	window.onIntercept=$.Callbacks();
	window.onIntercept.add(function(setting){
		if(setting.otherSetting){
			$.extend(setting,parseOtherSetting(setting.otherSetting));
			delete setting.otherSetting;
		}
	});
	var intercept=function(defs){$.each(defs,function(i,item){onIntercept.fire(item);intercept(item.children)});};
	window.authMap={};
	$.ajax("framework/datatable.do?t=T_S_SmartUI_Step&exactly",{
		async:false,
		data:{jobkey:jobkey,stepkey:stepkey,smartcode:"${setting.CODE}"},
		success:function(jr){
			$.each(jr.data,function(i,d){
				window.authMap[d.UIID]=d;
			});
		}
	});
	window.onIntercept.add(function(item){
		var auth=window.authMap[item.id];
		if(auth){item[auth.CONTROLTYPE]=true;}
	});
	
	var formDef=${setting.MOBILEFORMDEF};
	intercept(formDef);
	reviveFunctionInJson(formDef);
	
	var setting={formCode:formCode,formName:formName,formWidth:formWidth,formHeight:formHeight,queryUrl:queryUrl,addUrl:addUrl,saveUrl:saveUrl,deleteUrl:deleteUrl,id:id,jobid:jobid,relationField:relationField,relationid:relationid};

$(function(){
	var loadData=null;
	<c:if test="${list}">		
		var listDef=${setting.MOBILELISTDEF};
		intercept(listDef);
		reviveFunctionInJson(listDef);
		var list=parseList(listDef,formDef,setting);
		loadData=list.setFormValue;
	</c:if>
	<c:if test="${list!=true}">
		var form=parseForm(formDef,setting);
		loadData=form.hyForm.setFormValue;
		window.save=function(){form.save()};
	</c:if>
	
	loadData(null,{id:relationid},setting);
});
</script>