<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>

<div id="viewport" class="easyui-layout" data-options="fit:true">
	<div id="pSearch" data-options="region:'north',title:'搜索'" style="height:88px">
		<form id="fSearch">
			<table style="width:98%;margin:5px 1%;">
				<tr>
					<td >页面：</td><td><input type="text" class="easyui-textbox" name="jsp" /></td>
					<td >编码：</td><td><input type="text" class="easyui-textbox" name="code" /></td>
					<td >名称或描述：</td><td><input type="text" class="easyui-textbox" name="name" /></td>
					<td>
					<a onclick="window.search()" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >查询</a>
					<a onclick="$('#fSearch').asForm().reset()" style="margin-left:25px" class="easyui-linkbutton"  data-options="iconCls:'icon-redo'" >重置</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="pMain" data-options="region:'center',title:'UI资源表'" style="padding:5px 5px 5px 5px;"></div>
</div>

<div id="divResources">
	<table><tr>
		<td id="dToolbar">
		自动扫描页面：<select id="dSchema" ></select>
		UI元素：<select id="dUIs" ></select>
		</td>
	</tr></table>
</div>
<script type="text/javascript">
	// 使用同步吧，功能给开发人员自己用的，不做精确控制--通常不会出错！
	$.ajaxSettings.async=false;
	$(function(){
		var cmbUI=$("#dUIs").asCombogrid({
			width:300,
			//url:'framework/authtag.do?parse',
			checkbox:true,multiple:true,
			columns:[[{title:"选择",field:"selected",width:20,checkbox:true},
			          {field:'code',title:'编码',width:120},
			          {field:'name',title:'名称或描述',width:160},
				      {field:'type',title:'类型',width:100}
			]],
			idField:"code",textField:"name",//treeField:"name",
			loadFilter:function(d){
				$.grep(d.data,function(di,i){
					//di.id=di.text=di.code+'('+di.name+')';
					di.name=di.name||di.code;
					//di.checked=di.selected=true;
				}); 
				return d.data;
			}
		});
		var cmbSchem=$("#dSchema").asCombotree({
			width:400,lines:true,editable:true,
			url:'framework/authtag.do?jspSchema',
			loadFilter:function(d){
				var n2t=function(di,p){
					di.id=di.text=di.name;
					delete di.name;
					if(p){
						di.p=p;
						di.id=p.id+"/"+di.id;
					}
					if(di.type==0){
						di.state="closed";
						if(di.children==null){
							//di.state="opened";
							//di.children=[];
						}
						else 
							$.grep(di.children,function(dis,i){n2t(dis,di);});
					}
				};
				
				var root={id:'webpage'};
				$.grep(d.data,function(di,i){n2t(di,null);});
				
				var splice=function(di){
					var col=di.p?di.p.children:d.data;
					col.splice(col.indexOf(di),1);
				};
				var normal=function(di){
					if(di.type!=0)return;
					if(!di.children|| !di.children.length){
						splice(di);
						for(var p=di.p;p;p=p.p){
							if(!p.children || !p.children.length){
								splice(p);
							}
						}
					}else{
						for(var i=di.children.length-1;i>=0;i--){
							normal(di.children[i]);
						}
					}
				};
				for(var i=d.data.length-1;i>=0;i--){
					normal(d.data[i]);
				}				
				return d.data;
			},
			/*columns:[[{field:'name',title:'路径'}]],
			idField:"name",treeField:"name",*/
			onBeforeSelect:function(d){return d.type==1;},
			onBeforeLoad:function(n){return !n;},
			onSelect:function(d){
				if(d.type==1){
					cmbUI.clear();
					cmbUI.combogrid({url:"framework/authtag.do?parse&jsp="+d.id});
					cmbUI.showPanel();
					cmbUI.grid().datagrid("selectAll");
				}
			}
		});
		
		// toolbars 
		new sunz.Linkbutton({parent:dToolbar,iconCls:'icon-add',text:'添加',handler:function(){
				var vs=cmbUI.grid().datagrid("getChecked");
				if(vs.length==0){
					$.messager.alert('提示','请选择至少一个元素');
					return;
				}

				$.each(vs,function(i,v){
					var param={page:cmbSchem.getValue(),code:v.code,name:v.name,type:v.type||"step"},success=true;
					$.post("framework/authtag.do?addResource",param,function(jr){
						success=jr.success;
						if(!jr.success){
							$.messager.alert('提示','添加'+v.code+'（'+v.name+'）时出错了:'+jr.msg);
						}
					});
					return success; //出错则中断
				});
				search();
			}
		});
		new sunz.Linkbutton({parent:dToolbar,iconCls:'icon-edit',text:'修改',handler:function(){
			}
		});
		new sunz.Linkbutton({parent:dToolbar,iconCls:'icon-cancel',text:'删除',handler:function(){
			$.messager.confirm("删除确认","您确定要删除当前数据吗？",function(ok){
				if(ok){
					$.post("framework/datatable.do?delete&t=T_S_UIRESOURCE",{id:tree.getSelected().id},function(jr){
						if(jr.success){
							search();
						}else{
							$.messager.alert('提示','删除出错了:'+r.msg);
						}
					});
				}
			});
			}
		});
	});
</script>
<script type="text/javascript">
	var search=function(){
		var o=$("#fSearch").asForm().getFieldValues();
		o.total=-1;
		tree.load(o);
	};
	var uiLoadFilter=function(data,pid){
		if(!data.success) return ;
		var tdata=[],dic={};
		$.grep(data.data,function(d,i){
			var ps=d.page.split("/");
			//ps.shift();
			ps.push(d.code);
			var len=ps.length;
			for(var i=0;i<len;i++){
				var p=ps[i],di=dic[p]||{id:p,path:p,pid:(i==0?null:ps[i-1])};
				if(i==len-1){
					di=$.extend(di,d);
				}

				if(!dic[p]){
					if(di.pid){
						var subs=dic[di.pid].children=dic[di.pid].children||[];
						if(i==len-1) di.path=subs.length+1;
						subs.push(di);
					}else{
						tdata.push(di);
					}
					dic[p]=di;
				}
			}					
		});
		return {rows:tdata,total:data.total};
	};
	var queryUrl="framework/query.do?search&k=queryUiresource";
	var columns=[
	     {field:'id',width:10,hidden:true}, {field:'pid',width:10,hidden:true},
	     {field:'path',title:'路径',editor:'textarea',width:120},
	     {field:'code',title:'编码',editor:'text',width:120},
	     {field:'name',title:'名称或描述',editor:'text',width:200},	         
	     {field:'type',title:'类型',width:100}
	];
	$(function(){
		var tree=window.tree=new sunz.Treegrid({
			parent:pMain,iconCls:'icon-edit',
			url:queryUrl,
			fit:true,fitColumns:true,
			autoRowHeight:true,
			pagination:true,
			striped:true,
			columns:[columns],
			idField:"id",treeField:'path',
			loadFilter:uiLoadFilter,
			pageList:[10,20,50,100,200],
			toolbar:"#divResources"
		});
		
	});
</script>
