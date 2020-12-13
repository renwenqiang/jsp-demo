<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!Doctype html>
<html>
<head>
<meta charset="utf-8">
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,sunzmobile"></z:resource>
<script type="text/javascript" src="plug-in/echarts/echarts.min.js"></script>
<link rel="stylesheet" href="webpage/framework/charts/css/style.css"
	type="text/css"></link>
<script type="text/javascript"
	src="webpage/framework/charts/js/sunzcharts.jsp"></script>
<script type="text/javascript" src="plug-in/echarts/theme/dark.js"></script>
<script type="text/javascript" src="plug-in/echarts/extension/bmap.js"></script>

<!--  -->
<script type="text/javascript"
	src="http://echarts.baidu.com/gallery/vendors/echarts/map/js/china.js"></script>
<script type="text/javascript"
	src="http://echarts.baidu.com/gallery/vendors/echarts/map/js/world.js"></script>


<script type="text/javascript"
	src="http://api.map.baidu.com/api?v=2.0&amp;ak=ZUONbpqGBsYGXNIYHicvbAbM"></script>


<title>title</title>
<style>
html,body {
	width: 100%;
	height: 100%;
	margin: 0px;
	padding: 0px;
}
::-webkit-scrollbar {
    width: 10px;
    height: 10px;
}
::-webkit-scrollbar-thumb {
    height: 50px;
    background-color: rgba(222, 52, 52, 0.39);
    -webkit-border-radius: 6px;
    outline: 2px solid #23ca60;
    outline-offset: -2px;
    border: 2px solid #f95c5c;
    filter: alpha(opacity=50);
    -moz-opacity: 0.5;
    -khtml-opacity: 0.5;
    opacity: 0.5;
}
::-webkit-scrollbar-track-piece {
    /* background-color: #044061; */
    -webkit-border-radius: 0;
}
</style>
</head>

<body style="width: 100%;
    overflow-x: hidden; ">



</body>
<script> 
   
   function randomData() {
	    return Math.round(Math.random()*1000);
	}


   

   option = {
        
       tooltip : {
           trigger: 'item'
       }, 
       series : [ 
       ]
   };
 
   seriesConfig = [{
	    type: "lines",
	    name: "流动党员",
	    dataConfig: { 
	        url: "framework/query.do?search&k=queryLDDYStatic",
	        filter: function(result) {
	        	var _data = result.data;
	        	var data = [];
	        	for(var i=0;i<_data.length;i++){
	        		data.push([{"name":"鹰潭"},_data[i]]);
	        	}
	        	return convertLineData(data);
	        	
	        }
	    },
        symbolSize: 10,
        effect: {
            show: true,
            period: 5,
            trailLength: 0.8,
            color: '#fff',
            symbolSize: 4
        }
	}, {
	    type: "scatter",
	    name: "流动党员",
	    dataConfig: { 
	        url: "framework/query.do?search&k=queryLDDYStatic",
	        filter: function(result) {
	        	var _data = result.data;
	        	 console.log( convertPointData(_data))
	        	return convertPointData(_data);
	        	
	        }
	    },
	    symbolSize: function (val) {
            return Math.sqrt(val[2]) * 10;
        },
        rippleEffect: {
            brushType: 'stroke'
        },
        label: {
            normal: {
                show: true,
                position: 'right',
                formatter: '{b}'
            }
        },
        tooltip:{
        	trigger: 'item',
        	formatter: '{b0}:<br /> {c} ' 
        }
	},{
		 name:  ' 流动党员',
	        type: 'lines',
	        zlevel: 1,
	        effect: {
	            show: true,
	            period: 6,
	            trailLength: 0.7,
	            color: '#fff',
	            symbolSize: 3
	        },
	        lineStyle: {
	            normal: {
	                color:  '#a6c84c',
	                width: 1,
	                curveness: 0.2
	            }
	        },
	        dataConfig: { 
		        url: "framework/query.do?search&k=queryLDDYStatic",
		        filter: function(result) {
		        	var _data = result.data;
		        	var data = [];
		        	for(var i=0;i<_data.length;i++){
		        		data.push([{"name":"鹰潭"},_data[i]]);
		        	}
		        	return convertLineData(data);
		        	
		        }
		    }
	   
	}]; 

   
	map=sunz.charts.Map.create({
	    parent: "body",
	    width: "100%",
	    height: "100%",
	    option: option,
	    seriesConfig: seriesConfig
	});
	panel = sunz.charts.ChartPanel.create({
		title:"流动党员",
		parent:"body",
		width:"240px",
		height:"80%",
		right:"20px",
		bottom:"30px",
		sunzclazz: sunz.MList,
		chartOption:{
			pagesize:1000,
			url:"framework/query.do?search&k=queryLDDYList",
			template:"<li>{name}</li><li>{lddz}</li>",
			firstRow:'<li class=" list-group-item  ldlist-head"><a>人员</a><a>流动地点</a></li>'
		}
		
	});
	
   </script>
<script>
 
 </script>
</html>