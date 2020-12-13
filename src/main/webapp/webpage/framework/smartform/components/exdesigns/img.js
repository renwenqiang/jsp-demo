(function(){
	smart.extend("Img",{
		category:"原生Html",order:5,
		properties:[
		            {xtype:"Textbox",name:"src",label:"图片路径",prompt:"有name时此项无效"},
					commonProperties.widthX,commonProperties.heightX,
					commonProperties.margin,
					$.extend({},commonProperties.fileacode,{required:false}), // 定义在fileupload中
			        commonProperties.filesize,
		            {xtype:"Textbox",name:"errorImage",label:"图片错误路径"},
		            {xtype:"Textbox",name:"uploadImage",label:"显示为上传功能时图片路径",prompt:"诸如+号或请上传之类的图片(未指定acode时此项无效)"}
		]
	});
})();