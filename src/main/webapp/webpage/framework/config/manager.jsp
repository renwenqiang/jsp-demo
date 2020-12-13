<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>

<style type="text/css">
</style>
<script type="text/javascript">	
	$.fn.datagrid.defaults.halign="center";
	var fnYN=function(v){return v==0?"×":"√";}
	$(function(){
		new sunz.Datagrid({
			parent:"#viewport",
			rownumbers:true,striped:true,fit:true,fitColumns:true,autoRowHeight:true,
			rownumberWidth:48,
			url:"framework/config.do?all",
			columns:[[
				{title:"名称",field:"name",width:160},
				{title:"编码",field:"code",width:80},
				{title:"参数值",field:"value",width:200},
				{title:"Application可见",field:"background",width:64,formatter:fnYN},
				{title:"前端（js）可见",field:"foreground",width:64,formatter:fnYN},
				{title:"注入Spring",field:"springbean",width:64,formatter:fnYN},
				{title:"持久化（值变化保存到数据库）",field:"persist",width:64,formatter:fnYN},
				{title:"是否系统参数",field:"system",width:64,formatter:fnYN},
				{title:"分组",field:"group",width:75}
			]]
		});
	});
</script>
<div id="viewport" style="width:100%;height:100%;">
</div>