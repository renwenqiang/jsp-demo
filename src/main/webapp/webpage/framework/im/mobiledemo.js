/**
 * 
 */
define([],function(){
	$.ajax("webpage/framework/im/im.js",{async:false,dataType:"script",cache:true});
	$.ajax("webpage/framework/im/ui.js",{async:false,dataType:"script",cache:true});
	
	var im=sunz.im;
	im.baseUrl="ws://localhost:8080/ytccpsys/";
	
	var jele=$('<div style="width:100%;height:100%;background-color:#ccc;"></div>');
	var session=window.session=im.ui.createSession(jele,"48px",{id:123,name:"张"},{id:234,name:"刘"});
	session.onSendMessage.add(function(msg){
		im.sendMsg({t:"text",content:{toUser:session.toUser.id,content:msg}});
	});
	im.onMsgReceived.add(function(msg){
		if(msg.toUser==session.toUser.id)
			session.showMsg(msg.content,false,msg.sendTime);
	});
	
	return {
		title:"聊天会话",
		buttons:[],
		panel:{domNode:jele[0]},
		setParam:function(param){
			im.connect(param);
		}
	};
});