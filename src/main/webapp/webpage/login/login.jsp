<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
		<title><%=com.sunz.framework.core.Config.get("title")%></title>
		<z:resource items="jquery,easyui"></z:resource>
		<z:config items="account.validateExtendType,account.jsPublickKey,account.passwordStrictEcrypt"></z:config>
		<script type="text/javascript" src="plug-in/jquery/jquery.backstretch.min.js"></script>
		<script type="text/javascript" src="resource/js/encrypter.js"></script>
		<style type="text/css">
			.dLogoText {
				font-size: 36px;
				color: red;
				position: fixed;
				width: 100%;
				bottom: 8%;
				height: 48px;
				line-height: 48px;
			}
			
			#login-right {
				width: 50%;
				position: absolute;
				right: 0px;
				top: 0px;
				bottom: 0px;
				background: white;
			}
			
			#login-left {
				width: 50%;
				position: absolute;
				left: 0px;
				top: 0px;
				bottom: 0px;
				background: url(plug-in/login/images/bg1.jpg);
			}
			
			#logo {
				margin: 40px 0 15px 10px;
				width: 250px;
				height: 100px;
				background: url(plug-in/login/images/sunz_logo.png);
				background-size: 250px 100px;
			}
			
			#login-title {
				font-size: 15px;
				line-height: 22px;
				top: 30%;
			}			
			#login-title,#viewport{padding:10px 60px 15px;}			
			#form {}			
			.form-control {
				padding: 10px 0;
				border: #a0a9b4;
				border-bottom: 1px solid;
				color: #868e97;
				font-size: 14px;
				margin-bottom: 10px;
				border-radius: 0 !important;
				outline: 0 !important;
			}
			
			.login-username {
				width: 60%;
				left: 0px;
				top: 0px;
			}
			
			.login-password {
				width: 60%;
				right: 0px;
				top: 0px;
			}
			
			.login-copyright {
				position: absolute;
				bottom: 0px;
				right: 60px;
				font-size: 13px;
				color: #a9b5be;
			}
			
			input:-webkit-autofill {
				-webkit-box-shadow: 0 0 0px 1000px white inset;
			}
			
			#login-rember {
				color: #a9b5be;
				float: left;
			}
			
			#login-btn {
				color: #FFF;
				background-color: #3598dc;
				border-color: #3598dc;
				width: 100px;
				text-align: center;
				float: left;
				margin-left: 100px
			}
			
			#login-rember, #login-btn {line-height:36px;margin-top:10px}			
			#login-btn:hover {cursor: hand;}			
			#remember {height: auto}
			.btn-login{font-size:14px;color:white;text-decoration: none;}
			
			.login-vcode{width:120px;margin-right:60px;}
			.btn-sms {
				display: inline-block;
				min-width: 100px;
				border: 1px solid;
				text-align: center;
				padding: 10px;
				color: #933;
			}
			.sms-disabled{color:#bbb;}
			.randomcode{position:relative;top:10px;}
			.btn-login,.btn-sms,.randomcode{cursor:pointer;}
		</style>
	
	</head>
<body class="login-layout light-login">
	<div id="login-right">
		<div id="logo"></div>
		<div id="login-title">
			<h1>三正科技基础开发平台</h1>
			<span style="color: #a0a9b4;">三正科技基础开发平台基于java
				EE技术，采用主流的前端技术，具有基础权限框架、工作流程引擎、报表设计器、表单设计器等功能,目的是帮助实施人员实现零开发配置系统</span>
		</div>
		<div id="viewport">
			<form id="form">
				<input name="userName" placeholder="用户名" class="form-control login-username" />
				<input name="password" placeholder="密码" type="password" class="form-control login-password" />
				<div id="exValidate"></div>
				<div id="login-rember">
					<input type="checkbox" checked="checked" id="remember" /> <span>记住密码</span>
				</div>
				<div id="login-btn" onclick="login()">
					<a href="javascript:void(0);" class="btn-login"> <span>登录</span> </a>
				</div>
			</form>
		</div>
		<div>
			<div class="login-copyright">
				<p>Copyright &copy; <%=com.sunz.framework.core.Config.get("copyright")%></p>
			</div>
		</div>
	</div>
	<div id="login-left"></div>
</body>
<script type="text/javascript">
  function setCookie(name,value){localStorage.setItem(name,value);}
  function getCookie(name){localStorage.getItem(name);}
  function delCookie(name){localStorage.removeItem(name);}
  var en=function(u,p){
	  if(!u)return;
	  var len=u.length,x="",fn=function(r,i){return i%2==1?r:r.substr(0,r.length-1);};
	  if(p){x="";fn=function(r,i){return r+x.substr(i,1);};for(var l=0;l<len+1;l++){x+=String.fromCharCode(Math.floor(Math.random()*94+161));}}
	  var r=x.substr(0,1);for(var i=0;i<len;i++)r= fn(r+String.fromCharCode((128+u.charCodeAt(i))%256),i);
	  return r;
	  /*var r="",l=u.length;//m=u.length;for(i=0;i<u.length;i++){m+=u.charCodeAt(i)^u.length;}m=33+m%95;
	  for(i=0;i<p.length;i++){r+= String.fromCharCode(l^u.charCodeAt(i%l)^p.charCodeAt(i));} 
	  return r;*/
   }
  
  function login(){
	  var info={};$.each($("input[name]"),function(i,ele){info[$(ele).attr("name")]=$(ele).val();});
	  if(!info.userName || !info.password){$.messager.alert("提示","请输入用户名和密码");return;}
	  var n=info.userName,pk=en(n,"u");
	  if($("#remember")[0].checked){
		  setCookie("u",pk);
		  setCookie("p",en(info.password,pk));
	  }else{
		  delCookie("u");
		  delCookie(pk);
	  }
	  if(C["account.passwordStrictEcrypt"] !="false")
	  	info.password=encrypter.encryptPassword(info.password);
	  
	  $.post("framework/login.do?validate",info,function(jr){
		  if(jr.success)
			  location.href=getQueryParam("origUrl")||"framework/login.do?enter";
		  else
			  $.messager.alert("提示","您输入的用户名密码错误，请重新输入");
	  },"json");
  }
  $(function(){
	  var exVType=C["account.validateExtendType"],
	  	  jv;
	  if(exVType==1){
		  jv=$('<img class="randomcode">').on("click",function(){this.src=src="framework/login.do?getRandomCode&__t="+new Date().getTime()}).click();
	  }else if(exVType==2){
		  var cls="sms-disabled",
		  	  timing=function(){
				setTimeout(function(){
				  if(jv.timing==60){
					  jv.removeClass(cls);
					  jv.text("重新发送");
				  }else{
					  jv.text("重新发送("+(60-(jv.timing=(jv.timing||0)+1))+")");
					  timing();
				  }
			  	},1000);
		  };
		  jv=$('<a class="btn-sms">获取短信验证码</a>').on("click",function(){
			  if(jv.hasClass(cls))
				  return;
			  var uname=$("[name=userName]").val();
			  if(!uname){
				  $.messager.show({title:"提示",msg:"请先输入用户名"});
				  return;
			  }
			  $.post("framework/login.do?sendSmsCode",{userName:uname},function(jr){
				  if(jr.success){
					  $.messager.show({title:"提示",msg:"验证码已发送"});
				  }else{
					//jv.removeClass(cls);
				  	$.messager.alert("提示",jr.msg);
				  }
			  })
			  jv.addClass(cls);			  
			  timing(jv.timing=0);
		  });
	  }
	  if(jv){
	  	$("#exValidate").append('<input name="validateCode" placeholder="请输入验证码"  type="text" class="form-control login-vcode" />').append(jv)
	  }
	  
	  
	  $('#login-left').backstretch([
		                              "webpage/login/images/bg1.jpg",
		                              "webpage/login/images/bg2.jpg",
		                              "webpage/login/images/bg3.jpg"
		                              ], {
		                                fade: 1000,
		                                duration: 4000
		                              }
		                          );
	  var u=getCookie("u"),p=getCookie(u);
	  var pk=getCookie("u"),u=en(pk),p=getCookie("p");
	  $("#remember")[0].checked=!!u;
	  var info={userName:u,password:en(p)};
	  $.each($("input[name]"),function(i,ele){$(ele).val(info[$(ele).attr("name")]);});
	  
	  /* 图片轮播 */
	  
  });
	document.onkeydown=function(event){ 
		e = event ? event :(window.event ? window.event : null); 
		if(e.keyCode==13){ 
			login();  
	    } 
	}
	window.onload=function(){/*是否需要判断已登录和自动跳转*/}
  </script>
</html>