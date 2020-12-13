<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!Doctype html>
<html>
<head>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,sunzmobile,requirejs"></z:resource>
<z:dict items="all"></z:dict>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width,
initial-scale=1.0,
maximum-scale=1.0,
user-scalable=no">
<style
>
html,body{
height:100%;
}

.sunzmui-list-itemcontainer {
    margin-bottom: 54px;
}
</style>
<title>list</title>
 
</head>

<body style=" 
    overflow-x: hidden; ">



</body>
<script>
var data = [];
for(var i=0;i<10000;i++){
	//data.push({name: "序号："+i});
}
try{
	var sl = sunz.MSearchList.create({
		parent : "body",
		itemclick : function() {
			console.log(arguments)
		},
		refresh: true,
		//height:"500px",
		placeholder : "名称或描述",
		pagesize : 20,
		showBorder:true,
		searchField : "name",
		showbypage : true,
		//data:data,
		url : "framework/query.do?search&k=sqlSetting",
		template : "{_No}·<a>{name}</a>|{dd}",
		itemFilter:function(d){
			d.dd="sssss"
			}
	})
}catch(e){
	alert(e)
}
	
</script>
<script>
	
</script>
</html>