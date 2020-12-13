/**
 * 打印相关代码
 */
var QueryEditor=function(query,type){
	query=query||{};
	var me=this;
	var html_Window='<div class="qroot easyui-'+(type||"layout")+'" data-options="fit:true">\
						<div data-options="collapsed:false,region:\'north\',title:\'基本信息\',split:true,height:320"><div class="pBase"  style=""></div></div>\
						<div data-options="collapsed:false,region:\'west\',title:\'搜索字段配置\',split:true,width:600"><div class="pSField" style="height:100%;min-height:400px"></div></div>\
						<div data-options="collapsed:false,region:\'center\',title:\'结果字段配置\'"><div class="pRField" style="height:100%;min-height:400px"></div></div>\
					</div>';
	//<div class="operate" style="display:none" data-options="hidden:true,collapsed:false,region:\'east\',title:\'操作按钮配置\'"><div class="poperate" style="height:100%;min-height:400px"></div></div>\
	var html_Query='<form><table cellpadding="1" class="kv-table" >\
						<tr>\
							<td class="kv-label">编码:</td><td class="kv-content"><input type="text" class="easyui-textbox" data-options="required:true" name="KEY" /></td>\
							<td class="kv-label">名称或描述:</td><td class="kv-content"><input type="text" class="easyui-textbox" data-options="required:true" name="DESCRIPTION" /></td>\
						</tr>\
						<tr>\
							<td class="kv-label">数据url:</td><td class="kv-content"><input type="text" class="easyui-textbox" name="DATAURL" /></td>\
							<td class="kv-label">SQL语句<a href="framework/query.do?sqlSetting" target="_blank" class="" iconcls="icon-more">配置</a>：</td>\
							<td  class="kv-content"><select id="cmbSql" style="" class="easyui-combogrid" name="SQLID" data-options="url:\'framework/query.do?search&k=dictForSql\',panelWidth:600,panelHeight:400,delay:1000,mode:\'remote\',pagination:true,fitColumns:true,idField:\'id\',textField:\'code\',columns:[[{field:\'code\',title:\'编码\',width:160},{field:\'name\',title:\'名称\',width:240},{field:\'sqlgroup\',title:\'分组\',width:100,formatter:function(v){return D.getText(v)}}]],width:600,formatter:function(v,r){return r.code+\',\'+r.name;}" ></select></td>\
						</tr>\
						<tr>\
							<td class="kv-label">数据字典项:</td><td class="kv-content"><input type="text" class="easyui-textbox" name="DICTS" /></td>\
							<td class="kv-label">系统配置项:</td><td class="kv-content"><input type="text" class="easyui-textbox" name="CONFIGS" /></td>\
						</tr>\
						<tr><td class="kv-label">是否启动加载:</td><td class="kv-content"><input type="radio" name="AUTOLOADDATA" value="1">是</input><input type="radio" name="AUTOLOADDATA" value="0">否</input></td>\
							<td class="kv-label">是否允许多选:</td><td class="kv-content"><input type="radio" name="MULTISELECT" value="1">是</input><input type="radio" name="MULTISELECT" value="0">否</input></td>\
						</tr>\
						<tr>\
							<td class="kv-label">额外资源:</td><td class="kv-content" colspan="3"><textarea placeholder="html片断(右键可引入常见预定义资源)，如\r\n<style></style>\r\n<script></script>等" class="easyui-textarea htmlcell" name="RESOURCES"></textarea></td>\
						</tr>\
						<tr>\
							<td class="kv-label">双击处理函数:</td><td class="kv-content" colspan="3"><textarea placeholder="js函数，形如\r\nfunction(r,i,g){\r\n}" data-options="validType:\'json&quot;function&quot;\'" class="easyui-textarea htmlcell" name="DOUBLEHANDLER"></textarea></td>\
						</tr>\
						<tr>\
							<td class="kv-label">grid的额外设置:</td><td class="kv-content" colspan="3"><textarea placeholder="JSON类型的参数配置，形如\r\n{\r\n}" data-options="validType:\'json\'" class="easyui-textarea htmlcell" name="GRIDSETTING"></textarea></td>\
						</tr>\
						<tr>\
							<td class="kv-label">其它设置:</td><td class="kv-content" colspan="3"><textarea placeholder="JSON类型的参数配置，形如\r\n{\r\n}" data-options="validType:\'json\'" class="easyui-textarea htmlcell" name="SETTING"></textarea></td>\
						</tr>\
					</table><div class="qtoolbar"></div></form>';
	var inputTypes=[{value:"textbox",text:"文本框"}
					,{value:"numberbox",text:"数字框"}
					,{value:"combo",text:"下拉框(combo)"}
					,{value:"combobox",text:"下拉框(combobox)"}
					,{value:"combogrid",text:"下拉表格"}
					,{value:"combotree",text:"下拉树"}
					,{value:"datebox",text:"日期框"}
					,{value:"datetimebox",text:"时间框"}
					,{value:"dictcombo",text:"下拉字典（公司扩展）"}],dictType={};
	$.each(inputTypes,function(i,t){
		dictType[t.value]=t.text;
	});
	
	var win=new sunz.Window({title:'查询编辑窗口',fit:true,modal:true}).append(html_Window),
		pBase=win.find(".pBase"),pSField=win.find(".pSField"),pRField=win.find(".pRField"),
		form=$(html_Query).appendTo(pBase).asForm();
	
	win.find("textarea").on("dblclick",function(){
		var jme=$(this),n=jme.attr("name");
		getXEditor(XConstants["TITLE_"+n],jme.val(),(v)=>{
			jme.val(v);
		},XConstants["DEF_"+n],eval("({"+jme.attr("data-options")+"})").validType,XConstants["DOC_"+n]).open();
	}).on("keydown",tabSupport).attr("title","双击试试");
	win.find("[name=DOUBLEHANDLER]").on("contextmenu",function(e){
		e.preventDefault();
		var jme=$(this);
		var m=new sunz.Menu();
		m.appendItem({text:"绑定弹出Smart表单",onclick:()=>{
			var dlg=bindSmartForm({getRows:()=>[]},(g,o)=>{
				jme.val(o.JSHANDLER);
			},()=>{},()=>{}).setTitle("Grid双击绑定弹出Smart表单--请确保仅选择一种功能");
			var c=dlg.panel();
			c.find("[checkboxname]").checkbox({checked:false});
			c.find("[checkboxname=view],[checkboxname=auto]").checkbox({checked:true});
		}});
		m.show({left:e.clientX,top:e.clientY});
	});
	win.find("[name=RESOURCES]").on("contextmenu",function(e){
		e.preventDefault();
		var txt=this,start=txt.selectionStart,end=txt.selectionEnd,sel=txt.value.substring(start,end);
		var m=new sunz.Menu();
		$.each(eval("("+C.queryOptionalSources+")"),(i,o)=>{
			m.appendItem({text:o.title,onclick:()=>{
				var scroll=txt.scrollHeight - txt.scrollTop,
					frg=o.html+"\r\n";
				
				txt.value=txt.value.substr(0,start)+frg+txt.value.substr(end);
				txt.focus();
			    txt.selectionStart=start;
			    txt.selectionEnd=end+frg.length;
			    txt.scrollTop=txt.scrollHeight - scroll;
			}});
		})
		m.show({left:e.clientX,top:e.clientY});
	}).attr("title","右键或双击试试");
	
	var cmbSql= win.find("#cmbSql").asCombogrid({"onSelect":function(i){//nv,ov){
		//if(nv==null||query.SQLID==nv)return;
		//query.SQLID=nv;
		var ov=query.SQLID,row=cmbSql.grid().datagrid("getSelected");
		if(row.id==null || row.id==query.SQLID) return;

		query.SQLID=row.id;
		var setQuerySql=function(sampleParam){
			$.ajax("framework/query.do?setQuerySql"+(sampleParam?("&"+sampleParam):""),{
				data:{queryid:query.ID,sqlid:row.id,immedite:true},
				success:function(r){
					gSearch.load();
					gResult.load();
				}
			});
		};
		$.messager.prompt(ov?"更改sql语句会使得字段重置，您确定要继续吗？":"自动计算搜索字段和结果字段"
			,"计算结果字段时需要进行采样，若sql语句需要提供参数方能返回结果时，请输入附加参数："
			,function(pmpt){
				if(pmpt===undefined){ // 点击取消
					//cmbSql.setValue(ov);
				}else{
					if(query.ID)
						setQuerySql(pmpt)
					else saveBase(function(){setQuerySql(pmpt);});
				}
		});
	}});
	
	var setSql=function(row,sampleParam){
		// 查询字段，本地消化
		var reg=/:[ tnx0Bfr]*([0-9a-z.A-Z]+)|[$#]\{([^\.\t\n\s\f\r{}]+)\}/g;
		var arr=null,count=0;
		while(arr=reg.exec(row.sql)){
			var name=arr[1]||arr[2];
			gSearch.appendRow({NAME:name,TITLE:name,ORDERINDEX:++count,INPUTTYPE:"textbox"});
		}
		// 结果字段，要取样
		var url="framework/query.do?search";
		$.ajax(url,{
			data:$.extend({id:row.id,start:0,limit:1,total:1},sampleParam),
			success:function(r){
				if(r.success&&r.data){
					count=0;
					$.each(r.data[0],function(n,v){
						gResult.appendRow({NAME:n,TITLE:name,ORDERINDEX:++count});
					});
				}
			}
		});
	}
	
	var validateKey=function(k){
		var flag=false;
		if(k==null||k==""){
			$.messager.show({title:'提示',msg:'编码不能为空'});
			return flag;
		}
		var id=query.ID?query.ID:null;
		var url='framework/datatable.do?uniqueValidate&t=T_S_QUERY&c=key&v='+k+'&id='+id;
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
	
	var saveBase=function(fn){
		var url="framework/datatable.do?t=T_S_QUERY", o=form.getFieldValues();
		if(validateKey(o.KEY)){
			if(query.ID) o.save=null;else o.add=null;
			o.id=query.ID;
			$.ajax(url,{data:o,success:function(r){
				if(r.success){
					query=r.data;
					query.ID=query.ID||query.id;
					if(fn) 
						fn();
					else
						$.messager.show({title:'提示',msg:'保存成功'});
					window.grid.reload();
				}else{
					var message=r.msg.indexOf("ORA-00001")==-1 ? '保存时出错了，' : '字段<编码>重复，' ;
					$.messager.alert('提示',message+r.msg);
				} 
			}});	
		}
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
		var txt=g.getEditor({index:i,field:"STYLES"}).target;
		txt.on("dblclick",()=>{
			getXEditor("其它设置",txt.val(),(v)=>{txt.val(v);},"{\r\n\r\n}","json","JSON格式，具体参数参考easyui帮助文档").open();
		}).attr("placeholder","JOSN格式，形如 \r\n{\r\n}");
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
		var fnSave=function(){
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
		};
		if(query.ID)fnSave();else saveBase(fnSave);
	},saveRow=function(g){
		if(g.editingIndex!=undefined && g.validateRow(g.editingIndex)){
			g.endEdit(g.editingIndex);
		}
	},addRow=function(g){
		if(g.editingIndex!=undefined && !g.validateRow(g.editingIndex)){
			$.messager.show({title:'提示',msg:'请先完成当前正在进行的编辑操作'});
			return;
		}
		var ii=0,sel=g.getSelected(),nr={ORDERINDEX:g.getRows().length+1};
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
		sel.ORDERINDEX=+sel.ORDERINDEX+dOrder;
		g.refreshRow(curRow);
		startEdit(g,curRow,sel);
		
		//g.updateRow({index:curRow,row:$.extend(sel,{ORDERINDEX:sel.ORDERINDEX+dOrder})});
		//var editor=g.getEditor({index:curRow,field:'ORDERINDEX'}).target;
		//editor.val(+editor.val()+dOrder); // +号不能省，会认为是字符
	},editAuth=function(g,type){
		var sel=g.getSelected();
 		if(sel==null){
 			$.messager.show({title:'提示',msg:'请选择一行进行操作'});
 			return;
 		}
 		if(g.authEditor==null)
 			g.authEditor=new RestrictEditor(startEdit,afterEdit,saveRow,addRow,editRow,deleteRow,cancelEdit);
 		g.authEditor.setTitle(type+"--"+sel.TITLE+"--权限设置");
 		g.authEditor.edit(sel.ID);
	};
	
	var appendParam=function(opt){
		if(!query.ID){
			return false;
		}		
		opt.queryid=query.ID;
	};
	var gSearch=new sunz.Datagrid({
			parent:pSField,
			fit:true,fitColumns:true,
			autoRowHeight:true,
			rownumbers:true,striped:true,
			url:'framework/query.do?search&k=searchFieldsByQuery',
			onBeforeLoad:appendParam,
			columns:[[{field:'ID',hidden:true},
					  {field:'NAME',title:'字段名称',width:60,editor:'text'},
			          {field:'TITLE',title:'字段标题',width:80,editor:'text'},
			          {field:'INPUTTYPE',title:'字段类型',width:75,editor:{type:'combobox',options:{data:inputTypes}},formatter:function(v,r,i){return dictType[v]||v;}},
			          {field:'STYLES',title:'其它设置',width:100,editor:{type:'textarea',options:{validType:'json'}},formatter:function(v,r,i){return '<textarea  readonly="true" class="htmlcell">'+(v?v:"")+'</textarea>';}},
			          {field:'ORDERINDEX',title:'顺序',width:36,editor:'numberbox'}
			]],
			sortName:'ORDERINDEX',
			toolbar:[{iconCls:'icon-add',text:'新加',handler:function(){addRow(gSearch);}},
			         '-',
			         {iconCls:'icon-edit',text:'编辑',handler:function(){editRow(gSearch);}},
			         {iconCls:'icon-save',text:'应用',handler:function(){saveRow(gSearch);}},
			         {iconCls:'icon-undo',text:'取消',handler:function(){cancelEdit(gSearch);}},
			         '-',
			         {iconCls:'icon-remove',text:'删除配置',handler:function(){deleteRow(gSearch);}},
			         '-',
			         //{iconCls:'icon-putout',text:'顺序上升一位',handler:function(){editOrder(gSearch,-1);}},
			         //{iconCls:'icon-put',text:'顺序下移一位',handler:function(){editOrder(gSearch,1);}}
			         //,'-',
			         {iconCls:"icon-lock",text:"权限控制",handler:function(){editAuth(gSearch,"搜索字段");}}
			],
			onDblClickRow:function(i,d){startEdit(gSearch,i,d);},
			onAfterEdit:function(i,r,c){afterEdit(gSearch,i,r,c);}
	});
	var gResult=new sunz.Datagrid({
			parent:pRField,
			fit:true,fitColumns:true,
			autoRowHeight:true,
			rownumbers:true,striped:true,
			url:'framework/query.do?search&k=resultFieldsByQuery',
			onBeforeLoad:appendParam,
			columns:[[{field:'ID',hidden:true},
					  {field:'NAME',title:'字段名称',width:60,editor:'text'},
			          {field:'TITLE',title:'字段标题',width:80,editor:'text'},
			          {field:'STYLES',title:'其它设置',width:100,editor:{type:'textarea',options:{validType:'json'}},formatter:function(v,r,i){return '<textarea  readonly="true" class="htmlcell">'+(v?v:"")+'</textarea>';}},
			          {field:'ORDERINDEX',title:'顺序号',width:36,editor:'numberbox'}
			]],
			sortName:'ORDERINDEX',
			toolbar:[{iconCls:'icon-add',text:'新加',handler:function(){addRow(gResult);}},
			         '-',
			         {iconCls:'icon-edit',text:'编辑',handler:function(){editRow(gResult);}},
			         {iconCls:'icon-save',text:'应用',handler:function(){saveRow(gResult);}},
			         {iconCls:'icon-undo',text:'取消',handler:function(){cancelEdit(gResult);}},
			         '-',
			         {iconCls:'icon-remove',text:'删除配置',handler:function(){deleteRow(gResult);}},
			         '-',
			         //{iconCls:'icon-putout',text:'顺序上升一位',handler:function(){editOrder(gResult,-1);}},
			         //{iconCls:'icon-put',text:'顺序下移一位',handler:function(){editOrder(gResult,1);}}
			         //,'-',
			         {iconCls:"icon-lock",text:"权限控制",handler:function(){editAuth(gResult,"结果字段");}}
			],
			onDblClickRow:function(i,d){startEdit(gResult,i,d);},
			onAfterEdit:function(i,r,c){afterEdit(gResult,i,r,c);}
	});
	gSearch.editUrl="framework/datatable.do?t=T_S_QUERY_SEARCHFIELD";
	gResult.editUrl="framework/datatable.do?t=T_S_QUERY_RESULTFIELD";
	win.find("input[type=text]").css("width","80%");
	$.parser.parse(win);
	
	new sunz.Linkbutton({parent:win.find(".qtoolbar")[0],text:'保存&nbsp;',iconCls:'icon-save',handler:()=>{saveBase()},style:'margin-left:100px'});
	new sunz.Linkbutton({parent:win.find(".qtoolbar")[0],text:'操作定义配置&nbsp;',iconCls:'icon-edit',handler:()=>{
			if(query.id||query.ID){
				goOperDrf();
			}else{
				saveBase(goOperDrf);
			}
		},style:'margin-left:5px'});
	
	this.edit=function(q){
		query=q;
		win.setTitle((q.DESCRIPTION?(q.DESCRIPTION+"--编辑"):"新建查询")+"　　　　凡多行文本编辑框，可双击弹出大的专用编辑框");
		win.open();
		form.reset();
		form.load(q);
		cmbSql.setText(q.SQLNAME);
		gSearch.clearSelections();
		gResult.clearSelections();

		if(query.ID){
			gSearch.load();
			gResult.load();
		}else{
			gSearch.loadData([]);
			gResult.loadData([]);
		}
		$(":input").validatebox();
	}
	this.destroy=()=>{win.destroy()}
	
	var goOperDrf=function(){
		(grid.queryOperDef=grid.queryOperDef||new QueryOperDef()).operDef(query);
	}

}