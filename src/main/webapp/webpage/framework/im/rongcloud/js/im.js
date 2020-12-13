

if(!window.sunz){
	window.sunz = {};
}

sunz.IMConfig = {
		appkey:"mgb7ka1nm4gog"
};

SunzIM = function(callbacks){
	this.callbacks = callbacks;
	this.init();
	this.initListener();
};



SunzIM.prototype.init = function(){
	RongIMLib.RongIMClient.init(sunz.IMConfig.appkey, null, {});
	this.instance = RongIMClient.getInstance();	
};

SunzIM.prototype.initListener = function(){

	// 连接状态监听器
	RongIMClient.setConnectionStatusListener({
		onChanged: function (status) {
			// console.log(status);
		    switch (status) {
		        case RongIMLib.ConnectionStatus["CONNECTED"]:
		        case 0:
		        	console.log("连接成功")
		            callbacks.getInstance && callbacks.getInstance(this.instance);
		            break;

		        case RongIMLib.ConnectionStatus["CONNECTING"]:
		        case 1:
		        	console.log("连接中")
		            break;

		        case RongIMLib.ConnectionStatus["DISCONNECTED"]:
		        case 2:
		        	console.log("当前用户主动断开链接")
		            break;

		        case RongIMLib.ConnectionStatus["NETWORK_UNAVAILABLE"]:
		        case 3:
		        	console.log("网络不可用")
		            break;

		        case RongIMLib.ConnectionStatus["CONNECTION_CLOSED"]:
		        case 4:
		        	console.log("未知原因，连接关闭")
		            break;

		        case RongIMLib.ConnectionStatus["KICKED_OFFLINE_BY_OTHER_CLIENT"]:
		        case 6:
		        	console.log("用户账户在其他设备登录，本机会被踢掉线")
		            break;

		        case RongIMLib.ConnectionStatus["DOMAIN_INCORRECT"]:
		        case 12:
		        	console.log("当前运行域名错误，请检查安全域名配置")
		            break;
		    }
		}
	});

	/*
	文档：http://www.rongcloud.cn/docs/web.html#3、设置消息监听器

	注意事项：
		1：为了看到接收效果，需要另外一个用户向本用户发消息
		2：判断会话唯一性 ：conversationType + targetId
		3：显示消息在页面前，需要判断是否属于当前会话，避免消息错乱。
		4：消息体属性说明可参考：http://rongcloud.cn/docs/api/js/index.html
	*/
	RongIMClient.setOnReceiveMessageListener({
		// 接收到的消息
		onReceived: function (message) {
		    // 判断消息类型
			console.log("新消息: " + message.targetId);
			console.log(message);
			callbacks.receiveNewMessage && callbacks.receiveNewMessage(message);
		}
	});

	 // 消息监听器
	 RongIMClient.setOnReceiveMessageListener({
	    // 接收到的消息
	    onReceived: function (message) {
	        // 判断消息类型
	    	console.log(message);
	    	callbacks.receiveNewMessage && callbacks.receiveNewMessage(message);
	        switch(message.messageType){
	            case RongIMClient.MessageType.TextMessage:
	                // message.content.content => 消息内容
	                break;
	            case RongIMClient.MessageType.VoiceMessage:
	                // 对声音进行预加载                
	                // message.content.content 格式为 AMR 格式的 base64 码
	                break;
	            case RongIMClient.MessageType.ImageMessage:
	               // message.content.content => 图片缩略图 base64。
	               // message.content.imageUri => 原图 URL。
	               break;
	            case RongIMClient.MessageType.DiscussionNotificationMessage:
	               // message.content.extension => 讨论组中的人员。
	               break;
	            case RongIMClient.MessageType.LocationMessage:
	               // message.content.latiude => 纬度。
	               // message.content.longitude => 经度。
	               // message.content.content => 位置图片 base64。
	               break;
	            case RongIMClient.MessageType.RichContentMessage:
	               // message.content.content => 文本消息内容。
	               // message.content.imageUri => 图片 base64。
	               // message.content.url => 原图 URL。
	               break;
	            case RongIMClient.MessageType.InformationNotificationMessage:
	                // do something...
	               break;
	            case RongIMClient.MessageType.ContactNotificationMessage:
	                // do something...
	               break;
	            case RongIMClient.MessageType.ProfileNotificationMessage:
	                // do something...
	               break;
	            case RongIMClient.MessageType.CommandNotificationMessage:
	                // do something...
	               break;
	            case RongIMClient.MessageType.CommandMessage:
	                // do something...
	               break;
	            case RongIMClient.MessageType.UnknownMessage:
	                // do something...
	               break;
	            default:
	                // do something...
	        }
	    }
	});
}

SunzIM.prototype.connect = function(success){
	 //var token = "mKmyKqTSf7aNDinwAFMnz7NXKILeV3X0+CCRBOxmtOApmvQjMathViWrePIfq0GuTu9jELQqsckv4AhfjCAKgQ==";

	  RongIMClient.connect(RongIMToken, {
	        onSuccess: function(userId) {
	          console.log("Connect successfully." + userId);
	          success&&success(userId);
	        },
	        onTokenIncorrect: function() {
	          console.log('token无效');
	        },
	        onError:function(errorCode){
	              var info = '';
	              switch (errorCode) {
	                case RongIMLib.ErrorCode.TIMEOUT:
	                  info = '超时';
	                  break;
	                case RongIMLib.ErrorCode.UNKNOWN_ERROR:
	                  info = '未知错误';
	                  break;
	                case RongIMLib.ErrorCode.UNACCEPTABLE_PaROTOCOL_VERSION:
	                  info = '不可接受的协议版本';
	                  break;
	                case RongIMLib.ErrorCode.IDENTIFIER_REJECTED:
	                  info = 'appkey不正确';
	                  break;
	                case RongIMLib.ErrorCode.SERVER_UNAVAILABLE:
	                  info = '服务器不可用';
	                  break;
	              }
	              console.log(errorCode);
	            }
	      });
	    var callback = {
	            onSuccess: function(userId) {
	                console.log("Reconnect successfully." + userId);
	            },
	            onTokenIncorrect: function() {
	                console.log('token无效');
	            },
	            onError:function(errorCode){
	                console.log(errorcode);
	            }
	        };
	        var config = {
	            // 默认 false, true 启用自动重连，启用则为必选参数
	            auto: true,
	            // 重试频率 [100, 1000, 3000, 6000, 10000, 18000] 单位为毫秒，可选
	            url: 'cdn.ronghub.com/RongIMLib-2.2.6.min.js',
	            // 网络嗅探地址 [http(s)://]cdn.ronghub.com/RongIMLib-2.2.6.min.js 可选
	            rate: [100, 1000, 3000, 6000, 10000]
	        };
	        RongIMClient.reconnect(callback, config);
}
