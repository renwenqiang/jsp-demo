/**
 * 
 */
var properties={},activeProperty=null;
var getXEditor=function(t,v,fn){
	var win=getXEditor.win;
	if(win==null){
		var win=getXEditor.win=new sunz.Window({width:550,height:400,modal:true,content:'<div class="easyui-layout" data-options="fit:true"><div data-options="region:\'center\',border:false"><textarea id="xeditor" style="width:100%;height:100%;" title="右键试试"></textarea></div><div name="divButton" style="height:45px;padding:5px 15px;" data-options="region:\'south\',border:false"></div></div>'});
		var pele=$("[name=divButton]",win)[0];
		new sunz.Linkbutton({style:"margin:0 25px;float:right;",parent:pele,text:"取消",iconCls:"icon-cancel",handler:function(){win.close();}});
		new sunz.Linkbutton({parent:pele,style:"margin:0 15px;float:right;",text:"确定",iconCls:"icon-ok",handler:function(){if(win.callback(win.getValue())!==false)win.close();}});
		new sunz.Linkbutton({parent:pele,style:"margin:0 15px;float:right;",text:"紧凑",iconCls:"json-trim",handler:function(){win.setValue(trimJson(win.getValue()));}});
		new sunz.Linkbutton({parent:pele,style:"margin:0 15px;float:right;",text:"格式化",iconCls:"json-format",handler:function(){win.setValue(formatJson(win.getValue(),{removeNewline:true}));}});
		var txt=win.find("#xeditor").on("keydown",tabSupport)
		.on("contextmenu",function(e){
			e.preventDefault();
			if(!$("#editor-helper").length)
				$('<script id="editor-helper" type="text/javascript" src="webpage/framework/query/sqleditor.js"></script>').appendTo("head");
			var txt=this,start=txt.selectionStart,end=txt.selectionEnd,sel=txt.value.substring(start,end);
			new sunz.Menu()
			  .appendItem({text:"转为大写",id:"toUpper",onclick:function(){sqlEditor.toUpper(txt,sel,start,end)}})
			  .appendItem({text:"转为小写",id:"toLower",onclick:function(){sqlEditor.toLower(txt,sel,start,end)}})
			  .appendItem({text:"添加引号",id:"quot",onclick:function(){sqlEditor.quot(txt,sel,start,end)}})
			.show({left:e.clientX,top:e.clientY});
		});
		win.getValue=function(){return txt.val();};
		win.setValue=function(v){txt.val(v);};
	}
	win.setTitle(t);win.setValue(v);win.callback=fn;
	return win;
};
window.onComponentSelectionChanged.add(function(sels){
	lblSelected.innerHTML=sels.length==0?"未选择...":(
			sels.length==1?(sels[0].setting.define.name+sels[0].setting.id)
					:"已选择多个组件");
	if(activeProperty)
		activeProperty.hide();
	
	if(sels.length!=1)
		return;
	
	var ele=sels[0],define=ele.setting.define,xtype=define.xtype;
	if(!properties[xtype]){
		var f=properties[xtype]=new sunz.Form({parent:pProperty,fit:true,style:{width:"100%",height:"100%",position:"relative"}});
		var table=document.createElement("table");
		table.className="kv-table property";
		table.style.width="100%";
		$('<div class="pproperties" style="height:100%;padding:0 0 36px 0;overflow-y:scroll"></div').appendTo(f).append(table);
		//(define.properties||[]).concat([{xtype:"Textbox",label:"其它参数",name:"otherSetting",multiline:true,height:100}])
		if(define.isInput){
			define.properties=[commonProperties.name].concat(define.properties);
			define.properties.push({xtype:"Combobox",name:"required",label:"必填项",width:datas.comboDefWidth,data:datas.booldata,getValue:getBoolVaule});
			define.properties.push({xtype:"Combobox",name:"validType",label:"验证规则",width:datas.comboDefWidth,editable:true,
									formatter:function(r){return r.xtext;},textField:"value",
									data:[
									      {value:"length[0,10]",xtext:"长度（中括号内为起止长度，需手动改写）"},
									      {value:"url",xtext:"url"},
									      {value:"email",xtext:"邮件"},
									      {value:"json",xtext:"json格式"}
									      ]});
		}
		var normalProperties=[],propertyComponents=[];
		
		$.each([{xtype:"Textbox",name:"id",label:"id（唯一，谨慎修改）",required:true},
				{xtype:"Combobox",name:"modified",label:"将id作为此组件的全局变量名",data:[{value:1,text:"是（js中可用{id}直接引用此组件）"},{value:"",text:"否（js中使用componentManager.get({id})）"}],getValue:getBoolVaule,width:datas.comboDefWidth}
			   ].concat(define.properties||[],[{xtype:"Textbox",name:"otherSetting",label:"其它",multiline:true,height:100,validType:"json"}]),function(i,p){
			if(p.name && p.name!="otherSetting")normalProperties.push(p.name);
			var r=table.insertRow(),cell=r.insertCell();
			cell.className="kv-label";
			cell.innerHTML=(p.label||p.name)+":";
			cell=r.insertCell();cell.className="kv-content";
			var comp=new sunz[p.xtype]($.extend({parent:cell,attrs:{name:p.name},styles:{width:"95%",margin:"5px 2%"}},p,{label:null}));
			if(p.xtype=="Textbox" && !p.readonly){
				comp.textbox().on("dblclick",function(){getXEditor(p.label||p.name,comp.getValue(),function(v){comp.setValue(v);}).open();});
			}
			propertyComponents.push((comp.setting=p,comp));
		});
		f.normalProperties=normalProperties.concat(define.normalProperties||[]);
		
		cell=$('<div class="pbbar" style="position:absolute;bottom:0px;width:100%;background-color:#eee;padding:2px 5px;text-align:center"></div>').appendTo(f);
		new sunz.Linkbutton({parent:cell,iconCls:"icon-ok",text:"应用",style:"margin-left:5px",handler:function(){applySetting();}});
		new sunz.Linkbutton({parent:cell,iconCls:"icon-undo",text:"取消",style:"margin-left:5px",handler:function(){activeProperty.load(getSelectedComponent().setting);}});
		/*new sunz.Linkbutton({parent:cell,iconCls:"icon-reload",text:"恢复默认",style:"margin-left:5px",handler:function(){
			$.messager.confirm('请确认','部分组件有风险，请轻易不要使用此功能!<br/>您确定要恢复到默认值吗？',function(ok){
				if(ok){
					var setting=getSelectedComponent().setting;
					setting=$.extend({otherSetting:"{}"},setting.define.defaults,{define:setting.define,element:setting.element,xtype:setting.xtype,id:setting.id});
					activeProperty.reset();
					activeProperty.load(getSelectedComponent().setting=setting);
					applySetting();
				}
			});
		}});*/
		
		f.getFieldValues=function(){ // 重载，以支持自定义getValue--可解决数据类型及非直接输入的属性
			var o={};
			$.each(propertyComponents,function(i,comp){
				var s=comp.setting;
				var getValue=s.getValue||comp.getValue||comp.val;
				if(getValue){
					var n=s.name;
					var val=getValue.call(comp,o,n);
					if(n) o[n]=val;
				}
			});
			return o;
		}
	}
	activeProperty=properties[xtype];
	activeProperty.reset();
	var setting=ele.setting.otherSetting?ele.setting:toOtherSetting(ele.setting,activeProperty.normalProperties); /**/
	for(var n in setting){
		if($.isFunction(setting[n])){
			setting[n]=String(setting[n]);
		}
	}
	activeProperty.load(setting);
	lblComponentName.innerHTML="【"+define.name+ele.setting.id+"】属性";
	activeProperty.show();
});
var applySetting=function(){
	var selProxy=getSelectedComponent();
	if(!activeProperty ||selProxy==null)
		return ;
	
	if(!activeProperty.validate()){
		$.messager.show({title:"提示",msg:"数据较验未通过，某个或多个参数的值非法"});
		return false;
	}
	
	var nSetting=activeProperty.getFieldValues();	
	updateSetting(selProxy,nSetting);
};
window.onComponentDelete.add(function(){
	if(activeProperty)
		activeProperty.hide();
	activeProperty=null;
});
window.onBeforeComponentSelectionChange.add(applySetting);
window.onBeforeSave.add(applySetting);

window.onComponentSettingChanged.add(function(ele,setting){
	if(ele==getSelectedComponent() && activeProperty)
		activeProperty.load(setting);
});
