<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>

<div class="easyui-layout" data-options="fit:true">
	<div id="pSearch" data-options="region:'north',title:'搜索'" style="height:88px">
		<form id="fSearch">
			<table style="width:80%;margin:5px 10%;">
				<tr>
					<td width="5%"></td>
					<td >部门名：</td><td><input type="text" class="easyui-textbox" name="code" /></td>
					<td >用户名：</td><td><input type="text"  class="easyui-textbox" name="name" /></td>
					<td >名称或描述：</td><td><input type="text"  class="easyui-textbox" name="name" /></td>
					<td><a onclick="javascript:window.search()" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >过滤</a>
					<a onclick="window.reset();"  style="margin-left:25px" class="easyui-linkbutton"  data-options="iconCls:'icon-redo'" >重置</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="pMain" data-options="region:'center',title:'用户首页配置'" style="padding:5px 5px 5px 5px;"></div>
</div>

<script type="text/javascript" >
var all=[],dict={}
	,ad=function(id,code,name,remark,order,pid){
		var item=dict[id]=$.extend(dict[id]||{},{id:id,code:code,name:name,remark:remark,orderIndex:order,pid:pid});
		var pItem=dict[pid]=dict[pid]||{}
			,list=pItem.children=pItem.children||[];
		list.push(item);
		all.push(item);
	};

$.ajax({
	async: false,
    type: "POST",
    url: "framework/authuserindex.do?queryIndexTree",//framework/query.do?search&k=userIndexTree
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    success: function (data) {
        $.each(data.data,function(i,d){
			ad(d.ID,d.CODE,d.NAME,d.REMARK,null,d.PID==null?'0':d.PID);
		});
    },
    error: function (msg) {
        alert("加载数据失败");
    }
});
//<c:forEach items='${all}' var ='di'>ad('${di.id}','${di.code}','${di.name}','${di.remark}','${di.order}','${di.parentid}');</c:forEach>
var fnGrep=function(items,state){
	return $.grep(items,function(item){if(item.children)item.state=state; return item.pid==0;}); //item.children!=null;});
},nRoot={id:0,code:'请不要编辑此条',name:'所有部门',remark:'请不要编辑此条',state:'closed'};
var treedata=fnGrep(all,"closed");
treedata=[$.extend({children:treedata},nRoot)];
var search=function(){
	var o=$("#fSearch").asForm().getFieldValues();
	if(!o.code && !o.name){reset(); return;}
		
	var items=$.grep(all,function(item){
			var c= (o.code?item.code.indexOf(o.code)>-1:true),n=(o.name?item.name.indexOf(o.name)>-1:true);
			item.cflag=c&&o.code;item.nflag=n&&o.name;
			return c&&n;});	
	// 如果仅有子项，则循环找到根
	var nItems=[],nDict={};
	$.each(items,function(i,item){
		for(var o=item;!!o;o=dict[o.pid]){
			if(!o.pid && !nDict[o.id]){
				nItems.push(o);
				nDict[o.id]=1;
				break;
			}
		}		
	});
	var ndata=fnGrep(nItems,"opened");
	ndata=[$.extend({children:ndata},nRoot)];
	tree.loadData(ndata);
};
var reset=function(){$('#fSearch').asForm().reset();$.each(all,function(i,item){if(item.children)item.state='closed'; item.cflag=item.nflag=false;});tree.loadData(treedata);};
$(function(){
	var tree=window.tree=new sunz.Treegrid({
		parent:pMain,iconCls:'icon-edit',
		fit:true,fitColumns:true,
		autoRowHeight:true,
		data:treedata,
		//rownumbers:true,
		striped:true,
		columns:[[
		         {field:'id',hidden:true}, {field:'pid',hidden:true},
		         {field:'name',title:'部门及用户',width:200,styler:function(v,r){if(r.nflag)return 'color:red;';}},
		         {field:'code',title:'首页路径',editor:'text',width:120,styler:function(v,r){if(r.cflag)return 'color:red;';}},
		         {field:'remark',title:'名称或描述',editor:'text',width:200}
		]],
		idField:"id",treeField:'name',
		onDblClickRow: function(r){
			var id=r.id;
			if(id==0) return;
			if(tree.editingid==id)return true;
			if(tree.editingid != undefined){
				if(tree.validateRow(tree.editingid)){
					tree.endEdit(tree.editingid);
				}
				else 
					return false;
			}
			tree.beginEdit(id);
			tree.editingid=id;
		},
		onAfterEdit:function(r,c){
			delete tree.editingid;
			if(!r.id)c=r;
			c.id=r.id;
			c.t="T_S_USER_INDEX";
			if(r.pid==0 || r.pid == "0"){
				c.departid=c.id;
			}else{
				c.userid=c.id;
			}
			if(c.code)
				c.indexpath = c.code;
			else 
				c.indexpath = r.code;
			if(c.remark)
				c.indexname = c.remark;
			else
				c.indexname = r.remark;
			
			$.ajax('framework/authuserindex.do?save',{data:c,success:function(r){
				if(r.success){
					tree.updateRow({index:i,row:$.extend(d,{id:r.data.ID||r.data.id})});
					$.messager.show({title:'提示',msg:'保存成功'});
				}else{
					var message=r.msg.indexOf("ORA-00001")==-1 ? '保存时出错了，' : '字段<编码>重复，' ;
					$.messager.alert('提示',message+r.msg);
				} 
			}});
		},
		toolbar: [
			{iconCls:'icon-edit',text:'开始编辑',handler:function(){
					var sel=tree.getSelected();
					if(sel==null){
						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
						return;
					}
					tree.beginEdit(sel.id);
				}
			},
			{iconCls:'icon-save',text:'保存编辑',handler:function(){
					if(tree.validateRow(tree.editingid)){
						tree.endEdit(tree.editingid);
					}
				}
			},
			{iconCls:'icon-cancel',text:'取消编辑',handler:function(){tree.cancelEdit(tree.editingid);}
			}]

	});
	
});
</script>