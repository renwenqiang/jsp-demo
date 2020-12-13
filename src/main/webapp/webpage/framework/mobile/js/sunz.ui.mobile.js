/**
 * 移动端组件
 * 	1.所有组件支持parent参数
 * 	2.输入类的组件通用参数：label,labelWidth,inline,name，都有set、getValue方法
 * 	3.list类的主要参数：data：[]|url，template:function|string|dom
 * 	4.组件与easyui参数命名类似，[支持的参数]几乎可直接搬用
 */
if (typeof sunz == "undefined") {
	window.sunz = {};
}
if (!sunz.util) {
	sunz.util = {};
}
sunz.applyToElement = (
	globalAttrs = ["id", "name", "tabindex", "title", "class", "accesskey", "contenteditable", "readonly", "disabled", "action", "onclick","multiple","accept","placeholder"],/*来源于W3CSchool*/
	globalStyles = ["width", "height", "top", "left", "right", "bottom", "position", "float", "margin*", "padding*", "color", "font*", "z-index", "text-align", "display", "align", "valign", "background*","min-height"],
	globalFields = ["innerHTML", "innerText", "textContent", "value", 'checked'],
	function (ele, setting, extAttrs, extStyles) {
		if (!ele || !setting) return;
		if (setting.fit) { setting.width = "100%"; setting.height = "100%"; }
		if (setting.hidden) { setting.display = "none"; }
		var attrs = globalAttrs.concat(extAttrs), styles = globalStyles.concat(extStyles);
		var fnV = function (tps) {
			var r = {};
			$.each(tps, function (i, tp) {
				if (!tp) return;
				if (tp.indexOf("*") > -0) {
					for (var o in setting) {
						if (new RegExp(tp).test(o))
							r[o] = setting[o];
					}
				} else {
					var v = setting[tp];
					if (v != null)
						r[tp] = v;
				}
			});
			return r;
		};
		var oAttr = fnV(attrs), oStyle = fnV(styles), oFields = fnV(globalFields);
		for (var f in oFields) { // 直接属性字段（property）
			ele[f] = oFields[f];
		}
		for (var n in setting) {// 事件
			var v = setting[n];
			if ($.isFunction(v))
				ele[n] = v;
		}
		// Attributes和styles
		sunz.applyAttributes(ele, {
			attrs: $.extend(oAttr, setting.attrs || setting.attributes),
			style: $.extend(oStyle, sunz.parseStyle(setting.style))
		}
		);

	});
sunz.applyConfig = function (cfg, fn) {
	cfg = sunz.parseStyle(cfg);
	$.each(cfg, function (n, v) {
		fn(n, v);
	});
};
sunz.parseStyle = function (style) {
	if ("string" == typeof style) {
		var attrs = style.split(";");
		style = {};
		$.each(attrs, function (i, o) {
			if (!o) return;
			var attr = o.split(":");
			style[attr[0]] = attr[1];
		});
	}
	return style;
};
sunz.applyAttributes = function (ele, opt) {
	// 支持style ,attrs/attributes
	if (opt.style) sunz.applyConfig(opt.style, function (n, v) { ele.style[n] = v; });
	if (opt.attrs || opt.attributes) sunz.applyConfig(opt.attrs || opt.attributes, function (n, v) { if (n && n.toLowerCase() == "class") $(ele).addClass(v); else $(ele).attr(n, v); });
};
/**
 * 注册dom事件监听
 * @param ele
 * @param lisobj
 */
sunz.registListener1 = function(ele, lisobj){
	if(ele&&lisobj){
		for(var lisname in lisobj){
			$(ele).bind(lisname, lisobj[lisname]);
		}
	}else{
		return;
	}
}

window.tplReplace = sunz.tplReplace =window.tplReplace||function (s, o ,f) {
    /*
    模板替换，如 tplReplace("abc{test}def",{test:123}),将返回abc123def，
    注意，大小写敏感
    */
    s = s || '';f=f!=true; return s.replace(/\{([^{}]+)\}/g, typeof o === 'string' ? o : function (m, k) { var v=o[k]; return v == null?"":(f && typeof v=="string"?v.replace(/</g,"&lt;"):v); });
};
/**
 * 加载css文件
 * @param url
 */
sunz.loadCSS = function (url) {
    var link = document.createElement("link");
    link.type = "text/css";
    link.rel = "stylesheet";
    link.href = url;
    document.getElementsByTagName("head")[0].appendChild(link);
};
/**
 * 加载js文件
 * @param url
 */
sunz.loadScript = function (url) {
    var script = document.createElement("script");
    script.type = "text/javascript"; 
    script.src = url;
    document.getElementsByTagName("head")[0].appendChild(script);
}


/**
 * 标记验证未通过
 */
sunz.markInvalid = function (ele, tip) {
	$(ele).addClass("has-error");
	$(ele).append("<span class='sunzmui-alertbtn'></span>");
	$(ele).find(".sunzmui-alertbtn").bind("click",function(){
		sunz.createAlert(ele,tip);
		if(ele.setTimeoutfunc){
			clearTimeout(ele.setTimeoutfunc);
			ele.setTimeoutfunc = null;
		}
		ele.setTimeoutfunc = setTimeout(function(){
			sunz.removeAlert(ele);
		}, 2000);
	});
	//sunz.createAlert(ele,tip);
	
};
/**
 * 取消标记验证未通过
 */
sunz.clearInvalid = function (ele) {
	$(ele).removeClass("has-error");
	sunz.removeAlert(ele);
};
sunz.removeAlert = function(pele){
	$(pele).find(".alert-danger").remove();
}

sunz.createAlert = function(pele, tip){
	sunz.removeAlert(pele);
	$(pele).append('<div  class="alert alert-danger"><a href="#" class="close" data-dismiss="alert">&times;</a>'+(tip?tip:"验证错误")+'</div>');
}


//对Date的扩展，将 Date 转化为指定格式的String
//月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
//年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
//例子： 
//(new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
//(new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
Date.prototype.format = function (fmt) { //author: meizz 
 var o = {
     "M+": this.getMonth() + 1, //月份 
     "d+": this.getDate(), //日 
     "h+": this.getHours(), //小时 
     "m+": this.getMinutes(), //分 
     "s+": this.getSeconds(), //秒 
     "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
     "S": this.getMilliseconds() //毫秒 
 };
 if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
 for (var k in o)
 if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
 return fmt;
};
Date.prototype.toString=function(){return this.format("yyyy-MM-dd hh:mm:ss");};





sunz.require = function(url, callback){
	if(require&&require.config&&require.config.baseUrl){
		url = require.config.baseUrl + "/" + url
	}
	require([url], callback);
};
sunz.showLoading = function(a,b,c){
	var text = a;
	var time = 0;
	var type = "";
	if(typeof(b)=="string"){
		type = b;
		time = c;
	}else{
		time = b;
	}
	if(sunz.showLoading.hideTimeOut){
		console.log(111)
		clearTimeout(sunz.showLoading.hideTimeOut);
		sunz.showLoading.hideTimeOut = null;
		sunz.isloadingshowing = false;
	}
	text = text?text:""; 
	var html = '<div class="sunzmui-tip-type sunzmui-tip'+type+'"></div>'+(text||'...')+'</div>';
	if(type){
		html = '<div class="sunzmui-tip-type sunzmui-tip'+type+'"></div>'+(text||'...')+'</div>';
	}else{
		html = text;
	}
	if(sunz.isloadingshowing){
		 
		$(".sunzmui-loading").html(html);
	}else{
		$("body").append('<div class="sunzmui-tipcontainer"><div class="sunzmui-loading">'+ html +"</div></div>");
		sunz.isloadingshowing = true;
	}
	
	if(time>=0){
		setTimeout(function(){
			sunz.hideLoading(500);
		},time);
	} 
};
sunz.hideLoading = function(time){ 
	time?time:0;
	$(".sunzmui-loading").fadeOut(time);
	sunz.showLoading.hideTimeOut = setTimeout(function(){
		$(".sunzmui-loading").remove();
		sunz.isloadingshowing = false;
		clearTimeout(sunz.showLoading.hideTimeOut);
		sunz.showLoading.hideTimeOut = null;
		},time) ;
};
sunz.tip = sunz.showLoading;
sunz.hidetip = sunz.hideLoading;
$.messager = $.messager ||{};
$.messager.tip = sunz.tip;
$.messager.hideTip = sunz.hidetip;
$.tip = sunz.tip;
$.hideTip = sunz.hidetip;
$(document).ajaxSend(function(event, jqxhr, settings) {
	//sunz.showLoading("正在加载……");
});
$(document).ajaxComplete(function(event,request, settings) { 
	//sunz.hideLoading();
});  