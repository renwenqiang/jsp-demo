/**
 * 
 */
window.onLoginRequired.add(function(callback){
	require(["_loginx"],function(login){
		login.setParam(function(){
			location.href=location.href;
			window.onUserLogin.fire(window.loginUser); // 以防万一页面不刷新
		});
		viewport.add(login);
	});	
});
$.post("framework/login.do?loginInfo",function(jr){
	if(jr.success){
		window.loginUser=$.extend(window.loginUser,jr.data);
		window.onUserLogin.fire(jr);
	}
});