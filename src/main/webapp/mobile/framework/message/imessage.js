/**
 * 系统【实时】消息
 */
define([],function(templates){
	window.onImConnected=$.Callbacks();
	window.onImDisonnected=$.Callbacks();
	window.onMessageReceived=$.Callbacks();
	window.onImSqueezeout=$.Callbacks();
	var connectIM=function(){
		var everLoaded=false,squeezeout=false,
			lastHeart=0,lastBeat=1,lastError,
			tryReconnect=function(){// 重连，仅在成功连接过并且不是被挤掉的情况下(在出错的情况下)
				if(everLoaded && !squeezeout/*lastError*/){
					console.log("30秒后尝试重连");
					setTimeout(connectIM,30000);
				}
			};
		$.post("framework/login.do?userToken&type=im",(jr)=>{
			var ws=window.websocket=new WebSocket(C["im.baseUrl"]+"framework/imessager?token="+jr.data);
			var isAlive=function(){return ws.readyState==1};
			var heartBeat=function(){
				if(lastHeart<lastBeat && isAlive()){	
					ws.send("heart");
					lastHeart=new Date().getTime();
					setTimeout(heartBeat,30000);
				}else{// 心跳没有回应(有回应时beat>heart)并且已经断开了
					console.log("检测到数据连接关闭");
					tryReconnect();
				}
			};
			ws.onopen=function(){
				everLoaded=true;
				setTimeout(heartBeat,30000);
				window.onImConnected.fire();
			}
			
			ws.onerror=function(e){
				lastError=e;
				console.log("数据连接出错",e);
			};
			ws.onclose=function(){ 
				console.log("数据连接关闭");
				onImDisonnected.fire();
			};
			ws.onmessage=function(m){
				var msg=m.data;
				if(msg=="[beat]"){
					lastBeat=new Date().getTime();
					return;
				}
				if(msg=="[repeat]"){
					squeezeout=true;
					onImSqueezeout.fire();
					return;
				}
				if(msg=="[ticket-timeout]"){
					return;
				}
				window.onMessageReceived.fire($.decode(msg));
			};
		})
	}
	connectIM();
	
	return connectIM;
});