/**
 * 
 */
define([],function(){
	let suspend=false,websocket;
	let jele=$('<div style="width:100%;max-height:100%;overflow:scroll;background-color:#000;color:#0f0;line-height:26px;padding:5px 5px 5px 10px;word-spacing:4px;"></div>');
	function showMsg(msg) {
		return $("<pre><span>"+new Date().format("hh:mm:ss")+"  "+msg+"</span></pre>").appendTo(jele);
	}
	let debug=function(){
		suspend=false;
		if(!websocket){
			websocket = new WebSocket(webRoot.replace(/^http/,"ws")+"mobile/onlinedebug");
		}
		websocket.onerror = function () {
			showMsg("调试程序崩溃");
		};
		websocket.onopen = function () {
			jele.empty();
			showMsg("已进入调试模式")
			.append($('<span style="padding:0 15px;color:#58f">停止</span>').on("click",function(){
				if(websocket.readyState==3) 
					return (websocket=null,debug());
				
				let jme=$(this);
				websocket.addEventListener("close",()=>jme.text("重连"));
				websocket.close()
			}))
			.append($('<span style="padding:0 15px;color:#58f">暂停</span>').on("click",function(){
				suspend=!suspend;
				showMsg(suspend?"已暂停...":"已进入调试状态...")
				$(this).text(suspend?"继续":"暂停")
			}))
			.append('<span>\r\n<span style="color:transparent;">00:00:00  </span>等待...</span>')
		}
		websocket.onmessage = function (event) {
			if(suspend) {
				return showMsg("暂停中...").append($('<span style="padding:0 15px; color:#58f">继续</span>').on("click",()=>{suspend=false;showMsg("已进入调试状态...")}));
			}
			try{
				eval(event.data);
				showMsg("指令执行完毕\t等待...");
			}catch(e){
				showMsg(e);
			}
		}
		websocket.onclose = function () {
			$('<span style="margin-left:15px; color:#58f">重新连接</span>')
			.on("click",function(){
				websocket=null;
				debug();
				let jme=$(this);
				websocket.addEventListener("open",()=>jme.remove());
			})
			.appendTo(showMsg("调试已断开").css("padding","15px 0"));
		}
		window.onbeforeunload = function () {
			websocket.close();
		}
	}
	debug();
	$('<div style="z-index:9999;position:fixed;top:10px;right:15;padding:5px 10px;color:#f00;background-color:#fff;">控制台</div>')
	.appendTo(document.body)
	.on("click",function(){
		if(!jele.showned){
			jele.appendTo(viewport.container).show().css("position","absolute").css("width","100%").css("heigth",0).css("top",0).css("z-index",999);
			jele.showned=true;
		}else{
			jele.toggle();
		}
	});
	$.tip("已进入在线调试模式，等待指令...",2000);
	return;
	let jdiv=$('<div style="z-index:999;position:fixed;top:100px;right:25px;padding:5px 10px;color:#f00;background-color:#fff;">在线调试</div>')
	.appendTo(document.body)
	.on("click",function(){
		viewport.add({
			title:"在线调试",
			panel:{domNode:jele},
			onActive:function(){
				debug();
			},
			onDeactive:function(e){
				if(e.deta<0)
					suspend=false;
				jdiv.show();
			}
		});
		jdiv.hide();
	});
});