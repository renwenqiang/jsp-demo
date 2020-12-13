/**
 * 钉钉适配
 */
if(/DingTalk/.test(navigator.userAgent)){
	window.dingConfig=window.dingConfig||{};
	window.dingConfig.loginSuccess=function(jr){
		if(!window.loginUser){
			window.loginUser=jr.data;
			window.onUserLogin.fire(jr.data);
			console.debug("钉钉免登成功")
		}
	};
	require(["mobile/dingding/dingtalk"],function(dd){
		window.dd=dd;
		if(dd.version){
			console.debug("正在使用钉钉登录...");
			$('<script type="text/javascript" src="framework/dingLogin.do?js&t='+(new Date().getTime())+'"></script>').appendTo(document.head);
			// 定位 
			sunz.getPosition=function(fn,fnErr){
				dd.device.geolocation.get({
				    targetAccuracy : 200,
				    coordinate : 1,//1：获取高德坐标， 0：获取标准坐标；
				    withReGeocode : false, // 是否需要带有逆地理编码信息，即地名地址
				    useCache:false, //默认是true，如果需要频繁获取地理位置，请设置false
				    onSuccess : function(r){fn({coords:toWgs(r.latitude,r.longitude)});},
				    onFail:fnErr
				});
			};
			//
			let fnExit=function(){
				dd.device.notification.confirm({title:"提示",message:"您确定要退出当前应用？",buttonLabels: ['退出', '取消'],onSuccess:function(r){if(r.buttonIndex==0)dd.biz.navigation.close();}});
			},
			defButtons=[{text:"扫一扫",iconId:"scan",handler:function(){
									dd.biz.util.scan({onSuccess:function(data){
										var url=data.text,config={title:"扫码结果",panel:{domNode:$('<iframe style="width:100%;height:100%" src="'+url+'"></iframe>')[0]}};
										viewport.add(config);
									}
								});										
							}
						},
						{text:"退出",iconId:"reply",handler:fnExit}];
			
			if(getQueryParam("dd_full_screen")){
				let origSBS=viewport.setButtons;
				viewport.setButtons=function(btns,keep){
					if(keep)return;
					var dict={};
					btns=(btns||[]).concat(defButtons);
					origSBS(btns,keep);
				};
			}else{
				$("#viewport .navbar").hide();
				let pContainer=$(".main-container"),pBbar=$(".divBBar");
				pContainer.css("height","100%");
				viewport.hideBBar=function(){pBbar.hide();pContainer.css("height","100%");};
				viewport.showBBar=function(){pBbar.show();pContainer.css("height","calc(100% - 56px)")};
				viewport.setTitle=function(t){dd.biz.navigation.setTitle({title:document.title=t});};
				viewport.setButtons=function(btns,keep){
					if(keep)return;
					let dict={},idPost=1;
					btns=(btns||[]).concat(defButtons);
					$.each(btns,function(i,btn){
						if(i==0&&btn.text&&btn.text.length>3)
							btn.text=btn.text.substr(0,2)+"...";
						btn.id="hymenu"+(idPost++);
						dict[btn.id]=btn.handler||btn.onclick;
						//btn.onSuccess=btn.handler||btn.onclick;
					});
					dd.biz.navigation.setMenu({items:btns,onSuccess:function(btn){
						var fn=dict[btn.id];
						if(fn)fn();
					}});
				};
				dd.biz.navigation.setLeft({control:true,onSuccess:function(r){
						viewport.hasBack()?viewport.back():fnExit();
					}
				});	
			}
		}
	});
}