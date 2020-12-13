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

var appendParam=function(opt){
	opt.jobkey=$("#processkey").val();
};

$(function(){
	var grid=window.grid=new sunz.Datagrid({
		parent:pMain,iconCls:'icon-edit',
		url:'framework/query.do?search&k=queryReceiveByJobkey',
		onBeforeLoad:appendParam,
		fit:true,fitColumns:true,
		autoRowHeight:true,
		pagination:false,striped:true,
		singleSelect:false,
		selectOnCheck: true,
		checkOnSelect: false,
		onCheck:function(i,r){if(r.ORDERINDEX==null)r.ORDERINDEX=grid.getChecked().length;grid.updateRow({index:i,row:r});},
		loadFilter:function(jr){return jr.success?($.each(jr.data,function(i,d){d.ORDERINDEX=d.ORDERINDEX||d.orderindex;/*居然没用d.checked=d.ORDERINDEX!=null;*/}), {rows:jr.data,total:jr.total}):{rows:[],total:0};},
		onLoadSuccess:function(){$.each(grid.getData().rows,function(i,r){if(r.ORDERINDEX!=null)grid.checkRow(i);})},
		columns:[[
				 {field:'ID',width:10,checkbox:true},
		         {field:'NAME',title:'收件名称',width:300},
		         {field:'TYPE',title:'收件类型',width:100,formatter:function(v,r,i){return type[v];}},
		         {field:'COPYNUM',title:'份数',width:50},
		         {field:'PAGENUM',title:'页数',width:50},
		         {field:'SCANNUM',title:'扫描数',width:50},
		         {field:'ORDERINDEX',title:'顺序',editor:'numberbox',width:50}
		]],
		idField:"ID",
		sortName:'ORDERINDEX',sortOrder:"asc",
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
		},
		toolbar: [{
				iconCls: 'icon-save',text:'保存配置',
				handler: function(){
					if(grid.validateRow(grid.editingIndex)){
						grid.endEdit(grid.editingIndex);
					}
					var processkey=$("#processkey").val();
					var sels=grid.getChecked();
					if(sels.length>0){
						var url='framework/receive.do?deleteReceiveTemp&jobkey=' + processkey;
						$.ajax(url,{success:function(r){
							if(r.success){
								var count=0,scount=0;
								for(var i=0;i<sels.length;i++){
									$.post("framework/datatable.do?add",{t:"T_S_RECEIVE_TEMP",jobkey:processkey,receiveid:sels[i].ID,ORDERINDEX:sels[i].ORDERINDEX},function(jr){
										count++;
										if(jr.success)scount++;
										else $.messager.alert('保存'+sels[i].NAME+'失败',r.msg);
										if(count==sels.length){
											if(scount==count)$.messager.show({title:'提示',msg:'保存成功'});
										}
									});									
								}
							}else{
								$.messager.alert('提示','删除时出错了:'+r.msg);
							} 
						}});
						
					}else{
						$.messager.show({title:'提示',msg:'未选择收件'});
					}
					}
			},{iconCls:'icon-add',text:'新增收件定义',handler:function(){
				window.open("framework/receive.do?setting");
			}}]

	});
});
</script>