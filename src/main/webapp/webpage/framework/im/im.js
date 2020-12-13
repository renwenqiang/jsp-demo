/**
 * 
 */
window.sunz=window.sunz||{};
window.sunz.im=window.sunz.im||{};
(function(im){
	var conversationTypes={One:1,Discuss:2,Group:3,System:6},
		messageTypes={Text:"TextMessage",Image:"ImageMessage",DiscussionNotification:"DiscussionNotificationMessage",Voice:"VoiceMessage",RichContent:"RichContentMessage",Handshake:"HandshakeMessage",Unknown:"UnknownMessage",Location:"LocationMessage",InformationNotification:"InformationNotificationMessage",ContactNotification:"ContactNotificationMessage",ProfileNotification:"ProfileNotificationMessage",CommandNotification:"CommandNotificationMessage",Command:"CommandMessage",TypingStatus:"TypingStatusMessage",ChangeModeResponse:"ChangeModeResponseMessage",ChangeMode:"ChangeModeMessage",Evaluate:"EvaluateMessage",HandShake:"HandShakeMessage",HandShakeResponse:"HandShakeResponseMessage",Suspend:"SuspendMessage",Terminate:"TerminateMessage",CustomerContact:"CustomerContact",CustomerStatusUpdate:"CustomerStatusUpdateMessage",SyncReadStatus:"SyncReadStatusMessage",ReadReceiptRequest:"ReadReceiptRequestMessage",ReadReceiptResponse:"ReadReceiptResponseMessage",File:"FileMessage",Accept:"AcceptMessage",Ringing:"RingingMessage",Summary:"SummaryMessage",Hungup:"HungupMessage",Invite:"InviteMessage",MediaModify:"MediaModifyMessage",MemberModify:"MemberModifyMessage",JrmfReadPacket:"JrmfReadPacketMessage",JrmfReadPacketOpened:"JrmfReadPacketOpenedMessage",GroupNotification:"GroupNotificationMessage",PublicServiceRichContent:"PublicServiceRichContentMessage",PublicServiceMultiRichContent:"PublicServiceMultiRichContentMessage",PublicServiceCommand:"PublicServiceCommandMessage",RecallCommand:"RecallCommandMessage",ReadReceipt:"ReadReceiptMessage"},
		c=conversationTypes,m=messageTypes,
		//displayableCTypes=[c.One,c.Discuss,c.Group],
		displayableMTypes=[m.Text,m.Image,m.Voice,m.RichContent];
	
	var sender,
		isconnected=false,
		connect=function(){
			var webSocket=sender=new WebSocket(im.baseUrl+im.socketUrl+"?"+im.userid);
			webSocket.onerror=function(e){console.log("即时通讯断开",e);};
			webSocket.onopen=function(){
				console.log("即时通讯已连接");
				isconnected=true;
				im.onConnected.fire();
			};
			webSocket.onclose=function(){ // 重连
				console.log("即时通讯已断开");
				var lastSuccess=isconnected;
				isconnected=false;
				im.onDisconnected.fire();
				if(lastSuccess) // 连不上就不连了
					connect();
			};
			webSocket.onmessage=function(msg){
				console.log("收到消息",msg);			
				im.onMsgReceived.fire($.decode(msg.data));
			};
		};
	$.extend(im,{
		// 属性/常量
		CType:conversationTypes,
		MType:messageTypes,
		baseUrl:window.location.protocol.replace("http","ws")+"//"+window.location.host,
		socketUrl:"/framework/im",
		voiceUrl:"webpage/framework/im/newMsg.mp3",
		userid:null,
		// 事件
		onConnected:$.Callbacks(),
		onDisconnected:$.Callbacks(),
		onMsgReceived:$.Callbacks(),
		onMsgSended:$.Callbacks(),
		onGroupDismissed:$.Callbacks(),
		onUserQuitGroup:$.Callbacks(),
		onUserInfoChanged:$.Callbacks(),
		onGroupInfoChanged:$.Callbacks(),
		// 方法
		isConnected:function(){
			return isconnected;
		},
		connect:function(uid){
			this.userid=uid;
			connect();
		},
		reconnect:function(){
			if(isconnected) 
				return;
			connect();
		},
		getSessionList:function(ctype,callbacks){
			
		},
		removeSession:function(ctype,targetId,callbacks){
			
		},
		sendMsg:function(ctype,mtype,targetId,msg,callbacks){	
			msg={ctype:ctype,mtype:mtype,targetId:targetId,content:msg};
			sender.send($.encode(msg));
			this.onMsgSended.fire(msg);
		},
		getHistoryMsg:function(ctype,toid,time,limit,callbacks){
			
		},
		setMsgReaded:function(msgid,callbacks){
			
		},
		pullbackMsg:function(msgid,callbacks){
			
		},		
		dismissGroup:function(groupid,callbacks){
			
		},
		quitGroup:function(groupid){
			
		},
		getBlackList:function(){
			
		},
		addToBlack:function(uid,callbacks){
			
		},
		removeFromBlack:function(uid,callbacks){
			
		},
		getGroupList:function(){
			
		},
		getGroupInfo:function(groupid,callbacks){
			
		},
		createGroup:function(args,callbacks){
			
		},
		renameGroup:function(groupid,newname,callbacks){
			
		},		
		addToGroup:function(groupid,uid,callbacks){
			
		},
		removeFromGroup:function(groupid,uid,callbacks){
			
		},
		getUserInfo:function(uid,groupid){
			
		},
		isMsgDisplayable:function(msg){
			return displayableMTypes.indexOf(msg.mtype)>-1;
		}
	});
})(window.sunz.im);