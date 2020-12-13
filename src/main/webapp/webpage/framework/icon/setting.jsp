<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<z:dict items="SYS003"></z:dict>
<style>
	#formEditor input{width:310px;}
</style>

<div class="easyui-layout" data-options="fit:true">
	<div id="pSearch" data-options="region:'north',title:'搜索'" style="height:88px">
		<form id="fSearch">
			<table style="width:98%;margin:5px 15px;">
				<tr>
					<td >分类：</td><td><input type="text" class="easyui-combobox" name="category" data-options="data:D.get('SYS003',true)"/></td>
					<td >名称或描述：</td><td><input type="text" class="easyui-textbox" name="name" /></td>
					<td >css样式类名：</td><td><input type="text" class="easyui-textbox" name="iconClas" /></td>
					<td>生成css样式：</td><td><input type="text" class="easyui-combobox" data-options="width:80,data:[{text:'全部'},{value:0,text:'生成'},{value:1,text:'不生成'}],value:0"  name="rejectCss"/></td>
					<td>
						<a onclick="window.search()" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >查询</a>
						<a onclick="$('#fSearch').asForm().reset()" style="margin-left:25px" class="easyui-linkbutton"  data-options="iconCls:'icon-redo'" >重置</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<div id="pMain" data-options="region:'center',title:'查询配置'" style="padding:5px 5px 5px 5px;"></div>
</div>

<div id="winEditor" style="display:none;">
	<div class="easyui-layout" data-options="fit:true">
		<div data-options="region:'center'">
			<form id="formEditor" enctype="multipart/form-data">
				<input name="id" type="hidden"/>
				<table style="margin:15px 10px;width:500px;" cellspacing="8px" >
					<tr><td width="160">名称：</td><td width="320"><input type="text" class="easyui-textbox" name="name"/></td></tr>
					<tr><td>css样式类名：</td><td><input type="text" class="easyui-textbox" name="iconClas" data-options="validType:'unique({t:&quot;T_S_ICON&quot;,c:&quot;ICONCLAS&quot;})',delay:1200"/></td></tr>
					<tr><td>图标：</td><td><input type="text" class="easyui-filebox"  name="file" data-options="buttonText:'选择文件...',buttonAlign:'right'"/></td></tr>
					<tr><td>是否生成css样式：</td><td><input type="text" class="easyui-combobox" data-options="data:[{value:0,text:'生成'},{value:1,text:'不生成'}],value:0"  name="rejectCss"/></td></tr>
					<tr><td>分类：</td><td><input type="text" class="easyui-combobox"  data-options="data:D.get('SYS003'),value:'SYS003001'" name="category"/></td></tr>
				</table>
			</form>
		</div>
		<div  data-options="region:'south',height:40">
			<div id="pToolbar" style="position:relative;width:50%;margin:5px 50%;"></div>
		</div>
	</div>
</div>

<script type="text/javascript" >
var search=function(){
	var o=$("#fSearch").asForm().getFieldValues();
	o.total=-1;
	grid.load(o);
};
$(function(){
	var rUnique=$.fn.validatebox.defaults.rules.unique={
			//message:"数据表{0}中已有{2}此值",
			validator:function(v,param){
				if(!v) // 允许空
					return true;
				var tar=getEditor.editor.xtarget;
				if(tar.iconClas==v) // 编辑时为旧值
					return true;
				
				param.v=v;
				var jr=$.ajax({url:"framework/datatable.do?uniqueValidate",dataType:"json",data:param,async:false,cache:false,type:"post"}).responseJSON;
				
				rUnique.message="库中已有"+jr.data+"个样式名为【"+v+"】";
				return jr.success&&jr.data<1;
			}
		};
	var getEditor=function(){
		var editor=getEditor.editor;
		if(editor==null){
			var form=$(formEditor).asForm();
			new sunz.Linkbutton({parent:pToolbar,text:"确定",iconCls:"icon-ok",handler:function(){
					var formData=new FormData(formEditor);
					if(formData.get("rejectCss")!="1" && !formData.get("iconClas")){
						return $.messager.show({title:'数据不符合规则',msg:'生成CSS样式的情况下必须指定【css样式类名】！'});						
					}
					var	url="framework/icon.do?"+(editor.xtarget.id||editor.xtarget.ID?"save":"add");
					$.ajax({  
				          url: url,  
				          type: 'POST',  
				          data: formData,  
				          async: false,  
				          cache: false,  
				          contentType: false,  
				          processData: false,  
				          success: function (jr) {
							if(jr.success){
								editor.xtarget=jr.data;
								$.messager.show({title:'恭喜您',msg:'保存成功'});
								editor.close();
								grid.reload();
							}else{
				        		$.messager.alert("保存出错啦",msg);
							}
				          },  
				          error: function (msg) {  
				        	  $.messager.alert("保存出错啦",msg); 
				          }  
				     });	
				}
			});
			new sunz.Linkbutton({parent:pToolbar,text:"取消",iconCls:"icon-cancel",style:"margin-left:15px;",handler:function(){editor.close();}});
			editor=getEditor.editor=$(winEditor).asWindow({width:540,height:320});
			editor.edit=function(o){
				editor.xtarget=o;
				editor.setTitle(o.id?"图标“"+o.name+"”修改":"新建图标");
				form.reset();
				form.load(o);
			}
		}
		editor.open();
		return editor;
	};
	var grid=window.grid=new sunz.Datagrid({
		parent:pMain,
		url:'framework/query.do?search&k=queryForIcon',
		fit:true,fitColumns:true,
		autoRowHeight:true,
		pagination:true,striped:true,//rownumbers:true,// 因图标大小不一对不齐，直接用id模拟出来
		columns:[[
		         {field:'id',title:" ",formatter:function(v,r,i){return i+1;}},
		         {field:"img",title:"图标",width:120,formatter:function(v,r,i){return '<center style="padding:5px"><img src="'+r.path+'"/></center>';}},
		         {field:'name',title:'名称或描述',width:120},
		         {field:'iconClas',title:'css样式类名',width:80},
		         {field:'category',title:"分类",width:80,formatter:function(v){return D.getText(v);}},
		         {field:'extend',title:'格式',width:60},
		         {field:'path',title:'生成文件路径',width:200}	,
		         {field:'rejectCss',title:'生成css样式',width:60,formatter:function(v){return v?"不生成":"生成";}}
		]],
		idField:"ID",
		toolbar:[
			{
				iconCls: 'icon-add',text:'新增',
				handler: function(){
					getEditor().edit({});
				}
			},'-',
			{
				iconCls:'icon-edit',text:'修改',
				handler:function(){
					getEditor().edit(grid.getSelected());
				}
			},
			{
				iconCls: 'icon-no',text:'删除',
				handler: function(){
					var sel=grid.getSelected();
					if(sel==null){
						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
						return;
					}
					$.messager.confirm("删除确认","您确定要删除当前数据吗？",function(ok){
						if(ok){
							$.ajax('framework/icon.do?delete',{data:{id:sel.id},success:function(r){
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
			},"-",
			{
				iconCls:"icon-copy",text:"复制",
				handler:()=>{
					var sel=grid.getSelected();
					if(sel==null){
						$.messager.show({title:'提示',msg:'请选择复制源'});
						return;
					}
					var n=$.extend({},sel);
					delete n.id;
					delete n.ID;
					n.iconClas=(sel.iconClas||"").split("-")[0]+"-";
					getEditor().edit(n);	
				}
			},"-",
			{
				iconCls:"icon-refresh",text:"刷新缓存",
				handler:()=>{
					$.post("framework/cache.do?refresh&type=Icon",{item:(grid.getSelected()||{}).id},jr=>{
						if(jr.success){
							$.messager.show({title:'恭喜',msg:'刷新成功'});
						}else{
							$.messager.alert('提示','刷新出错了:'+jr.msg);
						}
					});
				}
			}
		]
	});
});
</script>