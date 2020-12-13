/**
 * 密码修改支持
 */
window.onUserLogin.add(function(){
	if(!window.loginUser.pwdversion){// 0
		// 1周以内不再提示
		let lastTime=localStorage.getItem("lastPwdEditTime");
		if(lastTime && new Date().getTime()-lastTime<604800000/*1000*3600*24*7*/)
			return;
		
		viewport.setTitle("首次使用密码修改");
		require(["_changepwd"],function(cpwd){
			viewport.add(cpwd); // 非强制使用此句即可
			localStorage.setItem("lastPwdEditTime",new Date().getTime());
			//$(cpwd.panel.domNode).appendTo(viewport.container).css("position","absolute").css("top",0).show();
		});
		$.tip("欢迎 "+loginUser.realName+"，您的密码过于简单，建议修改！",5000);
		//return;
	}
});