/**
 * 
 */
if(/micromessenger/i.test(navigator.userAgent)){
	require(["mobile/wechat/jweixin"],function(wx){
		window.wx=wx;
		$('<script type="text/javascript" src="framework/wx.do?js&t='+(new Date().getTime())+'"></script>').appendTo(document.head);
	});
}