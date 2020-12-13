<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!Doctype html>
<html>
<head>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,bootstrap,zepto,sunzmobile,requirejs"></z:resource>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,
initial-scale=1.0,
maximum-scale=1.0,
user-scalable=no">

    <title>nav</title>
  <link rel="stylesheet" href="plug-in/bootstrap-3.3.7/css/bootstrap.min.css" type="text/css"></link>
</head>

<body  style="width: 100%;
    overflow-x: hidden;">
  

 
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
   	var nav = new sunz.MNav();
   	nav.openUrl("default",{ url:"webpage/framework/mobile/page/index.json",
        listitemsrc:"../web/demo/demolist"});
   </script> 
   <script>
 
 </script>
</html>