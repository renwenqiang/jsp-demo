/**
 * 系统【实时】消息
 */
define(["mobile/framework/message/imessage","_listtemplates"],function(IM,templates){
	$('<style>\
		.imessages{display:none;position:fixed;top:60px;right:25px;z-index:10001;border:1px solid #eee;width:240px;}\
		.imessage-list{max-height:480;overflow-y:scroll;padding:5px 15px;background-color:#fff}\
		.imessage-more{line-height:36px;text-align:center;background-color:#fff;cursor:pointer;}\
		.imessage-item{cursor:pointer;border-bottom:1px solid #eee;padding:8px 0;position:relative}\
		.imessage-index{position:absolute;top:10px;left:-21px;width:18px;height:18px;border-radius:50%;border:1px solid #933;font-size:9px;text-align:center;line-height:18px;color:#fff}\
		.unread{background-color:#933;} .readed{background-color:#393} \
		.imessage-window{padding:5px 15px 5px 36px}\
	</style>').appendTo("head")
	var jele=$('<div class="imessages"><div class="imessage-list"></div><div class="imessage-more">查看更多</div></div>').appendTo("body"),
		jcontainer=jele.find(".imessage-list"),
		jproxy=$(".el-badge"),
		jbadge=jproxy.find(".el-badge__content");
	
	var h=0,deferHide=function(){
		h=setTimeout(function(){
			jele.fadeOut(1000);
		},200);
	};
	jproxy.on("mouseenter",function(){
		jele.fadeIn(200);
	}).on("mouseleave",deferHide);
	jele.on("mousemove",function(){
		if(h)clearTimeout(h);
		jele.stop(jele.show);
	}).on("mouseleave",deferHide);
	
	
	var list,hList;
	window.onMessageReceived.add(function(){
		if(hList)clearTimeout(hList);
		hList=setTimeout(function(){list && list.update()},1000);
	});
	var msgListUrl="framework/query.do?search&k=sys_user_message";
	var jmore=jele.find(".imessage-more").on("click",function(){
		//openwindow("消息查看","mobile/main.jsp?rely=pc_message")		
		list=new sunz.MList({
			parent:$('<div class="imessage-window"></div>').window({title:"消息查看",width:600,height:'80%',onClose:function(){list=null}}),
			url:msgListUrl,
			type:"panel",
			template:function(m){
				var code="pc_"+m.type;		// 规则：使用pc_前缀
				var unread=new Date(m.readtime)-getServerTime()>86400000; // 未读实际在几千年后，这儿使用1天作为容差
				var jitem=$('<div class="imessage-item"></div>')
				.on("click",function(){
					if(unread){
						setTimeout(function(){$.post("framework/datatable.do?save",{t:"t_s_message",id:m.id,timeFields:"readtime_"},function(jr){unread=false;jitem.find(".imessage-index").removeClass("unread").addClass("readed");jbadge.text(--msgCount);jele.find("#"+m.id).remove()});},200);
					}
				}).append('<div class="imessage-index '+(unread?"unread":"readed")+'">'+m._No+'</div>');
				
				var tpl=templates[code];
				if(tpl){
					var jcontent=tpl(m);
					if(typeof jcontent=="string")
						jcontent=$(jitem);
					jcontent.appendTo(jitem);
				}else{
					return $('<div class="imessage-unknown">【不可识别的消息】</div>').appendTo(jitem);
				}
				
				return jitem;
			},
			pagesize:10,
			nextMode:"button"
		});
	});	
	
	var msgCount=0,
		showMsg=function(m){
			m.time=m.time||new Date(getServerTime());
			var code=m.type;
			if(!code)				// 不认识的类型，丢弃消息
				return;
			
			code="pc_"+code;		// 规则：使用pc_前缀
			var tpl=templates[code]
			if(!tpl)				// 未定义模板，丢弃消息
				return;
			
			jbadge.text(++msgCount).show();
			var jitem=tpl(m);
			if(typeof jitem=="string")
				jitem=$(jitem);
			
			var jp=$('<div class="imessage-item" id="'+m.id+'"></div>').prependTo(jcontainer)
				.on("click",function(){
					setTimeout(function(){
						$.post("framework/datatable.do?save",{t:"t_s_message",id:m.id,timeFields:"readtime_"},function(jr){
							jp.slideUp(300,function(){jp.remove();jbadge.text(--msgCount)});
						});
					},200);
				});
			jitem.appendTo(jp);
		};
	window.onMessageReceived.add(showMsg);
	$.post(msgListUrl,{unread:true,limit:5},function(jr){
		if(jr.success){
			$.map(jr.data,showMsg);
			jbadge.text(msgCount=jr.total);
		}
	});
	
	
	var jframe=$("iframe");
	window.linkTo=function(url){jframe.attr("src",url);};
});