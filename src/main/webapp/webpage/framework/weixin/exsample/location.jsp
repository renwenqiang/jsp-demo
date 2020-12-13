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


 <z:resource items="jquery,sunzmobile,weixin"></z:resource>
<!--  <script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.2.0.js"></script>
 <script type="text/javascript" src="webpage/framework/weixin/weixinindex.js"></script> -->

<title>钉钉免登</title>
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
<div class="lgdiv">

</div>


</body>
<script> 
alert(JSON.stringify(navigator.userAgent))
 alert(JSON.stringify(WXCONFIG));
var userId = "2c9081df5dff30f3015dff630c5e00fc";
wxConfig=window.wxConfig||{};
wxConfig.loginSuccess = function(e){
	alert(JSON.stringify(e));
	location.href = href;
	
}
wxConfig.loginError = function(e){
	 
	alert(JSON.stringify(e));
}
sunz.MTextbox.create({label:"名称",parent:".lgdiv"});
	sunz.MTextbox.create({label:"密码",parent:".lgdiv"});
	sunz.MButton.create({text:"登录",parent:".lgdiv",width:"100%"})
		sunz.MButton.create({text:"退出登陆",parent:".lgdiv",width:"100%",
			listener : {
				click:function(){
					DingTalk.logout();
				}
			}	
		})
function addMenue(){
		dd.biz.navigation.setMenu({"backgroundColor":"#FF1493","items":[{"id":"1","text":"帮助"},{"id":"2","iconId":"photo","text":"dierge"},{"id":"3","iconId":"setting","text":"disange"},{"id":"4","iconId":"time","text":"disige"}]
		,onSuccess: function(data) {
			        /*
			        {"id":"1"}
			        */
			alert(JSON.stringify(data))
			        },
			        onFail: function(err) {
				alert(JSON.stringify(err))
			        }})
		   
	}	
	
function synicCurrentUser(){
	
}
SunzWX.getLocation(function(e){
	alert(5)
alert(JSON.stringify(e))
},function(e){
alert(JSON.stringify(e))
})
	//setTimeout(addMenue,1000)
	</script>
<script>
 
 </script>
</html>