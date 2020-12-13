<%@ page contentType="text/html; charset=UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<%@include file="ext.jsp"%>
<link rel="stylesheet" type="text/css" href="webpage/framework/system/recv/extjs3.0/resources/css/ext-all.css" />
<head>
<script type="text/javascript" src="webpage/framework/system/recv/swfobject.js"></script>
<script type="text/javascript">
	Date.prototype.Format = function(fmt){
	  var o = {   
	    "M+" : this.getMonth()+1,                 //月份   
	    "d+" : this.getDate(),                    //日   
	    "h+" : this.getHours(),                   //小时   
	    "m+" : this.getMinutes(),                 //分   
	    "s+" : this.getSeconds(),                 //秒   
	    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
	    "S"  : this.getMilliseconds()             //毫秒   
	  };   
	  if(/(y+)/.test(fmt))   
	    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
	  for(var k in o)   
	    if(new RegExp("("+ k +")").test(fmt))   
	  		fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
	  
	  return fmt;   
	} ;
	var imgId='${id}';
	if(imgId==''){
		alert("参数错误，无法加载图片");
		window.close();
	}
	var imgeditor=null;
	var editorWidth='100%',editorHeight='100%';
	var swfVersionStr = "11.1.0";
	var xiSwfUrlStr = "";
	var imgUrl='${imgUrl}';
	var flashvars = {imageUrl:imgUrl,width:editorWidth,height:editorHeight};
	var params = {
			quality : "high",
			bgcolor:"#ffffff",
			play:"true",
			loop:"true",
			wmode:"window",
			scale:"noScale",
			menu:"false",
			devicefont:"false",
			salign:"TL",
			allowscriptaccess:"sameDomain"
	};
	var attributes={id : "imgedit",name :"imgedit",align: "middle"};
	
	var bWidth=75; 
	var divEditor={xtype:'panel',layout:'fit',region:'center',html:'<div style="width:100%;height:100%" id="divswf">\
		<a href="http://www.adobe.com/go/getflash">\
			<img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="获得 Adobe Flash Player" />\
		</a>\
		<p>此页要求 Flash Player 版本 11.1.0 或更高版本。</p>\
		</div>'};
	
	function getQueryUrl(post){
		var p=imgeditor.getCutRect();
		var str='framework/image.do?'+post+'&time='+new Date().getTime()+'&id='+imgId;
		for(var o in p){
			str=str+'&'+o+'='+p[o];
		}
		return str;
	}
	function edit(){
		if(!confirm("确定要编辑吗?")){
			return;
		}		
		imgeditor.load(getQueryUrl("save"));
	}	
	function refreshSource(){
		if(this.owner&&this.owner.refreshImage){
			this.owner.refreshImage();
		}
	}
	this.onbeforeunload=refreshSource;
	var btnLoad={xtype:'button',width:bWidth,text:'还原文件',handler:function(){if(!confirm("确定要进行恢复吗?"))return;imgeditor.load('framework/image.do?revert&time='+new Date().getTime()+'&id='+imgId);}};
	var btnPan={xtype:'button',width:bWidth,text:'平移...',handler:function(){imgeditor.beginPan();}};
	var btnZoomin={xtype:'button',width:bWidth,text:'放大',handler:function(){imgeditor.zoomImage(1.2);}};
	var btnZoomout={xtype:'button',width:bWidth,text:'缩小',handler:function(){imgeditor.zoomImage(0.8);}};
	var btnRotate={xtype:'button',width:bWidth,text:'旋转',handler:function(){imgeditor.setRotateHander('showRotation');imgeditor.beginRotate();}};
	var nRotate=new Ext.form.NumberField({width:bWidth,value:0,listeners:{'specialkey':function(field, e){if (e.getKey() == e.ENTER)imgeditor.rotate(nRotate.getValue());}}});
	var showRotation=function (v){nRotate.setValue(v);};
	var btnCut={xtype:'button',width:bWidth,text:'裁切',handler:function(){imgeditor.beginCut();}};
	var btnOK={xtype:'button',width:bWidth,text:'保存',handler:edit};
	var btnCancel={xtype:'button',width:bWidth,text:'关闭',handler:function(){window.close();}};
	var btnPrint={xtype:'button',width:bWidth,text:'打印',handler:function(){imgeditor.printImage();/*window.open(imgUrl,'打印','location=no,menubar=no,titlebar=no,toolbar=no,z-look=yes').print();*/}};
	var btnPrint2={xtype:'button',width:bWidth,text:'打印...',handler:function(){
		var ele=document.createElement("object");
		ele.classid="clsid:4534AD5E-9A2E-4604-9A5D-F92BEC0497E1";
		ele.FullView=false;
		//ele.AddText("房屋登记档案图像",300,25,"宋体",1,20,-16777216); // text,x,y,font,fontStyle,font-size,color
		//ele.AddText("哈尔滨市阿城区房地产事业管理局",25,1110,"宋体",1,14,-16777216);
		//ele.AddText(cstUser.name,450,1110,"宋体",0,14,-16777216);
		//ele.AddText((new Date()).Format("yyyy-MM-dd hh:mm:ss"),600,1110,"宋体",0,14,-16777216);
		
		var fx=40,fy=70,fw=720,fh=1020;
		var r=imgeditor.getCutRect();
		// 图像的大小要用矩形来计算
		var iw=Math.round(Math.sqrt((r.x1-r.x2)*(r.x1-r.x2)+(r.y1-r.y2)*(r.y1-r.y2)))
		,ih=Math.round(Math.sqrt((r.x3-r.x2)*(r.x3-r.x2)+(r.y3-r.y2)*(r.y3-r.y2)));
		if(iw>fw|| ih>fh){
			var ratex=iw/fw,ratey=ih/fh;
			iw=ratex<ratey?iw/ratey:fw;
			ih=ratex<ratey?fh:ih/ratex;
		}
		fx=Math.round(fx+(fw-iw)/2),fy=Math.round(fy+(fh-ih)/2);		
		ele.AddImage(webRoot+getQueryUrl("edit"),fx,fy,iw,ih,'123'); // url,x,y,width,height,error
		ele.Print();
	}};
	var btnSaveAs={xtype:'button',width:bWidth,text:'另存为',handler:function(){imgeditor.load(getQueryUrl("edit"));}};//window.location=getQueryUrl("edit");}};
	
	Ext.onReady(function() {
		//是否归档   archived =true说明是归档环节。可以保存。其他都不可以保存图片
		var step=(opener||parent).parent.parent.parent.stepname
			,archived=step=="归档";
			
		var arrtbar=[
				       btnPan,
				       btnZoomin,
				       btnZoomout,
				       btnRotate,
				       nRotate 
				       ];
		var tempbar=[];
		tempbar=[btnLoad,btnCut,btnPrint2,btnSaveAs,btnOK,btnCancel];
	/* 	if(cstUser.HasRight('R004',3)){
			tempbar=[btnCut,btnPrint2,btnSaveAs,btnCancel];
		} 
		if(cstUser.HasRight('R005',3)){
			tempbar=[btnLoad,btnCut,btnSaveAs,btnOK,btnCancel];
		} */
        //既有裁剪又有保存的权限人员。需要判断当前环节是哪个。只有归档时才会有 两个一起
        //if(cstUser.HasRight('R004')&&cstUser.HasRight('R005')){  
       	//}
        
		//if(cstUser.name=='超级管理员'){
		//	tempbar=[btnLoad,btnCut,btnPrint2,btnSaveAs,btnOK,btnCancel];
		//}
		
		arrtbar=arrtbar.concat(tempbar);		   
		var win=new Ext.Window({
			frame:false,
			maximized:true,
			closable:false,
			layout:'border',
			tbar:arrtbar,
			items:[
			       divEditor
			]	
		});
		win.show();
		
		swfobject.createCSS("html", "height:100%; background-color: #eeeeee;");
		swfobject.createCSS("body", "margin:0; padding:0; overflow:hidden; height:100%;");
		
		swfobject.embedSWF("webpage/framework/system/recv/imgedit.swf", "divswf",editorWidth,editorHeight,swfVersionStr, xiSwfUrlStr,flashvars, params, attributes);
		
		imgeditor=swfobject.getObjectById("imgedit");
	});
		
</script>
</head>
<body>
	
</body>
</html>