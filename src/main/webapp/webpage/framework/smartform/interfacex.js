/**
 * 设计时
 */
window.onDragComplete=$.Callbacks();
window.onBeforeComponentUpdate=$.Callbacks();
window.onComponentUpdate=$.Callbacks();
window.onComponentDelete=$.Callbacks();
window.onComponentSelectionChanged=$.Callbacks();
window.onBeforeComponentSelectionChange=$.Callbacks("stopOnFalse");
window.onComponentSettingChanged=$.Callbacks();
window.onDefineLoad=$.Callbacks();
window.onBeforeSave=$.Callbacks("stopOnFalse");

window.selectedComponents=[];
var realSelect=function(ele,add){
	if(!add){
		$.each(selectedComponents,function(i,c){$(c).removeClass("selected");});
		selectedComponents=[];
	}	
	if(ele){
		var idx=selectedComponents.indexOf(ele);	// 支持反向选择（双次点击取消）
		if(idx>-1){
			$(ele).removeClass("selected");
			selectedComponents.splice(idx,1)
		}else{
			$(ele).addClass("selected");
			selectedComponents.push(ele);
		}
	}
	onComponentSelectionChanged.fire(selectedComponents,ele);
}
function selectComponent(ele,add){
	if(getSelectedComponent()==ele && selectedComponents.length<=1) return;
	onBeforeComponentSelectionChange.remove(realSelect);
	onBeforeComponentSelectionChange.add(realSelect); // 保证在其它监听之后执行
	onBeforeComponentSelectionChange.fire(ele,add);
}
function getSelectedComponent(){
	return selectedComponents&&selectedComponents.length==1?selectedComponents[0]:null;
}
var defaultNormalize=function(def){
	var plist=def.define?def.define.getPropertyMap?def.define.getPropertyMap():smart.getPropertyMap.call(def.define):null;
	for(var o in def){
		if(def[o]===""||def[o]==null||(plist && !plist[o]))
			delete def[o];
	}
};

function parseElement(ele, pSetting,clear,renew) {
	pSetting=pSetting||{children:[]};
	var s=$.extend({},ele.setting);
	s.xtype=s.define.xtype;
	s.define.normalize?s.define.normalize(s):defaultNormalize(s);
	if(clear){
		delete s.define;
		delete s.element;
		//resetOtherSetting(s);
	}
	if(renew) s.id=getGuid();
	pSetting.children.push(s);
	var subs = $(ele).find(".proxy-select");
	if (subs.length > 0) {
		s.children = [];
		subs.each(function (i, sub) {
			if(ele.master==sub.parentMaster)
				parseElement(sub,s,clear,renew);
		});
	}
	return pSetting.children;
};

/*
 * 解析设计器
 */
function parseDesigner(clear){
	var all = {children : []};
	pMain.setting={define:{xtype:"viewport"}};
	parseElement(pMain, all,clear);	
	return all.children[0].children||[];
}
var toOtherSetting=function(setting,innerFields){
	innerFields=smart.normalProperties.concat(innerFields);
	var other=setting.otherSetting?$.decode(setting.otherSetting):{},temp=$.extend({},setting,other);
	delete temp.otherSetting; 
	for(var len=innerFields.length,k=0;k<len;k++){
		delete temp[innerFields[k]];
	}
	for(var n in temp){
		delete setting[n];
	}
	if(setting.otherSetting){ // 不能破坏现有值
		for(var n in other){
			delete temp[n];
		}
		setting.otherSetting=setting.otherSetting.substr(0,setting.otherSetting.length-1)+$.encode(temp).substr(1);
	}else{
		setting.otherSetting=$.encode(temp);
	}
	
	return setting;
};
var updateSetting=function(selProxy,nSetting){
	$.extend(selProxy.setting,nSetting);
	var newSetting=$.extend({},selProxy.setting,parseOtherSetting(nSetting.otherSetting));
	delete newSetting.element;
	delete newSetting.define;
	reviveFunctionInJson(newSetting);
	
	onBeforeComponentUpdate.fire(selProxy,newSetting);
	if(selProxy.setting.define.customUpdate)
		selProxy.setting.define.customUpdate(selProxy,newSetting);
	else{
		selProxy.setting.fit?$(selProxy).removeClass("autoSize").addClass("fit"):$(selProxy).addClass("autoSize").removeClass("fit");
		var xtype=selProxy.setting.define.xtype;
		selProxy.master["as"+xtype](newSetting);
	}
	onComponentUpdate.fire(selProxy,newSetting);
};
function deleteComponents(){
	if(window.selectedComponents){
		$.each(selectedComponents,function(i,c){
			if(!c) return;
			
			if(c.setting&&c.setting.define.customDelete)
				c.setting.define.customDelete(c);
			else 
				c.remove();
			onComponentDelete.fire(c);
		});
		onComponentsChanged.fire();
		selectComponent(null);
	}
}

// 设计时使用Mode=0
var changeMode=function(div){
	var comp=div.master;
	comp.setting.define=div.setting.define;
	var fn=getModeChangeHandler(comp);
	if(fn) fn.call(comp,false,0,{});
}
onComponentAdd.add(changeMode);
onComponentUpdate.add(changeMode);

// 设计时不真的隐藏
var interceptHide=function(p,s){
	interceptHide[s.id]=s.hidden;
	delete s.hidden;
},proxyHide=function(div){
	var h=interceptHide[div.setting.id];
	if(h){
		$(div).addClass("hidden");
	}else{
		$(div).removeClass("hidden");
	}
};
onBeforeComponentAdd.add(interceptHide);
onComponentAdd.add(proxyHide);
onBeforeComponentUpdate.add(interceptHide);
onComponentUpdate.add(proxyHide);

$.decode=function(o, fn){try{return o&&typeof o=="string"?eval("("+o+")"):o}catch(e){console.error(o,e)}}//function(o,fn){return JSON.parse(o,fn||jsonReviver);};
$.encode=(function(){ // json序列化实现--目的是解决function
	var map1={"string":'"',number:'',boolean:'',array:'[',"function":'',object:'{'},
	    map2={"string":'"',number:'',boolean:'',array:']',"function":'',object:'}'},
	    m={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r","\"":"\\\"","\\":"\\\\"},
	    reg=/[\\"\u0000-\u001f\u007f-\u009f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
	    quote=v=>{return v==null?null:v.replace(reg,k=>m[k]||("\\u"+("000"+k.charCodeAt(0).toString(16)).substr(-4)))};
	return function toJson(obj,f){
		var padBase="",
			indent=f?(padBase="\r\n",typeof f=="string"?f:(()=>{var s="";for(var i=0;i<f;i++){s+=" "} return s})()):"",
			getPad=level=>{var s=padBase;for(var i=0;i<level;i++)s+=indent;return s;};
		return (function _toJson(o,level){
			if(o===undefined) return "undefined";
			if(o==null)return "null";
			
			var str="",
				kv,
				type=typeof o;			
			if(type=="object"){
				if(o instanceof Date){
					type="date";
					kv=o.toString();
				}else{
					var arr=[];
					if(Array.isArray(o)){
						type="array";
						for(var i=0,len=o.length;i<len;i++)
							arr.push(_toJson(o[i],level+1));
					}else{
						for(var i in o){
							if(o[i]===undefined)continue; /**/
							arr.push('"'+quote(i)+'":'+_toJson(o[i],level+1));
						}
					}
					var pad=getPad(++level);
					kv=pad+arr.join(","+pad)+getPad(level-1);
				}
			}else{
				kv=o.toString();
				if(type=="string")
					kv=quote(kv);
			}
			return map1[type]+kv+map2[type];
		})(obj,0)
	}
})();