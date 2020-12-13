/**
 * 
 */

window.smartForm={};
$(function(){
	var getOperateButton=function(arg){
	    var c={parent:pToolbar,plain:true};
	    if(typeof arg ==="function") c.handler=arg;  else  $.extend(c,arg);
	    //c.width=(c.text||"").length*12+48;
	    return new sunz[c.xtype||"Linkbutton"](c);
	};

	var validateField=function(k,msg){if(!smartForm[k]){$.messager.show({title:"提示",msg:msg});return false}return true},
		validate=function(){
			return validateField("CODE","表单编码为必填，请在“信息设置”中填写")
			    && validateField("NAME","表单名称为必填，请在“信息设置”中填写")
			    //&& validateField("QUERYURL","列表接口地址为必填，请在“信息设置”中填写")
			    //&& validateField("SELECTURL","表单接口地址为必填，请在“信息设置”中填写")
		};
	var save=function(){
		if(!validate()) return;
		var xDef=parseDesigner(true);
		smartForm[cmbPart.getValue()]=xDef;
		$.post("framework/datatable.do?"+(smartForm.ID?"save":"add")+"&t=T_S_SmartForm",
			$.extend({},smartForm,{FORMDEF:$.encode(smartForm.FORMDEF),LISTDEF:$.encode(smartForm.LISTDEF)/*,MOBILEFORMDEF:$.encode(smartForm.MOBILEFORMDEF),MOBILELISTDEF:$.encode(smartForm.MOBILELISTDEF)*/})
			,function(jr){
				if(jr.success){
					if(!smartForm.ID)window.smartForm.ID=jr.data.ID;
					$.messager.show({title:"恭喜您",msg:"保存成功"});
				}
				else 
					$.messager.alert("保存失败",jr.msg);
			}
		);
	}
	// 保存
	getOperateButton({text:"保存",iconCls:"icon-save",handler:function(){
		onBeforeSave.remove(save);
		onBeforeSave.add(save);
		onBeforeSave.fire();
	}});
	// 信息设置 
	var btnSetting=getOperateButton({text:"信息设置",iconCls:"smart-setting",handler:function(){
		if(btnSetting.editWindow==null){
			var html='<form id="frmSetting"><style type="text/css">#frmSetting .kv-label{width:60px}</style><table class="kv-table" cellpadding="2" style="width:100%;line-height:24px;" >\
				<tr><td class="kv-label">编码:</td><td class="kv-content"><input type="text" style="width:90%;" class="easyui-textbox" data-options="required:true" name="CODE" /></td></tr>\
				<tr><td class="kv-label">名称或描述:</td><td class="kv-content"><input type="text" style="width:90%;" class="easyui-textbox" name="NAME" /></td></tr>\
				<tr><td class="kv-label">绑定数据表:</td><td class="kv-content"><input disabled="disabled" type="text" style="width:90%;" class="easyui-textbox" name="TABLENAME" /></td></tr>\
				<tr><td class="kv-label">列表数据地址:</td><td class="kv-content"><input type="text" style="width:90%;" class="easyui-textbox" name="QUERYURL" placeholder="返回list的url，传入参数为空或id、jobid或[和]外键"/></td></tr>\
				<tr><td class="kv-label">表单数据地址:</td><td class="kv-content"><input type="text" style="width:90%;" class="easyui-textbox" name="SELECTURL" placeholder="返回list或object的url，传入参数为id、jobid或[和]外键"/></td></tr>\
				<tr><td class="kv-label">新增数据地址:</td><td class="kv-content"><input type="text" style="width:90%;" class="easyui-textbox" name="ADDURL" /></td></tr>\
				<tr><td class="kv-label">保存数据地址:</td><td class="kv-content"><input type="text" style="width:90%;" class="easyui-textbox" name="SAVEURL" /></td></tr>\
				<tr><td class="kv-label">删除数据地址:</td><td class="kv-content"><input type="text" style="width:90%;" class="easyui-textbox" name="DELETEURL" /></td></tr>\
				<tr><td class="kv-label">表单宽度:</td><td class="kv-content"><input type="text" style="width:90%;" class="easyui-textbox" name="FORMWIDTH" value="100%"/></td></tr>\
				<tr><td class="kv-label">表单高度:</td><td class="kv-content"><input type="text" style="width:90%;" class="easyui-textbox" name="FORMHEIGHT" value="100%"/></td></tr>\
				<tr><td class="kv-label">需要字典项:</td><td class="kv-content"><input type="text" style="width:90%;" class="easyui-textbox" name="DICTS" placeholder="多个使用逗号分隔，需要字典较多时建议直接用all"/></td></tr>\
				<tr><td class="kv-label">其它设置:</td><td class="kv-content"><textarea style="width:90%;height:64px;" name="OTHERSETTING" placeholder="JSON类型的参数配置，形如\r\n{\r\n}" /></td></tr>\
				<tr><td class="kv-label">内部资源:</td><td class="kv-content"><input type="text" style="width:90%;" class="easyui-textbox" name="INNERRESOURCES" placeholder="T_S_CONFIG中group为resources的编码，默认jquery,easyui,sunzui，右键选择"/></td></tr>\
				<tr><td class="kv-label">额外资源:</td><td class="kv-content"><textarea style="width:90%;height:80px;" name="RESOURCES" /></td></tr>\
				<tr><td class="kv-label" style="vertical-align:top">需要配置项:</td><td class="kv-content"><input type="text" style="width:90%;" class="easyui-textbox" name="CONFIGS" placeholder="多个使用逗号分隔，不要使用all"/><div style="word-break:break-all;color:#933;font-size:12px;margin-right:60px">（默认已引入'+C.smartformRuntimeConfigs+'，不要重复引入）</div></td></tr>\
				<tr><td class="kv-label"></td><td class="kv-content"><span style="color:blue;">说明：<br/>1.数据表绑定在主界面工具栏上进行；<br/>2.绑定数据表的情况下不需要修改各数据地址！<br/>3.表单数据地址为解决列表通常不返回所有列的问题，返回List或Object均可</span></td></tr>\
				</table></form>';
			var win=btnSetting.editWindow=new sunz.Window({title:'信息设置',width:800,height:666,modal:true,
					content:'<div id="winForm" style="padding:8px 0 5px 10px;height: calc(100% - 64px);overflow-y: scroll;border-bottom: 1px solid #c0c0c0;"></div><div id="winTool" style="position:absolute;bottom:10px;right:120px;"></div>'
				}),form=$(html).asForm();
			form.appendTo($("#winForm"));
			win.form=form;
			var ptl=$("#winTool");
			win.find("textarea").on("dblclick",function(){
				var jme=$(this),n=jme.attr("name");
				getXEditor("",jme.val(),(v)=>{
					jme.val(v);
				}).open();
			}).on("keydown",tabSupport).attr("title","双击试试");
			win.find("[name=INNERRESOURCES]").on("contextmenu",function(e){
				e.preventDefault();
				var txt=this,start=txt.selectionStart,end=txt.selectionEnd,sel=txt.value.substring(start,end);
				var m=new sunz.Menu();
				$.each(optionalInnerResources,(i,r)=>{
					m.appendItem({text:r,onclick:()=>{
						//txt.value=txt.value.substr(0,start)+","+frg+txt.value.substr(end);					
						var pre=txt.value.substr(0,start),post=txt.value.substr(end),v=pre+post,find=-1;
						for(var i=0; i>-1&&find<0;){
							i=v.indexOf(r,i);
							if(i>-1){
								var reg=/\s|,/;
								if((i==0||reg.test(v.substr(i-1,1))) && reg.test(v.substr(i+r.length,1))){
									find=i;
									break;
								}
								i=i+r.length;
							}
						}
						if(find<0){
							if(!sel){
								pre=v;
								post="";
								start=v.length;
							}
							v=pre+(/.*\w\s*$/.test(pre)?",":"")+r+(/^\s*\w/.test(post)?",":"");
							end=v.length;
							txt.value=v+post;
						}else{
							start=find;
							end=find+r.length;
						}
						txt.focus();
					    txt.selectionStart=start;
					    txt.selectionEnd=end;
					}});
				})
				m.show({left:e.clientX,top:e.clientY});
			});
			win.find("[name=RESOURCES]").on("contextmenu",function(e){
				e.preventDefault();
				var txt=this,start=txt.selectionStart,end=txt.selectionEnd,sel=txt.value.substring(start,end);
				var m=new sunz.Menu();
				$.each(eval("("+C.optionalSmartSources+")"),(i,o)=>{
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
			}).attr("title","右键或双击试试").attr("placeholder","html片断(右键可引入常见预定义资源)，如\r\n<style></style>\r\n<script></script>等");
			new sunz.Linkbutton({parent:ptl,text:"确定",iconCls:"icon-ok",handler:function(){
				var setting=form.getFieldValues();
				// 可能定义了方法给表单置用
				if(setting.RESOURCES){$(setting.RESOURCES).appendTo(document.head);}
				$.extend(window.smartForm,setting); win.close();}
			});
			new sunz.Linkbutton({parent:ptl,text:"取消",iconCls:"icon-cancel",handler:function(){win.close();},style:"margin-left:15px;"});
		}
		btnSetting.editWindow.form.load(window.smartForm);
		btnSetting.editWindow.open();
	}});
	var switchPart=function(nv,ov){
		if(ov)smartForm[ov]=parseDesigner(true);
		pMain.innerHTML="";
		selectComponent(null);
		if(nv)parsePart(smartForm[nv],pMain);
	};
	// part
	var cmbPart= getOperateButton({xtype:"Combobox",parent:pToolbar,width:160,label:"当前部分:",labelWidth:68,value:"FORMDEF",data:[{text:"表单",value:"FORMDEF"},{text:"列表",value:"LISTDEF"}/*,{text:"移动表单",value:"MOBILEFORMDEF"},{text:"移动列表",value:"MOBILELISTDEF"}*/],onChange:switchPart});
	//getOperateButton({text:"刷新",iconCls:"icon-refresh",handler:function(){switchPart(cmbPart.getValue(),null);	}});
	// 绑定
	var dictXtype={
			"-7"/*bit*/:"Dictcombo",
			16/*boolean*/:"Dictcombo",
			"-8"/*ROWID*/:"Dictcombo",
			
			"-6"/*tinyInt*/:"Numberbox",
			5/*smallInt*/:"Numberbox",
			4/*int*/:"Numberbox",
			"-5"/*bigInt*/:"Numberbox",
			6/*float*/:"Numberbox",
			7/*real*/:"Numberbox",
			8/*double*/:"Numberbox",
			2/*numeric*/:"Numberbox",
			3/*decimal*/:"Numberbox",
			
			1/*char*/:"Textbox",
			12/*varchar*/:"Textbox",
			"-1"/*longVarchar*/:"Textbox",
			"-15"/*NCHAR*/:"Textbox",
			"-9"/*NVARCHAR*/:"Textbox",
			"-16"/*LONGNVARCHAR*/:"Textbox",
			2005/*CLOB*/:"Textbox",
			2011/*NCLOB*/:"Textbox",
			2009/*SQLXML*/:"Textbox",
			
			91/*date*/:"Datebox",
			92/*time*/:"Datebox",
			92/*timestamp*/:"Datebox",
			
			"-2"/*binary*/:"Input",
			"-3"/*varBinary*/:"Input",
			"-4"/*longVarBinary*/:"Input",
			
			0/*null*/:"Input",
			1111/*other*/:"Input",
			2000/*JAVA_OBJECT*/:"Input",
			2001/*DISTINCT*/:"Input",
			2002/*STRUCT*/:"Input",
			2003/*ARRAY*/:"Input",
			2004/*BLOB*/:"Input",
			2006/*REF*/:"Input",
			2007/*DATALINK*/:"Input"
	};
	var selectAll=function(){$(".proxy-select").each(function(i,proxy){selectComponent(proxy,true);});},
	clearCurrent=function(){selectAll();deleteComponents();},
	createPart=function(part,def,append){
		if(cmbPart.getValue()==part){
			if(!append)clearCurrent();
			parsePart([def],getSelectedComponent()?getSelectedComponent().proxyContainer:pMain);
		}else{
			if(!append)window.smartForm[part]=[];
			window.smartForm[part].push(def);
		}
	},
	bandForm=function(g,colCount,append){
		colCount=colCount||2;
		var tDef=$.extend({},defines.Table.defaults,{xtype:"Table",id:getGuid(),class:"kv-table",fit:false,children:[]}),count=0,curRow=null;//,fDef=[tDef];
		$.each(g.getRows(),function(i,r){
			if(!r.showInForm) return;
			
			if(count++%colCount==0){
				curRow={xtype:"Tablerow",id:getGuid(),children:[]};
				tDef.children.push(curRow);
			}
			curRow.children.push({xtype:"Tablecell",id:getGuid(),class:"kv-label",children:[
			                       {xtype:"Label",id:getGuid(),innerHTML:(r.comment||r.name)+":"}
			                      ]});
			curRow.children.push({xtype:"Tablecell",id:getGuid(),class:"kv-content",children:[
			                       {id:getGuid(),validType:r.validType,xtype:r.xtype,name:r.name,label:null}
			                      ]});
		});
		createPart("FORMDEF",tDef,append);
	},bandMobileForm=function(g,append){
		var tDef=$.extend({},defines.Table.defaults,{xtype:"Table",id:getGuid(),class:"kv-table",cellspacing:"5px",fit:true,children:[]});
		$.each(g.getRows(),function(i,r){
			if(!r.showInForm) return;
			tDef.children.push({xtype:"Tablerow",id:getGuid(),children:
								[{xtype:"Tablecell",id:getGuid(),class:"kv-label",children:[{xtype:"Label",id:getGuid(),innerHTML:(r.comment||r.name)+":"}]},
								 {xtype:"Tablecell",id:getGuid(),class:"kv-content",children:[{id:getGuid(),validType:r.validType,xtype:r.xtype,name:r.name,label:null}]}
								]});		
		});
		createPart("MOBILEFORMDEF",tDef,append);
	},bandList=function(g,append){
		var rowSearch=$.extend({},defines.Tablerow.defaults,{id:getGuid(),xtype:"Tablerow",children:[]})
			,tableSearch=$.extend({},defines.Table.defaults,{id:getGuid(),xtype:"Table",class:"kv-table",fit:true,children:[rowSearch]})
			,sForm=$.extend({},defines.Form.defaults,{id:getGuid(),xtype:"Form",children:[tableSearch]});
		var cols=[]/*,sFields="var vTest=null;"*/;
		$.each(g.getRows(),function(i,r){
			// 
			if(r.isSearch){
				rowSearch.children.push($.extend({},defines.Tablecell.defaults,{id:getGuid(),xtype:"Tablecell",class:"kv-label",children:[
				          {xtype:"Label",id:getGuid(),innerHTML:(r.comment||r.name)+":"}]}));
				var sField=$.extend({},defines.Textbox.defaults,{id:String(getGuid()),xtype:"Textbox",name:r.name,label:null});
				rowSearch.children.push($.extend({},defines.Tablecell.defaults,{id:getGuid(),xtype:"Tablecell",class:"kv-content",children:[sField]}));
				//sFields=sFields+"vTest=c('"+sField.id+"').getValue();if(vTest)params['"+sField.name+"']=vTest;";			
			}
			//
			var c={field:r.name,title:r.comment,width:r.showWidth};
			if(!r.showInList) c.hidden=true;
			cols.push(c);
		});
		
		var gDef=$.extend({},defines.Datagrid.defaults,{id:getGuid(),xtype:"Datagrid",title:"",isSmartlist:true,fit:true,columns:[cols]});
		var handler='function(){var params=window.componentManager.get("'+sForm.id+'").getFieldValues(true);params.total=-1;window.componentManager.get("'+ gDef.id+'").load(params);}';
		var btnSearch={xtype:"Linkbutton",text:"搜索",iconCls:"icon-search",onClick:$.decode(handler)}
			,btnCancel={xtype:"Linkbutton",style:"margin-left:15px;",text:"重置",iconCls:"icon-redo",onClick:'function(){window.componentManager.get("'+sForm.id+'").reset();window.componentManager.get("'+gDef.id+'").load();}'};
		rowSearch.children.push($.extend({},defines.Tablecell.defaults,{id:getGuid(),xtype:"Tablecell",children:[btnSearch,btnCancel]}));
		
		createPart("LISTDEF",
				$.extend({},defines.Layout.defaults,{id:getGuid(),xtype:"Layout",children:[
		            $.extend({},defines.Sublayout.defaults,{id:getGuid(),xtype:"Sublayout",height:88,title:"条件",region:"north",children:[sForm]}),                                                                    
		            $.extend({},defines.Sublayout.defaults,{id:getGuid(),xtype:"Sublayout",title:"结果",region:"center",children:[gDef]})                                                                    
		       ]})
				,append);
	},bandFieldBand=function(fields){
		if(!bandFieldBand.menu){
			var pBand=$('<div></div>').appendTo(pComponentTool);
			bandFieldBand.menu=createOperate({xtype:"Menubutton",text:"绑定字段",iconCls:"icon-add"},pBand);
			pBand.hide();
			window.onComponentSelectionChanged.add(function(sels){
				pBand.hide();
				if(sels.length!=1)
					return;
				
				if(window.getSelectedComponent().setting.define.isInput)pBand.show();
			});
		}
		var m=new sunz.Menu();
		bandFieldBand.menu.menubutton({menu:m});
		$.each(fields,function(i,f){
			m.appendItem({text:f.comment?(f.comment+"("+f.name+")"):f.name,onclick:function(){applySetting();var setting=window.getSelectedComponent().setting;setting.name=f.name.toUpperCase();window.onComponentSettingChanged.fire(window.getSelectedComponent(),setting);}});
		});
	};
	window.onDefineLoad.add(function(def){
		if(def.TABLENAME)
			$.post("framework/datatable.do?columnInfos",{t:def.TABLENAME},function(jr){
				bandFieldBand(jr.data);
			});
	});
	var btnBand=/*getOperateButton*/({text:"关联表",iconCls:"smart-link",handler:function(){
		if(btnBand.bandWindow==null){
			var win=btnBand.bandWindow=new sunz.Window({title:'关联表',width:800,height:600,modal:true,
				content:'<div class="easyui-layout" data-options="fit:true">\
					<div id="pbTable" style="height:45px;padding:5px;" data-options="region:\'north\'"></div>\
					<div id="pbGrid"  data-options="region:\'center\'"></div>\
					<div id="pbTool" style="height:45px;padding:5px;" data-options="region:\'south\'"></div>\
					</div>'});
			new sunz.Combobox({label:"数据表：",parent:$("#pbTable")[0],editable:true,
				url:"framework/datatable.do?tableNames",valueField:"NAME",textField:"COMMENTS",width:680,
				loadFilter:function(data){$.each(data,function(i,d){d.COMMENTS=d.COMMENTS?(d.COMMENTS+"("+d.NAME+")"):d.NAME;});return data;},
				onSelect:function(r){
					var nv=r.NAME,ov=win.TABLENAME;
					if(ov==nv)return;
					win.TABLENAME=nv;win.NAME=r.COMMENTS;
					$.post("framework/datatable.do?columnInfos",{t:nv},function(jr){
						var dFields=[],searchCount=0;
						$.each(jr.data,function(i,d){
							d.name=d.name.toUpperCase(); // 因datatable.do处理过大小写，默认必须为大写
							d.comment=d.comment||d.name;
							var isString=(d.commonDatatype==1||d.commonDatatype==12||d.commonDatatype==-1);
							if(isString)d.validType="length[0,"+d.length+"]";
							d.showInForm=d.showInList=(d.name!="ID"&&d.name!="JOBID");
							d.xtype=dictXtype[d.commonDatatype]||"Input";
							d.showWidth=120;
							d.isSearch=(isString&&searchCount++<2);// 留2个
							dFields.push(d);
						});
						gBand.loadData(dFields);
					});
				}
			});
			
			var fnCheck=function(v,i,r){return v==true?"√":"×";};
			var gBand=new sunz.Datagrid({parent:$("#pbGrid")[0],fit:true,title:"设置",
				selectOnCheck: false,checkOnSelect: false,
				fit:true,fitColumns:true,
				idField:"name",
				columns:[[
						{field:'name',title:'字段',width:60},
						{field:'comment',title:'显示名',width:80,editor:'text'},
						{field:'datatype',title:'数据类型',width:80},
						{field:'isSearch',title:'作为查询条件',editor:{type:'checkbox',options:{on:true,off:false}},formatter:fnCheck,width:80},
						{field:'showInForm',title:'表单中显示',editor:{type:'checkbox',options:{on:true,off:false}},formatter:fnCheck,width:80},
						{field:'xtype',title:'显示类型',width:80,formatter:function(v,i,r){return (defines[v]||{}).name;},editor:{type:"combobox",options:{data:(function(){var ds=[];$.each(defines,function(i,d){if(d.isInput)ds.push({text:d.name,value:d.xtype});}); return ds;})()}}},
						{field:'showInList',title:'列表中显示',editor:{type:'checkbox',options:{on:true,off:false}},formatter:fnCheck,width:80},
						{field:'showWidth',title:'显示宽度',width:80,editor:'numberbox'}
				]],
				onClickRow:function(i,r){
					if(gBand.editIndex!=i && gBand.editIndex>-1)
						gBand.endEdit(gBand.editIndex);
					
					gBand.editIndex=i;
					gBand.beginEdit(i);
				},
				onLoadSuccess:function(data){
					//for(var i=0,len=data.total;i<len;i++)
					//	gBand.beginEdit(i);
				}
			});
			var pele=$("#pbTool")[0];
			new sunz.Numberspinner({parent:pele,width:120,label:"表单列数：",labelWidth:68,value:2,onChange:function(nv,ov){win.FormColumn=nv;}});
			new sunz.Switchbutton({parent:pele,style:"margin-left:15px",plain:true,onText:"仅生成当前部分",checked:false,offText:"为所有部分生成",width:120,onChange:function(c){win.onlyCurrent=c;}});
			new sunz.Switchbutton({parent:pele,style:"margin-left:15px",plain:true,onText:"追加",checked:false,offText:"清空后替换",width:120,onChange:function(c){win.appendMode=c;}});
			new sunz.Linkbutton({style:"float:right;margin-right:15px;",parent:pele,text:"取消",iconCls:"icon-cancel",handler:function(){win.close();}});
			new sunz.Linkbutton({style:"float:right;margin-right:15px;",parent:pele,text:"确定",iconCls:"icon-ok",handler:function(){
					var t=win.TABLENAME;
					window.smartForm.TABLENAME=t;
					window.smartForm.NAME=win.NAME;
					window.smartForm.QUERYURL="framework/datatable.do?likely&t="+t;
					window.smartForm.SELECTURL="framework/datatable.do?getById&t="+t;
					window.smartForm.ADDURL="framework/datatable.do?add&t="+t;
					window.smartForm.SAVEURL="framework/datatable.do?save&t="+t;
					window.smartForm.DELETEURL="framework/datatable.do?delete&t="+t;
					//
					if(!win.onlyCurrent||cmbPart.getValue()=="FORMDEF")bandForm(gBand,win.FormColumn,win.appendMode);
					if(!win.onlyCurrent||cmbPart.getValue()=="LISTDEF")bandList(gBand,win.appendMode);
					//if(!win.onlyCurrent||cmbPart.getValue()=="MOBILEFORMDEF")bandMobileForm(gBand,win.appendMode);
					//cmbPart.setValue(null);
					//cmbPart.select("FORMDEF");
					bandFieldBand(gBand.getRows());
					
					win.close();
				}
			});
		}
		btnBand.bandWindow.open();
	}});
	var ma=new sunz.Menu();
	ma.appendItem(btnBand);
	ma.appendItem({separator:true});
	ma.appendItem({text:"从已有表单复制",iconCls:"smart-copyfrom",handler:function(){
		}
	});
	getOperateButton({xtype:"Menubutton",text:"工具",iconCls:"smart-tool",style:'padding:0 10px',menu:ma});
	
	ma.appendItem({separator:true});
	ma.appendItem({text:"json视图",iconCls:"smart-jsonview",handler:function(){
			var jp=$(pMain).parent();
			var win=new sunz.Window({width:jp.width(),height:jp.height(),title:"JSON视图",modal:true,content:'<div class="easyui-layout" data-options="fit:true"><div data-options="region:\'center\',border:false"><textarea id="xeditor" style="width:100%;height:100%;" ></textarea></div><div name="divButton" style="height:45px;padding:5px 15px;" data-options="region:\'south\',border:false"></div></div>'});
			win.move({left:jp.offset().left,top:jp.offset().top});
			var pele=$("[name=divButton]",win)[0];
			new sunz.Linkbutton({style:"margin:0 25px;float:right;",parent:pele,text:"放弃",iconCls:"icon-cancel",handler:function(){win.close();}});
			new sunz.Linkbutton({parent:pele,style:"margin:0 25px;float:right;",text:"应用修改",iconCls:"icon-ok",handler:function(){
					win.close();
					clearCurrent();
					parsePart($.decode(win.getValue()),pMain);
				}
			});
			new sunz.Linkbutton({parent:pele,style:"margin:0 25px;float:right;",text:"格式化",iconCls:"json-format",handler:function(){win.setValue(formatJson(win.getValue(),{removeNewline:true}));}});
			var txt=win.find("#xeditor").on("keydown",tabSupport);
			win.getValue=function(){return txt.val();};
			win.setValue=function(v){txt.val(v);};
			win.setValue($.encode(parseDesigner(true)));
		}
	});

	// 辅助线切换
	ma.appendItem({separator:true});
	ma.appendItem({text:"显示/隐藏辅助线",iconCls:"smart-dash",handler:function(){$("#pMain").toggleClass("dash");}});
	//getOperateButton({xtype:"Switchbutton",style:'margin-left:10px',parent:pToolbar,plain:true,onText:"辅助线",checked:true,offText:"隐藏",width:64,onChange:function(c){$("#pMain").toggleClass("dash");}});	
	
	// 操作切换 
	ma.appendItem({separator:true});
	ma.appendItem({text:"开启/禁用拖拽",iconCls:"smart-drag",handler:function(){
		window.componentDraggable=!window.componentDraggable;
		$(".proxy-select").each(function(){
			$(this).draggable({disabled:!(window.componentDraggable&&this.setting.define.draggable!=false)});
		});
	}});
	ma.appendItem({text:"开启/禁用大小调节",iconCls:"smart-resize",handler:function(){
		$(".proxy-select").resizable({disabled:!(window.componentResizable=!window.componentResizable)});
	}});
	
	getOperateButton({xtype:"Switchbutton",parent:pToolbar,plain:true,onText:"拖拽",checked:false,offText:"大小调节",width:100,onChange:function(c){
			window.componentResizable=!(window.componentDraggable=c);
			$(".proxy-select").each(function(){
				$(this).draggable({disabled:!(c&&this.setting.define.draggable!=false)});
				$(this).resizable({disabled:c});
			});
		}
	}); // 先同时保留
	
	// 全选
	var m=new sunz.Menu();
	getOperateButton({xtype:"Menubutton",text:"选择",iconCls:"smart-select",style:'padding:0 10px',menu:m});
	m.appendItem({disabled:true,text:"撤消",iconCls:"icon-undo"});
	m.appendItem({disabled:true,text:"重做",iconCls:"icon-redo"});
	
	m.appendItem({separator:true});
	m.appendItem({text:"全选",iconCls:"smart-select-all",handler:selectAll});
	m.appendItem({separator:true});
	m.appendItem({disabled:true,text:"清空选择",iconCls:"smart-select-clear",handler:function(){selectComponent(null);}});
	
	window.onCopybordChanged=$.Callbacks();
	window.copiedItems=null;
	var copySelected=function(renewid){
		if(!window.selectedComponents||!window.selectedComponents.length){
			$.messager.show({title:"提示",msg:"请先选择一个组件"});
			return;
		}
		var temp={children:[]};
		$.each(window.selectedComponents,function(i,sel){
			parseElement(sel,temp,true,renewid==null?true:renewid);
		});
		window.onCopybordChanged.fire(window.copiedItems=temp.children);
	}
	// 复制
	//getOperateButton({parent:pComponentShareTool,
	m.appendItem({separator:true});
	m.appendItem({disabled:true,text:"复制",iconCls:"smart-copy",handler:copySelected}); 
	m.appendItem({disabled:true,text:"剪切",iconCls:"smart-cut",handler:function(){
		copySelected(false);
		deleteComponents();
	}});
	// 粘贴
	var pasteItem=function(items){
		var sel=getSelectedComponent();
		if(!sel){sel=pMain;}
		try{
			parsePart(items,sel);
		}catch(e){
			var name=items.length==1?defines[items[0].xtype].name:"所选组件中一个或多个";
			$.messager.show({title:"提示",msg:"当前位置不支持【"+name+"】类型的子组件"});
		}
	}
	//var btnPaste=getOperateButton({parent:pComponentShareTool,
	m.appendItem({disabled:true,text:"粘贴",iconCls:"smart-paste",handler:function(){
		pasteItem(window.copiedItems);
	}});
	window.onCopybordChanged.add(function(){/*btnPaste.enable(true);*/m.enableItem(m.findItem("粘贴").target);});	
	m.appendItem({text:"从json手动粘贴",iconCls:"smart-paste-manual",handler:function(){
		getXEditor("自定义粘贴表单组件",window.lastManualItems,function(v){
			try{var j=eval("("+(window.lastManualItems=v)+")");}catch(e){$.messager.show({title:"提示",msg:"格式不正确"}); return false;}
			pasteItem([j]);
		}).open();
	}});
	// 属性复制粘贴
	m.appendItem({separator:true});
	window.onCopiedSettingChanged=$.Callbacks();
	window.copiedSetting=null;
	m.appendItem({disabled:true,text:"复制属性",iconCls:"smart-setting-copy",handler:function(){
		if(window.selectedComponents.length!=1){
			$.messager.show({title:"提示",msg:"请先选择一个组件"});
			return;
		}
		var s=$.extend({},getSelectedComponent().setting);
		delete s.define;delete s.element;delete s.id;
		window.copiedSetting=s;
		window.onCopiedSettingChanged.fire(s);
	}});
	var pasteSetting=function(setting){
		if(!window.selectedComponents.length){
			$.messager.show({title:"提示",msg:"请选择目标组件"});
			return;
		}
		$.each(window.selectedComponents,function(i,ele){updateSetting(ele,setting);});
	}
	m.appendItem({disabled:true,text:"粘贴属性",iconCls:"smart-setting-paste",handler:function(){
		pasteSetting(window.copiedSetting);
	}});
	m.appendItem({disabled:true,text:"从json手动粘贴属性",iconCls:"smart-setting-paste-manual",handler:function(){
		getXEditor("自定义粘贴组件属性",window.lastManualStyles,function(v){
			try{var j=eval("("+(window.lastManualStyles=v)+")");}catch(e){$.messager.show({title:"提示",msg:"格式不正确"}); return false;}
			pasteSetting(j);
		}).open();
	}});
	// 删除
	//getOperateButton({parent:pComponentShareTool,
	m.appendItem({disabled:true,text:"删除",iconCls:"smart-delete",handler:function(){deleteComponents();}});
	window.onComponentSelectionChanged.add(function(sels){
		var oper=sels.length?"enableItem":"disableItem";
		m[oper](m.findItem("复制").target);
		m[oper](m.findItem("剪切").target);
		m[oper](m.findItem("删除").target);
		m[oper](m.findItem("清空选择").target);
		m[oper](m.findItem("从json手动粘贴属性").target);
		
		oper=sels.length==1?"enableItem":"disableItem";
		m[oper](m.findItem("复制属性").target);
		
		
		oper=sels.length && window.copiedSetting?"enableItem":"disableItem";
		m[oper](m.findItem("粘贴属性").target);
	});
	
	// 清屏
	m.appendItem({separator:true});
	m.appendItem({text:"清屏",iconCls:"smart-part-clear",handler:function(){$.messager.confirm("清屏操作确认","清屏操作将删除界面设计器内所有元素，您确定要进行吗？",function(ok){if(ok)clearCurrent();});}});
	
	
	// 预览
	new sunz.Linkbutton({parent:pExTools,iconCls:"smart-preview",text:"快速预览",handler:function(){
		window.open("framework/smartform.do?preview").master=window;
	}});	
	new sunz.Linkbutton({parent:pExTools,style:'margin-left:5px;',iconCls:"smart-exportjsp",text:"导出jsp",handler:function(){
		 var blob=new Blob([smart.toHtml(parseDesigner(true))],{type:"application/jsp;charset=utf-8"});
		 var aLink = document.createElement('a');
		 aLink.download =(window.smartForm.NAME||window.smartForm.CODE||"新建表单")+".jsp";
		 aLink.href = URL.createObjectURL(blob);
		 var evt = document.createEvent("mouseEvents");
		 evt.initEvent("click");
		 aLink.dispatchEvent(evt);
		 URL.revokeObjectURL(blob);
	}});	

	// *************** 元素操作  *********************
	var miSplit={separator: true},miForDelete={text:"删除",iconCls:"icon-cancel",onclick:function(){deleteComponents();}};
	var operates={},activeMenus=null;
	var createOperate=function(mi,pele){
		return getOperateButton($.extend({parent:pele,onClick:mi.onclick},mi));
	};
	window.onComponentSelectionChanged.add(function(sels){
		if(activeMenus)
			activeMenus.hide();
		
		if(sels.length!=1)
			return;
		
		var ele=sels[0],define=ele.setting.define,xtype=define.xtype;
		if(!operates[xtype]){
			var div=document.createElement("div");
			pComponentTool.appendChild(div);
			$.each(define.menus,function(i,mi){
				createOperate(mi,div);
			});
			operates[xtype]=$(div);
			/*
			var menu=new sunz.Menu();
			$.each(define.menus,function(i,mi){
				menu.appendItem(mi);
			});
			menu.appendItem(miSplit);
			menu.appendItem(miForDelete);
			operates[xtype]=new sunz.Menubutton({parent:pToolbar,text:define.name,menu:menu});
			*/
		}
		activeMenus=operates[xtype];
		activeMenus.show();
	});
	
	// *********** 多元素选择（单元格)操作  **********************
	var miCombine={text:"合并单元格",iconCls:"",onclick:function(){
			var minRow=999,maxRow=0,
			minCol=999,maxCol=0,dcCount={};
			$.each(window.selectedComponents,function(i,proxy){
				var cell=proxy, ri=cell.parentNode.rowIndex,ci=cell.cellIndex;
				minRow=Math.min(minRow,ri);
				maxRow=Math.max(maxRow,ri+(cell.rowSpan-1));
				minCol=Math.min(minCol,ci);
				maxCol=Math.max(maxCol,ci+(cell.colSpan-1));
				dcCount[ri]=(dcCount[ri]||0)+cell.colSpan;
			});
			var dCol=maxCol-minCol+1;
			for(var i=minRow;i<=maxRow;i++){
				if(dcCount[i]!=dCol){
					$.messager.show({title:"无法合并单元格",msg:"所选的单元格不规则"});
					return ;
				}
			}
			var firstCell=window.selectedComponents[0].parentNode.parentNode.rows[minRow].cells[minCol];
			firstCell.rowSpan=maxRow-minRow+1;
			firstCell.colSpan=dCol;
			$.each(window.selectedComponents,function(i,proxy){
				if(proxy!=firstCell){
					var contianer=proxy.firstChild,subCount=contianer.childElementCount;
					for(var i=0;i<subCount;i++){
						firstCell.firstChild.appendChild(contianer.childNodes[i]);
					}
					proxy.remove();
				}else{
					proxy.setting.rowSpan=firstCell.rowSpan;
					proxy.setting.colSpan=firstCell.colSpan;
				}
			});
			window.onComponentsChanged.fire();
			selectComponent(firstCell);
		}
	};
	
	var mMulti=new sunz.Menu(),mCellCombine=new sunz.Menu();
	mMulti.appendItem(miForDelete);
	mCellCombine.appendItem(miCombine);
	mCellCombine.appendItem(miSplit);
	mCellCombine.appendItem(miForDelete);
	
	var activeMultiMenu=null,mbMultiSelect,mbCellCombine
		,getMultiOperate=function(){
			return mbMultiSelect=mbMultiSelect||new sunz.Menubutton({parent:pToolbar,text:"多个组件",menu:mMulti});
		}
		,getCombineOperate=function(){
			if(!mbCellCombine){
				var div=document.createElement("div");
				pComponentTool.appendChild(div);
				createOperate(miCombine,div);//new sunz.Linkbutton($.extend({parent:div,onClick:miCombine.onclick},miCombine));
				mbCellCombine= $(div)
			}
			return mbCellCombine;
			//return mbCellCombine=mbCellCombine||new sunz.Menubutton({parent:pToolbar,text:"单元格操作",menu:mCellCombine});
		};
	window.onComponentSelectionChanged.add(function(sels){
		if(activeMultiMenu)
			activeMultiMenu.hide();
		
		if(sels.length<=1)
			return;
		
		var isAllCell=true;
		$.each(sels,function(i,c){
			if(!c || c.setting.define.xtype!="Tablecell")
				return isAllCell=false;
		});
		if(isAllCell){
			activeMultiMenu=getCombineOperate();
			activeMultiMenu.show();
		}
		//activeMultiMenu=isAllCell?getCombineOperate():getMultiOperate();
		//activeMultiMenu.show();
	});
});