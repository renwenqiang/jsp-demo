<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
	<head>
		<style type="text/css">
		<!--
			body{margin:0;padding:0}
			body,div.proxy-container{width:100%;height:100%;}
			.autoSize,div.proxy-select {display:inline-block;*display:inline;*zoom:1; }
			.fit{width:100% !important;height:100% !important;}
			.smartform{margin:0 auto;padding:0;overflow:scroll;}
			.smartmask{position:absolute;width:100%;height:100%;top:0;left:0;z-index:99999;background-color:rgba(0,0,0,0.01)}
			.smartmask-msg{font-size:16px;color:#666;text-align:center;position:relative;top:50%;height:36px;line-height:36px;margin-top:-18px;}
			.mask-error{background-color:#fff}
			.mask-error .smartmask-msg{font-size:20px;color:#f00}
			#load-mask .smartmask-msg{/*font-size:20px*/}
		-->
		</style>
	</head>
	<body>
		<div id="load-mask" class="smartmask"><div class="smartmask-msg">正在渲染界面，请稍候......</div></div>
	</body>
</html>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery"></z:resource>
<z:config groups="smartformRuntimeConfigs" items="${setting.CONFIGS}"></z:config>
<z:dict items="${setting.DICTS}"></z:dict>
<z:resource items="easyui,sunzui,fileHelper,${setting.INNERRESOURCES}"></z:resource>
<script type="text/javascript" src="webpage/framework/smartform/interface.js"></script>
<script type="text/javascript" src="webpage/framework/smartform/components/defines.js"></script>
<script type="text/javascript" src="webpage/framework/smartform/parse.js"></script>
${setting.RESOURCES}
<script type="text/javascript">
	var q=getQueryParam;
    var formCode="${setting.CODE}",formName="${setting.NAME}",tableName="${setting.TABLENAME}";
	var relationField=q("relationField"),relationid=q("relationid");
	var reg1=/,|;/,reg2=/:|=/,relationData={};
	var toRelationMap=function(rmString,nvlAsSame){rm=[];if(rmString)$.map(rmString.split(reg1),function(n,i){var m=n.split(reg2);rm.push({from:m[1]||(nvlAsSame?m[0]:null),to:m[0]});});return rm;};
	var toRelationData=function(rdString){rd={};if(rdString)$.each(rdString.split(reg1),function(i,n){var m=n.split(reg2);rd[m.length==1?"id":m[0]]=m.length>1?m[1]:m[0];}); return rd;};
	var loadRelation=function(rm,rd,e){e=e||{};$.each(rm,function(i,m){e[m.to]=m.from?rd[m.from]:rd["ID"]||rd["id"];if(e[m.to]===undefined)e[m.to]=null;});return e};
	var setting=$.extend({
			CODE:formCode,formCode:formCode,formName:formName,
			formWidth:"${setting.FORMWIDTH}".trim(),formHeight:"${setting.FORMHEIGHT}".trim(),
			queryUrl:"${setting.QUERYURL}",selectUrl:"${setting.SELECTURL}",addUrl:"${setting.ADDURL}",saveUrl:"${setting.SAVEURL}",deleteUrl:"${setting.DELETEURL}",
			addable:q("noadd")==null,// 增删改查动态控制
			editable:q("noedit")==null,
			deleteable:q("nodelete")==null,
			viewable:q("noview")==null,
			logRequired:q("nolog")==null,
			relationMap:[],	// 从url传来的值都是自己的，不属于parentData 
			smartMode:q("smartMode")
	},${setting.OTHERSETTING==null?"{}":setting.OTHERSETTING});

	var jobid=q("jobid"),jobkey=q("jobkey"),stepkey=q("stepkey");
	var formData=toRelationData(q("params"));	// 通过params传参 
	if(relationid&&relationField){			// relation Field=id
		formData[relationField]=relationid;
	}else {									// relationField+q
		loadRelation(toRelationMap(relationField,true),q(),formData);
	}		
	formData.id||(formData.id=q("id")||q("ID"))||delete formData.id;		// id & jobid
	formData.jobid||(jobid && (formData.jobid=jobid));
	
	if(setting.smartMode==null){ 			// 未传值时看有没有传关联参数，没有为新增，有为修改
		setting.smartMode=$.map(formData,n=>{return n}).length?1:(toRelationMap(relationField).length?1:-1);
	}
	
	var formDef=${setting.FORMDEF};	
	window.onSmartDefineLoad.fire(setting);
	$(function(){
		var loadData,smartDom,parentData;
		<c:if test="${list}">		
			var listDef=${setting.LISTDEF};
			smartDom=window.smartlist=parseList(listDef,formDef,setting);
			loadData=smartDom.setFormValue;
			// list认为没有值是自己的，都是parent的--实际上相反，考虑统一接口setFormValue才造成此状况 
			parentData=formData;
			formData=null;
		</c:if>
		<c:if test="${list!=true}">
			var form=window.smartform=parseForm(formDef,setting);
			smartDom=form[0];
			loadData=form.setFormValue;
			window.save=form.save;
			parentData={};
		</c:if>
		window.onSmartRendered.fire(smartDom);
		$("#load-mask").remove();
		loadData(formData,parentData,setting);
	});
</script>