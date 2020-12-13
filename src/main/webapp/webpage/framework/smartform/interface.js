/**
 * 
 */
window.defines={};
window.componentManager={get:function(id){return this[id];}};
window.onBeforeComponentAdd=$.Callbacks();
window.onComponentAdd=$.Callbacks();
window.onComponentsChanged=$.Callbacks();
window.smart={
		defines:window.defines,
		selectProxyClass:"proxy-select",
		containerProxyClass:"proxy-container",
		containerProxyTpl:'<div proxyid="{id}" class="proxy-container"></div>',
		normalProperties:["define","element","id","modified","xtype","name","required","validType"],
		getPropertyMap:function(){if(!this.propertyMap){var def=this,l=def.propertyMap={otherSetting:1};$.each(smart.normalProperties.concat(def.normalProperties).concat($.map(def.properties,function(o){return o.name;})),function(i,n){l[n]=1;});} return this.propertyMap;},
		define:function(name,def){def.xtype=name;def.getPropertyMap=smart.getPropertyMap;return window.defines[name]=def;},
		extend:function(name,ext){return $.extend(true,window.defines[name],ext);}
};

var jsonReviver=function(k,v){return (typeof v==="string" && v.substr(0,8)=="function" && v.substr(-1)=="}")?$.decode(v):v;},
	reviveFunctionInJson=function(jo){for(var k in jo){var v=jo[k];if(typeof v==="object")reviveFunctionInJson(v);else jo[k]=jsonReviver(k,v);}};

$.decode=getQueryParam("debug")==null?function(o,fn){return o&&typeof o=="string"?eval("("+o+")"):o}:function(o,fn){try{return o&&typeof o=="string"?eval("("+o+")"):o}catch(e){console.error(o,e)}};
parseOtherSetting=function(o){return o?$.decode(o):null};

function getGuid(){
	if(getGuid.guid==null){
		var d=new Date();
		getGuid.guid=(d.getFullYear()-2000)*Math.pow(10,13)
					+(d.getMonth()+1)*Math.pow(10,11)
					+d.getDate()*Math.pow(10,9)
					+d.getHours()*Math.pow(10,7)
					+d.getMinutes()*Math.pow(10,5)
					+d.getSeconds()*Math.pow(10,3);
	}
	return ++getGuid.guid;
};

function addComponent(pele,define,params,silent){
	var setting=$.extend({},params||define.defaults),div;
	if(setting.otherSetting){
		$.extend(setting,parseOtherSetting(setting.otherSetting));
	}
	if(define.isInput&&setting.name!=null){
		setting.attrs=setting.attrs||{};
		setting.attrs.name=setting.name;
	}
	setting.id=setting.id||getGuid();
	setting.define=define;
	
	onBeforeComponentAdd.fire(pele,setting);
	div=define.customAdd?define.customAdd(pele,setting):defaultComponentAdd(pele,setting);
	componentManager[setting.id]=div.master;
	if(setting.modified)window[setting.id]=div.master;
	div.master.proxy=div;
	div.master.setting=setting;
	setting.element=div;
	div.parentMaster=pele.master;
	div.setting=setting;
	div.master.hide=function(){$(div).hide();};
	div.master.show=function(){$(div).show();};
	
	if(define.init){
		define.init(div.master,setting);
	}
	$(div).find(".proxy-container").each(function(){
		this.master=div.master;
		this.master.proxyContainer=div.proxyContainer=this;// 如有多个代理容器，此处得改变逻辑
		this.setting=div.setting;
	});
	
	delete setting.parent;
	onComponentAdd.fire(div,silent);
	if(!silent){
		onComponentsChanged.fire();
		selectComponent(div);
	}
	
	return div;
}

var defaultComponentAdd=function(pele,setting,fnConstructor){
	var divHtml='<div class="proxy-select'+ (setting.fit?' fit"':'') +'"></div>'
	div=$(divHtml)[0];
	pele.insertPoint?pele.insertBefore(div,pele.insertPoint):pele.appendChild(div);
	
	setting.parent=div;
	div.master=fnConstructor?fnConstructor(setting):new sunz[setting.define.xtype](setting);
	return div;
};

function parseComponent(pele,comp,silent){
	var config=$.extend({},comp);
	delete config.children;
	/* define的获取，如果扩展特别多影响到加载性能了，要考虑从exdefines延迟加载 */
	var define=defines[config.xtype]||defines[config.xtype.substr(0,1).toUpperCase()+config.xtype.substr(1)];
	if(define==null){
		var js="webpage/framework/smartform/components/exdefines/"+config.xtype.substr(0,1).toLowerCase()+config.xtype.substr(1)+".js";
		$.ajax(js,{async:false,dataType:"script",cache:true,
			success:function(){
				define=defines[config.xtype]||defines[config.xtype.substr(0,1).toUpperCase()+config.xtype.substr(1)];
			},
			err:function(jr){
				$.alert("友情提示","内部错误，使用了不可识别的组件");
			}
		});
	}
	var proxy=addComponent(pele,define,config,silent);
	var container=$(proxy).find(".proxy-container")[0]||proxy;//$(".proxy-container[proxyid="+config.id+"]")[0]||proxy;
	return container;
}

function parsePart(formDesigns,root){
	if(!formDesigns || !root) return;
	var parseItem=function(pele,item){
		//if(item.hidden==true)return;
		var c=parseComponent(pele,item,true);
		$.each(item.children,function(i,sub){
			parseItem(c,sub);
		});
	};
	root.insertPoint=null;
	$.each(formDesigns,function(i,item){parseItem(root,item)});	
	onComponentsChanged.fire();
}

function defaultModeChangeHandler(b){ // 默认smartMode处理
	if(this.readonly)this.readonly(!b);
	else if(this.enable && b) this.enable();
	else if(this.disable && !b) this.disable();
	else this.readOnly=!b;
}
function getModeChangeHandler(comp){
	return comp.changeMode||(comp.changeMode=comp.setting.changeMode||comp.setting.define.changeMode||defaultModeChangeHandler);
}