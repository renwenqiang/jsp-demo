if(!window.require)C.requireResources("requirejs");
commonProperties.fileacode={
	xtype:"Combogrid",name:"acode",label:"附件配置项（填写此值将可点击图片上传文件）",prompt:"先定义，再选择",width:datas.comboDefWidth,
	url:'framework/query.do?search&k=sys_attach_types&ext=true',
	delay:1000,mode:'local',pagination:false,fitColumns:true,panelWidth:960,panelHeight:400,
	idField:'code',textField:'name',
	columns:[[
			{field:'code',title:'编码',width:60},
			{field:'name',title:'名称',width:120},
			{field:'pathExpression',title:'保存路径',width:80},
			{field:'acceptTypes',title:'允许的文件类型',width:80},
			{field:'imageSizes',title:'生成缩略图',width:60}
			//,{field:'remark',title:'描述',width:160}
	]],required:true
};
commonProperties.filesize={name:"maxsize",xtype:"Numberbox",label:"上传最大尺寸（Byte）",value:C.defaultMaxImageSize||(5*datas.M),prompt:"双击使用计算器",events:{
	dblclick:function(){
		var jme=$(this).parent().prev().asNumberbox(), v=jme.getValue();
		var dlg=new sunz.Dialog({buttons:[{text:"　确定　",icon:"icon-ok",handler:function(){
			jme.setValue(txt.getValue()*cmb.getValue());
			dlg.close();
		}},{text:"　取消　",icon:"icon-cancel",handler:function(){dlg.close();}}],width:300,height:200,title:"计算器",style:"padding:25px 15px"});
		var txt=new sunz.Numberbox({parent:dlg,value:v/datas.M});
		var cmb=new sunz.Combobox({parent:dlg,width:80,data:[{text:"字节",value:1},{text:"K",value:datas.K},{text:"M",value:datas.M}],
			onChange:function(nv,ov){
    			txt.setValue(txt.getValue()*ov/nv);
    		},value:datas.M});
	}
},getValue:getNumberValue};
(function(){
	var origAdd=defines.Fileupload.customAdd,
		origUpdate=defines.Fileupload.customUpdate,
		mask=function(e){
			$(this).find("*").off(); //去掉事件
		};
	smart.extend("Fileupload",{
		category:"自定义扩展",order:1,
		properties:[
		            commonProperties.name,
		            commonProperties.widthX,
					commonProperties.heightX,
					commonProperties.margin,
		            {xtype:"Combobox",name:"type",label:"样式类型",width:datas.comboDefWidth,
		             data:[
		            	{text:"普通文件列表",value:"create"},
		            	{text:"纯图片",value:"createImage"},
		            	{text:"纯视频",value:"createVideo"}
		            ],required:true},	            
		            commonProperties.fileacode,
		            commonProperties.filesize,
		            {name:"maxcount",xtype:"Numberbox",label:"最大文件个数",getValue:getNumberValue},
		            {xtype:"Textbox",name:"btype",label:"业务类型（一个表单有多种附件时必填）"},
		            {xtype:"Combobox",name:"editable",label:"是否可编辑",value:true,width:datas.comboDefWidth,data:datas.booldata,getValue:getBoolVaule}
		],
		customAdd:function(pele,setting){
			smart.appendInnerResources("requirejs,smartformHelper"); 	// 运行时依赖
			var ele= origAdd.call(this,pele,setting);
			ele.masterLoaded.add(function(uploader){(uploader.changeMode=mask)()})
			return ele;
		},
		customUpdate:function(ele,setting){				
			smart.applyToElement(ele,setting||ele.setting);
			if(!setting.type){
				$(ele).html('<div style="color:red;font-size:24px;">尚未指定样式类型</div>');
				return;
			}
			origUpdate(ele,setting);
			ele.changeMode=mask;
		}
	});
})();