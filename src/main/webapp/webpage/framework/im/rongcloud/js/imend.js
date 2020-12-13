
var callbacks = {
		getInstance : function(_instance){
			instance = _instance;
		},
		receiveNewMessage : function(message){
			// 判断消息类型
			//showResult("新消息，类型为：" + message.messageType);
            //showResult(message.content.user.name + " : "+message.content.content);
			window.receiveNewMessage&&window.receiveNewMessage(message);
            console.log("messageUId:" + message.messageUId + ",   messageId:" + message.messageId);
            console.log(message);
		},
		getCurrentUser : function(userInfo){
			showTips("链接成功 用户id：" + userInfo.userId + ", 耗时" + getTimer(begin));
			userId = userInfo.userId;
		    afterConnected();
		}
	};

sunz.IM = new SunzIM(callbacks);