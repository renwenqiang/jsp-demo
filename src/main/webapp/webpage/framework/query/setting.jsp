<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<z:config items="all,defalutQueryEditLayoutType,queryOptionalSources"></z:config>
<z:dict items="SYS004,SYS003,UserRole"></z:dict>
<script type="text/javascript" src="webpage/framework/query/queryEdit.js"></script>
<script type="text/javascript" src="webpage/framework/query/queryOperationDef.js"></script>
<script type="text/javascript" src="webpage/framework/query/restrict.js"></script>
<script type="text/javascript" src="webpage/framework/smartform/util.js"></script>
<script type="text/javascript" src="webpage/framework/query/xconstants.js"></script>
<script type="text/javascript" src="webpage/framework/query/xeditor.js"></script>
<script type="text/javascript" src="webpage/framework/query/smartformSupport.js"></script>
<style>
	textarea{padding:5px 10px}
	.htmlcell{width:100%;height:75px;overflow:auto;white-space:pre-wrap;}
	.datagrid-cell .htmlcell{background-color:transparent;}
	.jsonEditor{min-height:120px !important;font-size:15px !important;line-height:20px}
	.l-btn-icon-left .l-btn-icon {background-size:contain;}
	
	.easyui-panel .qtoolbar{padding:5px 25% 15px}
	.easyui-panel .pBase,.easyui-panel .pSField,.easyui-panel .pRField{height: auto !important;}
	.easyui-layout .qtoolbar{position:absolute;top:2px;z-index:99}
	.easyui-accordion .qtoolbar{position:absolute;top:34px;z-index:99}
	.easyui-tabs .qtoolbar{position:relative;top:5px;width:50%;margin-left:25%;}
	
	.dialog-button{text-align:center;}
	.dialog-button .l-btn{margin:0 15px}
</style>

<div class="easyui-layout" data-options="fit:true">
	<div id="pSearch" data-options="region:'north',title:'搜索'" style="height:88px">
		<form id="fSearch">
			<table style="margin:5px 0 5px 5px;">
				<tr>
					<td width="220px"><input type="text" class="easyui-textbox" data-options="label:'编码',width:200,labelWidth:40"  name="code" /></td>
					<td width="340px"><input type="text" class="easyui-textbox" data-options="label:'名称或描述',width:320,labelWidth:90" name="name" /></td>
					<td>
					<a onclick="window.search()" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >查询</a>
					<a onclick="$('#fSearch').asForm().reset()" style="margin-left:10px" class="easyui-linkbutton"  data-options="iconCls:'icon-redo'" >重置</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="pMain" data-options="region:'center',title:'查询配置'" style="padding:5px 5px 5px 5px;"></div>
</div>

<script type="text/javascript" >
var txtEditor=$.fn.datagrid.defaults.editors.textarea.init;
$.fn.datagrid.defaults.editors.textarea.init=function(c,opt){return txtEditor(c,opt).addClass("jsonEditor")};
var search=function(){
	var o=$("#fSearch").asForm().getFieldValues();
	o.total=-1;
	grid.load(o);
};
fSearch.onkeydown=function(e){ 
    if(e.keyCode==13){ 
    	search();  
    } 
}
var fnCheck=function(v,i,r){return v==true?"√":"×";},
	fnModel=function(v,i,r){return v?'<span style="color:#f00">白名单</span>':"黑名单";},
	fnSwitchMode=function(o,n,t){
		if(!o){
			$.messager.show({title:'提示',msg:'请选择一行数据'});
			return;
		}
		var d={id:o.ID};
		d[n]=o[n]?0:1;
		$.messager.confirm("权限模式切换确认","您确定要切换"+t+"权限模式为“"+fnModel(d[n])+"”模式吗？",function(ok){
			if(ok){
				$.post("framework/datatable.do?t=T_S_QUERY&save",d,function(jr){
					if(jr.success){
						$.messager.show({title:'恭喜您',msg:'权限模式切换成功'});
						delete d.id;
						grid.updateRow({index:grid.getRowIndex(o),row:d});
					}else{
						$.messager.alert("对不起","权限模式切换失败了！"+jr.msg);
					}
				});
			}
		});
	};
$(function(){
	var KEY_LAYOUT_TYPE="defalutQueryEditLayoutType",
		switchLayoutType=(t)=>{
			localStorage.setItem(KEY_LAYOUT_TYPE,t);
			grid.queryEditor&&grid.queryEditor.destroy();
			grid.queryEditor=null;
		},
		getEditor=()=>{	return grid.queryEditor=grid.queryEditor||new QueryEditor(null,localStorage.getItem(KEY_LAYOUT_TYPE)||C[KEY_LAYOUT_TYPE]);},
		getOperDef=()=>{return grid.queryOperDef=grid.queryOperDef||new QueryOperDef();};
	
	var grid=window.grid=new sunz.Datagrid({
		parent:pMain,
		url:'framework/query.do?search&k=querySetting',
		fit:true,fitColumns:true,
		autoRowHeight:true,
		pagination:true,rownumbers:true,striped:true,pageSize:5,pageList:[5,10,20,50],
		columns:[[
		         {field:'KEY',title:'编码',editor:'text',width:100,rowspan:2},
		         {field:'DESCRIPTION',title:'名称或描述',editor:'text',width:120,rowspan:2},
		         {field:'SQLNAME',title:'sql语句定义',editor:'textarea',width:120,rowspan:2}	,
		         {field:'AUTOLOADDATA',title:'启动加载数据',editor:'textarea',width:75,align:"center",formatter:fnCheck,rowspan:2},
		         {field:'MULITSELECT',title:'多选',editor:'textarea',width:48,align:"center",formatter:fnCheck,rowspan:2},
		         {title:"权限控制模式",width:147,colspan:3},
		         {field:'SETTING',title:'其它设置',editor:'textarea',width:300,rowspan:2,formatter:function(v){return '<textarea  readonly="true" class="htmlcell">'+(v?v:"")+'</textarea>';}},
		         //{field:'DOUBLEHANDLER',title:'双击处理函数',editor:'textarea',width:300,formatter:function(v){return '<textarea  readonly="true" class="htmlcell">'+(v?v:"")+'</textarea>';}}
		         /*,{field:'Operate',title:'操作',width:120,formatter:function(v,r,i){
		        	 var url="framework/query.do?goQuery&key="+r.KEY,vUrl=url+"&direction=vertical";
		        	 return '<center><a onclick="window.open(\''+url+'\')" class="easyui-linkbutton" data-options="iconCls:\'icon-redo\'">预览</a>\
		        	 				 <a onclick="window.open(\''+vUrl+'\')" class="easyui-linkbutton" data-options="iconCls:\'icon-redo\'">竖排</a>\
		        	 		</center>';
		         }}*/
		],[{field:'SFRESTRICTMODE',title:'查询条件',editor:'textarea',width:48,align:"center",formatter:fnModel},
	         {field:'RFRESTRICTMODE',title:'结果字段',editor:'textarea',width:48,align:"center",formatter:fnModel},
	         {field:'OPERATIONRESTRICTMODE',title:'操作按钮',editor:'textarea',width:48,align:"center",formatter:fnModel}]],
		//onLoadSuccess:function(){$(".easyui-linkbutton").asLinkbutton();},
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
					var sel=grid.getSelected();
					if(sel==null){
						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
						return;
					}
					getEditor().edit(sel);
				}
			},'-',
			{
				iconCls:'icon-edit',text:'操作定义',
				handler:function(){
					var sel=grid.getSelected();
					if(sel==null){
						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
						return;
					}
					getOperDef().operDef(grid.getSelected());
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
							$.ajax('framework/query.do?delete',{data:{id:id},success:function(r){
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
			},'-',
			{
				iconCls:"icon-preview",text:"预览",
				handler:function(){
					var sel=grid.getSelected();
					if(sel==null)return;
					window.open("framework/query.do?goQuery&key="+sel.KEY);
				}
			},
			{
				iconCls:"icon-preview-v",text:"竖排预览",
				handler:function(){
					var sel=grid.getSelected();
					if(sel==null)return;
					window.open("framework/query.do?goQuery&key="+sel.KEY+"&direction=vertical");
				}
			},'-',
			{
				iconCls:"icon-export",text:"导出",
				handler:function(){
					var sel=grid.getSelected();
					if(sel==null)return;
					window.open("framework/query.do?exportToFile&id="+sel.ID+"&k="+sel.KEY);
				}
			},
			{
				iconCls:"icon-import",text:"导入",
				handler:function(){
					$('<input type="file" accept="qdf" >').on("change",function(){
						var formData=new FormData();
						formData.append("file", this.files[0]);
				        $.ajax("framework/query.do?importFromFile",{
				        	type: 'POST',
				        	data:formData,
				        	contentType: false,processData:false,
				        	success:function(jr){
				        		if(jr.success){
					        		var q=jr.data;
					        		$.messager.show({title:'提示',msg:q.DESCRIPTION+'('+q.KEY+')导入成功'});
					        		grid.reload();
				        		}else{
				        			$.messager.alert('提示','导入出错啦:'+jr.msg);
				        		}
				        	},
				        	error:function(){
								$.messager.alert('提示','导入出错啦!');
				        	}
				        });
				    }).click();
				}
			}
		]
	});
	$.parser.parse();
	
	new sunz.Menubutton({parent:$('<td></td>').appendTo($(".datagrid-toolbar tr",pMain)),text:"权限控制模式切换",iconCls:"icon-lock",menu:new sunz.Menu()
	  .appendItem({text:"查询条件",id:"sf",onclick:()=>{fnSwitchMode(grid.getSelected(),"SFRESTRICTMODE","查询条件");}})
	  .appendItem({separator: true})
	  .appendItem({text:"结果字段",id:"rf",onclick:()=>{fnSwitchMode(grid.getSelected(),"RFRESTRICTMODE","查询条件");}})
	  .appendItem({separator: true})
	  .appendItem({text:"操作按钮",id:"oper",onclick:()=>{fnSwitchMode(grid.getSelected(),"OPERATIONRESTRICTMODE","查询条件");}})
	 });
	
	new sunz.Menubutton({parent:$('<td></td>').appendTo($(".datagrid-toolbar tr",pMain)),text:"编辑框显示模式切换",iconCls:"icon-layout",menu:new sunz.Menu()
	  .appendItem({text:"上-左-右",onclick:()=>{switchLayoutType("layout")}})
	  .appendItem({separator: true})
	  .appendItem({text:"手风琴",onclick:()=>{switchLayoutType("accordion")}})
	  .appendItem({separator: true})
	  .appendItem({text:"选项卡",onclick:()=>{switchLayoutType("tabs")}})
	  .appendItem({separator: true})
	  .appendItem({text:"平铺",onclick:()=>{switchLayoutType("panel")}})
	 });
});
</script>