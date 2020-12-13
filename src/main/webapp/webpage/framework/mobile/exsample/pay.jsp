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

    <title>star</title>
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
    f=sunz.MForm.create({
    	parent:"body" ,
    	action:""
    });
   
    aname = sunz.MTextbox.create({
    	label:"名称",
    	value:"支付测试",
    	parent:f.domNode
    });
    content = sunz.MTextArea.create({
    	label:"订单内容",
    	value:"支付测试支付测试支付测试支付测试",
    	parent:f.domNode,
    	height:"120px"
    });
    price = sunz.MTextbox.create({
    	label:"订单金额",
    	type:"number",
    	value:"0.01",
    	parent:f.domNode
    });
    btn = sunz.MButton.create({
    	text:"提交订单", 
    	handler: function(){
    		pay()
    	},
    	parent:f.domNode
    });
    function pay(){
    	p = sunz.MPay.create({
    		orderPrice: price.getValue(),
    		orderName: aname.getValue(),
    		orderContent: content.getValue(),
    		payType: "ali"
    	});
    	
    	p.newOrder(function(){
    		p.pay();
    	})
    	
    }
   </script> 
   <script>
 
 </script>
</html>