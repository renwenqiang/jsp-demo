<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>钉钉用户列表</title>
<z:resource items="jquery,easyui,sunzui,datepicker"></z:resource>
<script type="text/javascript" src="webpage/dingding/extendquery.js"></script>
<z:dict items=""></z:dict>
</head>
<body>
	<table id="dUserList"></table>
</body>
<script type="text/javascript">
var arr_userid=new Array();
var arr_username = new Array();
$(function(){
$("#dUserList").treegrid(
		{
			/* url:'lzxmdc/lzuserController.do?getUser', */
			url:'framework/dingUser.do?getAllUser',
			title : '系统用户',
			fit:true,
			fitColumns : true,
			frozenColumns : [ [ {
				field : 'ck',
				checkbox : false,
				hidden:true
			} ] ],
			singleSelect:true,
			striped : true,
			idField : 'id',
			treeField:'text',
			//singleSelect:false,
			loadMsg : '数据加载中......',
			pagination:true,
			rownumbers : true,
			//sortName:'id',
			columns : [ [
					{
						field : 'text',
						title : '系统用户',
						width : 250
					},{
						field : 'code',
						title : '编号',
						width : 250
					},{
						field : 'connuser',
						title : '钉钉用户',
						width : 250
					}] ],
			toolbar : [{
					iconCls : 'icon-cancel',
					text : '取消关联',
					handler : function() {
						var rowsData = $("#dUserList").treegrid('getSelections');
						if (!rowsData|| rowsData.length == 0) {
							top.$.messager.alert('警告','请选择用户', 'warning');
							return;
						}else if (rowsData.length > 1) {
							top.$.messager.alert('警告','请选择一个用户取消关联','warning');
							return;
						}else{
							cancel('dingUser.do?doCancel','dUserList');
						}
					}
			}],
			loadFilter:function(data){//显示数据库传来的值显示到树上
			   var q=data.data;
		       var  count = 0;
			   //把开发测试组删除掉
			   for(var i=0;i<q.length;i++){
					if(data.data[i].id=="21"){
						q.splice(i,1);
						count++;
						return q;
					}
				}
			   if(count == 0){
				   return q;
			   }
		    },
			onBeforeExpand:function(node,param){
				/* $('#dUserList').treegrid('options').url = "lzxmdc/lzuserController.do?getNextUser&nodeid="+node.id; */
				$('#dUserList').treegrid('options').url = "framework/dingUser.do?getAllUser&flag=101";
			},
			/* onLoadSuccess:function(){
				$('#dUserList').treegrid('expandAll');
			}, */
			onLoadError : function() {
				alert("系统繁忙，请稍后再试！");
			}
		});
	$("#dUserList").treegrid('getPager').pagination({beforePageText:'第',afterPageText:'页    共 {pages} 页',displayMsg:'显示{from}-{to}条   共{total}条数据',showPageList:true,showRefresh:true});
	$("#dUserList").treegrid('getPager').pagination({onBeforeRefresh:function(pageNumber, pageSize){ $(this).pagination('loading');$(this).pagination('loaded'); }});
});
function reloadTable(){
	window.location.reload();
	//$("#dUserList").treegrid('reload');
}
function cancel(url,gname){
	top.$.messager.confirm('提示','确定取消关联吗?', function(r) {
		if(r){
			var rows = $("#"+gname).treegrid('getSelections');
			var tsid=rows[0].code;
			$.ajax({
				url : url,
				type : 'post',
				data : {t_s_id:tsid},
				cache : false,
				success : function(data) {
					//var d = $.parseJSON(data);
					if (data.success) {
						var msg = data.msg;
						$.messager.show({title : '取消关联结果',msg : msg});
						setTimeout('reloadTable()',2000);
						$("#"+gname).treegrid('unselectAll');
					}
				}
			});
		}
	});
}
</script>
</html>