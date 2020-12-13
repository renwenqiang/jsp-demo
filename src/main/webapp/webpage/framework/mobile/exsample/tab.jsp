<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!Doctype html>
<html  >
<head>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,sunzmobile,requirejs"></z:resource>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,
initial-scale=1.0,
maximum-scale=1.0,
user-scalable=no">

    <title>tab</title>
  	<style type="text/css" rel="stylesheet">
		html,body{
		width:100%;
		height:100%;
		margin:0px;
		padding:0px; 
		}
		
	</style>
</head>

<body  style="  overflow-x: hidden;">
  

 
</body>
   <script> 
   require.config({
	    //开发专用，阻止浏览器缓存
	    //urlArgs: "v=" + Date.now(),
	    //js文件的目录，相对于引入main.js的那个文件的目录
	    baseUrl: "webpage/framework/mobile/page",
	    shim: {
	        'name': {
	            deps: ['dep'],
	            exports: 'exports'
	        }
	    },
	    //模块的加载路径（不要加.js后缀，因为默认就是加载js，加了会报错）
	    //路径是相对于上面的baseUrl
	    paths: {
	         "default":"default"
	    }
	});
   var data = [
         		      {title:"党建地图",iconCls:"dang1",activeCls:"dang",ele:$("<li>sssssssssssssssssssss</li>")}, 
           		      {title:"会议签到",iconCls:"qiandao1",activeCls:"qiandao"}, 
           		      {title:"服务方块",iconCls:"fuwu1",activeCls:"fuwu"}, 
           		      {title:"个人中心",iconCls:"geren1",activeCls:"geren"}
           		      ];
   var data = [{
	    title: "党建地图",
	    imgsrc:"webpage/framework/mobile/image/dang1.png",
	    activeimgsrc:"webpage/framework/mobile/image/dang.png",ele:$('<div style="background-color:red;height:100%"></div>')
	}, {
	    title: "会议签到",
	    imgsrc:"webpage/framework/mobile/image/qiandao1.png",
	    activeimgsrc:"webpage/framework/mobile/image/qiandao.png",
	    ele:$('<div style="background-color:#aaeecc;height:100%"></div>')
	}, {
	    title: "服务方块",
	    imgsrc:"webpage/framework/mobile/image/fuwu1.png",
	    activeimgsrc:"webpage/framework/mobile/image/fuwu.png",
	    ele:$('<div style="background-color:#ffaabb;height:100%"></div>')
	}, {
	    title: "个人中心",
	    imgsrc:"webpage/framework/mobile/image/geren1.png",
	    activeimgsrc:"webpage/framework/mobile/image/geren.png",
	    ele:$('<div style="background-color:#aabbcc;height:100%"></div>')
	}, {
	    title: "会议签到",
	    imgsrc:"webpage/framework/mobile/image/qiandao1.png",
	    activeimgsrc:"webpage/framework/mobile/image/qiandao.png",
	    ele:$('<div style="background-color:#aaeecc;height:100%"></div>')
	}, {
	    title: "服务方块",
	    imgsrc:"webpage/framework/mobile/image/fuwu1.png",
	    activeimgsrc:"webpage/framework/mobile/image/fuwu.png",
	    ele:$('<div style="background-color:#ffaabb;height:100%"></div>')
	}, {
	    title: "个人中心",
	    imgsrc:"webpage/framework/mobile/image/geren1.png",
	    activeimgsrc:"webpage/framework/mobile/image/geren.png",
	    ele:$('<div style="background-color:#aabbcc;height:100%"></div>')
	}];
   var data1= [{
	    title: "党建地图",
	    imgsrc:"webpage/framework/mobile/image/dang1.png",
	    activeimgsrc:"webpage/framework/mobile/image/dang.png",ele:$('<div style="background-color:red;height:100%"></div>')
	}, {
	    title: "会议签到",
	    imgsrc:"webpage/framework/mobile/image/qiandao1.png",
	    activeimgsrc:"webpage/framework/mobile/image/qiandao.png",
	    ele:$('<div style="background-color:#aaeecc;height:100%"></div>')
	}, {
	    title: "服务方块",
	    imgsrc:"webpage/framework/mobile/image/fuwu1.png",
	    activeimgsrc:"webpage/framework/mobile/image/fuwu.png",
	    ele:$('<div style="background-color:#ffaabb;height:100%"></div>')
	}, {
	    title: "个人中心",
	    imgsrc:"webpage/framework/mobile/image/geren1.png",
	    activeimgsrc:"webpage/framework/mobile/image/geren.png",
	    ele:$('<div style="background-color:#aabbcc;height:100%"></div>')
	}];
   itemOption1 = {
			template:'<span style="line-height:35px; " >{title} </span>',
			activetemplate: ' <span style="line-height:35px;color:red;border-bottom:0px solid red;" >{title} </span>',
			iconCls:"",
			activeCls:""
		}
   itemOption = {
			template:'<img src="{imgsrc}"/>{title}1',
			activetemplate: '<img src="{activeimgsrc}"/><span style="color:red;"">{title}1</span>'
		}
   	var tab = new sunz.MTab({parent:"body",data:data ,
   		itemOption:itemOption,type:"top",activeBarTemplate:""
   		});
   	
   	
   	//webpage/framework/mobile/image/dang1.png
   	//tab.openUrl("default",{ url:"webpage/framework/mobile/page/index.json",
     //   listitemsrc:"../web/demo/demolist"});
   </script> 
   <script>
 
 </script>
</html>