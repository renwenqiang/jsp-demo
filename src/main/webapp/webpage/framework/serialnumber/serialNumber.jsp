<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>

<div class="easyui-layout" data-options="fit:true">
	<div id="pSearch" data-options="region:'north',title:'搜索'" style="height:88px">
		<form id="fSearch">
			<table style="width:80%;margin:5px 10%;">
				<tr>
					<td width="5%"></td>
					<td >规则编号：</td><td><input type="text" class="easyui-textbox" name="code" /></td>
					<td >编号名称：</td><td><input type="text"  class="easyui-textbox" name="name" /></td>
					<td><a onclick="javascript:window.search()" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >查询</a>
					<a onclick="$('#fSearch').asForm().reset()"  style="margin-left:25px" class="easyui-linkbutton"  data-options="iconCls:'icon-redo'" >重置</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="pMain" data-options="region:'center',title:'规则配置'" style="padding:5px 5px 5px 5px;"></div>
</div>

<script type="text/javascript" >
var search=function(){
	var o=$("#fSearch").asForm().getFieldValues();
	o.total=-1;
	grid.load(o);
};
var dateStyles = [{value:"",text:"不按时间编排"}
				,{value:"yyyy",text:"按年编排"}
				,{value:"yyyyMM",text:"按年月编排"}
				,{value:"MMdd",text:"按月日编排"}
				,{value:"yyyyMMdd",text:"按年月日编排"}],dateStyle={};
$.each(dateStyles,function(i,t){
	dateStyle[t.value]=t.text;
});

var validateKey=function(k,id){
	var flag=false;
	if(k==null||k==""){
		$.messager.show({title:'提示',msg:'编码不能为空'});
		return flag;
	}
	id=id?id:null;
	var url='framework/datatable.do?uniqueValidate&t=T_S_SERIALNUMBER_INFO&c=key&v='+k+'&id='+id;
	$.ajax(url,{async:false,success:function(r){
		if(r.success){
			if(r.data>0){
				$.messager.show({title:'提示',msg:'保存失败，编码已存在'});
			}else{
				flag=true;
			}
		}else{
			$.messager.alert('提示',"无法保存数据,验证key失败");
		} 
	}});
	return flag;
};

$(function(){
	var grid=window.grid=new sunz.Datagrid({
		parent:pMain,iconCls:'icon-edit',
		url:'framework/query.do?search&k=queryRule',
		fit:true,fitColumns:true,
		autoRowHeight:true,
		pagination:true,rownumbers:true,striped:true,
		columns:[[
				 {field:'ID',hidden:true},
		         {field:'KEY',title:'规则编码',editor:'text',width:120},
		         {field:'RULENAME',title:'编号名称',editor:'text',width:200},
		         {field:'FIXEDPREFIX',title:'固定前缀',editor:'text',width:50},
		         {field:'DYNAMICPREFIX',title:'动态前缀',editor:'textarea',width:400},
		         {field:'DATESTYLE',title:'日期样式',width:150,editor:{type:'combobox',options:{data:dateStyles}},formatter:function(v,r,i){return dateStyle[v];}},
		         {field:'RULELENGTH',title:'规则长度',editor:'text',width:50},
		         {field:'RULEEXPRESS',title:'规则表达式',editor:'text',width:150}
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
			//if(!c.KEY && !c.RULENAME && !c.FIXEDPREFIX && !c.DYNAMICPREFIX && !c.DATESTYLE && !c.RULELENGTH && !c.RULEEXPRESS){return;}
			if($.isEmptyObject(c)){return;}
			if(d.KEY=="" || d.RULENAME==""){
				$.messager.show({title:'提示',msg:'保存失败,规则编码和编号名称不能为空'});
				return;
			}
			if(validateKey(d.KEY,d.ID)){
				$.ajax('framework/datatable.do?'+(d.ID?'save':'add')+'&t=T_S_SERIALNUMBER_INFO',{data:d,success:function(r){
					if(r.success){
						grid.updateRow({index:i,row:$.extend(d,{ID:r.data.ID||r.data.id})})
						$.messager.show({title:'提示',msg:'保存成功'});
					}else{
						var message=r.msg.indexOf("ORA-00001")==-1 ? '保存时出错了，' : '字段<规则编码>重复，' ;
						$.messager.alert('提示',message+r.msg);
					} 
				}});
			}
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
				iconCls: 'icon-add',text:'新增规则',
				handler: function(){grid.appendRow({});}
			},'-',
			{
				iconCls: 'icon-no',text:'删除规则',
				handler: function(){
					var sel=grid.getSelected();
					if(sel==null){
						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
						return;
					}
					var id=sel.ID;
					if(!id){ 
						grid.deleteRow(grid.getRowIndex(sel)); 
						return;
					}
					$.messager.confirm("删除确认","您确定要删除当前数据吗？",function(ok){
						if(ok){
							$.ajax('framework/datatable.do?delete&t=T_S_SERIALNUMBER_INFO',{data:{id:id},success:function(r){
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
			},'-',{
				text:'预览和测试',
				iconCls:'icon-tip',
				handler:function(){
					var sel=grid.getSelected();
					if(sel==null){
						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
						return;
					}
					window.open("framework/serialNumber.do?getNext&k="+sel.KEY+"&jobid=0&userid="+loginUser.id);
				}
			}
			]
		
	});
});
</script>