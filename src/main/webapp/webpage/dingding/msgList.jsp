<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>

<!DOCTYPE t:base PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />

<z:resource items="jquery,easyui,sunzui"></z:resource>
<style>
body{
width:100%;height:100%;
}
input{
	width:200px;
}
</style>
</head>
<body>
	<div class="easyui-layout" fit="true">
		<div id="pSearch" data-options="region:'north',title:'搜索'" style="height:88px">
			<form id="fSearch">
				<table style="width:80%;margin:5px 10%;">
					<tr>
						<td width="5%"></td>
						<td >编码：</td><td><input type="text" class="easyui-textbox" name="code" /></td>
						<td >名称或描述：</td><td><input type="text"  class="easyui-textbox" name="name" /></td>
						<td><a onclick="javascript:window.search()" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >查询</a>
						<a onclick="$('#fSearch').asForm().reset()"  style="margin-left:25px" class="easyui-linkbutton"  data-options="iconCls:'icon-redo'" >重置</a></td>
					</tr>
				</table>
			</form>
		</div>
		<div  data-options="region:'center',title:'消息'" style="padding:0px;border:0px; ">
			<div id="datagrid" style="width:100%;height:100%;"></div>
		</div>
	</div>
	<script type="text/javascript">
	var toolbar =  [ {iconCls:'icon-add',text:'发送消息',handler:function(){
	        					var sel=grid.getSelected();
	        					if(sel==null){
	        						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
	        						return;
	        					}
	        					startEdit(grid.getRowIndex(sel),sel);
	        				}
	        			} ,
	        			{iconCls:'icon-info',text:'查看明细',handler:function(){
        					var sel = grid.getSelected();
        					if(sel==null){
        						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
        						return;
        					}
        					 
        				}
        			}];

		function initlist() {
			window.grid = sunz.Datagrid({
				height:"100%",
				parent : $("#datagrid")[0],
				url : "framework/dingUser.do?queryMsgList",
				columns : [ [ {
					field : 'ID',
					title : '编码',
					width : "15%"
				},
				{
					field : 'REALNAME',
					title : '发送人',
					width : "15%"
				},
				{
					field : 'USERIDS',
					title : '相关用户编码',
					width : "15%",
					align : 'left'
				}, {
					field : 'MSGID',
					title : '消息编号',
					width : "15%",
					align : 'left'
				}, {
					field : 'TEXTSTR',
					title : '消息内容',
					width : "50%"
				} ] ],
				pageSize : 10,
				pagination:true,
				toolbar:toolbar
			})
		}
		$(function() {
			initlist();
		})
	</script>

</body>
</html>

