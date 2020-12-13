<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!Doctype html>
<html>
<head>
<%@include file="/sunzbase.jsp"%>

 
<z:resource items="jquery,easyui,sunzmobile"></z:resource>
 <z:dict items="all"></z:dict> 
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,
initial-scale=1.0,
maximum-scale=1.0,
user-scalable=no">

    <title>index</title>
    <style>
    html,body{
    	height:100%;
    	width:100%;
    	margin:0px;
    	padding:0px;
    }
    .title{
    	font-size:30px;    padding: 7px;
    }
    .appuserinfo{
    	    width: 200px;
	    height: 100%;
	    -background-color: red;
	    float: right;
	    position: absolute;
	    right: 0;
	    top: 0;
    }
    .appuserimg{    border: 1px solid #00BCD4;
    	    height: 45px;
    width: 45px;
    border-radius: 40px;
    background-color: #2196F3;
    float: right;
    /* margin: 3px; */
    position: absolute;
    top: 2px;
    right: 2px;
    background-size: contain;
    }
    .appusername{
         font-size: 23px;
    position: absolute;
    top: 14px;
    right: 65px;

       }
       .conversation_item{
           line-height: 42px;
	    padding-left: 17px;
	    font-size: 20px;
	    margin-bottom: 3px;
	    cursor: pointer;
	    background-color: #d1ebf7;
       }
       #ConversationDiv2{
       height:100%;
    	width:100%;
    	margin:0px;
    	padding:0px;
       }
       .conversationInput{
        height:calc(100% - 100px);resize: none;
    	width: 100% ;
    	margin:0px;
    font-size: 20px;
    padding: 10px;
       }
       .ConversationDivInputbtn{
       float:right;
       }
       #ConversationDetailDiv{
       font-size:20px;line-height: 30px;
       }
    </style>
   </head>

<body  style=" 
    overflow-x: hidden; ">
	<div class="easyui-layout" style="width:100%;height:100%;">
		<div data-options="region:'north'" style="height:50px">
			<div class="title">IM</div>
			<div class="appuserinfo">
				<div class="appusername">
				</div>
				<div class="appuserimg">
				</div>
			</div>
		</div>
		<div data-options="region:'south',split:true" style="height:50px;"></div> 
		<div id="ConversationListDiv" data-options="region:'west',split:true" title="会话列表" style="width:200px;">
		
		</div>
		<div id="ConversationDiv" data-options="region:'center',title:'',iconCls:'icon-ok'">
			 
		</div>
	</div>
</body>
<script type="text/javascript" src="framework/rongim.do?js"></script>
   <script> 
    $(function(){ 
    	sunz.IM.connect(function(userid){
    		getConversationList();
    	});
    	appInit();
    	
    })
   showResult = function(e){
    	 
    }
    
    
    appInit= function(){
    	$(".appusername").html(RongIMUerInfo.name);
    	$(".appuserimg").css("background-image","url("+RongIMUerInfo.portrait+")");
    }
    
    getConversationList = function( ){
    	sunz.IM.getConversationList(function(result){
    		//console.log(result);
    		window.ConversationList = sunz.MList.create({
    			type:"panel",
    			parent:"#ConversationListDiv",
    			data: result,
    			template: templateFunction,
    			itemclick: function(obj){
    				initConversation(this.data)
    			}
    		})
    		
    	}) 
    }
    function templateFunction(obj){
    	var html = '<div class="conversation_item"></div>' 
			var userInfo = {};
			if(obj.latestMessage&&obj.latestMessage.content){
				//userInfo = obj.latestMessage.content.user;
			}
			if(obj.conversationType==1){//单人对话
				html = '<div class="conversation_item">{name}</div>' ;
				html = sunz.tplReplace(html, userInfo);
				var url = "framework/rongim.do?getUser&userId=" + obj.targetId
				$.ajax({
					url:url,
					dataType:"json",
					success: function(result){
						//console.log(result);
						var idhtml = '{name}';
						if(result.data){
							obj.userInfo = result.data;
							idhtml = sunz.tplReplace(idhtml, result.data);
						}
						dom.html(idhtml);
					}
				})
			}
			var dom = $(html);
			
			return dom;
    }
    
    
    function initConversation(conversation){ 
    	try{
	    	$("#ConversationDiv").html("");
    		
    	}catch(e){
    		console.log(e)
    	}
    	$("#ConversationDiv").html('<div id="ConversationDiv2"><div id="ConversationDetailDiv" data-options="region:\'center\',title:\''+conversation.userInfo.name+'\' "></div>'+ 
    			'<div id="ConversationDivInput" data-options="region:\'south\',split:true" style="height:250px;"></div> </div>');
    	$("#ConversationDiv2").layout()
    	
    	$("#ConversationDivInput").html("<textarea class='conversationInput'></textarea>");
    	
    	sunz.MButton.create({
    		parent:"#ConversationDivInput",
    		text:"发送",
    		type:"primary",
    		class:"ConversationDivInputbtn",
    		handler: function(){
    			var msg = $(".conversationInput").val();
    			$(".conversationInput").val("");
    			
    			var html = $("#ConversationDetailDiv").html();
    	    	html +=  "我: "+msg ;
    	    	html += "</br>"
    	    	$("#ConversationDetailDiv").html(html) 
    	    	
    			sunz.IM.sendTextMessage(conversation.userInfo.id,msg);
    		}
    	})
    	
    	if(conversation.conversationType==1){
    		
    		
    	}
    }
    
    function receiveNewMessage(message){
    	var html = $("#ConversationDetailDiv").html();
    	html += (message.content.user.name + " : "+message.content.content);
    	html += "</br>"
    	$("#ConversationDetailDiv").html(html) 
    }
   </script> 
   <script>
 
 </script>
</html>