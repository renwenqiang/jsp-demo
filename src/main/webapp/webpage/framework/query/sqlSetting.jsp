<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<z:config items="SQL.keywords"></z:config>
<z:dict items="all"></z:dict>
<div class="easyui-layout" data-options="fit:true">
	<div id="pSearch" data-options="region:'north',title:'搜索'" style="height:88px">
		<form id="fSearch">
			<table style="width:80%;margin:5px 10%;">
				<tr>
					<td width="5%"></td>
					<td >编码：</td><td><input type="text" class="easyui-textbox" name="code" /></td>
					<td >名称或描述：</td><td><input type="text"  class="easyui-textbox" name="name" /></td>
					<td >sql分组：</td><td><input type="text"  class="easyui-combotree" name="sql_group" 
						data-options="editable:true,loadFilter:function (rows) {  
                            return convert(rows);  
                        },data:[{text:'全部',value:''},{text:'为空',value:'null'}].concat(D.get('SYS004'))"
					/></td>
					<td><a onclick="javascript:window.search()" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >查询</a>
					<a onclick="$('#fSearch').asForm().reset()"  style="margin-left:25px" class="easyui-linkbutton"  data-options="iconCls:'icon-redo'" >重置</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="pMain" data-options="region:'center',title:'Sql配置'" style="padding:5px 5px 5px 5px;"></div>
	
</div>
<style id="sqlSettingCss">
	.sql{padding:5px;max-height:200px;overflow-y:scroll;font-size:15px;line-height:24px}
	.sql div{padding:15px;white-space:pre-wrap;color:#333}
	.sql ,.xeditor{word-spacing:5px}
	.xeditor{color:#c10441;padding:15px !important;min-height:200px !important;font-size:15px !important;line-height:24px}
	.datagrid-cell-c1-sql{position:relative}
	.sqlhview{position:absolute;top:2px;left:2px;width:20px;height:20px;background:url(webpage/framework/query/hvsql.png) no-repeat;background-size:contain;}
</style>
<script type="text/javascript" src="webpage/framework/query/sqlformat.js"></script>
<script type="text/javascript" src="webpage/framework/query/sqleditor.js"></script>

<script type="text/javascript" src="webpage/framework/smartform/util.js"></script>
<script type="text/javascript" src="webpage/framework/query/xeditor.js"></script>

<script type="text/javascript" >
var txtEditor=$.fn.datagrid.defaults.editors.textarea.init;
$.fn.datagrid.defaults.editors.textarea.init=function(c,opt){
	return txtEditor(c,opt).addClass("xeditor").attr("title","本编辑器中可：\r\n\t1.提供右键功能\r\n\t2.支持tab键缩进\r\n\t3.按住ctrl双击可弹出独立窗口进行编辑（同样支持右键和tab缩进）")
		.on("keydown",tabSupport)
		.on("contextmenu",sqlEditSupport)
		.on("dblclick",function(e){
			if(!e.ctrlKey)return;
			var xeditor=this.xeditor;
			if(!xeditor){
				var jme=$(this);
				xeditor=this.xeditor=getXEditor("SQL编辑器",jme.val(),(v)=>jme.val(v),'',"length[0,1<<30]",`
<br>	1.SQL通常需要符合Freemarker语法
		--最常用的是&lt;#if paramName?? && paramName!="">[SQL STATEMENT]&lt;/#if>
		--Freemarker的其它语法可参考<a href="http://freemarker.foofun.cn/ref_builtins_alphaidx.html" target="_black">《官网中文参考手册》</a>或自行网上搜索
<br>	2.建议SQL中对结果字段进行展开（右键有展开工具）并使用双引号固定大小写（如select id "id"），
		--以方便数据库迁移（Oracle默认大写，mysql、pg默认小写）
<br>	3.右键提供了大小写转换、字段大小写固定等常用工具
<br>	4.编辑器中支持tab、shift+tab键缩进功能`);
				xeditor.find(".xeditor").on("contextmenu",sqlEditSupport);
				xeditor.resize({width:800,height:600});
				xeditor.center();
			}
			xeditor.open();
		});
};

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
function convert(rows) {
	for(var i =0;i<rows.length;i++) {
		if(rows[i].children) {
			convert(rows[i].children);
		}
		rows[i].id = rows[i].value;
	}
	return rows;
}
var validateKey=function(k,id){
	var flag=false;
	if(k==null||k==""){
		$.messager.show({title:'提示',msg:'编码不能为空'});
		return flag;
	}
	id=id?id:null;
	var url='framework/datatable.do?uniqueValidate&t=T_S_SqlStatement&c=key&v='+k+'&id='+id;
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

var startEdit=function(i,d){
	if(grid.editingIndex !=i && grid.editingIndex != undefined){
		if(grid.validateRow(grid.editingIndex)){
			grid.endEdit(grid.editingIndex);
		}
		else 
			return false;
	}
	grid.beginEdit(i);
	grid.editingIndex=i;
};
$(function(){
	var grid=window.grid=new sunz.Datagrid({
		parent:pMain,iconCls:'icon-edit',
		url:'framework/query.do?search&k=sqlSetting',
		fit:true,fitColumns:true,
		autoRowHeight:true,nowrap:false,
		pagination:true,rownumbers:true,striped:true,pageSize:3,pageList:[3,10,20,50],
		columns:[[
		         {field:'id',hidden:true},
		         {field:'code',title:'编码',editor:'text',width:120},
		         {field:'name',title:'名称或描述',editor:'text',width:160},
		         {field:'sql',title:'sql语句定义',editor:'textarea',width:600,formatter:function(v,r,i){
		         	return hlsql(v)+'<div class="link sqlhview" title="点击弹出查看定义" onclick="hoverSql(this,true)"><span style="display:none">'+r.code+'</span></div>';
		         }},
		         {field:'sql_group',title:'sql分组',formatter:function(v){ return D.getText(v);},editor:{type: 'combotree', options: { loadFilter:function (rows) {  
                     return convert(rows);  
                 },data: D.get("SYS004")}},width:100}
		]],
		idField:"id",
		onDblClickRow:startEdit,
		onAfterEdit:function(i,d,c){
			delete grid.editingIndex;
			if(!d.id)c=d;
			if(!c.name && !c.code && !c.sql && !c.sql_group)return;
			d.key=d.code; 
			d.description=d.name;
			if(d.key=="" || d.description==""){
				$.messager.show({title:'提示',msg:'保存失败,编码和名称不能为空'});
				return;
			}
			if(validateKey(d.key,d.id)){
				$.ajax('framework/datatable.do?'+(d.id?'save':'add')+'&t=T_S_SqlStatement',{data:d,success:function(r){
					if(r.success){
						grid.updateRow({index:i,row:$.extend(d,{id:r.data.ID||r.data.id})})
						$.messager.show({title:'提示',msg:'保存成功'});
					}else{
						var message=r.msg.indexOf("ORA-00001")==-1 ? '保存时出错了，' : '字段<编码>重复，' ;
						$.messager.alert('提示',message+r.msg);
					} 
				}});
			}
		},
		toolbar: [
			{iconCls:'icon-edit',text:'编辑',handler:function(){
					var sel=grid.getSelected();
					if(sel==null){
						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
						return;
					}
					startEdit(grid.getRowIndex(sel),sel);
				}
			},
			{iconCls:'icon-save',text:'保存',handler:function(){
					if(grid.validateRow(grid.editingIndex)){
						grid.endEdit(grid.editingIndex);
					}
				}
			},
			{iconCls:'icon-undo',text:'取消',handler:function(){if(grid.editingIndex==null)return;grid.cancelEdit(grid.editingIndex);grid.editingIndex=null;}
			},'-',
			{
				iconCls: 'icon-add',text:'新增',
				handler: function(){
					var g=grid,ii=0,sel=g.getSelected(),nr={};
					if(sel)ii=g.getRowIndex(sel);
					g.insertRow({index:ii,row:nr});
					startEdit(g.getRowIndex(nr),nr);
				}
			},'-',
			{
				iconCls: 'icon-remove',text:'删除',
				handler: function(){
					var sel=grid.getSelected();
					if(sel==null){
						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
						return;
					}
					var id=sel.id;
					if(!id){ // 
						grid.deleteRow(grid.getRowIndex(sel)); 
						return;
					}
					$.messager.confirm("删除确认","您确定要删除当前数据吗？",function(ok){
						if(ok){
							$.ajax('framework/datatable.do?delete&t=T_S_SqlStatement',{data:{id:id},success:function(r){
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
				text:'结果预览',
				iconCls:'icon-preview',
				handler:function(){
					var sel=grid.getSelected();
					if(sel==null){
						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
						return;
					}
					window.open("framework/query.do?search&start=0&limit=10&total=-1&k="+sel.code);
				}
			},{
				text:'SQL输出',
				iconCls:'sql-debug',
				handler:function(){
					var sel=grid.getSelected();
					if(sel==null){
						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
						return;
					}
					window.open("framework/query.do?debug&k="+sel.code+"&sqlid="+sel.id);
				}
			},{
				text:'SQL调试',
				iconCls:'sql-debug-ui',
				handler:function(){
					var sel=grid.getSelected();
					if(sel==null){
						$.messager.show({title:'提示',msg:'请选择一行进行操作'});
						return;
					}
					window.open("framework/query.do?testSql&k="+sel.code+"&sqlid="+sel.id);
				}
			},'-',{
				text:'刷新服务器缓存',
				iconCls:'icon-reload',
				handler:function(){
					var fnSuccess=function(){$.messager.show({title:'提示',msg:'刷新成功'});};
					var refresh=function(k,fn){$.post('framework/cache.do?refresh&type=SqlStatement',{item:k},function(jr){
						if(jr.success){
							fn && fn();
						}else{
							$.messager.show({title:'提示',msg:'刷新出错了:'+jr.msg});
						} 
					})};
					var o=grid.getSelected();
					o?refresh(o.id,function(){refresh(o.code,fnSuccess)}):refresh(null,fnSuccess);
				}
		},'-',
		{
			iconCls:"icon-export",text:"导出",
			handler:function(){
				var sel=grid.getSelected();
				if(sel==null)return;
				window.open("framework/query.do?exportSql&id="+sel.id+"&code="+sel.code);
			}
		},
		{
			iconCls:"icon-import",text:"导入",
			handler:function(){
				$('<input type="file" accept="qdf" >').on("change",function(){
					var formData=new FormData();
					formData.append("file", this.files[0]);
			        $.ajax("framework/query.do?importSql",{
			        	type: 'POST',
			        	data:formData,
			        	contentType: false,processData:false,
			        	success:function(jr){
			        		if(jr.success){
				        		var q=jr.data;
			        			$.messager.alert('提示','成功导入语句<br/>\t【'+q.success.join('】<br/>\t【')+'】'
			        					+(q.err.length?('<br/><br/>以下语句导入失败<br/>\t【'+q.err.join('】<br/>\t【')+'】'):""));
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
		},"-",{
			iconCls:"sql-table-helper",text:"表字段展开工具",
			handler:function(){
				showTableParser();
			}
		}]

	});
	
	//if(!top.sqlSettingCss){$($("#sqlSettingCss")[0].outerHTML).appendTo(top.document.head);top.sqlSettingCss=true}
});
</script>