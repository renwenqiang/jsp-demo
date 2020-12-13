/**
 * 因代码量大，单列js文件
 */
$(function(){
	var innerFields=["field","title","width","isDict","hidden"],uselessFields=["deltaWidth","boxWidth","cellClass","auto"],normalize=function(def){for(var len=uselessFields.length,k=0;k<len;k++){delete def[uselessFields[k]];defaultNormalize(def);}};
	var toEditData=function(columns){
		var ncols=[];
		$.each(columns,function(i,subs){
			var subCols=[];
			$.each(subs,function(j,column){
				normalize(column);
				subCols.push(toOtherSetting(column,innerFields));
			});
			ncols.push(subCols);
		});
		return ncols;
	},toSetting=function(editDatas){
		$.each(editDatas,function(i,datas){
			$.each(datas,function(i,data){
				$.extend(data,parseOtherSetting(data.otherSetting));
				delete data.otherSetting;
				data.width=Number(data.width);
				if(data.isDict){
					data.formatter=data.formatter||function(v,i,r){return D.getText(v);};
				}
				if(typeof data.formatter=="string")
					data.formatter=eval("("+data.formatter+")");
			});
		});
		return editDatas;
	};
	var getEditor=function(columns){
		if(getEditor.editor==null){
			var win=getEditor.editor=new sunz.Window({title:'网格控件字段设置',width:800,height:600,modal:true,
					content:'<div class="easyui-layout" data-options="fit:true">\
					<div id="pgTable" style="height:45px;padding:5px;" data-options="region:\'north\'"></div>\
					<div id="pgGrid"  data-options="region:\'center\'"></div>\
					<div id="pgTool" style="height:45px;padding:5px;" data-options="region:\'south\'"></div>\
					</div>'});
			var fnCheck=function(v,i,r){return v==true?"√":"×";};
			var moveRow=function(e, up){
				var sel=gColumn.getSelected();
				if(!sel) return;
				var index=gColumn.getRowIndex(sel);
			    var oIndex=index+(up?-1:1);
			    var datas=gColumn.getRows();
			    if(oIndex<0||oIndex>=datas.length)
			    	return;
			    gColumn.applyEdit();
			    var temp=datas[index];datas[index]=datas[oIndex];datas[oIndex]=temp;
			    gColumn.refreshRow(index);gColumn.refreshRow(oIndex);
			    gColumn.selectRow(oIndex);
			};
			var gColumn=new sunz.Datagrid({parent:win.find("#pgGrid"),fit:true,title:"列设置",
				selectOnCheck: false,checkOnSelect: false,
				fit:true,fitColumns:true,rownumbers:true,
				idField:"field",
				columns:[[
						{field:'field',title:'字段',width:60,editor:'text'},
						{field:'title',title:'表头',width:80,editor:'text'},
						{field:'hidden',title:'隐藏',width:40,editor:{type:'checkbox',options:{on:true,off:false}},formatter:fnCheck},
						{field:'width',title:'宽度',width:60,editor:'numberbox'},
						{field:'isDict',title:'是否字典项',width:60,editor:{type:'checkbox',options:{on:true,off:false}},formatter:fnCheck},
						{field:'otherSetting',title:'其它设置',width:160,editor:'textarea'}
				]],
				onClickRow:function(i,r){
					if(gColumn.editIndex!=i && gColumn.editIndex>-1)
						gColumn.endEdit(gColumn.editIndex);
					
					gColumn.editIndex=i;
					gColumn.beginEdit(i);
				},
				toolbar:[
				         {iconCls:'icon-add',text:'新加',handler:function(){gColumn.appendRow({width:120,title:"新增列"});}},
				         {iconCls:'icon-remove',text:'删除',handler:function(){
				        	 	var sel=gColumn.getSelected();
				     			if(sel!=null)
				     				gColumn.deleteRow(gColumn.getRowIndex(sel));
				         	}
				         },
				         {iconCls:'icon-ok',text:'应用',handler:function(){
				        	 	gColumn.applyEdit();
				        	 }
				         },
				         {iconCls:'icon-up',text:'上移',handler:function(){moveRow(event,true);}},
				         {iconCls:'icon-down',text:'下移',handler:function(){moveRow(event,false);}}
				]
			});
			gColumn.applyEdit=function(){if(gColumn.editIndex>-1)gColumn.endEdit(gColumn.editIndex);};
			var pele=win.find("#pgTable");
			var cmbIndex=getEditor.cmbIndex=new sunz.Combobox({	
					label:"表头行：",parent:pele,
					width:240,
					onChange:function(nv,ov){
						if(nv==null||nv==="")return;
						if(ov!=null && ov!=="")win.xdata[ov]=gColumn.getData().rows;
						gColumn.loadData(nv!=null?(win.xdata[nv]||[]):[]);
					}
			});
			new sunz.Linkbutton({style:"margin-left:15px;",parent:pele,text:"添加表头行",iconCls:"icon-add",handler:function(){
				var ds=cmbIndex.getData(),len=ds.length;
				cmbIndex.loadData(ds.concat([{text:len+1,value:len}]));
				win.xdata[len]=[];
				cmbIndex.setValue(len);
			}});
			new sunz.Linkbutton({style:"margin-left:15px;",parent:pele,text:"删除表头行",iconCls:"icon-remove",handler:function(){
				var ds=cmbIndex.getData();
				if(ds.length==0){
					$.messager.show({title:"提示",msg:"当前已没有表头行了"});
					return;
				}
				var idx=cmbIndex.getValue();
				ds.pop();
				win.xdata.splice(Number.parseInt(idx),1);
				//
				cmbIndex.loadData(ds);
				var nv=ds.length>0?ds.length-1:null;
				cmbIndex.setValue(nv);
				if(nv!=null)
					gColumn.loadData(win.xdata[nv]);
			}});
			
			pele=win.find("#pgTool");
			new sunz.Linkbutton({style:"float:right;margin-right:15px;",parent:pele,text:"取消",iconCls:"icon-cancel",handler:function(){win.close();}});
			new sunz.Linkbutton({style:"float:right;margin-right:15px;",parent:pele,text:"确定",iconCls:"icon-ok",handler:function(){
					gColumn.applyEdit();
					cmbIndex.setValue(null);//cmbIndex.select(null);
					getSelectedComponent().setting.columns=toSetting(win.xdata);
					win.close();
					applySetting();
				}
			});
		}
		
		getEditor.cmbIndex.setValue(null);
		getEditor.editor.xdata=columns;
		getEditor.cmbIndex.loadData($.map(columns,function(col,i){return {text:i+1,value:i};}));
		getEditor.cmbIndex.setValue("0");
		return getEditor.editor;
	};
	var setting={
			normalProperties:["columns"],
			normalize:normalize,
			defaults:{columns:[[]]},
			menus:[{xtype:"Linkbutton",text:"字段设置",iconCls:"icon-more",handler:function(){
				var columns=getSelectedComponent().setting.columns,
					editDatas=toEditData(columns),			
					w=getEditor(editDatas);
				w.open();
			}}
			]
		};
	smart.extend("Datagrid",setting);
	smart.extend("Treegrid",setting);
	smart.extend("Combogrid",setting);
	smart.extend("Combotreegrid",setting);
});