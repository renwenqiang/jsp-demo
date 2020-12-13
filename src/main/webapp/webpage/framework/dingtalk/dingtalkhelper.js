/**
 * 钉钉类
 *dingConfig.loginSuccess  登录成功回调
 *dingConfig.loginError  登录失败回调
 *dingConfig.ddapilist   授权列表
 */
window.dingConfig = window.dingConfig||{};
DDCONFIG.jsApiList = dingConfig.ddapilist
					||dingConfig.jsApiList
					||[ 'runtime.info', 'biz.contact.choose','device.notification.confirm', 'device.notification.alert',"device.geolocation.get",'device.notification.prompt', 'biz.ding.post', 'biz.util.openLink'];

dd.config(DDCONFIG);
var DingTalk = {};
/**
 * 免登
 */
DingTalk.login = function(code) {
	var url = "framework/dingLogin.do?checkusercode&code=" + code;
	$.ajax({
		url : url,
		dataType : "json",
		type : 'get',
		success : function(jr) {
			if (jr.success) {
				if(dingConfig.loginSuccess){
					dingConfig.loginSuccess(jr);
				}
			}else{
				if(dingConfig.loginError){
					dingConfig.loginError(jr);
				}else
					$.tip(jr.msg);
			}
		},
		error : function(e) {
			$.tip("钉钉免登服务出错啦");
			console.error(e);
		}
	})
}

/**
 * 获取授权码
 */
DingTalk.requestAuthCode = function() {
	if (ISLOGIN){
		dingConfig.loginSuccess && dingConfig.loginSuccess(LOGINDATA);
		return;
	}
	dd.runtime.permission.requestAuthCode({
		corpId : DDCONFIG.corpId,
		onSuccess : function(jr) {
			DingTalk.login(jr.code);
		},
		onFail : function(err) { 
			if(dingConfig.loginError){
				dingConfig.loginError(err); 
			}
		}
	});
}

DingTalk.synicCurrentUser = function(userid){
	
}
DingTalk.synicUser = function(userid, callback, errorback){
	if(!userid) {
		throw("userid is null")
	} 
	dd.runtime.permission.requestAuthCode({
		corpId : DDCONFIG.corpId,
		onSuccess : function(jr) { 
			DingTalk._synicUser(userid, jr.code, callback, errorback);
		},
		onFail : function(err) {
			errorback?errorback(jr):"";
		}

	})
}
DingTalk._synicUser = function(userid, code, callback, errorback){
	var url = "framework/dingUser.do?dolink&code=" + code +"&userid="+userid; 
	$.ajax({
		url : url,
		dataType : "json",
		type : 'get',
		success : function(jr) { 
			if (jr.success) { 
				sunz.alert("关联成功");
				callback?callback(jr):"";
			}else{
				sunz.alert("关联失败");
				errorback?errorback(jr):"";
			}
		},
		error : function(e) {
			sunz.alert("关联失败");
			errorback?errorback(e):"";
		}
	})
};
/**
 * 钉钉初始化完成事件
 */
dd.ready(function(e) {
	DingTalk.requestAuthCode();
});
dd.error(function(e) {
	if (ISLOGIN){
		dingConfig.loginSuccess && dingConfig.loginSuccess(LOGINDATA);
		return;
	}
	dingConfig.loginError?dingConfig.loginError(e):"";  
});