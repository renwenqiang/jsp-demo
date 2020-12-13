/**
 * 
 */
(function(){
	var append=function(s,n,has,splitor){
		if(!s)return;
		var os=smartForm[n]||"";
		(has?!has(os,s):os.indexOf(s)<0) && (smartForm[n]=os+(splitor||"")+s);
	},appends=function(ss,n,has,splitor){
		$.each(ss.split(/\s*,\s*/),function(i,s){append(s,n,has,splitor)});
	},hasX=(os,s)=>{return os.split(/\s*,\s*/).indexOf(s)>-1};
	
	smart.appendResources=function(s){append(s,"RESOURCES",null,"")}
	smart.appendInnerResources=function(s){appends(s,"INNERRESOURCES",hasX,",")}
	smart.appendDicts=function(s){appends(s,"DICTS",hasX,",")}
	smart.appendConfigs=function(s){appends(s,"CONFIGS",hasX,",")}
	smart.appendOtherSetting=function(s){
		var os=smartForm.OTHERSETTING||"";
		if(os.indexOf(s)<0){
			var last=os.lastIndexOf("}"),pre=last<0?"{":os.substring(0,last);
			smartForm.OTHERSETTING=pre+(/(\{|,)\s*$/.test(pre)?"":",")+s+"}";
		}
	}
})();
var datas={
		K:1024,M:1<<20,
		comboDefWidth:	160,
		positions:		[{value:"",text:"默认"},{value:"absolute",text:"绝对定位"},{value:"fixed",text:"固定(fixed)"},{value:"static",text:"静态"},{value:"relative",text:"相对"}],
		booldata:		[{value:1,text:"是"},{value:"",text:"否"}],
		align:			[{value:"",text:"默认"},{value:"left",text:"左对齐"},{value:"center",text:"居中"},{value:"right",text:"右对齐"},{value:"justify",text:"justify"},{value:"char",text:"char"}],
		valign:			[{value:"",text:"默认"},{value:"top",text:"上对齐"},{value:"middle",text:"居中"},{value:"bottom",text:"底部对齐"},{value:"baseline",text:"baseline"}]
		,inputTypes:	[{value:"",text:"默认"},{value:"text",text:"文本输入"},{value:"button",text:"按钮"},{value:"password",text:"密码输入"}
			,{value:"file",text:"文件输入"},{value:"checkbox",text:"复选框"},{value:"radio",text:"单选框"},{value:"image",text:"图像提交"}
			,{value:"rese",text:"重置"},{value:"submit",text:"提交"},{value:"color",text:"颜色"},{value:"date",text:"日期（年月日)"}
			,{value:"datetime",text:"时间"},{value:"email",text:"email"},{value:"month",text:"月份"},{value:"number",text:"数字"}
			,{value:"search",text:"搜索框"},{value:"tel",text:"电话号码"},{value:"time",text:"时间"},{value:"url",text:"Url"}
		]
},getBoolVaule=function(){
	var v=this.getValue();return v=="1"||v=="true";
},getNumberValue=function(){return Number(this.getValue())};

var commonProperties={
		fit:		{xtype:"Combobox",name:"fit",label:"fit(填满)",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth},
		split:		{xtype:"Combobox",name:"split",label:"split(可分割)",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth},
		name:		{xtype:"Textbox",name:"name",label:"name"},
		color:		{xtype:"Textbox",name:"color",label:"颜色"},
		font:		{xtype:"Textbox",name:"font",label:"字体"},
		margin:		{xtype:"Textbox",name:"margin",label:"外间距"},
		padding:	{xtype:"Textbox",name:"padding",label:"内间距"},
		position:	{xtype:"Combobox",name:"position",label:"位置类型",data:datas.positions,width:datas.comboDefWidth},
	    width:		{xtype:"Numberbox",name:"width",label:"宽度",getValue:getNumberValue},
	    height:		{xtype:"Numberbox",name:"height",label:"高度",getValue:getNumberValue},
	    top:		{xtype:"Numberbox",name:"top",label:"上边距"},
	    left:		{xtype:"Numberbox",name:"left",label:"左边距"},
	    right:		{xtype:"Numberbox",name:"right",label:"右边距"},
	    bottom:		{xtype:"Numberbox",name:"bottom",label:"下边距"},	    
	    widthX:		{xtype:"Textbox",name:"width",label:"宽度"},
	    heightX:	{xtype:"Textbox",name:"height",label:"高度"},
	    topx:		{xtype:"Textbox",name:"top",label:"上边距"},
	    leftx:		{xtype:"Textbox",name:"left",label:"左边距"},
	    rightx:		{xtype:"Textbox",name:"right",label:"右边距"},
	    bottomx:	{xtype:"Textbox",name:"bottom",label:"下边距"},	 
	    inputType:	{xtype:"Combobox",name:"type",label:"类型",data:datas.inputTypes,width:datas.comboDefWidth},
	    title:		{xtype:"Textbox",name:"title",label:"标题"},
	    label:		{xtype:"Textbox",name:"label",label:"标题"},
	    labelWidth:	{xtype:"Numberbox",name:"labelWidth",label:"标题宽度",getValue:getNumberValue},
	    url:		{xtype:"Textbox",name:"url",label:"数据url"},
	    idField:	{xtype:"Textbox",name:"idField",label:"id字段"},
	    valueField:	{xtype:"Textbox",name:"valueField",label:"value字段"},
	    textField:	{xtype:"Textbox",name:"textField",label:"显示字段"},
	    icon:		{xtype:"Combogrid",name:"iconCls",label:"图标",editable:true,url:'framework/query.do?search&k=queryForIcon&rejectCss=0',fit:true,fitColumns:true,
	    	columns:[[
		  		{field:'path',title:'预览',width:64,align:"center",formatter:function(v){return '<img src="'+v+'"/>'}},
		  		{field:'iconClas',title:'css样式类名',width:80},
	            {field:'name',title:'名称或描述',width:120},
	            {field:'category',title:"分类",width:80,formatter:function(v){return D.getText(v);}},
	            {field:'extend',title:'格式',width:48}
            ]],
			idField:"iconClas",delay:1000,mode:'remote',pagination:true,
			panelWidth:600,panelHeight:400
		},
	  	cls:{xtype:"Textbox",name:"class",label:"css class"},
	  	dict:{width:180,xtype:"Combotree",editable:true,name:"dict",label:"字典项",prompt:"选择或填入字典编码",delay:1200,data:D.get("all",{text:"未设置",id:"",value:""}),
	  		onSelect:function(r){
	    	if(!r.id||r.children) return;
	    	$.messager.show({title:"友情提示",msg:"当前字典项不含有子项，请仔细确认！<br/>--除非情况特殊，请选择包含子项的节点。"});
	    }},
	    dictX:{xtype:"Textbox",name:"dict",label:"字典项"},
	    readonly:{xtype:"Combobox",name:"readonly",label:"只读",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth},
	    editable:{xtype:"Combobox",name:"editable",label:"可编辑",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth},
	    disabled:{xtype:"Combobox",name:"disabled",label:"禁用",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}
};

var p=commonProperties;
smart.extend("Form",{
	category:"容器",order:6,
	normalProperties:[],
	properties:[p.fit,p.widthX,p.heightX
		,{xtype:"Textbox",name:"url",label:"提交地址"}
		,p.name
		,{name:"novalidate",label:"禁用较验",xtype:"Combobox",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}
		,{name:"dirty",label:"仅提交变更字段",xtype:"Combobox",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}
		,{name:"ajax",label:"异步提交",xtype:"Combobox",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}
		,{name:"iframe",label:"iframe模式提交",xtype:"Combobox",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}
	]
});
smart.extend("Panel",{
	category:"容器",order:5,
	normalProperties:["content"],
	properties:[p.fit,p.width,p.height,p.title]
});
smart.extend("Layout",{
	category:"容器",order:1,
	componentParams:{items:[{region:"north",title:"上面版",split:true,height:160},{region:"center",title:"主面版"}]},
	menus:[
	       {xtype:"Menubutton",text:"新增块",iconCls:"icon-add",menu:(function(){
	    	   var m=new sunz.Menu();
	    	   m.appendItem({text:"（中）",iconCls:"addCenter",onclick:function(){addComponent(getSelectedComponent(),defines.Sublayout,{region:"center"});}});
	    	   m.appendItem({text:"（上）",iconCls:"addNorth",onclick:function(){addComponent(getSelectedComponent(),defines.Sublayout,{region:"north",split:true,height:160});}});
	    	   m.appendItem({text:"（左）",iconCls:"addWest",onclick:function(){addComponent(getSelectedComponent(),defines.Sublayout,{region:"west",split:true,width:160});}});
	    	   m.appendItem({text:"（右）",iconCls:"addEast",onclick:function(){addComponent(getSelectedComponent(),defines.Sublayout,{region:"east",split:true,width:160});}});
	    	   m.appendItem({text:"（下）",iconCls:"addSouth",onclick:function(){addComponent(getSelectedComponent(),defines.Sublayout,{region:"south",split:true,height:160});}});
	    	   return m;
	       })()},//{separator: true},
	       {xtype:"Menubutton",text:"删除块",iconCls:"icon-remove",menu:(function(){
	    	   var m=new sunz.Menu();
	    	   m.appendItem({text:"（中）",iconCls:"removeCenter",onclick:function(){getSelectedComponent().master.remove("center");}});
	    	   m.appendItem({text:"（上）",iconCls:"removeNorth",onclick:function(){getSelectedComponent().master.remove("north");}});
	    	   m.appendItem({text:"（左）",iconCls:"removeWest",onclick:function(){getSelectedComponent().master.remove("west");}});
	    	   m.appendItem({text:"（右）",iconCls:"removeEast",onclick:function(){getSelectedComponent().master.remove("east");}});
	    	   m.appendItem({text:"（下）",iconCls:"removeSouth",onclick:function(){getSelectedComponent().master.remove("south");}});
	    	   return m;
	       })()}
	],
	properties:[p.fit,p.width,p.height]		
});
smart.extend("Sublayout",{
	category:"内部子组件",
	draggable:false,
	menus:[],
	normalProperties:["region","content"],
	properties:[p.title,p.height,p.width],
	customUpdate:function(ele,setting){
		var s=setting||ele.setting;
		if(!s.content)
			s.content=null;
		ele.parentMaster.panel(s.region).panel(s);
	},
	customDelete:function(ele){
		ele.parentMaster.remove(ele.setting.region);
	}
});
smart.extend("Tabs",{
	category:"容器",order:3,
	componentParams:{items:[{title:"选项卡1"},{title:"选项卡2"}]},
	menus:[
	       {text:"添加选项卡",iconCls:"icon-add",onclick:function(){addComponent(getSelectedComponent(),defines.Subtab,{title:"新选项卡"+$.guid++});}}
	],
	properties:[p.fit,p.width,p.height
	]
});
smart.extend("Subtab",{
	category:"内部子组件",
	draggable:false,
	menus:[],
	normalProperties:["content"],
	properties:[p.title],
	customDelete:function(ele){
		ele.parentMaster.close(ele.setting.title);
	},
	customUpdate:function(ele,setting){
		var s=setting||ele.setting;
		if(!s.content)
			s.content=null;
		ele.parentMaster.update({tab:ele.master.tab,options:s});
	}
});
smart.extend("Accordion",{
	category:"容器",order:4,
	componentParams:{items:[{title:"面版1"},{title:"面版2"}]},
	menus:[{text:"添加子面版",iconCls:"icon-add",onclick:function(){addComponent(getSelectedComponent(),defines.Subaccordion,{title:"新面版"+$.guid++});}}],
	properties:[p.fit,p.width,p.height,
        		{xtype:"Combobox",name:"multiple",label:"允许同时展开多个面版",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}]
});
smart.extend("Subaccordion",{
	category:"内部子组件",
	draggable:false,
	menus:[],
	normalProperties:["content"],
	properties:[p.title],
	customDelete:function(ele){
		ele.parentMaster.remove(ele.setting.title);
	},
	customUpdate:function(ele,setting){
		var s=setting||ele.setting;
		if(!s.content)
			s.content=null;
		$(ele.master.item).panel(s);
	}
});
// *********  *********
smart.extend("Datagrid",{
	category:"easyui组件",order:8,
	properties:[{xtype:"Combobox",name:"isSmartlist",label:"是否smart列表",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth},
	            p.fit,p.width,p.height,p.title,
	            {xtype:"Textbox",label:"数据Url",name:"url"},
	            {xtype:"Combobox",name:"pagination",label:"是否分页",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}
	],
	customAdd:function(pele,params){
		params=$.extend({},params);
		if(params.isSmartlist){
			params.title=params.title||""+"（smart列表需要上下文环境，具体效果请使用预览）";
		}
		return defaultComponentAdd(pele,params);
	}
});

(()=>{
	var orgAdd=defines.Tree.customAdd;
	smart.extend("Tree",{
		category:"easyui组件",order:9,
		customAdd:function(pele,setting){
			var div=orgAdd(pele,setting);
			div.master.appendTo($('<div class="proxy-inner-container"></div>').appendTo($('<div style="position:relative"><div class="proxy-mask"></div></div>').appendTo(div)));
			return div;
		},
		properties:[p.fit,p.width,p.height//,p.title
					,{xtype:"Textbox",label:"数据Url",name:"url"}
					,{xtype:"Textbox",label:"id字段",name:"idField"}
					,{xtype:"Textbox",label:"关系字段(pid)",name:"relationField"}
					,{xtype:"Textbox",label:"显示字段",name:"displayField"}
		]
	})
})();

smart.extend("Textbox",{
	category:"easyui组件",order:1,
	properties:[
	    p.width,p.height,p.label,p.labelWidth,p.readonly,p.editable,p.disabled
	]
});
smart.extend("Numberbox",{
	category:"easyui组件",order:2,
	properties:[p.width,p.height,p.label,p.labelWidth,p.readonly,p.editable,p.disabled]
});
smart.extend("Numberspinner",{
	category:"easyui组件",order:2,
	properties:[
			    p.width,p.height,p.label,p.labelWidth,p.readonly,p.editable,p.disabled
			]
});
smart.extend("Calendar",{
	category:"easyui组件",order:5,
	properties:[
			    p.width,p.height,p.label,p.labelWidth,p.readonly,p.editable,p.disabled
			]
});
smart.extend("Datebox",{
	category:"easyui组件",order:4,
	properties:[
			    p.width,p.height,p.label,p.labelWidth,p.readonly,p.editable,p.disabled
			]
});
smart.extend("Datetimebox",{
	category:"easyui组件",order:4,
	properties:[
			    p.width,p.height,p.label,p.labelWidth,p.readonly,p.editable,p.disabled
			]
});
smart.extend("Datetimespinner",{
	category:"easyui组件",order:5,
	properties:[
			    p.width,p.height,p.label,p.labelWidth,p.readonly,p.editable,p.disabled
			]
});
smart.extend("Timespinner",{
	category:"easyui组件",order:4,
	properties:[
			    p.width,p.height,p.label,p.labelWidth,p.readonly,p.editable,p.disabled
			]
});
smart.extend("Dictcombo",{
	category:"easyui组件",order:6,
	normalize:function(s){if(s.dict)delete s.data;defaultNormalize(s);},
	properties:[
	    p.width,p.height,p.label,p.labelWidth
	    ,p.url,p.valueField,p.textField
	    ,getQueryParam("dictx")!=null||localStorage.getItem("dictx")?p.dictX:p.dict
	    ,{xtype:"Textbox",name:"black",label:"空选项文本（为空不显示）"}
	    ,p.readonly,p.editable,p.disabled
	]
});
smart.extend("Slider",{
	category:"easyui组件",order:20,
	properties:[
		    p.width,p.height,p.label,p.labelWidth
		    ,{xtype:"Numberbox",name:"max",label:"最大值",getValue:getNumberValue}
		    ,{xtype:"Numberbox",name:"min",label:"最小值",getValue:getNumberValue}
		    ,{xtype:"Numberbox",name:"step",label:"步进(每小格值)",getValue:getNumberValue}
		    ,p.readonly,p.editable,p.disabled
	]
});
smart.extend("Linkbutton",{
	category:"easyui组件",order:1,
	properties:[
		    p.width,p.height,{xtype:"Textbox",name:"text",label:"文本"}
		    ,p.labelWidth
		    ,p.icon
		    ,{xtype:"Textbox",multiline:true,height:64,prompt:"形如 function(){...}",name:"onClick",label:"click函数",validType:'json"function"',getValue:function(){return $.decode(this.getValue())}}
	]
});
smart.extend("Filebox",{
	category:"easyui组件",order:10,
	properties:[ p.width,p.height,p.label,p.labelWidth
	             ,{xtype:"Textbox",name:"buttonText",label:"按钮文字"}
	             ,{xtype:"Combobox",name:"buttonAlign",label:"按钮文字",data:[{value:"right",text:"右"},{value:"left",text:"左"}],width:datas.comboDefWidth}
	             ,{xtype:"Combobox",name:"accept",editable:true,label:"文件类型",prompt:"application/*",
	            	 data:[{value:"application/vnd.ms-excel",text:"Excel"},
	            	       {value:"application/msword",text:"Wold"},
	            	       {value:"application/pdf",text:"Pdf"},
	            	       {value:"application/zip",text:"压缩文件"},
	            	       {value:"image/*",text:"图片"},
	            	       {value:"text/*",text:"文本"}
	             ],width:datas.comboDefWidth}
	             ,p.icon
	             ,p.readonly,p.editable,p.disabled
	]
});
smart.extend("Combo",{
	category:"easyui组件",order:7,
	properties:[ p.width,p.height,p.label,p.labelWidth
	             ,p.url,p.readonly,p.editable,p.disabled
	]
});
smart.extend("Combotree",{
	category:"easyui组件",order:7,
	properties:[ p.width,p.height,p.label,p.labelWidth
	             ,p.url
	             ,{name:"checkbox",label:"多选框",xtype:"Combobox",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}
				 ,p.readonly,p.editable,p.disabled
	]
});
smart.extend("Combogrid",{
	category:"easyui组件",order:7,
	properties:[  p.width,p.height,p.label,p.labelWidth
	             ,p.url,p.idField,p.textField
	             ,p.readonly,p.editable,p.disabled
	]
});
smart.extend("Combotreegrid",{
	category:"easyui组件",order:7,
	properties:[p.width,p.height,p.label,p.labelWidth
		,p.url,p.idField,p.textField
		,p.readonly,p.editable,p.disabled
		]
});
smart.extend("Datalist",{
	category:"easyui组件",order:19,
	properties:[ p.fit,p.width,p.height,p.title,p.labelWidth
	             ,p.url,p.valueField,p.textField
	             ,{name:"checkbox",label:"多选框",xtype:"Combobox",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}
	]
});
smart.extend("Propertygrid",{
	category:"easyui组件",order:18,
	properties:[ p.fit,p.width,p.height,p.title,p.labelWidth
	             ,{name:"showGroup",label:"多选框",xtype:"Combobox",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}
				 ,{name:"groupField",label:"分组字段",xtype:"Textbox"}
	]
});
smart.extend("Treegrid",{
	category:"easyui组件",order:17,
	properties:[p.fit,p.width,p.height,p.title,p.labelWidth
	             ,p.url,p.idField,{label:"[树]字段",name:"treeField",xtype:"Textbox"},{label:"关系字段(pid)",name:"relationField",xtype:"Textbox"}
	]
});
/** html原生组件 **/
smart.extend("Table",{
	category:"容器",order:2,
	componentParams:{columns:2,rows:2},
	componentHtml:'<div title="在行、列数字上滚动鼠标滚轮可调整初始行、列数" class="component" style="background-image:url(webpage/framework/smartform/components/thumbs/table.png)">表格\
		<span onmousewheel="return defines.Table.componentParamHandle(this,\'columns\')" style="width:60px;height:32px;padding:5px 8px;color:red;">2</span>×\
		<span onmousewheel="return defines.Table.componentParamHandle(this,\'rows\')" style="width:60px;height:32px;padding:5px 8px;color:red;">2</span>\
		</div>',
	componentParamHandle:function(me,field){
		me.parentNode.componentParams=me.parentNode.componentParams||{};
		me.parentNode.componentParams[field]=me.innerHTML=Number(me.innerHTML)+(event.deltaY>0?-1:1);
	},	
	menus:[
	       {text:"添加行",iconCls:"addRow",onclick:function(){defines.Table.addRow(getSelectedComponent());}},
	       {text:"添加列",iconCls:"addCol",onclick:function(){defines.Table.addColumn(getSelectedComponent());}}
	],
	properties:[
			    p.fit,p.widthX,p.heightX
			    ,{xtype:"Textbox",name:"cellpadding",label:"单元格缩进"}
			    ,{xtype:"Textbox",name:"cellspacing",label:"单元格间距"}
			    ,{xtype:"Textbox",name:"border",label:"边框(border)"}
			    ,p.cls
			    //,{xtype:"Textbox",name:"frame",label:"外框(frame)"}
	]
});
smart.extend("Tablerow",{
	category:"内部子组件",draggable:false,
	menus:[
	       {text:"添加单元格",iconCls:"addCell",onclick:function(){addComponent(getSelectedComponent(),defines.Tablecell);}}
	       ,{text:"插入行（前）",iconCls:"insertRow",onclick:function(){var tr=getSelectedComponent().master, table=tr.parentMaster;defines.Table.addRow(table,$.extend({insertIndex:tr.rowIndex},defines.Tablerow.defaults));}}
	       ,{text:"插入行（后）",iconCls:"appendRow",onclick:function(){var tr=getSelectedComponent().master, table=tr.parentMaster;defines.Table.addRow(table,$.extend({insertIndex:tr.rowIndex+1},defines.Tablerow.defaults));}}
	],
	properties:[
				p.heightX,
	            {xtype:"Combobox",label:"水平对齐",name:"align",width:120,data:datas.align},
	            {xtype:"Combobox",label:"垂直对齐",name:"vertical-align",width:120,data:datas.valign}
	            ,p.cls
		]
});
smart.extend("Tablecell",{
	category:"内部子组件",draggable:false,
	menus:[
	       {text:"插入单元格",iconCls:"insertCell",onclick:function(){var td=getSelectedComponent().master, tr=td.parentMaster;addComponent(tr,defines.Tablecell,$.extend({insertIndex:td.cellIndex},defines.Tablecell.defaults));}}
	       ,{text:"插入单元格（后）",iconCls:"appendCell",onclick:function(){var td=getSelectedComponent().master, tr=td.parentMaster;addComponent(tr,defines.Tablecell,$.extend({insertIndex:td.cellIndex+1},defines.Tablecell.defaults));}}
	       ,{text:"插入行",iconCls:"addRow",onclick:function(){var td=getSelectedComponent().master,tr=td.parentMaster, table=tr.parentMaster;defines.Table.addRow(table,$.extend({insertIndex:tr.rowIndex},defines.Tablerow.defaults));}}
	       ,{text:"插入列",iconCls:"addCol",onclick:function(){var td=getSelectedComponent().master,tr=td.parentMaster, table=tr.parentMaster;defines.Table.addColumn(table,$.extend({insertIndex:td.cellIndex},defines.Tablecell.defaults));}}
	],
	properties:[
				p.widthX,p.heightX,
	            {xtype:"Numberbox",label:"跨行数",name:"rowSpan",value:1,getValue:getNumberValue},
	            {xtype:"Numberbox",label:"跨列数",name:"colSpan",value:1,getValue:getNumberValue},
	            {xtype:"Combobox",label:"水平对齐",name:"text-align",width:120,data:datas.align},
	            {xtype:"Combobox",label:"垂直对齐",name:"vertical-align",width:120,data:datas.valign}
	            ,p.cls
	]
});
smart.extend("Label",{
	category:"原生Html",order:2,
	properties:[
            {xtype:"Textbox",name:"innerHTML",label:"文本"},
				p.widthX,p.heightX,p.color,
				p.font,p.position,p.margin,p.padding
				,p.cls
	]
});
smart.extend("Div",{
	category:"原生Html",order:1,
	properties:[
				p.widthX,p.heightX,p.color,p.font,
				p.position,p.topx,p.rightx,p.leftx,p.bottomx,
				p.margin,p.padding
				,p.cls
	]
});
smart.extend("Input",{
	category:"原生Html",order:3,
	properties:[
	            p.inputType,{xtype:"Textbox",name:"value",label:"文本"},
				p.widthX,p.heightX,p.color,
				p.font,p.margin
				,p.cls,p.readonly,p.disabled
	]
});

// 页面
smart.extend("Smartform",{
	category:"子表单",order:1,
	properties:[{label:"关联表",name:"smartcode",xtype:"Combogrid",mode:"remote",width:datas.comboDefWidth,url:"framework/query.do?search&k=queryForSmartform",idField:"code",textField:"name",panelWidth:960,panelHeight:400,fitColumns:true,pagination:true,columns:[[{field:"id",hidden:true},{field:"code",title:"编码",width:120},{field:"name",title:"描述",width:240}]]},
	            {label:"外键字段",name:"relationField",xtype:"Textbox",width:datas.comboDefWidth},
				{label:"目标类型",name:"part",xtype:"Combobox",data:[{value:"form",text:"表单"},{value:"list",text:"列表"}],width:datas.comboDefWidth}
	            ,{label:"新增按钮",name:"addable",xtype:"Combobox",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}
	            ,{label:"修改按钮",name:"editable",xtype:"Combobox",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}
	            ,{label:"删除按钮",name:"deleteable",xtype:"Combobox",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}
	            ,{label:"查看按钮",name:"viewable",xtype:"Combobox",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}
	            ,{label:"编辑日志记录",name:"logRequired",xtype:"Combobox",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}
	            ,{label:"台账查看",name:"historyable",xtype:"Combobox",data:datas.booldata,getValue:getBoolVaule,width:datas.comboDefWidth}
	            ,p.fit,p.widthX,p.heightX
	],
	customAdd:function(pele,params){
		var jele=$('<div style="position:relative;margin:0;padding:0;"></div>'), ele=jele[0];
		pele.insertPoint?pele.insertBefore(ele,pele.insertPoint):pele.appendChild(ele);//pele.appendChild(ele);
		defines.Smartform.customUpdate(ele,params);
		jele.addClass(smart.selectProxyClass);
		ele.master=ele;
		//jele.html('<div style="color:red;width:50%;height:50%;margin:25%;font-size:24px;">一对多关联表单，真实效果请保存后使用预览（通常需要挂载数据）</div>');
		
		return ele;
	},customUpdate:function(ele,setting){
		var s=setting||ele.setting;
		smart.applyToElement(ele,s);
		if(!s.smartcode){
			$(ele).html('<div style="color:red;width:50%;height:50%;margin:25%;font-size:24px;">Smart表单:尚未关联表单，请进行关联</div>');
		}else{
			if(ele.smartcode!=s.smartcode||ele.part!=s.part){
				$(ele).html(tplReplace('<div style="position:absolute;width:100%;height:100%;"></div><iframe width="100%" height="100%" src="framework/smartform.do?parse&smartid={smartid}&smartcode={smartcode}&smartpart={part}"></iframe>',s));
				ele.smartid=s.smartid;ele.part=s.part;	
			}
		}
	}
});