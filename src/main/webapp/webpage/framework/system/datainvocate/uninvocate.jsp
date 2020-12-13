<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/webpage/framework/query/query.jsp"%>

<script type="text/javascript" >
	var fnEmpty=function(){};
	var key='${invocationDef.key}'
		,name='${invocationDef.description}'
		,idField='${invocationDef.uninvocateIdField}';
	document.title="取消调档："+name;
	html='<input type="hidden" name="jobid" value="${param.jobid}" />'+html;
	vertical=false; // 强制横向
	
	// 暴露方法
	var uninvocate=function(){
		var dataids="", sels=grid.getChecked();
		if(sels==null || sels.length==0){
			$.messager.alert("提示","当前没有选择任何数据！");
			return;
		}
		$.grep(sels,function(o,i){
			dataids=dataids+","+o[idField]
		});
		dataids=dataids.substr(1);
		$.post("framework/datainvocate.do?uninvocate",{k:key,jobid:'${param.jobid}',dataids:dataids},function(jr){
			if(jr.success){
				$.messager.show({msg:"取消调档成功",title:""})
				// 广播事件
				if(parent.onUninvocated){
					parent.onUninvocated(dataids,sels);
				}
			}else{
				$.messager.alert("取消调档失败",jr.msg);
			}
		});
	}
	$(function(){
		new sunz.Linkbutton({parent:divButtons,text:'取消调档',style:'margin-left:25px',iconCls:'icon-cancel',handler:uninvocate});
	});
</script>