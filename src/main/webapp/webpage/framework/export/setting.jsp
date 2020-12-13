<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<script type="text/javascript" src="webpage/framework/export/exportEdit.js"></script>
<style>
<!--
	.htmlcell{width:100%;height:48px;}
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
	<div id="pMain" data-options="region:'center',title:'导出配置'" style="padding:5px 5px 5px 5px;"></div>
</div>

<script type="text/javascript" >
var search=function(){
	var o=$("#fSearch").asForm().getFieldValues();
	grid.load(o);
};
$(function(){
	var getEditor=function(){
		if(grid.exportEditor==null){
			grid.exportEditor=new ExportEditor();
		}
		return grid.exportEditor;
	};
	var grid=window.grid=new sunz.Datagrid({
		parent:pMain,
		url:'framework/query.do?search&k=exportSetting',
		fit:true,fitColumns:true,
		autoRowHeight:true,
		pagination:true,rownumbers:true,striped:true,
		columns:[[
		         {field:'ID',hidden:true},
		         {field:'SQLID',hidden:true},{field:'SQLKEY',hidden:true},{field:'SQL',hidden:true},
		         {field:'KEY',title:'编码',editor:'text',width:100},
		         {field:'DESCRIPTION',title:'名称或描述',editor:'text',width:120},
		         {field:'TEMPLATE',title:'模板文件',editor:'text',width:120,formatter:function(v,r,i){
		        	 return v?('<a href="'+'framework/export.do?downloadTemplate&id='+r.ID+'">已上传模板文件</a>\
		        			 	<a href="javascript:$.post(\'framework/export.do?deleteTemplate\',{id:\''+r.ID+'\'},function(){grid.reload()})">删除</a>')
		        			 :"-";
		       	 }},
		         {field:'FIELDMAPPING',title:'字段映射',editor:'text',width:120},
		         {field:'STARTROW',title:'起始行',editor:'text',width:120},
		         {field:'STARTCOLUMN',title:'起始列',editor:'text',width:120},
		         {field:'IGNOREROWS',title:'跳过行',editor:'text',width:120},
		         {field:'IGNORECOLUMNS',title:'跳过列',editor:'text',width:120},
		         {field:'DICTFIELDS',title:'编码字段',editor:'text',width:120},
		         {field:'SQLNAME',title:'sql语句定义',editor:'textarea',width:120},	
		         {field:'Operate',title:'操作',width:75,formatter:function(v,r,i){
		        	 var url="framework/export.do?excel&id="+r.ID;
		        	 return '<center><a onclick="window.open(\''+url+'\')" class="easyui-linkbutton" data-options="iconCls:\'icon-redo\'">测试</a></center>';
		         }}
		]],
		onLoadSuccess:function(){$(".easyui-linkbutton").asLinkbutton();},
		idField:"ID",
		toolbar:[
			{
				iconCls: 'icon-add',text:'新增',
				handler: function(){
					getEditor().edit({SQLID:null,KEY:null,DESCRIPTION:null,TEMPLATE:null,FIELDMAPPING:null,STARTROW:null,STARTCOLUMN:null,IGNOREROWS:null,IGNORECOLUMNS:null,DICTFIELDS:null,SQLNAME:null});
				}
			},'-',
			{
				iconCls:'icon-edit',text:'修改',
				handler:function(){
					if(grid.getSelected().TEMPLATE){
						grid.getSelected().TEMPLATE=null;
						grid.getSelected().TEMPLATE_label="已上传模板文件";
					}
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
					$.messager.confirm("删除确认","您确定要删除当前数据吗？",function(ok){
						if(ok){
							$.ajax('framework/export.do?delete',{data:{id:id},success:function(r){
								if(r.success){
									grid.deleteRow(grid.getRowIndex(sel));
									$.messager.show({title:'提示',msg:'删除成功'});
								}else{
									$.messager.alert('提示','删除时出错了:'+r.msg);
								} 
							}});
						}
					});
				}
			}
		]
	});
	$.parser.parse();
});
</script>