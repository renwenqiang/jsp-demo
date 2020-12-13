<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>

<style type="text/css">
a{cursor:pointer;color:blue;margin-left:15px;padding-left:20px;background-position:left center !important;}
</style>
<script type="text/javascript">
	function refresh(ele,type){
		var item=$(ele.previousSibling).val();	
		$.post("framework/cache.do?refresh",{type:type,item:item},function(jr){
			if(jr.success){
				$.messager.show({title:"恭喜您",msg:"缓存刷新成功"});
			}else{
				$.messager.alert("缓存刷新失败",jr.msg);
			}
		});
	}
	$(function(){
		var all=[];
		(function a(t,n){all.push({type:t,name:n});return a})<c:forEach items="${all}" var="cr">("${cr.category}","${cr.description}")</c:forEach>;
		new sunz.Datagrid({
			title:"系统缓存管理（刷新）--  <span style='color:red'>当前系统<c:if test='${!clusterSupport}'>不</c:if>支持集群同时刷新（通过MQ中转）<c:if test='${!clusterSupport}'>--如系统确实多实例部署，则您需要手动连接每个实例分别刷新</c:if></span>",
			parent:"#viewport",
			rownumbers:true,striped:true,fit:true,fitColumns:true,autoRowHeight:true,
			rownumberWidth:48,
			data:all,
			columns:[[
				{title:"代号",field:"type",width:80,halign:"center"},
				{title:"名称",field:"name",width:400,halign:"center"},
				{title:"操作",field:"none",width:320,halign:"center",align:"center",formatter:function(v,r,i){
					return '刷新参数：<input type="text" /><a class="icon-reload" onclick="refresh(this,\''+r.type+'\');">刷新</a>';
				}}
			]]
		});
	});
</script>
<div id="viewport" style="width:100%;height:100%;">
</div>