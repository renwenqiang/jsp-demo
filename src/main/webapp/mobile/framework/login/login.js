/**
 * 
 */
define(["resource/js/encrypter"],function(encrypter){	
	//<img src="mobile/model/framework/login/logo.jpg" style="width:100%;height:100%"/>padding:25px;height:256px;font-size:36px;line-height:64px;color:#66f;background-image:url(webpage/login/bg.jpg);background-repeat: no-repeat;background-size:100% auto;"></div>\
	let jele=$('<div class="mcontainer" style="text-align:center;">\
			<div style="color:#ccc;height:100%;width:100%;background:url(mobile/framework/login/logo.png) no-repeat;background-size:cover;background-color:#02012b;">\
				<div style="padding:80px 15px 50px;font-size:36px;">三正科技移动平台</div>\
				<div style="padding:25px;line-height:48px;font-size:16px;">\
					<input style="color:#fff;line-height:48px;border-radius:24px;border:1px #999 solid !important;background:url(mobile/framework/login/user.png) no-repeat;background-color:transparent !important;background-size:contain;width:100%;padding-left:48px;" name="userName" placeholder="请输入身份证号" type="text"/>\
					<input style="color:#fff;line-height:48px;border-radius:24px;border:1px #999 solid !important;background:url(mobile/framework/login/pwd.png) no-repeat;background-color:transparent !important;background-size:contain;width:100%;padding-left:48px;margin-top:25px;" name="password" placeholder="请输入密码" type="password"/>\
					<div class="btnlogin" style="margin:25px 0;color:#fff;background-color:#139;border-radius:24px;">登录</div>\
				</div>\
				<div style="padding:5px 15px;">北京三正科技股份有限公司</div>\
				<div style="padding:5px 15px;">服务热线：<a href="tel:010-62152257">010-62152257</a></div>\
			</div>\
			</div>');
	
	
	let login=function(){
		var uname=jele.find("input[name=userName]").val(),
			pwd=jele.find("input[name=password]").val();
		if(!uname||!pwd){
			$.tip("请输入您的用户名密码",2000);
			return;
		}
		$.post("framework/login.do?validate&",{userName:uname,password:C["account.passwordStrictEcrypt"] !="false"?encrypter.encryptPassword(pwd):pwd},function(jr){
			if(jr.success){	
				localStorage.setItem("lastUser",uname);
				localStorage.setItem("lastPwd",pwd);
				window.loginUser=jr.data;
				jele.hide();
				callback();
			}else{
				//localStorage.setItem("lastUser","");
				localStorage.setItem("lastPwd","");
				jele.find("input[name=password]").val("");
				sunz.alert("温馨提示",jr.msg||"您输入的用户名密码错误，请重新输入");
			}
		});
	}
	jele.find(".btnlogin").on("click",login);
	jele.find("input[name=userName]").val(localStorage.getItem("lastUser"));
	jele.find("input[name=password]").val(localStorage.getItem("lastPwd"));
	let callback=function(){viewport.back();};	
	return {
		title:"登录",
		buttons:[/*{text:"取消登录",iconCls:"tbar",handler:viewport.back}*/],
		panel:{domNode:jele},
		show:function(){
			if(window.app){
				var o={userName:jele.find("input[name=userName]").val(),password:jele.find("input[name=password]").val()};
				if(o.userName&&o.password){
					login();
					return;
				}
			}
			jele.appendTo(document.body).show();jele.css("position","fixed").css("bottom","0px").css("z-index","9999");
		},
		setParam:function(param){
			callback=param;
		}
	};
});