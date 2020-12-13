<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<style type="text/css">
<!--
	html,body,div{box-sizing:border-box;}
	.tdSearchTitleH {text-align:right}
	.searchTableH {width:100%;padding:0;}
	.searchHRow label {margin:0 5px 0 10px;}
	.searchHRow {padding:5px 0}
	.searchTableH #divButtons {display:inline-block;margin-left:15px}
	.searchField-more{padding:0 10px;cursor:pointer;}
	.searchField-more::after{
		content:" ";
		background:url(webpage/framework/query/more.png) no-repeat;
		display:inline-block;width:10px;height:10px;
		position: relative;top: 1px;left:5px;
	}
	.expanded::after{background-image:url(webpage/framework/query/less.png)}
	
	.searchTableV {width:100%;margin:2px 0;}
	.searchTableV td{padding:5px;}
	.tdSearchButtonV {text-align:right}
	td.tdSearchInputV {padding-left: 25px;}
	.searchTableV #divButtons {text-align: center;}
	#btn-reset{margin-left:10px}
-->
</style>
<z:dict items="${queryDef.dicts}"></z:dict>
<z:config groups="queryRuntimeConfigs" items="$queryDef.configs"></z:config>
${queryDef.resources}

<script type="text/javascript" >
	var fnEmpty=function(){};
	var queryid='${queryDef.id}'
		,n='${queryDef.description}'
		,qSetting=${queryDef.setting==null||"".equals(queryDef.setting)?"{searchColumns:3}":queryDef.setting}
		,sqlid='${queryDef.sqlid}'
		,dataUrl='${queryDef.dataUrl}'
		,multiSelect=${queryDef.multiSelect}
		,doublehandler=${queryDef.doubleHandler==null||"".equals(queryDef.doubleHandler)?"fnEmpty":queryDef.doubleHandler};
	if(qSetting.searchTitle===undefined)qSetting.searchTitle="查询条件";
	if(qSetting.resultTitle===undefined)qSetting.resultTitle="";		
	document.title=n;//"查询："+n;
	
	var sql=null,searchFields=[],resultFields=[],operations=[]
	,addSf=function(n,t,type,s,i){searchFields.push({name:n,title:t,inputType:type||'textbox',styles:$.extend({labelAlign:"right"},s),order:i});}
	,addRf=function(n,t,s,i){resultFields.push({name:n,title:t,styles:s,order:i});}
	,addOp=function(t,ico,h,i){operations.push({text:t,iconCls:ico,handler:h,order:i,separator:t=="-"||t=="|"});};
	
	<c:forEach items='${queryDef.searchFields}' var='sf'>addSf('${sf.name}','${sf.title}','${sf.inputType}',${sf.styles==null||"".equals(sf.styles)?"{}":sf.styles},${sf.orderIndex});
	</c:forEach>
	<c:forEach items='${queryDef.resultFields}' var='rf'>addRf('${rf.name}','${rf.title}',${rf.styles==null||"".equals(rf.styles)?"{}":rf.styles},${rf.orderIndex});
	</c:forEach>
	<c:forEach items='${queryDef.operations}' var='op'>addOp('${op.text}','${op.icon}',${op.jsHandler==null||"".equals(op.jsHandler)?"fnEmpty":op.jsHandler},${op.orderIndex});
	</c:forEach>
	// 排序
	var fnSort=function(a,b){return a.order-b.order;};
	searchFields=searchFields.sort(fnSort);
	resultFields=resultFields.sort(fnSort);
	operations=operations.sort(fnSort);
	operations=$.map(operations,function(o){return o.separator?"-":o});
	
	// 搜索字段
	var html='<input type="hidden" name="total" value="-1" />',searchConfig={id:'pSearch',title:qSetting.searchTitle};
	var getHSearch=function(){
		var headHeight=C.easyuiPanelHead||39,rowExpandHeight=C.easyuiInputExpandHeight||3,defColumns=C.defaultSearchColumns||3;
		var rowColumns=qSetting.searchColumns||defColumns,searchRows=1;
		var rowStyles=qSetting.searchRowStyles||[];
		
		html=$('<div class="searchTableH">'+html+'</div>');
		if(qSetting.searchHeight)
			html.css("height",qSetting.searchHeight);
		
		var getMaxCIndex=function(rIndex){return typeof rowColumns=="number"?rowColumns:(rowColumns[rIndex]||defColumns)},
			getRowStyle=function(rIndex){return typeof rowStyles=="string"?rowStyles:(rowStyles[rIndex]||"")},
			getRowHtml=function(rIndex){return '<div class="searchHRow sr'+rIndex+'" style="'+getRowStyle(rIndex)+'"></div>'},
			rcIndex=getMaxCIndex(0),
			jrow=$(getRowHtml(0)).appendTo(html);
		$.each(searchFields,function(i,f){
			f.i=i;
			if(i==rcIndex){ // 该换行了
				jrow=$(getRowHtml(searchRows)).appendTo(html);
				rcIndex=rcIndex+getMaxCIndex(searchRows);
				searchRows++;
			}
			$(tplReplace('<input id="searchField-{i}" type="text" name="{name}" label="{title}"/></td>',f)).appendTo(jrow);
		});
		
		var jBtnRow=qSetting.searchFieldShowAll?jrow:$(html.find(".searchHRow")[0]);		
		$('<div id="divButtons"></div>').appendTo(jBtnRow); // 按钮
		if(!qSetting.searchFieldShowAll && searchRows>1){	// 更多
			var switchMore=function(){
				html.find(".searchHRow").each(function(){$(this).toggle();});
				jBtnRow.show();
			};
			var jmore=$('<span class="l-btn-text searchField-more">更多</span>') /*使用easyui颜色*/
				.appendTo(jBtnRow)
				.on("click",function(){
					jmore.toggleClass("expanded");
					jmore.html(jmore.hasClass("expanded")?"收起":"更多");
					switchMore();
					$(viewport).layout("panel","north").panel("resize",{height:calcHeight()});
					$(viewport).layout();
				});
			
			switchMore();
		}

		var calcHeight=function(){return qSetting.searchHeight||((qSetting.searchTitle?headHeight:0)+html.height()+rowExpandHeight*searchRows);}
		html.appendTo(document.body);
		var actHeight=calcHeight();
		//html.remove();
		
		$(viewport).asLayout().add($.extend(searchConfig,{region:"north",height:actHeight}));
	};
	
	var getVSearch=function(){
		html +='<table class="searchTableV" >';
		$.each(searchFields,function(i,f){
			f.i=i;
			html+=tplReplace('<tr><td class="tdSearchTitleV">{title}:</td></tr><tr><td class="tdSearchInputV"><input id="searchField-{i}" type="text" name="{name}" /></td></tr>',f);
		});
		html+='<tr><td class="tdSearchButtonV" id="divButtons"></td></tr> </table>';	

		$(viewport).asLayout().add($.extend(searchConfig,{split:true,region:"west",width:qSetting.searchWidth||240}));
	};
	var vertical=getQueryParam("direction")=='vertical';
		
	var urlParams=$.extend({},getQueryParam());
	delete urlParams.goQuery;
	delete urlParams.k;
	delete urlParams.key;
	window.onBeforeSearch=$.Callbacks("stopOnFalse unique");
	var	search=(realSearch=function(o,formValues,urlValues){grid.load(o);delete grid.options().queryParams.total;},
		function(){
			var fv=window.form?window.form.getFieldValues():null,
				o=$.extend({},urlParams,fv);
			if(onBeforeSearch.has(realSearch))
				onBeforeSearch.remove(realSearch);
			onBeforeSearch.add(realSearch);
			onBeforeSearch.fire(o,fv,urlParams);
		}
	);
	
	$(function(){
		if(!qSetting.hideSearch){
			vertical?getVSearch():getHSearch();
			var pSearch=$("#pSearch").asPanel();
			var pForm=window.form=new sunz.Form({parent:pSearch[0],attrs:{name:"searchForm"},style:{"position:relative;margin-bottom":0}})
			.on("keydown",function(e){
				if(e.keyCode==13){ 
			    	search();  
			    }
			});
			$(html).appendTo(pForm);
			$.each(searchFields,function(i,f){
				pForm.find("#searchField-"+i)[f.inputType](f.styles); 
			});
			pForm.load(urlParams);
			//$.parser.parse(form);
			new sunz.Linkbutton({parent:divButtons,text:qSetting.searchText||'查询',id:"btn-search",iconCls:'icon-search',handler:search});
			
			if(!qSetting.hideReset)
				new sunz.Linkbutton({parent:divButtons,text:qSetting.resetText||'重置',id:"btn-reset",iconCls:'icon-redo',handler:function(){form.reset();}});
		}
		
		// 结果字段
		var columns=[];
		$.each(resultFields,function(i,f){
			if(f.styles && (f.styles.dict ||f.styles.isDict)){
				f.formatter=function(v){return D.getText(v)};
				delete f.styles.dict || delete f.styles.isDict;
			}
			$.extend(f,f.styles);
			f.field=f.name;
			f.width=f.width||100;
			delete f.name;delete f.styles;
			columns.push(f);
		});
		if(multiSelect)columns=[{title:"选择",field:"selected",width:48,checkbox:true}].concat(columns);
				
		$(viewport).asLayout().add({id:"pResult",region:'center',title:qSetting.resultTitle});
		var pResult=$("#pResult")[0];
		var gridConfig=$.extend({
				iconCls:'icon-edit',
				parent:pResult,
				columns:[columns],
				singleSelect:!multiSelect,
				fit:true,fitColumns:true,
				autoRowHeight:true,
				pagination:true,rownumbers:true,striped:true,
				onDblClickRow:function(i,row){
					doublehandler(row,i,grid);
				},
				/*onLoadSuccess:function(jr){
					 $(viewport).layout("panel","center").panel("setTitle","<span style=\"font-weight:bold;\">共查询到<span style=\"color:red;font-size:16px;font-weight:bold;\"> "+jr.total+" </span>条相关结果</span>");
				},*/
				toolbar:operations
			},${queryDef.gridSetting==null||"".equals(queryDef.gridSetting)?"{}":queryDef.gridSetting});
		var grid=window.grid=new sunz.Datagrid(gridConfig);
		
		// doLayout，否则north部分height导致布局有问题
		//$("#viewport").layout();
		grid.options().url=dataUrl||('framework/query.do?search&sqlid='+sqlid);
		<c:if test="${queryDef.autoLoadData}" >search();</c:if>
		//grid.datagrid({url:'framework/query.do?search&k='+sqlkey,queryParams:form.getFieldValues()});		
	});
	
	function handleSelectedRow(fn,nsMsg){
		var sel=grid.getSelected();
		if(!sel) {
			if($.isFunction(nsMsg))
				return nsMsg();
			
			$.messager.show({title:C.defaultNullSelectTitle||"温馨提示",msg:nsMsg||C.defaultNullSelectMsg||"请选择需要操作的行"});
			return;
		}
		fn && fn(sel);
	}
	function createRowHandler(fn,nsMsg){return function(){return handleSelectedRow(fn,nsMsg)}}
	
</script>
<div id="viewport" class="easyui-layout" data-options="fit:true">
</div>
