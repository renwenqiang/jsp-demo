<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<z:dict items="SYS005"></z:dict>
<div class="easyui-layout" data-options="fit:true">
	<div id="pSearch" data-options="region:'north',title:'搜索'" style="height:88px">
		<form id="fSearch">
		<input type="hidden" name="processkey" id="processkey" value="${processkey}"/>
			<table style="width:80%;margin:5px 10%;">
				<tr>
					<td width="5%"></td>
					<!--td >收件编号：</td><td><input type="text" class="easyui-textbox" name="code" /></td -->
					<td></td><td></td>
					<td width="100px">收件名称：</td><td><input type="text"  class="easyui-textbox" name="name" /></td>
					<td><a onclick="javascript:window.search()" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >查询</a>
					<a onclick="$('#fSearch').asForm().reset()"  style="margin-left:25px" class="easyui-linkbutton"  data-options="iconCls:'icon-redo'" >重置</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="pMain" data-options="region:'center',title:'收件配置'" style="padding:5px 5px 5px 5px;"></div>
</div>

<script type="text/javascript" >
var search=function(){
	var o=$("#fSearch").asForm().getFieldValues();
	o.total=-1;
	grid.load(o);
};
//var types = [{value:"yj",text:"原件"}
//			,{value:"fyj",text:"复印件"}],type={};
var types=D.get("SYS005"),type={};
$.each(types,function(i,t){
	type[t.value]=t.text;
});

$(function(){
	var grid=window.grid=new sunz.Datagrid({
		parent:pMain,iconCls:'icon-edit',
		url:'framework/query.do?search&k=queryReceive',
		fit:true,fitColumns:true,
		autoRowHeight:true,rownumbers:true,
		pagination:false,striped:true,
		singleSelect:false,
		columns:[[
				 {field:'ID',hidden:true,width:10},
		         {field:'NAME',title:'收件名称',editor:'text',width:300},
		         {field:'TYPE',title:'收件类型',width:100,editor:{type:'combobox',options:{data:types}},formatter:function(v,r,i){return type[v];}},
		         {field:'COPYNUM',title:'份数',editor:'text',width:50},
		         {field:'PAGENUM',title:'页数',editor:'text',width:50},
		         {field:'SCANNUM',title:'扫描数',editor:'text',width:50}
		]],
		idField:"ID",
		onDblClickRow: function(i,d){
			if(grid.editingIndex==i)return true;
			if(grid.editingIndex != undefined){
				if(grid.validateRow(grid.editingIndex)){
					grid.endEdit(grid.editingIndex);
				}
				else 
					return false;
			}
			grid.beginEdit(i);
			grid.editingIndex=i;
		},
		onAfterEdit:function(i,d,c){
			delete grid.editingIndex;
			if(!d.ID)c=d;
			if(!c.NAME && !c.TYPE && !c.COPYNUM && !c.PAGENUM && !c.SCANNUM){
				return;
			}
			$.ajax('framework/datatable.do?'+(d.ID?'save':'add')+'&t=T_S_RECEIVE_DEF',{data:d,success:function(r){
				if(r.success){
					grid.updateRow({index:i,row:$.extend(d,{id:r.data.ID||r.data.id})})
					$.messager.show({title:'提示',msg:'保存成功'});
				}else{
					$.messager.alert('提示',r.msg);
				} 
			}});
		},
		toolbar: [
			{iconCls:'icon-edit',text:'开始编辑',handler:function(){
					var sel=grid.getSelected();
					if(sel==null){
						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
						return;
					}
					grid.editingIndex=grid.getRowIndex(sel);
					grid.beginEdit(grid.getRowIndex(sel));
				}
			},
			{iconCls:'icon-save',text:'保存编辑',handler:function(){
					if(grid.validateRow(grid.editingIndex)){
						grid.endEdit(grid.editingIndex);
					}
				}
			},
			{iconCls:'icon-cancel',text:'取消编辑',handler:function(){grid.cancelEdit(grid.editingIndex);}
			},'-',
			{
				iconCls: 'icon-add',text:'新增收件',
				handler: function(){grid.appendRow({});}
			},
			{
				iconCls: 'icon-no',text:'删除收件',
				handler: function(){
					var sels=grid.getSelections();
					if(sels.length==0){
						$.messager.show({title:'提示',msg:'请至少选择一行'});
						return;
					}
					$.each(sels,function(i,sel){
						var id=sel.ID;
						if(!id){ 
							grid.deleteRow(grid.getRowIndex(sel)); 
							if(i>0 && sels.length==0)
								$.messager.show({title:'提示',msg:'操作完成'});
						}else{
							$.ajax('framework/datatable.do?delete&t=T_S_RECEIVE_DEF',{data:{id:id},success:function(r){
								if(r.success){
									grid.deleteRow(grid.getRowIndex(sel));
									if(sels.length==0)
										$.messager.show({title:'提示',msg:'操作完成'});
								}else{
									$.messager.alert('提示','删除时出错了:'+r.msg);
								} 
							}});
						}
					});
				}
			}]

	});
});
</script>