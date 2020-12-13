<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
  <title>三正科技基础开发平台</title>
  <z:resource items="jquery,easyui"></z:resource>
  <style type="text/css">
  #viewport{width:400px;margin:15% auto;} 
  #dLogin{background: rgba(192,214,218,0.8);}
  body{background: #394557 url(webpage/login/bg.jpg) repeat;}
  .dLogoText{font-size:36px;color:red;position:fixed;width:100%;bottom:8%;height:48px;line-height:48px;}
  </style>
 
</head>
<body class="login-layout light-login">
	<div id="viewport">
		<form id="form">
			<div id="dLogin" class="easyui-window" data-options="title:'<center><b>用户登录</b></center>',closable:false,minimizable:false,maximizable:false,collapsible:false"  style="width:400px;padding:20px 70px 20px 70px;">
		        <div style="margin-bottom:10px">
		             <input class="easyui-textbox" name="userName" style="width:100%;height:30px;padding:12px" data-options="prompt:'登录用户名',iconCls:'icon-man',iconWidth:38">
		        </div>
		        <div style="margin-bottom:20px">
		            <input class="easyui-textbox" name="password" type="password" style="width:100%;height:30px;padding:12px" data-options="prompt:'登录密码',iconCls:'icon-lock',iconWidth:38">
		        </div>
		        <div style="margin-bottom:20px">
		            <input class="easyui-textbox" type="text" id="logyzm" style="width:60%;height:30px;padding:12px" data-options="prompt:'验证码'">
					<a href="javascript:void(0);" class="showcode" onclick="changeVeryfy()">
					<img style=" margin:0 0 0 15px ; vertical-align:middle; height:26px;width:75px;" src=""></a>
		        </div>
		        <div style="margin-bottom:20px">
		            <input type="checkbox" checked="checked" id="remember">
		            <span>记住密码</span>
		        </div>
		        <div>
		            <a href="javascript:void(0);" onclick="login()" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" style="padding:5px 0px;width:100%;">
		                <span style="font-size:14px;">登录</span>
		            </a>
		        </div>           
		   </div>
	   </form>
	</div>
	<div class="dLogoText"><center ><span class="logoText">三正科技基础开发平台 @2016 www.sunztech.com.cn</span></center></div>		
</body>
 <script type="text/javascript">
  function setCookie(name,value,day)
  {
      var exp  = new Date();
      exp.setTime(exp.getTime() + (day||30)*24*60*60*1000);
      document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
  }
  function getCookie(name)
  {
      var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
       if(arr != null) return unescape(arr[2]); return null;

  }
  function delCookie(name)
  {
      var exp = new Date();
      exp.setTime(exp.getTime() - 1);
      var cval=getCookie(name);
      if(cval!=null) document.cookie= name + "="+cval+";expires="+exp.toGMTString();
  }
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
	  if(remember.checked){
		  setCookie("u",pk);
		  setCookie("p",en(info.password,pk));
	  }else{
		  delCookie("u");
		  delCookie(pk);
	  }
	  $.post("framework/login.do?validate",info,function(jr){
		  if(jr.success)
			  location.href="framework/login.do?enter";
		  else
			  $.messager.alert("提示","您输入的用户名密码错误，请重新输入");
	  },"json");
  }
  $(function(){
	  var pk=getCookie("u"),u=en(pk),p=getCookie("p");
	  remember.checked=!!u;
	  var info={userName:u,password:en(p)};
	  $.each($("input[name]"),function(i,ele){$(ele).val(info[$(ele).attr("name")]);});
  });
  </script>
</html>