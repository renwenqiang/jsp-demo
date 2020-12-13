<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!Doctype html>
<html>
<head>
<%@include file="/sunzbase.jsp"%>
<z:resource items="weixin,jquery,sunzmobile"></z:resource>
 <z:dict items="all"></z:dict> 
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,
initial-scale=1.0,
maximum-scale=1.0,
user-scalable=no">

    <title>index</title>
   </head>

<body  style=" 
    overflow-x: hidden; ">
  

 
</body>
   <script> 
   var swiper = sunz.MCarousel.create({parent:"body",
	   children:["http://www.runoob.com/wp-content/uploads/2014/07/slide1.png",
	             "http://www.runoob.com/wp-content/uploads/2014/07/slide2.png",
	             "http://www.runoob.com/wp-content/uploads/2014/07/slide3.png"],
	   listener:{
		   "itemClick": function(){
			   console.log(arguments)
		   }
   
	   },
	   height:"400px"
   });
   
   
   var panel = sunz.MPanel.create({ title:"基本信息",bottom:"  ",parent:"body"});
   
 var slider = sunz.MSliderBar.create({parent:panel.contentNode,
	 value:15,
	 min:11,
	 max:33,
	 maxText:"33",
	 minText:"11",
	 listener:{
		 "change":function(){
			 //console.trace();
			 console.log(arguments[2]);
			 na.setValue(arguments[2])
		 }
	 } 
 }); 
   
   var form = sunz.MForm.create({parent:panel.contentNode}); 
   var split = sunz.MSplitBar.create({text:"基本信息",parent:form.contentNode});
   var na  = sunz.MTextbox.create({label:"姓名",readonly:false, parent:form.contentNode,value:"111",
	   rightButton:{ html:"菜s单",
        rightButtonWidth: "40px",
       rightButtonClass:"",onclick:function(){
    	   
    	   console.log(this)
       }}});
   //$(form.contentNode).append(textinput.domNode);
   na.markInvalid();
   var age = sunz.MTextbox.create({label:"年龄",type:"number",inline:true,parent:form.contentNode,listener:{
	   //"click":function(){alert(1)},
	   "create":function(e,a1,a2){console.error("年龄 初始化事件");console.log(arguments)},
	   "destroy":function(e){console.error("年龄 销毁事件");return false}
   }});
   age.bind("destroy",function(e){console.error("年龄 销毁事件2");return false});
   //$(form.contentNode).append(age.domNode);
   
   //age.markInvalid();
   
   var date = sunz.MTextbox.create({label:"生日",name:"birth",type:"date",inline:true,parent:form.contentNode});
   //$(form.contentNode).append(date.domNode);
   //date.markInvalid();
   
   
   var sex = sunz.MComboBox.create({label:"性别",inline:true,parent:form.contentNode,data:[{text:"请选择",value:""},{text:"男",value:"man"},{text:"女",value:"woman"}]});
   //$(form.contentNode).append(sex.domNode);
   var data = [];
   for (var i = 0; i < 10; i++) {
       data.push({ title: i });
   }
   
   var list = sunz.MList.create({data:data,parent:"body",multiple:true});
   
   var remeber = sunz.MSwitch.create({ label:"记住名称",inline:true,parent:form.contentNode, checked:false})
    
   
   var button = sunz.MButton.create({text:"提交",parent:panel.bottomNode,
	   listener:{
		   "click":function(){
			   alert("保存","保存成功")
			   //window.win = sunz.MWindow.create({title:"保存",content:"保存成功",parent:"body"});
			   //win.onOk(function(){alert("ok")}).onCancel(function(){alert("cancel")})
			   
		   }
	   }});
   
   var split = sunz.MSplitBar.create({text:"其他信息",parent:form.contentNode,noborder:true});
   var sws = sunz.MSwitchGroup.create({
	    label: "人员",
	    name: "people",
	    parent: form.contentNode,
	    data: [{
	        text: "Tom",
	        value: "Tom"
	    }, {
	        text: "Tony",
	        value: "Tony"
	    }],
	    listener: {
	        change: function() {
	            sunz.alert("change")
	        }
	    }
	});
   
   var split = sunz.MSplitBar.create({text:"这个是一个空白行",parent:form.contentNode});
   var ta = sunz.MTextArea.create({label:"简介",type:"date",name:"jianjie",height:"100px",inline: false,parent:form.contentNode})
   var img = sunz.MFileBox.create({label:"附件",type:"file",inline:true,parent:form.contentNode});
   
   
   var bt6 = sunz.MButton.create({text:"显示弹框",parent:"body",onclick:function(){
	   //alert("触发事件")
	   },
	   listener:{
		   "click":function(e){
			   console.log(e) ;
			   sunz.MActiveBottom.create({title:"<div style='margin:5px;color:blue ;'>标题（HTML dom，自己定义样式）</div>",content:"<div style='height:100px;color:red;'>内容放这里，自己定义样式,支持dom</div>"});
			   
		   }
	   }});
   
   var bt2 = sunz.MButton.create({text:"显示窗口",parent:"body",onclick:function(){
	   //alert("触发事件")
	   },
	   listener:{
		   "click":function(e){
			   console.log(e) ;
			   showMenu(e)
			   
		   }
	   }});
   var bt3 = sunz.MButton.create({text:"显示菜单",parent:"body",onclick:function(){
	   //alert("触发事件")
	   },
	   listener:{
		   "click":function(e){
			   console.log(e) ;
			   showMenu(e)
			   
		   }
	   }});
   var bt3 = sunz.MButton.create({text:"显示菜单",parent:"body",onclick:function(){
	   //alert("触发事件")
	   },
	   listener:{
		   "click":function(e){
			   console.log(e) ;
			   showMenu(e)
			   
		   }
	   }});   
   var bt3 = sunz.MButton.create({text:"显示菜单",parent:"body",onclick:function(){
	   //alert("触发事件")
	   },
	   listener:{
		   "click":function(e){
			   console.log(e) ;
			   showMenu(e)
			   
		   }
	   }});
   var bt3 = sunz.MButton.create({text:"微信分享",parent:"body",onclick:function(){
	   //alert("触发事件")
	   },
	   listener:{
		   "click":function(e){
			   SunzWX.mobileShare(location.href,"分享测试","分享测试测试测试测试测试测试","http://www.leechg.com/lee/upload/file20171108170111pm13612",function(e){
				   alert(JSON.stringify(e))
			   })
			   
		   }
	   }});
   var lx = sunz.MDictComboBox.create({
	    label: "类型",
	    inline: true,
	    parent: form.contentNode,
	    dict: "3D244E9C9F64C4DBE05010AC09C807A6",
	    valueField: "id",multiple:true,
	    listener: {
	        change: function() {
	            sunz.alert(""+this.getValue())
	        }
	    }
	});
   	var xb2 = sunz.MSwitchGroup.create({
	    label: "类型",
	    name: "xx",
	    parent: form.contentNode,
	    dict: "3D244E9C9F64C4DBE05010AC09C807A6",
	    issingle:true,
	     
	    listener: {
	        
	    }
	});
   
   //issingle
  // var sw = new sunz.Swipe($("body"),{
	  // onEnd:function(){console.log("onend")}
   //});
   
   
 
   var textinput = sunz.MTextbox.create({label:"姓名",readonly:false,disabled:"disabled",inline:false,parent:"body"});
   var split = sunz.MSplitBar.create({text:"这个是一个空白行",parent:"body"});
   var textinput = sunz.MTextbox.create({label:"姓名",readonly:false,disabled:"disabled",inline:false,parent:"body"});
   
   function showMenu(e){
	   var data = [];
	   for(var i=0;i<9;i++){
		   data.push({title:i});
	   }
	   data = [{title:"帮助",__iconCls:"glyphicon-arrow-left"},{title:"分享"},{title:"消息提醒"},{title:"退出登录"},{title:"关于我们"}]
	   window.menu = sunz.MMenu.create({event:e,target:e.target,data:data,itemclick:function(){
		   console.log(this.data)
	   }});
   }
   //open("https://www.baidu.com")
   </script> 
   <script>
 
 </script>
</html>