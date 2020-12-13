<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!Doctype html>
<html>
<head>
<meta charset="utf-8">
<%@include file="/sunzbase.jsp"%>

    <meta name="viewport" content="width=device-width,
initial-scale=1.0,
maximum-scale=1.0,
user-scalable=no">
 
<script type="text/javascript">
//var ddconfig = ${dingConfig};
</script>

<script>
WXDEBUG = true
</script>
 <z:resource items="jquery,sunzmobile,weixin"></z:resource>
<!--  <script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.2.0.js"></script>
 <script type="text/javascript" src="webpage/framework/weixin/weixinindex.js"></script> -->
<script type="text/javascript" src="plug-in/QRCode/QRCode.js"></script> 
<title>微信支付</title>
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

.lgdiv{
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
    /* height: 200px; */
    width: 90%;
    margin: auto;
    height: 200px;
}
</style>

</head>

<body style="width: 100%;
    overflow-x: hidden; ">
    <div>
    当前用户：'${loginuser.userName}' 
    </div>
 


</body>
<script>   

SunzWX.onReady(function(e){
	alert("微信初始化成功")
	alert(JSON.stringify(e))
	
})
  
	//setTimeout(addMenue,1000)
	</script>
	
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
    		payType: "weixin"
    	});
    	
    	p.startPay(function(e){
    		console.log(e)
    	},
    	function(e){
    		console.log(e)
    	});
    	
    }
   </script> 
<script>
 
 </script>
</html>