<%@ page language="java" pageEncoding="GBK"%>
<%@ include file="/baseext.jsp" %>
<%String root=request.getScheme() + "://" + request.getServerName()+ ":" + request.getServerPort() + request.getContextPath()+ "/";%>
<html>
<head>
<base href="<%=root%>">
<script type="text/javascript" src="public/js/swfobject.js"></script>
<script type="text/javascript">
	var cstTreeNodesJson = '${receives}';
	var cstTreeNodes = Ext.util.JSON.decode(cstTreeNodesJson);
	
	var nRoot=new Ext.tree.TreeNode({text : '扫描件'});
	var tImg=new Ext.tree.TreePanel({
		region:'west',
		split:true,
		width:200,
		title : '扫描件',
		border:false,
		autoScroll : true,
		animCollapse : true,
		rootVisible : false,
		collapsible : true,
		expanded : true,
		root : nRoot
	});
	
	var count=cstTreeNodes.length;
	for(var i=0;i<count;i++){
		var o=cstTreeNodes[i];
		var nType=new Ext.tree.TreeNode({
			text:o.text,
			autoScroll : true,
			animCollapse : true,			
			animCollapse : true,
			collapsible : true
		});
		nRoot.appendChild(nType);
		var iCount=o.children.length;
		for(var j=0;j<iCount;j++){
			var oSub=o.children[j];
			var nSub=new Ext.tree.TreeNode({
				text:oSub.text,
				leaf:true,
				listeners : {
				'click' : function(node, event){
					imgeditor.load(node.imgsrc);//node.imageid);
					imgeditor.setFrame(false);
					imgeditor.beginPan();
					}
				}
			});
			//nSub.imgsrc=oSub.data.filename;
			var imgid=oSub.id.substr(2);
			nSub.imgsrc='image_load.action?id='+imgid;
			//nSub.imageid=imgid;
			nType.appendChild(nSub);
		}
	}
		
	//
	var imgeditor=null;
	var editorWidth='100%',editorHeight='100%';
	var swfVersionStr = "11.1.0";
	var xiSwfUrlStr = "";
	var flashvars = {frame:false};
	var params = {
			quality : "high",
			bgcolor : "#ffffff",
			play : "true",
			loop : "true",
			wmode : "window",
			scale : "noScale",
			menu : "false",
			devicefont : "false",
			salign : "TL",
			allowscriptaccess : "sameDomain"
		};
	var attributes = {
			id : "imgedit",
			name : "imgedit",
			align : "middle"
		};
	
	var bWidth=75; 
	var divEditor={xtype:'panel',layout:'fit',region:'center',html:'<div style="width:100%;height:100%" id="divswf">\
		<a href="http://www.adobe.com/go/getflash">\
			<img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="获得 Adobe Flash Player" />\
		</a>\
		<p>此页要求 Flash Player 版本 11.1.0 或更高版本。</p>\
		</div>'};
	var btnPan={xtype:'button',width:bWidth,text:'平移...',handler:function(){imgeditor.pan();}};
	var btnRotate={xtype:'button',width:bWidth,text:'旋转...',handler:function(){imgeditor.setRotateHander('showRotation');imgeditor.beginRotate();}};
	var nRotate=new Ext.form.NumberField({width:bWidth,value:0,listeners:{'specialkey':function(field, e){if (e.getKey() == e.ENTER)imgeditor.rotate(nRotate.getValue());}}});
	var showRotation=function (v){nRotate.setValue(v);};
	Ext.onReady(function() {
		var pEditor=new Ext.FormPanel({
			region:'center',
			frame:false,
			layout:'border',			
			items:[
			       divEditor
			]	
		});
		var win=new Ext.Viewport({
			frame:false,
			border:false,
			layout:'border',
			items:[tImg,pEditor]
		});
		

		swfobject.embedSWF("net/frame/imgedit.swf", "divswf",editorWidth,editorHeight,swfVersionStr, xiSwfUrlStr,flashvars, params, attributes,function(){imgeditor=swfobject.getObjectById("imgedit");});

		tImg.expandAll();
	});
	
	
	
	
</script>
</head>
<body>	
</body>
</html>