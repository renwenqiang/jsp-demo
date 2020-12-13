<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,easyui-datagrid-dnd,sunzui"></z:resource>
<style>
<!--
.uneditabled{background-color:#ff0}
.uneditabled .datagrid-cell{color:#666}
.keyword_code,.keyword_name{color:#f00;}
.searched_code,.searched_name{line-height:36px;font-size:18px;background-color:#0f0}
.tree-title{height:auto}
-->
</style>
<div class="easyui-layout" data-options="fit:true">
	<div id="pSearch" data-options="region:'north',title:'搜索'" style="height:88px">
		<form id="fSearch">
			<table style="width:80%;margin:5px 10%;">
				<tr>
					<td width="5%"></td>
					<td >编码：</td><td><input type="text" class="easyui-textbox" name="code" /></td>
					<td >名称或描述：</td><td><input type="text"  class="easyui-textbox" name="name" /></td>
					<td><a onclick="javascript:window.search()" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >过滤</a>
					<a onclick="window.reset();"  style="margin-left:25px" class="easyui-linkbutton"  data-options="iconCls:'icon-redo'" >重置</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="pMain" data-options="region:'center',title:'数据字典配置--页面刷新前需要点击“刷新服务器缓存”，拖放可直接移动目标项下'" style="padding:5px 5px 5px 5px;"></div>
</div>

<script type="text/javascript" >
var all=[],dict={},add=function(id,code,name,remark,order,pid,origin){
	var item=dict[id]=$.extend(dict[id]||{},{id:id,code:code,name:name,remark:remark,orderIndex:order,pid:pid,origin:origin});
	var pItem=dict[pid]=dict[pid]||{};		
	(pItem.children=pItem.children||[]).push(item);
	all.push(item);
};
"<c:forEach items='${all}' var ='di'>${di.id}'${di.code}'${di.text}'${di.remark}'${di.order}'${di.parentid}'${di.origin};</c:forEach>".split(";").forEach(function(d){d&&add.apply(null,d.split("'"));});

var nRoot=dict["all"];//all.find(function(d){return d.id=="all" || d.code=="all";});
if(nRoot){
	nRoot.code="请不要编辑此条";nRoot.name="所有字典项--请不要编辑此条";nRoot.remark="黄色背景项由业务数据扩展而来，不可在此界面编辑";
}else {
	nRoot=all[0];
}
var fnGrep=function(items,state){
	return $.grep(items,function(item){if(item.children)item.state=state; return !item.pid||item.pid==nRoot.id;}); //item.children!=null;});
};
all=all.slice(1);//dict["all"].children;
var treedata=fnGrep(all,"closed");
treedata=[$.extend({},nRoot,{children:treedata})];
var searchCode=null,searchName=null,codeReg,nameReg,
	codeFormatter=function(v,r){
		return r.cflag?('<span class="searched_code">'+v.replace(codeReg,searchCode)+'</span>'):v;
	},
	nameFormatter=function(v,r){
		return r.nflag?('<span class="searched_name">'+v.replace(nameReg,searchName)+'</span>'):v;
	}
var search=function(){
	var o=$("#fSearch").asForm().getFieldValues();
	if(!o.code && !o.name){reset(); return;}
	
	searchCode='<span class="keyword_code">'+o.code+'</span>';
	searchName='<span class="keyword_name">'+o.name+'</span>';
	codeReg=new RegExp(o.code,"g");
	nameReg=new RegExp(o.name,"g");
	
	var items=$.grep(all,function(item){
			item.state="closed";
			var c= (o.code?item.code.indexOf(o.code)>-1:true),n=(o.name?item.name.indexOf(o.name)>-1:true);
			item.cflag=c&&o.code;item.nflag=n&&o.name;
			return c&&n;});	
	// 如果仅有子项，则循环找到根
	var nItems=[],nDict={};
	$.each(items,function(i,item){
		for(var o=item;!!o;o=dict[o.pid]){
			o.state="opened";
			if(o.pid==nRoot.id && !nDict[o.id]){
				nItems.push(o);
				nDict[o.id]=1;
				break;
			}
		}		
	});
	var ndata=ndata=[$.extend({},nRoot,{children:nItems})];
	//ndata=fnGrep(nItems,"opened");	
	tree.loadData(ndata);
};
fSearch.onkeydown=function(e){ 
    if(e.keyCode==13){ 
    	search();  
    } 
}
var reset=function(){$('#fSearch').asForm().reset();$.each(all,function(i,item){if(item.children)item.state='closed'; item.cflag=item.nflag=false;});tree.loadData(treedata);};
$(function(){
	var canEdit=function(r,exAll){
		return r.origin =="t"||(exAll && r.id==nRoot.id);
	};
	var startEdit=function(r){
		var id=r.id;
		if(id==0) return;
		if(tree.editingid==id)return true;
		if(!canEdit(r)){
			$.messager.show({title:'提示',msg:'当前字典项不可在此界面维护'});
			return false;
		}
		if(tree.editingid != undefined){
			if(tree.validateRow(tree.editingid)){
				tree.endEdit(tree.editingid);
			}
			else 
				return false;
		}
		tree.beginEdit(id);
		tree.options().finder.getTr(tree[0],id).draggable({disabled:true,revert:true,cursor:'pointer'});
		tree.editingid=id;
	};
	var tree=window.tree=new sunz.Treegrid({
		parent:pMain,iconCls:'icon-edit',
		fit:true,fitColumns:true,
		autoRowHeight:true,
		animate: false,
		data:treedata,
		//lines:true,
		editorHeight:48,
		rowStyler:function(i,r){
			r=r||i; // easyui bug?
			return canEdit(r)?'':{"class":"uneditabled"};
			var cls=canEdit(r)?"":"uneditabled";
			if(r.cflag) cls=cls+" searched_code";
			if(r.nflag) cls=cls+" searched_name";
			return cls?{"class":cls}:cls;
		},
		striped:true,
		columns:[[
		         {field:'id',hidden:true,width:1}, {field:'pid',hidden:true,width:1},
		         {field:'name',title:'名称',editor:{type:'text',options:{required:true,validType:'length[1,255]'}},width:200,formatter:nameFormatter},
		         {field:'code',title:'编码',editor:{type:'text',options:{required:true,validType:'length[1,255]'}},width:100,formatter:codeFormatter},
		         {field:'remark',title:'备注',editor:'textarea',width:120},
		         {field:'orderIndex',title:'顺序',editor:{type:'numberbox',options:{required:true,validType:'length[1,10]'}},width:60}
		]],
		idField:"id",treeField:'name',
		onDblClickRow: startEdit,
		onBeforeDrag:function(r){
			return canEdit(r);
		},
		onBeforeDrop:function(tr,sr,pt){
			tr=tr||nRoot;
			if(tr.id==sr.pid) return false; 	//	没动
			if(!canEdit(tr,true)) return false;	//	仅允许移动到顶级（null）及t表节点
			var o={id:sr.id,typegroupid:tr.id},success=false,err="未保存成功";
			$.ajax("framework/datatable.do?save&t=T_S_TYPE",{
				async:false,
				data:o,
				success:function(jr){
					success=jr.success;
				}
			});
			if(success){
				sr.pid=tr.id;
				var pmp=tr.id==nRoot.id?"已成功移动为顶级项":("已成功移动到【"+tr.name+"】下");
				$.messager.show({title:'恭喜您',msg:"【"+sr.name+"】"+pmp+"，请记得刷新服务器缓存！"});
			}else{
				$.messager.show({title:'提示',msg:err});
				return false;
			}
		},		
		onEndEdit:function(r,c){					
			delete tree.editingid;
			if(!r.id)c=r;
			if(c.orderIndex===null||c.orderIndex===""){
				$.messager.show({title:'提示',msg:'顺序（数字）不能改为空值'});
				return false;
			}
			if(!c.name && !c.code && !c.remark && !c.orderIndex)return false;
			
			tree.enableDnd(r.id);
			
			c.id=r.id;
			if(r.pid){
				c.t="T_S_TYPE";
				c.typecode=c.code;
				c.typename=c.name;
				c.typegroupid=c.pid;
			}else{
				c.t="T_S_TYPEGROUP";
				c.typegroupcode=c.code;
				c.typegroupname=c.name;
			}
			$.ajax('framework/datatable.do?save',{data:c,success:function(r){
				if(r.success){
					//tree.updateRow({index:i,row:$.extend(d,{id:r.data.ID||r.data.id})})
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
					startEdit(sel);					
				}
			},
			{iconCls:'icon-save',text:'保存编辑',handler:function(){
					if(tree.validateRow(tree.editingid)){
						tree.endEdit(tree.editingid);
						tree.editingid=0;
					}
				}
			},
			{iconCls:'icon-cancel',text:'取消编辑',handler:function(){
					tree.cancelEdit(tree.editingid);
					tree.enableDnd(tree.editingid);
					tree.editingid=0;
				}
			},'-',
			{
				iconCls: 'icon-add',text:'新增字典项',
				handler: function(){
					var sel=tree.getSelected()
						,pIsRoot=!sel||sel.id==nRoot.id
						,pid=pIsRoot?null:sel.id;
					if(sel && sel.id!=nRoot.id && !canEdit(sel)){
						$.messager.show({title:'提示',msg:'当前字典项不可在此界面维护'});
						return;
					}
					var defName="新建项"+new Date().toLocaleString(),
						defNum=((sel||nRoot).children||[]).length+1,
						defCode=(pIsRoot?"B":sel.code)+(defNum<10?"00":"0")+defNum;
					var d={TYPEGROUPID:pid,ORDERINDEX:0,TYPENAME:defName,TYPECODE:defCode};
					$.ajax('framework/datatable.do?add&t=T_S_TYPE',{data:d,success:function(r){
						if(r.success){
							var id=r.data.ID||r.data.id,
								nrow={id:id,pid:pid,name:d.TYPENAME,code:d.TYPECODE,orderIndex:d.ORDERINDEX,origin:"t"};
							tree.append({parent:pid,data:[nrow]});
							tree.expand(pid);
							tree.scrollTo(id);
							startEdit(nrow);
							$.messager.show({title:'提示',msg:'新增成功'});
						}else{
							var message=r.msg.indexOf("ORA-00001")==-1 ? '保存时出错了，' : '字段<编码>重复，' ;
							$.messager.alert('提示',message+r.msg);
						} 
					}});
				}
			},'-',
			{
				iconCls: 'icon-no',text:'删除字典项',
				handler: function(){
					var sel=tree.getSelected();
					if(sel==null){
						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
						return;
					}
					if(!canEdit(sel)){
						$.messager.show({title:'提示',msg:'当前字典项不可在此界面维护'});
						return;
					}
					var id=sel.id;
					if(!id){ // 
						tree.deleteRow(tree.getRowIndex(sel)); 
						return;
					}
					$.messager.confirm("删除确认","您确定要删除当前数据吗？",function(ok){
						if(ok){
							$.ajax('framework/dict.do?delete',{data:{id:id},success:function(r){
								if(r.success){
									tree.remove(id);
									$.messager.show({title:'提示',msg:'删除成功'});
								}else{
									$.messager.alert('提示','删除时出错了:'+r.msg);
								} 
							}});
						}
					});
				}
			},'-',{
				text:'刷新服务器缓存',
				iconCls:'icon-reload',
				handler:function(){
					$.ajax('framework/cache.do?refresh&type=dict',{
						success:function(r){
							if(r.success){
								$.messager.show({title:'提示',msg:'刷新成功'});
							}else{
								$.messager.show({title:'提示',msg:'删除时出错了:'+r.msg});
							} 
						}
					});
				}
		}]

	});
	tree.enableDnd();
});
</script>