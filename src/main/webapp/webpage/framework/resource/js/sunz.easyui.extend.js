/**
 * 扩展easyui，使其拥有面向对象的能力
 *    如 var dwindow=$("#div");
 *    通常dwindow.window()后，仍然需要dindow.window('操作',参数)来进行操作
 *    使用扩展后，可以直接dwindow.操作(参数)
 * 并将这种能力赋与element本身   //这个功能暂时不开放吧，导致无限循环
 * 	  即dwindow[0].操作(参数)
 */
(function(){
	$.fn.datagrid.defaults.remoteSort=false;	// 默认不服务器端排序
	$.fn.datagrid.defaults.singleSelect=true;	// 默认单选，实际中单选的更多
	$.fn.combobox.defaults.editable=false;//下拉框默认不可编辑，更符合使用习惯
	$.fn.window.defaults.collapsible=false;	// 默认不显示收缩
	// 对发送和返回数据做预处理，方更符合我们自己的习惯
	$.fn.pagination.defaults.onBeforeRefresh=function(){$(this).pagination("options").total=-1;};
	$.fn.pagination.defaults.nav.refresh.handler=function(){
		var opt = $(this).pagination("options"),pn=opt.pageNumber,ps=opt.pageSize;
		if (opt.onBeforeRefresh.call(this, pn,ps) != false) {
			opt.onSelectPage.call(this, pn,ps);
			opt.onRefresh.call(this, pn,ps);
		}
	}
	var dgReload=$.fn.datagrid.methods.reload;
	$.fn.datagrid.methods.reload=function(jq,params){
		var opt=$.extend($(jq).datagrid("options").queryParams,typeof params == "string"?{url:params}:params);
		if(opt.total==null)opt.total=-1;
		var r=dgReload.call(jq,jq,opt);
		delete opt.total;
		return r;
	}
	var pr2SL=function(opt){if(!opt.rows||!opt.page) return; opt.start=opt.rows*(opt.page-1);opt.limit=opt.rows; if(opt.total==undefined) opt.total=$(this).datagrid("getPager").pagination("options").total;if(opt.total==0) delete opt.total; delete opt.page;delete opt.rows;};
	$.fn.combogrid.defaults.onBeforeLoad=$.fn.datagrid.defaults.onBeforeLoad=pr2SL;
	$.fn.treegrid.defaults.onBeforeLoad=function(row,opt){pr2SL.call(this,opt);};
	$.fn.combotree.defaults.loadFilter=function(jr,pEle){return $.isArray(jr)?jr:(jr.success?"":$.messager.show({title:"数据加载出错",msg:jr.msg}),jr.data)};
	$.fn.datalist.defaults.loadFilter=$.fn.treegrid.defaults.loadFilter=$.fn.datagrid.defaults.loadFilter=$.fn.combogrid.defaults.loadFilter=function(jr){if($.isArray(jr))return /*{rows:jr,total:jr.length}*/ jr;if(jr.success)return {rows:jr.data,total:jr.total}; else if(false==jr.success){/*$(this).datagrid("loading",{loadMsg:jr.msg||"数据加载出错"});*/$.messager.show({title:"数据加载出错",msg:jr.msg}); return {rows:[],total:0};}else return jr.data||jr;};
	$.fn.combobox.defaults.loadFilter=function(jr){if($.isArray(jr))return jr;return jr.success?jr.data:(false==jr.success?($.messager.show({title:"数据加载出错",msg:jr.msg}),[]):(jr.data||jr));};
	//$.fn.treegrid.defaults.view.transfer=function(_this,pid,data){return data;};
	// 扩展字典下拉
	var cmb=$.fn.combobox;
	$.fn.combobox=$.fn.dictcombo=$.fn.dictcombobox=$.extend(function(opt,param){
		if('string'!=typeof opt && opt.dict)opt.data=D.get(opt.dict,opt.black);
		return cmb.call(this,opt,param);
	},cmb);
	// excel导出
	var realExport=function(jdg,fieldMap,dictFields,filename,all){
		var opts=jdg.options()
		,url=opts.url
		,queryParams=opts.queryParams
		,gPager=jdg.getPager()
		,pgOpt=gPager.length?gPager.pagination("options"):{pageNumber:1,pageSize:4294967296};//不分页时，尽管逻辑上认为不分页时不会导出当前页，还是做一下兼容
		
		var exportUrl=opts.exportUrl;
		if(!exportUrl){
			var isQuerySearch=url.indexOf("query.do")>0 && (url.indexOf("search")||queryParams.search);
			if(!isQuerySearch){
				$.messager.show({title:"操作失败",msg:"内部配置无效，无法执行导出"});
				return;
			}
			exportUrl=url.replace(/query\.do[\?](search)?/,"export.do?searchExcel&");
		}
		var param={
				fieldMap:fieldMap||$.map(opts.columns[0],function(c){return typeof(c)=="string"?c:(c.field+":"+c.title)}),//(fieldMap||(fm="",$.each(opts.columns[0],function(i,c){if(c.hidden)return; fm+=c.field+":"+c.title+",";}),fm)),
				filename:(filename||(document.title+(all?"":("第"+pgOpt.pageNumber+"页")))),
				dictFields:dictFields//(dictFields||[]).join(",")
		};
		var exportUrl=(exportUrl+(exportUrl.indexOf("?")?"":"?"))
		+"&"+$.param($.extend({},window.urlParams,queryParams,param,{total:pgOpt.total},(all?null:{start:(pgOpt.pageNumber-1)*pgOpt.pageSize,limit:pgOpt.pageSize})),true);
		window.open(exportUrl);
	},exportToExcel=function(jdg,fieldMap,dictFields,filename,all){
		var fm=[[{title:"选择",field:"id",width:60,checkbox:true},{title:"字段",field:"title",width:400}]],
		gd=[],
		df=dictFields;
		$.each(jdg.options().columns[0],function(i,col){
			if(col.hidden || col.field=='attachment') return;
			gd.push({id:i,field:col.field,title:col.title,checked:true});
			/*if(col.dict||col.isDict/*||col.formatter) {
	    	df.push(col.field);
	    }*/
		});
		var win=new sunz.Window({modal:true,width:500,height:400,title:"导出字段选择",
			content:'<div class="container" style="position:relative;box-sizing:border-box;width:100%;height:100%;padding-bottom:50px">\
				</div>\
				<div class="toolbar" style="position:absolute;bottom:0;height:48px;padding:5px 100px 0px;"></div>'
		});
		var c=win.find(".container")[0],t=win.find(".toolbar")[0];
		var g=new sunz.Datagrid({
			parent:c,
			columns:fm, data:gd,idField:"id",
			singleSelect:false,fit:true,fitColumns:true,rownumbers:true,striped:true,selectOnCheck:true,checkOnSelect:true
		});
		g.checkAll();
		
		new sunz.Linkbutton({parent:t,style:"margin-left:15px;float:right;",text:"取消",iconCls:"icon-cancel",handler:function(){win.close();}});
		new sunz.Linkbutton({parent:t,style:"float:right;",text:"确定",iconCls:"icon-ok",handler:function(){
			//var fmString=(fm="",$.each(g.getChecked(),function(i,c){fm+=c.field+":"+c.title+",";}),fm);
			realExport(jdg,$.map(g.getChecked(),function(c){return c.field+":"+c.title}),df,filename,all);
			win.close();
		}
		});
		win.open();
	};
	$.fn.datagrid.methods.fastToExcel=realExport;
	$.fn.datagrid.methods.exportToExcel=exportToExcel;
	var dg=$.fn.datagrid;
	dg.defaults.halign="center";
	dg.defaults.align="";
	$.fn.datagrid=function(opts,param){
		var jdg;
		if(typeof opts !="string"){
			opts=opts||{};
			if(opts.excelCurrent){
				var c=opts.excelCurrent,
					fn=(c.fast||c.notSelectFields)?realExport:exportToExcel;
				c=$.extend(c===true?{text:"导出当前页到Excel",iconCls:"excelCurrent"}:c,{handler:function(){fn(jdg,c.fieldMap,c.dictFields,c.filename,false);}})
				opts.toolbar=(opts.toolbar||[]).concat(['-',c]);
			}
			if(opts.excel){
				var config=opts.excel,
					fnExport=(config.fast||config.notSelectFields)?realExport:exportToExcel;
				config=$.extend(config===true?{text:"导出所有结果到Excel",iconCls:"excel"}:config,{handler:function(){fnExport(jdg,config.fieldMap,config.dictFields,config.filename,true);}})
				opts.toolbar=(opts.toolbar||[]).concat(['-',config]);
			}
			
			// 2019-07-26 defaults.halign|align
			if(opts.columns && (opts.halign||opts.align||dg.defaults.halign||dg.defaults.align)){
				$.each(opts.columns,function(i,cols){
					$.each(cols,function(j,col){
						col.align=col.align||opts.align||dg.defaults.align;
						col.halign=col.halign||opts.halign||dg.defaults.halign;
					});
				});				
			}
		}
		jdg=dg.call(this,opts,param);
		return jdg;
	}
	$.extend($.fn.datagrid,dg);
})();
$.fn.validatebox.defaults.rules.json={
	message:"语法分析发现错误",
	validator:function(v,param){
		if(!v)return true;
		try{
			var tmp=eval("("+v+")");
			if(param){
				return param===typeof tmp;
			}
			return true;
		}catch(e){
			return false;
		}
	}
};

// asX
(function(){
	var config={"draggable":{parent:""}
		,"droppable":{parent:""}
		,"resizable":{parent:""}
		,"linkbutton":{parent:""}
		,"pagination":{parent:""}
		,"tree":{parent:""}
		,"progressbar":{parent:""}
		,"panel":{parent:""}
		,"window":{parent:"panel"}
		,"dialog":{parent:"window"}
		,"accordion":{parent:""}
		,"tabs":{parent:""}
		,"layout":{parent:""}
		,"menu":{parent:""}
		,"menubutton":{parent:"linkbutton"}
		,"splitbutton":{parent:"menubutton"}
		,"searchbox":{parent:"textbox"}
		,"validatebox":{parent:""}
		,"form":{parent:""}
		,"textbox":{parent:"validatebox"}
		,"numberbox":{parent:"textbox"}
		,"calendar":{parent:""}
		,"spinner":{parent:"textbox"}
		,"numberspinner":{parent:"spinner"}
		,"timespinner":{parent:"spinner"}
		,"datagrid":{parent:""}
		,"propertygrid":{parent:"datagrid"}
		,"treegrid":{parent:"datagrid"}
		,"combo":{parent:"textbox"}
		,"combobox":{parent:"combo"}
		,"combotree":{parent:"combo"}
		,"combogrid":{parent:"combo"}
		,"combotreegrid":{parent:"combo"}
		,"datebox":{parent:"combo"}
		,"datetimebox":{parent:"datebox"}
		,"slider":{parent:""}
		,"dictcombo":{parent:"combobox"}
		,"datalist":{parent:"datagrid"}
		,"switchbutton":{parent:""}
		,"datetimespinner":{parent:"timespinner"}
		,"filebox":{parent:"textbox"}
		//,"tagbox":{parent:"combobox"}
	},
	ases=[];
	$.each(config,function(n,c){
		var rn=n.substr(0,1).toUpperCase()+n.substr(1);  // 首字母大写
		ases.push(rn);
		$.fn["as"+rn]=function(o,p){
			var _this=this;
			if(_this.ased && _this.ased!=rn)throw new Error("当前对象已经作为"+_this.ased+"，需要作为别的使用，请先unAs");
			_this.ased=rn;
			
			//
			_this.getFieldValues=function(skipNull){
				var values=_this.serializeArray(),result={};
				$.each(values,function(i,o){
					if(!skipNull||(o.value!=undefined && o.value!=null && o.value!=""))result[o.name]=o.value;
				});
				return result;
			}
			//
			_this.ms=[];
			var all={};
			for(var p=n;p;p=config[p].parent){
				all=$.extend({},$.fn[p].methods,all);	// 有可能会被子类重写
			}					
			
			$.each(all,function(n,m){
				_this.ms.push(n);
				_this[n]=function(opt){
					return m.call(_this,_this,opt);
				};
				// 让element本身也具有面向对象能力 这个功能暂时不开放吧，导致无限循环
				/*_this.each(function(i,ele){
					ele[n]=function(opt){
						return m.call(ele,_this,opt);
					};
					ele.unAs=function(){
						$.each(_this.ms,function(i,n){
							delete ele[n];
						});
					}
				});*/
			});
			return $.fn[n].call(_this,o,p);
		}
	});
	
	$.fn.as=function(n,o,p){
		var fn=$.fn["as"+n];
		if(fn){
			return fn.call(this,o,p);
		}
		throw new Error("不可识别的类型");
	};
	$.fn.unAs=function(){
		var _this=this;
		if(_this.ms){
			$.each(_this.ms,function(i,n){
				delete _this[n];
				_this.each(function(){
					delete this[n];
				});
			});
		}
		delete _this.ased;
	}
})();