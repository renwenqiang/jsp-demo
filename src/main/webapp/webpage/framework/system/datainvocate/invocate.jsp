<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/webpage/framework/query/query.jsp"%>

<script type="text/javascript" >
	var fnEmpty=function(){}
		,key='${invocationDef.key}'
		,name='${invocationDef.description}'
		,idField='${invocationDef.idField}'
		,jobid='${param.jobid}';
	document.title="调档："+name;
	// 将环境参数传递到搜索条件中
	html='<input type="hidden" name="jobid" value="${param.jobid}" />'+html;
	vertical=false; // 强制横向
	
	// 暴露方法
	var invocate=function(){
		var dataids="", sels=grid.getChecked();
		if(sels==null || sels.length==0){
			$.messager.alert("提示","当前没有选择任何数据！");
			return;
		}
		$.grep(sels,function(o,i){
			dataids=dataids+","+o[idField]
		});
		dataids=dataids.substr(1);
		$.post("framework/datainvocate.do?invocate",{k:key,jobid:jobid,dataids:dataids},function(jr){
			if(jr.success){
				$.messager.show({msg:"调档成功",title:""})
				// 广播事件
				if(parent.onInvocated){
					parent.onInvocated(dataids,sels);
				}
			}else{
				$.messager.alert("调档失败",jr.msg);
			}
		});
	}
	$(function(){
		new sunz.Linkbutton({parent:divButtons,text:'调档　',style:'margin-left:25px',iconCls:'icon-ok',handler:invocate});
		//grid.datagrid({toolbar:['-',{text:'调档　　',iconCls:'icon-ok',handler:invocate},'-'].concat(grid.options().toolbar)});
	});
</script>