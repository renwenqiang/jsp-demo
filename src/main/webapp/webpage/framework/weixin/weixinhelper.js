/**
 * 微信类
 */
if(!window.wxConfig){
	window.wxConfig = {};
}

WXCONFIG.debug=false;
WXCONFIG.jsApiList =wxConfig.jsApiList||WXCONFIG.jsApiList;

 
if(!window.wx){
	console.error("必须引用微信接口"); 
	wx = {
			config:function(c){
				setTimeout(function(){c.errorback?c.errorback("必须引用微信接口"):""},1000)
			},
			ready: function(c){},
			error: function(c){c&&c.call&& (wx.errorback = c);}
	}
}
/**
 * 微信初始化完成事件
 */
wx.ready(function(e) {
	//alert("微信接口初始化成功")
	sunz.getPosition=(fn,fnErr)=>wx.getLocation({success:function(r){fn && fn({coords:r})},fail:fnErr});//wx.getLocation({success:fn,cancel:fnErr});
	sunz.share=()=>$.alert("温馨提示","您当前正处于微信中，无法直接分享，请使用右上角菜单进行分享");
	sunz.registerNativeShareData=(url,title,desc,imgUrl,fn)=>{
		let c={title:title,desc:desc,link:url,imgUrl:imgUrl,success:fn};
		wx.onMenuShareAppMessage(c);
		wx.onMenuShareTimeline(c);
		wx.onMenuShareQQ(c);
		wx.onMenuShareWeibo(c);
		wx.onMenuShareQZone(c);
		wx.updateAppMessageShareData(c);
		wx.updateTimelineShareData(c);
	}
});
wx.error(function(e) {});
wx.config(WXCONFIG);