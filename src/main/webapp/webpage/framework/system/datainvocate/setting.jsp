<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<script type="text/javascript" src="webpage/framework/system/datainvocate/invocateEdit.js"></script>
<style>
<!--
	.htmlcell{width:100%;height:48px;}
	.kv-label{width:200px !important}
	.w-required{width:480px}
-->
</style>

<div class="easyui-layout" data-options="fit:true">
	<div id="pSearch" data-options="region:'north',title:'搜索'" style="height:88px">
		<form id="fSearch">
			<table style="width:80%;margin:5px 10%;">
				<tr>
					<td width="5%"></td>
					<td >编码：</td><td><input type="text" class="easyui-textbox" name="code" /></td>
					<td >名称或描述：</td><td><input type="text" class="easyui-textbox" name="name" /></td>
					<td>
					<a onclick="window.search()" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >查询</a>
					<a onclick="$('#fSearch').asForm().reset()" style="margin-left:25px" class="easyui-linkbutton"  data-options="iconCls:'icon-redo'" >重置</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="pMain" data-options="region:'center',title:'调档配置'" style="padding:5px 5px 5px 5px;"></div>
</div>

<script type="text/javascript" >
var search=function(){
	var o=$("#fSearch").asForm().getFieldValues();
	o.total=-1;
	grid.load(o);
};
$(function(){
	var getEditor=function(){
		if(grid.invocateEditor==null){
			grid.invocateEditor=new InvocateEditor();
		}
		return grid.invocateEditor;
	};
	var grid=window.grid=new sunz.Datagrid({
		parent:pMain,
		url:'framework/query.do?search&k=queryDataInvocation',
		fit:true,fitColumns:true,
		autoRowHeight:true,
		pagination:true,rownumbers:true,striped:true,
		columns:[[
		         {field:'ID',hidden:true},
		         {field:'QUERYKEY',hidden:true},
		         {field:'UNINVOCATEQUERYKEY',hidden:true},
		         {field:'KEY',title:'编码',editor:'text',width:120},
		         {field:'DESCRIPTION',title:'名称或描述',editor:'text',width:120},
		         {field:'QNAME',title:'查询语句定义',editor:'textarea',width:120},
		         {field:'IDFIELD',title:'主键字段（结果）',editor:'text',width:80},
		         {field:'INVOCATEPROCEDURE',title:'存储过程名',editor:'text',width:200},
		         {field:'UQNAME',title:'取消时查询语句定义',editor:'textarea',width:120},
		         {field:'UNINVOCATEIDFIELD',title:'取消时主键字段（结果）',editor:'text',width:80},
		         {field:'UNINVOCATEPROCEDURE',title:'取消时存储过程名',editor:'text',width:200},
		]],
		onLoadSuccess:function(){$(".easyui-linkbutton").asLinkbutton();},
		idField:"ID",
		toolbar:[
			{
				iconCls: 'icon-add',text:'新增',
				handler: function(){
					getEditor().edit({KEY:null,DESCRIPTION:null,QUERYKEY:null,IDFIELD:null,INVOCATEPROCEDURE:null,UNINVOCATEQUERYKEY:null,UNINVOCATEIDFIELD:null,UNINVOCATEPROCEDURE:null});
				}
			},'-',
			{
				iconCls:'icon-edit',text:'修改',
				handler:function(){
					getEditor().edit(grid.getSelected());
				}
			},'-',
			{
				iconCls: 'icon-no',text:'删除',
				handler: function(){
					var sel=grid.getSelected();
					if(sel==null){
						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
						return;
					}
					var id=sel.ID;
					if(!id){ // 
						grid.deleteRow(grid.getRowIndex(sel)); 
						return;
					}
					$.messager.confirm("删除确认","您确定要删除此配置吗？",function(ok){
						if(!ok)return;
						$.ajax('framework/datatable.do?delete&t=T_S_DataInvocation',{data:{id:id},success:function(r){
							if(r.success){
								grid.deleteRow(grid.getRowIndex(sel));
								$.messager.show({title:'提示',msg:'删除成功'});
							}else{
								$.messager.alert('提示','删除时出错了:'+r.msg);
							} 
						}});
					});
				}
			}
		]
	});
	$.parser.parse();
});
</script>