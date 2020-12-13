/**
 * 代码兼容（老代码中大量使用lhgdialog来弹窗并提供几个常用方法）
 */
(function(){
	// 支持百分比
	var parseSize=function(num,type){
		return typeof num =="number"?num:/\d+%/.test(num)?eval("("+num.replace("%","*$(window)."+type+"()/100")+")"):Number(num);
	},fit="100%",defaultWidth=C.defaultWindowWidth||"80%",defaultHeight=C.defaultWindowHeight||"80%"
	,validateSize=function(c){
		!c.width && (c.width=defaultWidth);  c.width>helper.maxWidth && (c.width=fit);
		!c.height && (c.height=defaultHeight); c.height>helper.maxHeight && (c.height=fit);
	},helper={parseWidth:function(w){return parseSize(w,"width")},parseHeight:function(h){return parseSize(h,"height")}}
	Object.defineProperty(helper,"maxWidth",{get:function(){return this.parseWidth(fit)}});
	Object.defineProperty(helper,"maxHeight",{get:function(){return this.parseHeight(fit)}});
	
	var autoDestroy=function(){$(this).window("destroy")}
	window.openwindow=top.openwindow||function(title, url,name, width, height,config){
		/**
		 * 在顶层打开一个窗口
		 *    1.可以省略name（即name为数字），其它参数相应前移
		 *    2.title可直接用于config
		 */
		if(typeof title =="string"){
			if(typeof name =="number") {
				config=height;
				height=width;
				width=name;
			}
			config=$.extend({modal:true,title:title,width:width,height:height,onClose:autoDestroy},config);
			validateSize(config);
			config.content='<iframe src="'+url+'" style="width:100%;height:100%" ></iframe>';	
		}else{
			config=title;
		}
		return new sunz.Window(config);
	};
	window.createwindow=top.createwindow||function(t, url,w,h,fnOk,fnCancel,okText,cancelText,okWidth,cancelWidth) {
		/**
		 * 打开窗口的时候提供“确定”和“取消”按钮
		 */
		var okid="btnOK",cancelid="btnCancel";
		var html='<div class="easyui-layout" fit="true">\
			<div region="center" border="false" style="overflow:hidden">\
				<iframe src="'+url+'" style="width:100%;height:100%" ></iframe>\
			</div>\
			<div region="south" border="false" style="height:42px;"><div class="d-win-toolbar" style="margin:5px 0 0;text-align:center;">\
				<a style="width:'+(okWidth||88)+'px" class="'+ okid +'"></a>\
				<a style="margin-left:20px;width:'+(cancelWidth||88)+'px" class="'+ cancelid +'"></a>\
			</div></div>\
		</div>';
		var c={modal:true,title:t,content:html,width:w,height:h,onClose:autoDestroy};
		validateSize(c);
		var jele=top.$('<div class="d-win"></div>');
		jele.window(c);
		
		var fun=function(fn){var frmEle=jele.find("iframe")[0];frmEle.api={opener:window};var iframe=frmEle.contentWindow;try{iframe.iframe=iframe;iframe.$=iframe.$||{};iframe.$.messager=top.$.messager;}catch(exp){}var r=fn?fn(iframe,function(){jele.window("close")}):undefined;if(r!==false)jele.window("close");};
		var btnOk=jele.btnOk=jele.find("."+okid);
		btnOk.linkbutton({text:okText||"确定",iconCls:"icon-ok"});
		btnOk.bind("click",function(){fun(fnOk||function(frm,fn){if(frm.save){frm.save(fn);return false}else if(frm.saveObj)return frm.saveObj(fn);});});
		btnCancel=jele.btnCancel=jele.find("."+cancelid);
		btnCancel.linkbutton({text:cancelText||"取消",iconCls:"icon-cancel"});
		btnCancel.bind("click",function(){fun(fnCancel);});
		
		return jele;
	}

})()