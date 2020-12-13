<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
	<%@include file="/sunzbase.jsp"%>
	<title>移动端在线调试控制台</title>
	<z:resource items="jquery,easyui,sunzui"></z:resource>
	<z:dict items=""></z:dict>
	<style type="text/css">
		.history-item,#status,#txtCode{word-break:break-all;white-space:pre-wrap;line-height:20px;}
		.history-item{margin-bottom:2px;padding:5px 5px 5px 15px;background-color:#eee;}
		.history-js{color:#339;cursor:pointer}
		#status{color:#933}
		#btnSend,#btnLink{padding:0 15px}
	</style>
</head>
<body>
	<div class="easyui-layout"	data-options="'fit':true,'xtype':'Layout'">
		<div data-options="'region':'center'">
			<div class="easyui-layout"	data-options="'fit':true">
				<div data-options="'region':'center','title':'代码输入','xtype':'Sublayout'">
					<textarea id="txtCode" style="width:100%; height: 100%;padding:15px;display:inline-block;"></textarea>
				</div>
				<div data-options="'region':'south','height':'50%','xtype':'Sublayout','split':true">
					<div style="text-align:center;padding:15px">
						<a id="btnSend" class="easyui-linkbutton" data-options="'iconCls':'sunz','xtype':'Linkbutton','text':'执行'" ></a>
						<a id="btnLink" class="easyui-linkbutton" data-options="'iconCls':'sunz','xtype':'Linkbutton','text':'重连'" style="display:none"></a>
					</div>
					<div id="status"></div>
				</div>
			</div>
		</div>
		<div data-options="'region':'east','width':'600','split':true,title:'历史记录'">
			<div id="history" style="width:100%;height:100%;border:none;word-spacing:5px">
			</div>
		</div>
	</div>
</body>
</html>
<script type="text/javascript">	
	window.onload=null;
	let websocket,
		hKeep=0,
		liveMsg="showMsg('持续调试中');";
	function keepAlive(){clearInterval(hKeep);hKeep=setInterval(function(){websocket.send(liveMsg)},59000);};
	
	var link=function(){
		websocket = new WebSocket(webRoot.replace(/^http/,"ws")+"mobile/onlinedebug");
		//连接发生错误的回调方法
		websocket.onerror = function () {
			showLog("WebSocket连接发生错误");
		};

		//连接成功建立的回调方法
		websocket.onopen = function () {
			keepAlive();
			$("#btnLink").hide();
			showLog("WebSocket连接成功");
		}

		//接收到消息的回调方法
		var lastMsg=null;
		websocket.onmessage = function (event) {
			var msg=event.data;
			if(liveMsg==msg)return;
			if(lastMsg==msg) return;
			setMessageInnerHTML(lastMsg=msg,"history-js");
		}

		//连接关闭的回调方法
		websocket.onclose = function () {
			showLog("WebSocket连接闭关");
			$("#btnLink").show();
		}

		//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
		window.onbeforeunload = function () {
			websocket.close();
		}
	}
	
	var dstatus=$("#status"),showLog=function(msg){
		return dstatus.text(new Date().toLocaleTimeString()+"\t"+msg);
	};
	var dlog=$("#history").on("dblclick",".history-js",function(){
		$("textarea").val(this.textContent)
	});
	function setMessageInnerHTML(innerHTML,cls) {
		$('<pre class="history-item '+cls+'">').text(innerHTML).appendTo(dlog);
	}
	$(function(){
		link();
		$("#btnSend").linkbutton({"onClick":function(){
				var js=$("#txtCode").val();
				websocket.send(js);
				$('<pre class="history-item history-js">').text(js).appendTo(showLog("脚本已发送："));
				keepAlive();
			}
		});
		$("#btnLink").on("click",link);
		$("textarea").on("keydown",function(e){if(e.keyCode===9){
			var position=this.selectionStart+1;
			this.value=this.value.substr(0,this.selectionStart)+'\t'+this.value.substr(this.selectionStart);
			this.selectionStart=position;
			this.selectionEnd=position;
			this.focus();
			e.preventDefault();
		}});
	});
</script>

<script type="text/javascript" src="plug-in/requirejs/require.js"></script>
