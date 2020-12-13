

SunzIM.prototype.getConversationList = function(callback){	
	/*
	文档：http://www.rongcloud.cn/docs/web_api_demo.html#会话接口
		http://www.rongcloud.cn/docs/web.html#5_2、同步会话列表
		http://www.rongcloud.cn/docs/api/js/Conversation.html

	历史消息云存储开通位置：https://developer.rongcloud.cn/service/roam/rXxI4IAJjxRAD72SpQ==

	注意事项：
		1：一定一定一定要先开通 历史消息云存储 功能，本服务收费，测试环境可免费开通
		2：只有发过消息才能生成会话
	*/

	var conversationTypes = null;  //具体格式设置需要补充
	var limit = 150; //获取会话的数量，不传或传null为全部，暂不支持分页
	var start = new Date().getTime();
	this.instance.getConversationList({
		onSuccess: function(list) {
			// list.sort(function(a,b){
			// 	return a.sentTime > b.sentTime;
			// });
			callback&&callback(list);
			var title = "成功获取 " + list.length + " 个会话";
            //showResult(title,list,start);
		},
		onError: function(error) {
            //showResult("获取会话失败",error,start);
		}
    }, conversationTypes, limit);
}

SunzIM.prototype.sendTextMessage = function( targetId, msgstr,userInfo){
	/*
	文档： http://www.rongcloud.cn/docs/web.html#5_1、发送消息
		   http://rongcloud.cn/docs/api/js/TextMessage.html
	1: 单条消息整体不得大于128K
	2: conversatinType 类型是 number，targetId 类型是 string
	*/

	/*
		1、不要多端登陆，保证所有端都离线
		2、接收 push 设备设置:
			（1）打开系统通知提醒
			（2）小米设置 “授权管理” －> “自己的应用” 为自启动
			（3）应用内不要屏蔽新消息通知
		3、内置消息类型，默认 push，自定义消息类型需要
		   pushData 显示逻辑顺序：自定义 > 默认
		4、发送其他消息类型与发送 TextMessage 逻辑、方式一致
	*/
	var conversationType = 1;
	var pushData = "pushData" + Date.now();

	var isMentioned = false;

	var content = {
		content: msgstr ,
		user : userInfo||RongIMUerInfo,
		extra: "{\"key\":\"value\", \"key2\" : 12, \"key3\":true}"
	};

	var msg = new RongIMLib.TextMessage(content);
	
	var start = new Date().getTime();
	this.instance.sendMessage(conversationType, targetId, msg, {
        onSuccess: function (message) {
        	//markMessage(message);
            showResult("发送文字消息 成功",message,start);
        },
        onError: function (errorCode,message) {
            showResult("发送文字消息 失败",message,start);
        }
    }, isMentioned, pushData);
}