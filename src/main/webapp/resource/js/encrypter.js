(window.define||function(d,fn){window.JSEncrypt?fn():$.getScript(d[0],fn)})(["plug-in/encrypt/rsa.js"],function(){
	var rsa=new JSEncrypt();
	var key="account.jsPublickKey";
	if(!window.C||!C[key])
		console.error("加密工具初始化出错，原因是未配置公钥--依赖的配置项【"+key+"】未定义");
	rsa.setPublicKey(C[key]);
	return window.encrypter={
		encrypt:function(input){
			return rsa.encrypt(input);
		},
		encryptPassword:function(input){
			if(!$.isFunction(window.getServerTime))
				throw Error("密码加密依赖服务器时间，需要提供全局getServerTime函数--返回13位服务器毫秒数")
			return rsa.encrypt(getServerTime()+input)
		},
		decrypt:function(input){
			return rsa.decrypt(input);
		}
	};
})