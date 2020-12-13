/**
 * 
 */
if(window.app){
	var update=function(fnUpdate,fnLast){
		let versionCode=app.getVersion?app.getVersion():1;
		$.post("mobile/update.json",{t:new Date().getTime()},function(jr){
			if(jr.success){
				if(jr.data.versionCode>versionCode){
					if(fnUpdate)fnUpdate(jr.data);
				}else{
					if(fnLast)fnLast(jr.data);
				}
			}else{
				$.tip('版本服务查询出错',2000);
			}
		});
	},
	showUpdate=function(desc){
		new sunz.MWindow({
			"class":"mobileAlert",height:desc.height||120,
			title: '<div style="width:100%;text-align:center">版本更新</div>',
	        content: '发现新版本'+desc.versionName+"</br><div style='margin:10px 0 0 25px;line-height:28px'>"+desc.log+'</div>',
			showBottom: true,
			okText:'<span style="color:red">立即更新</span>',
			cancelText:desc.force?"退出":"暂不更新",
			hideWhenBtnClick:!desc.force
		}).onOk(function(){
			window.open('mobile/update.apk');
		}).onCancel(function(){
			if(desc.force)app.exit();
		});
	};
	update(showUpdate);
}