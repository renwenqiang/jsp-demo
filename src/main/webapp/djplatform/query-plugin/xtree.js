/**
 * 左侧组织树
 * xtreeTitle:string="党组织选择"
 * xtreeWidth:number=240
 * xtreeSearchField:string="partyid"
 * xtreeValueField:string="id"
 * xtreeConfig:{}=null
 * xtreeSqlkey:string="dj_departTree"
 * xtreeFormatter:function=return text;
 * idField,relationField,textField:string="id","pid","text"
 * 
 */
window.onXTreeSelect=$.Callbacks();
$(function(){
	var xSearchField=window.xtreeSearchField||"partyid";
	onBeforeSearch.add(function(o,formValues,urlValues){return !!urlValues[xSearchField];}) /*阻止无效加载*/
	var jb=window.viewportX=$('<div><div data-options="region:\'center\'"><div id="viewport" data-options="fit:true"></div></div>'
			+'<div data-options="region:\'west\',split:true,width:'+(window.xtreeWidth||300)+'"></div>'
			+'</div>').appendTo($(document.body).empty()).asLayout({fit:true});
	var _url="framework/query.do?search&total=-1&k="+(window.xtreeSqlkey||"p_loginPartyTree"),
		_idField=window.idField||"id",
		_pidField=window.relationField||"pid",
		_textField=window.textField||"text",
		_asyncField=window.asyncField||_pidField;
	var _loadFilter=function(jr,p){
		if(!jr.success){
			$.messager.show({title:"温馨提示",msg:(window.xtreeTitle||"组织树")+"加载出错："+jr.msg});
			return;			
		}
		var data=jr.data;
		if(!data || !data.length)return [];
		var idf=_idField,pf=_pidField,df=_textField;
		if((idf&&idf!="id")||(df&&df!="text")){
			var fnVd=function(item){
				if(idf){item.id=item[idf];}
				if(df){item.text=item[df];}
				$.each(item.children,function(i,sub){fnVd(sub);});
			};
			$.each(data,function(i,item){fnVd(item);});
		}
		if(pf && !p /*异步*/){
			var $d=D.createNew();
			$.each(data,function(i,d){
				$.extend($d.add(d.id,d.id,d.text,d[pf]),d);
			})
			return $d.get($d.ROOT);
		}
		
		return data;
	},_onSelect=function(node){
		xtree.selectedNode=node;
		urlParams[xSearchField]=node[window.xtreeValueField||"id"];
		window.onXTreeSelect.fire(node.id,node);
		search();
	};
	
	window.xtree=new sunz.Tree({
		parent:jb.panel("west").panel($.extend({
			title:window.xtreeTitle||"党组织选择"
		},window.xtreeConfig))[0],		
		fit:false,
		split:true,
		url:_url,
		loadFilter:_loadFilter,
		onClick:_onSelect,													//	广播事件并刷新列表
		onLoadSuccess:function(nil,datas){
			if(xtree.selectedNode)
				return;
			
			var root=xtree.getRoot();
			if(root){
				xtree.expand(root.target);
				_onSelect(root); 											//	选中第一个
			}
			
			if(datas.length==1 && (!root.children|| !root.children.length))	//	仅一个数据时删除（收起？）面版
				jb.remove("west");
		}
	})
});