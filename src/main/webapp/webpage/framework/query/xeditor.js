/**
 * 
 */
var getXEditor=function(t,v,fn,def,validType,doc){
	var win=getXEditor.win;
	if(win==null){
		var win=getXEditor.win=new sunz.Window({width:640,height:480,modal:true,content:'<div class="easyui-layout" data-options="fit:true"><div data-options="region:\'center\',border:false"><textarea class="xeditor" style="width:100%;height:100%;" ></textarea></div><div name="divButton" style="height:45px;padding:5px 15px;" data-options="region:\'south\',border:false"></div></div>'});
		var pele=$("[name=divButton]",win)[0];
		new sunz.Linkbutton({style:"margin:0 25px;float:right;",parent:pele,text:"取消",iconCls:"icon-cancel",handler:function(){win.close();}});
		new sunz.Linkbutton({parent:pele,style:"margin:0 15px;float:right;",text:"确定",iconCls:"icon-ok",handler:function(){
			if(win.callback(win.getValue())!==false)win.close();
		}});
		var btnTrim=new sunz.Linkbutton({parent:pele,style:"margin:0 15px;float:right;",text:"紧凑",iconCls:"json-trim",handler:function(){win.setValue(trimJson(win.getValue()));}});
		var btnFormat=new sunz.Linkbutton({parent:pele,style:"margin:0 15px;float:right;",text:"格式化",iconCls:"json-format",handler:function(){win.setValue(formatJson(win.getValue(),{removeNewline:true}));}});
		new sunz.Linkbutton({parent:pele,style:"margin:0 15px;float:right;",text:"说明",iconCls:"icon-help",handler:function(){
			var w=top.sunz.Window({width:800,height:600,modal:true,title:"参数填写说明"});
			w.html('<pre style="white-space:pre;font-size:16px;line-height:28px;padding:10px">'+win.doc+'</pre>');
		}});
		var txt=win.find(".xeditor").on("keydown",tabSupport);
		win.getValue=function(){return txt.val();};
		win.setValue=function(v){txt.val(v);};
		win.setDoc=function(doc){txt.attr("placeholder",doc);}
		win.setValidType=function(vt){
			var isJ=vt=="json";
			btnTrim[isJ?"show":"hide"]();
			btnFormat[isJ?"show":"hide"]();
			txt.validatebox({validType:win.validType=vt});
		}
	}
	win.setTitle(t);win.setValue(v||def);win.callback=fn;
	win.setDoc(win.doc=doc);
	win.setValidType(validType);
	return win;
};