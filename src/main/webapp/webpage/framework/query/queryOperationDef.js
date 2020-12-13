/**
 * 操作定义
 */
var QueryOperDef=function(query){
	query=query||{};
	var def_Window='<div class="easyui-layout" data-options="fit:true">\
						<div class="qoperation" data-options="region:\'center\',title:\'操作定义配置\'" ></div>\
					</div>';
					
	var win=new sunz.Window({title:'操作定义窗口',fit:true,modal:true}).append(def_Window);
	
	var appendParam=function(opt){
		if(!query.ID){
			return false;
		}		
		opt.queryid=query.ID;
	};
	
	var startEdit=function(g,i,d){
		if(g.editingIndex==i)return true;
		if(g.editingIndex !=undefined){
			$(":input",g).validatebox();
			if(g.validateRow(g.editingIndex)){
				g.endEdit(g.editingIndex);
			}	 
			else 
				return false;
		}
		g.editingIndex=i;
		g.beginEdit(i);
		var txt=g.getEditor({index:i,field:"JSHANDLER"}).target;
		txt.on("dblclick",()=>{
			getXEditor("其它设置",txt.val(),(v)=>{txt.val(v);},"function(){\r\n\r\n}","function",`JS函数
	可通过全局变量grid对datagrid进行引用，如获取被选中行可使用：grid.getSelected()
	一个常见的写法开头如下：
		function(){
			var sel=grid.getSelected();
			if(sel==null){$.messager.show({title:'提示',msg:'请选择一行进行操作'});return;}
			// business codes
		}
	`).open();
		}).attr("placeholder","JS函数，形如\r\nfunction(){\r\n\r\n}\r\n双击可弹出大的专用编辑框");
	},afterEdit=function(g,i,r,c){
		delete g.editingIndex;
		var f=!r.ID;
		$.each(c,function(n){
			return !(f=true);
		});
		if(!f)return ;		
		if(r.ID) c.save=null; 
		else {
			c=r;
			c.add=null;
		}
		c.id=r.ID;
		var url=g.editUrl;
		c.queryid=query.ID;
		$.ajax(url,{data:c,success:function(r){
			if(r.success){
				g.updateRow({index:i,row:$.extend(r,{ID:r.data.ID})})
				g.acceptChanges();
				$.messager.show({title:'提示',msg:'保存成功'});
			}else{
				$.messager.alert('提示','保存时出错了'+r.msg);
			} 
		}});
	},saveRow=function(g){
		if(g.editingIndex!=undefined && g.validateRow(g.editingIndex)){
			g.endEdit(g.editingIndex);
		}
	},addRow=function(g,def){
		if(g.editingIndex!=undefined && !g.validateRow(g.editingIndex)){
			$.messager.show({title:'提示',msg:'请先完成当前正在进行的编辑操作'});
			return;
		}
		var ii=0,sel=g.getSelected(),nr=$.extend({ORDERINDEX:g.getRows().length+1},def);
		if(sel)ii=g.getRowIndex(sel)+1;
		g.insertRow({index:ii,row:nr});
		startEdit(g,g.getRowIndex(nr),nr);
	},editRow=function(g){
		var sel=g.getSelected();
		if(sel==null){
			$.messager.show({title:'提示',msg:'请选择一行进行操作'});
			return;
		}
		startEdit(g,g.getRowIndex(sel),sel);
	},deleteRow=function(g){
		var sel=g.getSelected();
		if(sel==null){
			$.messager.show({title:'提示',msg:'请选择一行进行操作'});
			return;
		}
		var id=sel.ID;
		if(!id){ // 
			g.deleteRow(g.getRowIndex(sel)); 
			return;
		}
		$.ajax(g.editUrl,{data:{"delete":null,id:id},success:function(r){
			if(r.success){
				g.deleteRow(g.getRowIndex(sel));
				$.messager.show({title:'提示',msg:'删除成功'});
			}else{
				$.messager.alert('提示','删除时出错了:'+r.msg);
			} 
		}});
	},cancelEdit=function(g){
		if(g.editingIndex==null)return;
		g.cancelEdit(g.editingIndex);
		g.editingIndex=null;
	},editOrder=function(g,dOrder){
		var sel=g.getSelected();
		if(null==sel) return;
		var curRow=g.getRowIndex(sel);
		if(g.editingIndex!=curRow){
			editRow(g);
		}
		var editor=g.getEditor({index:curRow,field:'ORDERINDEX'}).target;
		editor.val(+editor.val()+dOrder); // +号不能省，会认为是字符
		g.editingIndex=g.getRowIndex(sel);
	};
	
	var createEditor=window.createOperationEditor=(pele)=>{
		var gOperDef=new sunz.Datagrid({
			parent:pele,
			fit:true,fitColumns:true,
			autoRowHeight:true,
			rownumbers:true,striped:true,
			url:'framework/query.do?search&k=qOprationByQuery',
			onBeforeLoad:appendParam,
			columns:[[{field:'ID',hidden:true},
					  {field:'TEXT',title:'文字/名称',width:100,editor:'text'},
			          {field:'ICON',title:'图标',width:80,
						  editor:{type:'combogrid',
							  options:{url:'framework/query.do?search&k=queryForIcon&rejectCss=0',fit:true,fitColumns:true,
								  columns:[[
									  		{field:'path',title:'预览',width:64,align:"center",formatter:function(v){return '<img src="'+v+'"/>'}},
									  		{field:'iconClas',title:'css样式类名',width:80},
								            {field:'name',title:'名称或描述',width:120},
								            {field:'category',title:"分类",width:80,formatter:function(v){return D.getText(v);}},
								            {field:'extend',title:'格式',width:48}
				                          ]],
						  		  idField:"iconClas",delay:1000,mode:'remote',pagination:true,
						  		  panelWidth:600,panelHeight:400
							  }
						  }
					  },
			          {field:'JSHANDLER',title:'js处理函数',width:150,editor:{type:'textarea',options:{validType:'json'}},formatter:function(v,r,i){return '<textarea  readonly="true" class="htmlcell">'+(v?v:"")+'</textarea>';}},
			          {field:'ORDERINDEX',title:'顺序',width:36,editor:'numberbox'}
			]],
			sortName:'ORDERINDEX',
			toolbar:[{iconCls:'icon-add',text:'新加',handler:function(){addRow(gOperDef);}},
			         '-',
			         {iconCls:'icon-bind',text:'绑定Smart表单',handler:()=>{
			        	 var g=gOperDef;
			        	 if(g.editingIndex!=undefined){
			     			 $.messager.show({title:'提示',msg:'请先完成当前正在进行的编辑操作'});
			     			 return;
			     		 }
			        	 
			        	 bindSmartForm(g,addRow,editRow,saveRow);
			         }},
			         '-',
			         {iconCls:'icon-edit',text:'编辑',handler:function(){editRow(gOperDef);}},
			         {iconCls:'icon-save',text:'应用',handler:function(){saveRow(gOperDef);}},
			         {iconCls:'icon-undo',text:'取消',handler:function(){cancelEdit(gOperDef);}},
			         '-',
			         {iconCls:'icon-remove',text:'删除配置',handler:function(){deleteRow(gOperDef);}},
			         '-',
			         {iconCls:'icon-putout',text:'顺序上升一位',handler:function(){editOrder(gOperDef,-1);}},
			         {iconCls:'icon-put',text:'顺序下移一位',handler:function(){editOrder(gOperDef,1);}}
			         ,'-',
			         {iconCls:"icon-lock",text:"权限控制",handler:function(){
			        	 	var g=gOperDef,sel=g.getSelected();
				     		if(sel==null){
				     			$.messager.show({title:'提示',msg:'请选择一行进行操作'});
				     			return;
				     		}
				     		if(gOperDef.authEditor==null)
				     			gOperDef.authEditor=new RestrictEditor(startEdit,afterEdit,saveRow,addRow,editRow,deleteRow,cancelEdit);
				     		gOperDef.authEditor.setTitle("操作--"+sel.TEXT+"--权限设置");
				     		gOperDef.authEditor.edit(sel.ID);
			        	 }
			         }
			],
			onDblClickRow:function(i,d){startEdit(gOperDef,i,d);},
			onAfterEdit:function(i,r,c){afterEdit(gOperDef,i,r,c);}
		});
		gOperDef.editUrl="framework/datatable.do?t=T_S_QUERY_OPERATION";
		
		return gOperDef;
	};
	
	$.parser.parse(win);
	var gOperDef=createEditor(win.find(".qoperation"));
	this.operDef=function(q){
		query=q;
		win.setTitle("操作定义");
		win.open();
		gOperDef.clearSelections();
		if(query.ID){
			gOperDef.load();
		}else{
			gOperDef.loadData([]);
		}
		$(":input").validatebox();
	}
	
}