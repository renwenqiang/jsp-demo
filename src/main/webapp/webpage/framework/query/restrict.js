/**
 * 权限控制
 */
$(function(){
	var userAndRole=D.add("UserAndRole","UserAndRole","权限设置选择树",0,0),
		roles=D.getItem("UserRole"),
		tempD=D.createNew(),
		departUsers=tempD.add({id:"UserDepart",code:"UserDepart",text:"组织机构",pid:"UserAndRole",order:1});
	roles.pid="UserAndRole";
	userAndRole.children=[roles,departUsers];
	
	$.post("framework/query.do?search&k=departUserTree",null,function(jr){
		$.each(jr.data,function(i,d){if(!d.pid)d.pid="UserDepart";else d.id=d.code; D.add(d);tempD.add(d);});
	});
});
var preFixs={"UserAndRole":" ","UserDepart":"组织机构：","UserRole":" "};
var RestrictEditor=function(/*有现成的这些方法不用？*/startEdit,afterEdit,saveRow,addRow,editRow,deleteRow,cancelEdit){
	var me=this;	
	var win=new sunz.Window({title:"权限控制",width:480,height:320});
	var grid=new sunz.Datagrid({parent:win[0],
		fit:true,fitColumns:true,
		autoRowHeight:true,
		pagination:true,rownumbers:true,striped:true,
		columns:[[
		         {field:'ID',hidden:true},
		         //{field:'TARGETTYPE',title:'作用对象类型',width:120,formatter:function(v){return v=="Role"?"角色":(v=="User"?"用户":"未知");}},
		         {field:'TARGET',title:'作用对象',width:360,formatter:function(v,r){return !v?null:((r.TARGETTYPE=="Role"?"角色：":"用户：")+D.getText(v));},
		        	editor:{type:"combotree",options:{
		        		 prompt:"请选择一个角色或用户",
		        		 editable:true,required:true,
		        		 data:D.get("UserAndRole"),idField:"code",formatter:function(r){return (preFixs[r.pid]||" ")+r.text;},
		        		 onBeforeSelect:function(r){
		        			 if(r.pid=="UserDepart"||r.pid=="UserAndRole")return false;
			        		 var sel=grid.getRows()[grid.editingIndex];
			        		 sel.TARGETTYPE=r.pid=="UserRole"?"Role":"User";
		        		 }
		        	 }
		         }}
		]],
		idField:"ID",
		toolbar:[{iconCls:'icon-add',text:'新加',handler:function(){addRow(grid);}},
		         '-',
		         {iconCls:'icon-edit',text:'编辑',handler:function(){editRow(grid);}},
		         {iconCls:'icon-save',text:'应用',handler:function(){saveRow(grid);}},
		         {iconCls:'icon-undo',text:'取消',handler:function(){cancelEdit(grid);}},
		         '-',
		         {iconCls:'icon-remove',text:'删除',handler:function(){deleteRow(grid);}}
		],
		onDblClickRow:function(i,d){startEdit(grid,i,d);},
		onAfterEdit:function(i,r,c){afterEdit(grid,i,r,c);}
	});
	
	
	//
	this.edit=function(resourceid){
		grid.clearSelections();
		grid.datagrid({url:"framework/datatable.do?exactly&t=T_S_QUERY_ResourceRestrict&resourceid="+resourceid});
		grid.editUrl="framework/datatable.do?t=T_S_QUERY_ResourceRestrict&Restriction=hidden&resourceid="+resourceid;
		win.open();
	}
	$.extend(this,win);
};