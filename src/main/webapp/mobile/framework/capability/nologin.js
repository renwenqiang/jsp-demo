/**
 * 允许未登录情况下直接加载界面 (与权限无关)
 */
$(function(){
	// 尝试获取登录信息一次，之后按未登录处理
	let vlogin=function(){
		window.onLoginRequired.empty();
		window.onLoginRequired.add(callback=>{
			require(["_loginx"],login=>{
				login.setParam(()=>{
					location.href=location.href;
				});
				login.show();
			});
		});
		window.onUserLogin && window.onUserLogin.fire(window.loginUser=(window.loginUser||{}));
	}
	window.onLoginRequired.add(vlogin);
	$.post("framework/login.do?loginInfo",jr=>{
		window.loginUser=jr.data||{id:"guest"};
		vlogin();
	});
})