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
	var sl = sunz.MSearchList.create({
		parent : "body",
		itemclick : function() {
			console.log(arguments)
		},
		refresh: false,
		type:"panel",
		placeholder : "名称或描述",
		pagesize : 10,
		showBorder:true,
		searchField : "name",
		showbypage : true,
		//data:data,
		url : "framework/query.do?search&k=sqlSetting",
		template : "{_No}·<a>{name}</a>|{dd}",
		itemFilter:function(d){
			d.dd="sssss"
			},
			
			template:function(data){
				//var panel = sunz.MPanel.create({title:data.name,content:data.code,bottom:" "});
				var panel = sunz.MPanel.create({ title:data.name,content:'<img src="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1499064044&di=77ba13e4a410d2c56fda78866262c5a0&imgtype=jpg&er=1&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fe%2F557957c70fa3a.jpg" style="width:100%"></img>',bottom:" "});
				sunz.MButton.create({text:"like" ,type:"link",parent: panel.bottomNode});
				sunz.MButton.create({text:"share" ,type:"link",parent: panel.bottomNode});
				return panel.domNode;
			}
	})
</script>
<script>
	
</script>
</html>