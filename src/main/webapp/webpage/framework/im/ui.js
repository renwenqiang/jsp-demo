/**
 * im相关ui，以函数形式提供
 */
$('<link rel="stylesheet" href="webpage/framework/im/im.css">').appendTo(document.head);
window.sunz=window.sunz||{};
window.sunz.im=window.sunz.im||{};
sunz.im.ui={
	template:{
		chatList:'<ul class="chat-list"></ul>',
		chatFooter:'<div class="chat-foot" style="height:{footHeight}"></div>',
		chatSender:'<div class="chat-send"><textarea class="chat-input" type="text" autocomplete="off"></textarea><button class="chat-send-btn" >发送</button></div>',
		systemMessage:'<li msgid="{id}" class="chat-system {subtype}"><span raw="{raw}" time="{time}">{content}</span></li>',
		textMessage:'<li msgid="{id}" class="chat-li"><div class="chat-user"><img src="{photo}" uid="{id}" dyid="{dyid}" onerror="javascript:this.src=\'mobile/image/nopic.png\';"><cite>{name}</cite></div><div class="chat-text"><pre class="msgcontent">{content}</pre></div></li>',
		textMessageMine:'<li msgid="{id}" class="chat-li chat-mine"><div class="chat-user"><img  uid="{id}" dyid="{dyid}" src="{photo}" onerror="javascript:this.src=\'mobile/image/nopic.png\';"><cite>{name}</cite></div><div class="chat-text"><pre class="msgcontent">{content}</pre></div></li>',
		sessionItem:'<li class="chat-session"><div><img src="{photo}" onerror="javascript:this.src=\'mobile/image/nopic.png\';"></div><span class="target">{name}</span><p><span class="msger">{lastMsger}</span><span class="msgSplit">:</span><span class="msg">{lastMsg}</span></p><span class="msg-time">{lastTime}</span><span class="msg-status">{newCount}</span></li>',
	}
};
(function(){
	var assert=function(user){
		if(!user.id){
			console.log("用户id为必须");
		}
		if(!user.photo){
			console.log("用户没有指定头像");
		}
		if(!user.name){
			console.log("用户名是必须的");
		}
	};
	var ui=sunz.im.ui;
	ui.formatDate=function(time){
		if($.isNumeric(time))
			time=new Date(time);
		if(typeof time =="string")
			return time;
		
		var now=new Date(),stamp=now-time;
		//if(stamp<59000)
		//	return "刚刚";
		if(now.getDate()-time.getDate()==1)
			return "昨天"+time.format("hh:mm");
		//if(stamp<3600000)
		//	return Number.parseInt(stamp/60000)+"分钟前";
		if(stamp<86400000)
			return time.format("hh:mm");
		if(now.getYear()-time.getYear()==0)
			return time.format("MM-dd hh:mm");
		if(now.getYear()-time.getYear()==1)
			return "去年"+time.format("MM-dd");
		
		return time.format("yy-MM-dd");
		
	};
	ui.normalizeText=(patt=/[\ud800-\udbff][\udc00-\udfff]/g ,function(str) {
		str = str.replace(patt, function(char){
			var H, L, code;
			if (char.length===2) {
				H = char.charCodeAt(0); // 取出高位
				L = char.charCodeAt(1); // 取出低位
				code = (H - 0xD800) * 0x400 + 0x10000 + L - 0xDC00; // 转换算法
				return "&#" + code + ";";
			} else {
				return char;
			}
		});
		return str;
	});
	ui.createChatList=function(pele,me,target){
		var jlist=$(ui.template.chatList),domList=pele.jquery?pele[0]:pele;
		if(pele) 
			jlist.appendTo(pele);
		
		var lastSendTime=0,tolerence=60000;
		$.extend(jlist,{
			self:me,
			target:target,
			onRemoveMessage:$.Callbacks(),
			clearMsg:function(){
				jlist.empty();
			},
			removeMsg:function(id){
				jlist.find("#msg-"+id).remove();
				this.onRemoveMessage.fire(id);
			},
			setTimeTolerence:function(milliseconds){
				tolerence=milliseconds;
			},showMsg:function(content,user,time){
				var msg=typeof content=="string"?{content:content,sendTime:time}:content;
				msg.mine=user.id==me.id;
				$.extend(msg,user);
				if(msg.sendTime-lastSendTime>tolerence){
					$(tplReplace(ui.template.systemMessage,{time:time,content:ui.formatDate(time)})).appendTo(jlist);
					lastSendTime=time;
				}
				var jmsg=$(tplReplace(ui.template["textMessage"+(msg.mine?"Mine":"")],msg));				
				jlist.append(jmsg);
				//jlist.find("li:last")[0].scrollIntoView();
				domList.scrollTop=domList.scrollHeight;
				return jmsg;
			},showHistoryMsg:function(ms,referTime){
				if(!ms||!ms.length)
					return;
				
				var anchor=domList.scrollHeight;
				var earliest=ms[0].sendTime,
					jbegin=jlist.find("li:first"),
					jbefore=$(tplReplace(ui.template.systemMessage,{time:earliest,content:ui.formatDate(earliest)})).insertBefore(jbegin);
				$.each(ms,function(i,msg){
					if(msg.sendTime-earliest>tolerence){
						jbefore=$(tplReplace(ui.template.systemMessage,{time:earliest,content:ui.formatDate(earliest)})).insertAfter(jbefore);
						earliest=msg.sendTime;
					}
					$.extend(msg,sunz.im.getUserInfo(msg.fromUser));
					msg.mine=msg.fromUser==me.id;
					jbefore= $(tplReplace(ui.template["textMessage"+(msg.mine?"Mine":"")],msg)).insertAfter(jbefore);
				})
				//jbefore[0].scrollIntoView();
				domList.scrollTop=domList.scrollTop+domList.scrollHeight-anchor;
				return jbefore;
			}
		});
		
		return jlist;
	};
	
	ui.createFooter=function(pele,footHeight){
		var jdiv= $(tplReplace(ui.template.chatFooter,{footHeight:footHeight}));
		if(pele) 
			jdiv.appendTo(pele);
		
		return jdiv;
	};
	ui.createSender=function(pele){
		var jdiv=$(tplReplace(ui.template.chatSender,{})),
			jinput=jdiv.find(".chat-input"),
			jsend=jdiv.find(".chat-send-btn");
		if(pele) 
			jdiv.appendTo(pele);
		
		jdiv.onSendMessage=$.Callbacks()
		jsend.on("click",function(){
			var content=jinput.val();
			if(!content) return;
			
			jinput.val("");		
			jdiv.onSendMessage.fire(ui.normalizeText(content));
		});
		return jdiv;
	};
	ui.createChat=function(pele,footHeight,ctype,me,target){
		var mHeight='80%',fHeight='20%';
		if(footHeight){
			mHeight='calc(100% - '+footHeight+')';
			fHeight=footHeight;
		}
		var dlist=$('<div class="chat-main" style="height:'+mHeight+'"></div>').appendTo(pele),
			list=ui.createChatList(dlist,me,target),
			footer=ui.createFooter(pele,fHeight),
			sender=ui.createSender(footer);
		
		//sender.onSendMessage.add(function(content){list.showMsg(content,me,new Date().getTime());});	
		return {
				domNode:pele,
				listNode:dlist,
				footerNode:footer,
				senderNode:sender,
				fromUser:me,
				target:target,
				ctype:ctype,
				onSendMessage:sender.onSendMessage,
				onRemoveMessage:list.onRemoveMessage,
				clearMsg:list.clearMsg,
				removeMsg:list.removeMsg,
				setTimeTolerence:list.setTimeTolerence,
				showMsg:list.showMsg,
				showHistoryMsg:list.showHistoryMsg,
				scrollToNewst:function(){
					//var lmsg=list.find("li.chat-li:last")[0];
					//if(lmsg)
					//	lmsg.scrollIntoView();
					var dom=dlist[0];
					dom.scrollTop=dom.scrollHeight;
				}
		}; 		
	}	
})();
