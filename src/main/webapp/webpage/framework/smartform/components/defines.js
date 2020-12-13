/**
 * 
 */
smart.applyToElement=(
	globalAttrs=["readonly","disabled","id","name","tabindex","title","class","accesskey","contenteditable"/*来源于W3CSchool*/],
	globalStyles=["width","height","top","left","right","bottom","position","float","margin*","padding*","color","font*","z-index","text-align","display","align","vertical-align","valign","background*"],
	globalFields=["innerHTML","innerText","textContent","value"],
	function(ele,setting,extAttrs,extStyles){
		if(!ele || !setting) return;
		if(setting.fit){setting.width="100%";setting.height="100%";}
		if(setting.hidden){setting.display="none";}
		var attrs=globalAttrs.concat(extAttrs),styles=globalStyles.concat(extStyles);
		var fnV=function(tps){
			var r={};
			$.each(tps,function(i,tp){
				if(!tp)return;
				if(tp.indexOf("*")>-0){
					for(var o in setting){
						if(new RegExp(tp).test(o))
							r[o]=setting[o];
					}
				}else{
					var v=setting[tp];
					if(v!=null)
						r[tp]=v;
				}
			});
			return r;
		};
		var oAttr=fnV(attrs),oStyle=fnV(styles),oFields=fnV(globalFields);
		for(var f in oFields){ // 直接属性字段（property）
			ele[f]=oFields[f];
		}
		for(var n in setting){// 事件
			var v=setting[n];
				if($.isFunction(v))
				ele[n]=v;
		}
		// Attributes和styles
		sunz.applyAttributes(ele, {attrs:$.extend(oAttr,setting.attrs||setting.attributes),
								   style:$.extend(oStyle,sunz.parseStyle(setting.style))}
		);
		
});
smart.define("Form",{
	name:"表单（Form）",
	defaults:{fit:true},
	init:function(instance,params){
		$(tplReplace(smart.containerProxyTpl,params)).appendTo(instance);
	}
});
smart.define("Panel",{
	name:"简单面版",
	defaults:{width:400,height:200,title:"标题",fit:false},
	init:function(instance,params){
		instance.asPanel({content:tplReplace(smart.containerProxyTpl,params)});
		params.content=null;
	}
});
smart.define("Layout",{
	name:"布局",
	defaults:{fit:true},		
	init:function(instance,params){
		$.each(params.items,function(i,item){addComponent(params.parent,defines.Sublayout,item);});
		delete params.items;
	}			
});
smart.define("Sublayout",{
	name:"布局块",
	defaults:{split:true,title:"新布局块",height:160,width:160},
	customAdd:function(pele,params){
		params.content=tplReplace(smart.containerProxyTpl,params);
		pele.master.add(params);
		params.content=null;
		var div= pele.master.panel(params.region)[0].parentElement;
		$(div).addClass(smart.selectProxyClass);
		div.master={
			resize:function(size){params.width=size.width;params.height=size.height;}
		};
		return div;
	}
});

smart.define("Tabs",{
	name:"tabs",
	defaults:{fit:true},
	init:function(instance,params){
		$.each(params.items,function(i,item){addComponent(params.parent,defines.Subtab,item);});
		delete params.items;
	}
});
smart.define("Subtab",{
	name:"选项卡",
	defaults:{title:"新选项卡"},
	customAdd:function(pele,params){
		params.content=tplReplace(smart.containerProxyTpl,params);
		pele.master.add(params);
		params.content=null;
		var tab=pele.master.getTab(params.title),div=tab[0].parentElement;
		$(div).addClass(smart.selectProxyClass);
		div.master={
			tab:tab,
			resize:function(){}
		};
		return div;
	}
});
smart.define("Tree",{
	name:"树",
	defaults:{fit:false,width:200,height:400,title:"标题",
		url:"framework/query.do?search&k=queryForTree&limit=10",
		idField:"ID",relationField:"PARENTID",displayField:"NAME",
		loadFilter:function(data,p){
			data=data.data;
			if(!data||!data.length)return [];
			var opt=$(this).tree("options"),idf=opt.idField,df=opt.displayField;
			if((idf&&idf!="id")||(df&&df!="text")){
				var fnVd=function(item){
					if(idf){item.id=item[idf];}
					if(df){item.text=item[df];}
					$.each(item.children,function(i,sub){fnVd(sub);});
				};
				$.each(data,function(i,item){fnVd(item);});
			}
			if(opt.relationField){
				var $d=D.createNew();
				$.each(data,function(i,d){
					$.extend($d.add(d.id,d.id,d.text,d[opt.relationField]),d);
				})
				return $d.get($d.ROOT);
			}
			
			return data;
		}
	},
	customAdd:function(pele,params){
		if(params.relationField/*兼容原生树-原生树没有relationField*/&&!params.loadFilter){
			params.loadFilter=defines.Tree.defaults.loadFilter;
		}
		return defaultComponentAdd(pele,params);
	},
	init:function(instance,params){
		if(params.loadFilter==defines.Tree.defaults.loadFilter)
			delete params.loadFilter;
	}
});
smart.define("Accordion",{
	name:"手风琴",
	defaults:{fit:true},
	init:function(instance,params){
		$.each(params.items,function(i,item){addComponent(params.parent,defines.Subaccordion,item);});
		delete params.items;
	}			
});

smart.define("Subaccordion",{
	name:"手风琴子面版",
	defaults:{title:"新面版"},
	customAdd:function(pele,params){
		params.content=tplReplace(smart.containerProxyTpl,params);
		pele.master.add(params);
		params.content=null;
		var item=pele.master.getPanel(params.title),div= item[0].parentElement;
		$(div).addClass(smart.selectProxyClass);
		div.master={
			item:item,
			resize:function(){}
		};
		return div;
	}
});
smart.define("Datagrid",{
	name:"网格控件",
	defaults:{fit:false,width:500,height:400,title:"标题",fit:true,fitColumns:true,selectOnCheck: false,checkOnSelect: false,pagination:true,rownumbers:true}
});
smart.define("Textbox",{
	name:"文本框",isInput:true,
	defaults:{width:200,label:"新文本"},
});
smart.define("Numberbox",{
	name:"数值输入框",isInput:true,
	defaults:{width:200,label:"新数值"},
	init:function(instance,params){}
});
smart.define("Numberspinner",{
	name:"数字微调",isInput:true,
	defaults:{width:200,label:"新数字微调"},
	init:function(instance,params){}
});
smart.define("Calendar",{
	name:"日历",isInput:true,
	defaults:{width:200,label:"新日历"},
	init:function(instance,params){}
});
smart.define("Datebox",{
	name:"日期框",isInput:true,
	defaults:{width:200,label:"新日期"},
	init:function(instance,params){}
});
smart.define("Datetimebox",{
	name:"日期时间框",isInput:true,
	defaults:{width:200,label:"新日期时间"},
	init:function(instance,params){}
});
smart.define("Datetimespinner",{
	name:"日期时间微调框",isInput:true,
	defaults:{width:200,label:"新日期时间微调"},
	init:function(instance,params){}
});
smart.define("Timespinner",{
	xtype:"Timespinner",name:"时间微调",isInput:true,
	defaults:{width:200,label:"新时间微调"},
	init:function(instance,params){}
});
smart.define("Dictcombo",{
	name:"字典项下拉框",isInput:true,
	defaults:{width:200,label:"新下拉框"},
	init:function(instance,params){
	}
});
smart.define("Slider",{
	name:"滑动条",isInput:true,
	defaults:{max:100,min:0,step:1,width:200,showTip:true}
});
smart.define("Linkbutton",{
	name:"按钮",
	defaults:{text:"新按钮",iconCls:"sunz",width:null}
});
smart.define("Filebox",{
	name:"文件框",isInput:true,
	defaults:{width:200,buttonText:"请选择..."}
});
smart.define("Combo",{
	name:"自定义下拉框Combo",isInput:true,
	defaults:{width:200,label:"自定义下拉框"}
});
smart.define("Combotree",{
	name:"下拉树",isInput:true,
	defaults:{width:200,label:"新下拉树"}
});
smart.define("Combogrid",{
	name:"下拉表格",isInput:true,
	defaults:{width:200,label:"新下拉表格"}
});
smart.define("Combotreegrid",{
	name:"下拉表格树",isInput:true,
	defaults:{width:200,label:"新下拉表格树"}
});
smart.define("Datalist",{
	name:"数据列表",
	defaults:{title:"数据列表",fit:true}
});
smart.define("Propertygrid",{
	name:"属性表格",
	defaults:{title:"属性表格",fit:true}
});
smart.define("Treegrid",{
	name:"表格树",
	defaults:{title:"新表格树",fit:true,loadFilter:function(jr){
		var jrx= $.fn.treegrid.defaults.loadFilter(jr);
		var opt=$(this).treegrid("options"),pf=opt.relationField;
		if(!opt.columns)opt.columns=[[{title:opt.treeField,field:opt.treeField}]];
		if(pf){$.each(jrx.rows,function(i,d){d._parentId=d[pf];});}
		return jrx;
	}}
});


/*  html原生控件  */
smart.define("Table",{
	name:"表格",
	defaults:{fit:true,border:0},
	attrs:["align","border","cellspacing","cellpadding","frame","rules","summary","width","height"],
	styles:[],
	addRow:function(proxy,params){
    	   var row=addComponent(proxy,defines.Tablerow,params,true);
    	   for(var i=0;i<proxy.master.columnCount();i++)
    		   addComponent(row,defines.Tablecell,null,true);
    	   window.onComponentsChanged.fire();
	},
	addColumn:function(proxy,params){
		$.each(proxy.master.rows,function(i,row){
	   		addComponent(row,defines.Tablecell,params,true);
	   	});
		window.onComponentsChanged.fire();
	},
	init:function(instance,params){
		// 行列的初始化，
		var proxy=instance;
		if(params.rows>0){
			for(var i=0;i<params.rows;i++)
				defines.Table.addRow(proxy);
		}
		if(params.columns>0){
			for(var i=0;i<params.columns;i++)
				defines.Table.addColumn(proxy);
		}
		delete params.rows;
		delete params.columns;
	},
	customAdd:function(pele,params){
		var ele=document.createElement("table");
		pele.insertPoint?pele.insertBefore(ele,pele.insertPoint):pele.appendChild(ele);//pele.appendChild(ele);
		$(ele).addClass(smart.selectProxyClass);
		ele.columnCount=function(){
			var count=0;
			for(var i=0;i<ele.rows.length;i++){
				var col=0;
				for(var k=0;k<ele.rows[i].cells.length;k++){
					col =col+ele.rows[i].cells[k].colSpan;
				}
				count=Math.max(count,col);
				//count=Math.max(count,ele.rows[i].cells.length);			
			}
			return count;
		};
		ele.master=ele;
		ele.setting=params;
		this.customUpdate(ele);
		
		return ele;
	},
	customUpdate:function(ele,setting){
		smart.applyToElement(ele.master,setting||ele.setting,defines.Table.attrs,defines.Table.styles);
		//$.extend(ele,ele.setting);
	}	
});
smart.define("Tablerow",{
	name:"表格行",
	attrs:["align","valign","char","charoff"],
	styles:[],
	customAdd:function(pele,params){
		var table=pele.master, row=table.insertRow(params.insertIndex==null?table.rows.length:params.insertIndex);
		delete params.insertIndex; //用完即删
		$(row).addClass(smart.selectProxyClass);
		row.master=row;
		row.setting=params;
		this.customUpdate(row);
		return row;
	},
	customUpdate:function(ele,setting){
		smart.applyToElement(ele,ele.setting||setting,defines.Tablerow.attrs,defines.Tablerow.styles);
		//$.extend(ele,ele.setting);
	}
});
smart.define("Tablecell",{
	name:"单元格",
	defaults:{rowSpan:1,colSpan:1},
	attrs:["rowSpan","colSpan","axis","headers","nowrap","width","align","valign","char","charoff","scope"],
	styles:[],
	customAdd:function(pele,params){
		var cell=pele.master.insertCell(params.insertIndex==null?pele.master.cells.length:params.insertIndex);
		delete params.insertIndex;//用完即删
		$(cell).addClass(smart.selectProxyClass);
		if(window.designMode){
			cell.innerHTML=tplReplace(smart.containerProxyTpl,params);
		}
		else{
			$(cell).addClass(smart.containerProxyClass);
			$(cell).attr("proxyid",params.id);
		}
		cell.master=cell;
		cell.setting=params;
		this.customUpdate(cell);
		return cell;
	},
	customUpdate:function(ele,setting){
		smart.applyToElement(ele.master,setting||ele.setting,defines.Tablecell.attrs,defines.Tablecell.styles);
		//$.extend(ele,ele.setting);
	}
});
smart.define("Label",{
	name:"原生文本",
	defaults:{innerHTML:"新标签"},
	customAdd:function(pele,params){
		var ele=document.createElement("span");
		pele.insertPoint?pele.insertBefore(ele,pele.insertPoint):pele.appendChild(ele);//pele.appendChild(ele);
		$(ele).addClass(smart.selectProxyClass);
		ele.master=ele;
		ele.setting=params;
		this.customUpdate(ele);
		return ele;
	},
	customUpdate:function(ele,setting){
		smart.applyToElement(ele.master,setting||ele.setting);
	}
});
smart.define("Div",{
	name:"原生div",
	defaults:{width:"48px",height:"48px"},
	customAdd:function(pele,params){
		var ele=document.createElement("div");
		pele.insertPoint?pele.insertBefore(ele,pele.insertPoint):pele.appendChild(ele);//pele.appendChild(ele);
		$(ele).addClass(smart.selectProxyClass);
		if(window.designMode){
			ele.innerHTML=tplReplace(smart.containerProxyTpl,params);
		}
		else{
			$(ele).addClass(smart.containerProxyClass);
			$(ele).attr("proxyid",params.id);
		}
		ele.master=ele;
		ele.setting=params;
		this.customUpdate(ele);
		return ele;
	},
	customUpdate:function(ele,setting){
		smart.applyToElement(ele.master,setting||ele.setting);
	}
});
smart.define("Input",{
	name:"原生input",isInput:true,
	defaults:{width:"200",height:"24px",type:"text"},
	customAdd:function(pele,params){
		var ele=document.createElement("input");
		ele.setValue=function(v){if(ele.type=="radio"||ele.type=="checkbox")ele.checked=(ele.value==v);else ele.value=v;};
		ele.getValue=function(){return (ele.type=="radio"||ele.type=="checkbox")?ele.checked:ele.value;};
		pele.insertPoint?pele.insertBefore(ele,pele.insertPoint):pele.appendChild(ele);//pele.appendChild(ele);
		$(ele).addClass(smart.selectProxyClass);
		ele.master=ele;
		ele.setting=params;
		this.customUpdate(ele);
		return ele;
	},
	customUpdate:function(ele,setting){
		var params=setting||ele.setting;
		if(params.required||params.validType)
			$(ele).validatebox(params);
		else
			$(ele).validatebox({novalidate:true});
		
		smart.applyToElement(ele.master,params,["value","type","hidden"]);
	}
});

/* 页面  */
smart.define("Smartform",{
	name:"子表单",
	defaults:{fit:true,part:"list",addable:true,editable:true,viewable:true,deleteable:true,logRequired:true},
	customUpdate:function(ele,setting){
		var s=setting||ele.setting;
		smart.applyToElement(ele,s);
	},
	customAdd:function(pele,params){
		var jele=$('<div style="margin:0;padding:0;"></div>'), ele=jele[0];
		//读取目标表单设计然后展示
		pele.insertPoint?pele.insertBefore(ele,pele.insertPoint):pele.appendChild(ele);
		defines.Smartform.customUpdate(ele,params);
		ele.master={};
		$.ajax("framework/query.do?object&k=sys_smartform",{
			async:false,
			data: {smartid:params.smartid,smartcode:params.smartcode}, 
			success:function(jr){
				if(jr.success){
					var def=jr.data;
					delete def.id;// id被特殊使用
					
					var parentForm=(function fnp(ele){var p=ele.parentElement;if(p==null||p==document.body) return null; if(p.tagName=="FORM" && p.attributes["type"] && p.attributes["type"].value=="smartform") return p;else return fnp(p);})(ele);
					if(!parentForm || !parentForm.setting){
						$(ele).html("内部错误：关联表单关系无法建立，这通常是因为没有指定数据参数引起的");return;
					}
					def.parentForm=parentForm;
					def.relationMap=toRelationMap(params.relationField);
					if(def.OTHERSETTING){
						$.extend(def,$.decode(def.OTHERSETTING));
					}
					def.addable=params.addable;
					def.editable=params.editable;
					def.deleteable=params.deleteable;
					def.viewable=params.viewable;
					def.logRequired=params.logRequired;	
					def.historyable=params.historyable;

					if(window.onSmartDefineLoad)
						window.onSmartDefineLoad.fire(def);
					
					var formDef=$.decode(def.FORMDEF);
					if(params.part=="list"){
						var listDef=$.decode(def.LISTDEF);
						ele.master=parseList(listDef,formDef,def,ele);						
					}
					else{
						ele.master=parseForm(formDef,def,ele);						
					}
				}else{
					$(ele).html("内部错误：关联表单不存在");
				}
			}
		});
		return ele;
	}
});