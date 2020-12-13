<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>钉钉用户列表</title>
<z:resource items="jquery,easyui,sunzui,datepicker"></z:resource>
<!-- <script src = "webpage/dingding/dingDing.js"></script> -->
<script type="text/javascript" src="webpage/dingding/extendquery.js"></script>
<z:dict items=""></z:dict>
</head>
<body> 
	<div id="win" class="easyui-window" title="选择用户"
		style="width:720px;height:520px;padding:5px;top: 100px;"
		data-options="modal : 'true',closed:'true'">
		<div class="easyui-layout" data-options="fit:false" style="width:100%;height:93%">
			<div data-options="region:'east',title:'<h>可选人员</h>',split:true"
				style="width:45%;">
				<input id="searchput" name="searchput" type="text" style="width: 100%" class="inputxt"  value=''>
				<ul id="tt"></ul>
			</div>
			<div data-options="region:'center',title:'<h>已选人员</h>'"
				style="padding:5px;" id="hasSelect">
				<span id="selUserid" style="display: none;"></span>
				<span id="selUser"></span>
			</div>
		</div>
		<div style="padding:5px;text-align:center;">
			<a href="javascript:connect('dUserList')" style="width:200px;height:30px;" class="easyui-linkbutton" icon="icon-save" >确定</a>
		</div>
	</div>
	
	<div id="userinfo" class="easyui-window" title="邀请用户"
		style="width:420px;height:330px;padding:5px;top: 100px;"
		data-options="modal : 'true',closed:'true'">
		 
			 
			<div style="margin-bottom:20px">
				<div>姓名:</div>
				<input class="easyui-textbox" id="addusername" style="width:100%;height:32px">
			</div>
			<div style="margin-bottom:20px">
				<div>电话:</div>
				<input class="easyui-textbox"  id="addusermobile" style="width:100%;height:32px">
			</div>
			<div style="margin-bottom:20px">
			<div>邮箱:</div>
				<input class="easyui-textbox"  id="adduseremail" data-options="prompt:' ',validType:'email'" style="width:100%;height:32px">
			</div>
	 
		<div style="padding:5px;text-align:center;">
			<a href="javascript:adduser('')" style="width:200px;height:30px;" class="easyui-linkbutton" icon="icon-save" >确定</a>
		</div>
	</div>
	
	<table id="dUserList"></table>
</body>
<script type="text/javascript">
 
var arr_userid=new Array();
var arr_username = new Array();
$(function(){
$("#dUserList").treegrid(
		{
			url:'framework/dingUser.do?getDingUserList',
			title : '钉钉用户',
			fit:true,
			fitColumns : true,
			singleSelect:false,
			striped : true,
			idField : 'id',
			treeField:'text',
			loadMsg : '数据加载中......',
			pagination:false,
			rownumbers : true,
			columns : [ [{
						field : 'ck',
						checkbox : true
					},
			             {
				field : 'text',
				title : '用户名',
				width : 250
			},
					{
						field : 'id',
						title : '编号',
						width : 250
					},{
						field : 'code',
						title : '是否关联',
						width : 250,
						formatter:function(a,b){
							 
							if(b.code=="1"){
								return "已关联系统用户";
							}else if(b.code=="0"){
								return "未关联系统用户";
							}
						}
					}] ],
					toolbar : [{
								iconCls : 'icon-add',
								text : '邀请用户',
								handler : function() {
									$("#userinfo").window("open");
								}
							},{
									iconCls : 'icon-add',
									text : '同步部门',
									handler : function() {
										tongbuDepart('framework/dingUser.do?getDingDepart','dUserList');
									}
								},{
									iconCls : 'icon-add',
									text : '同步用户',
									handler : function() { 
										tongbu('同步用户','framework/dingUser.do?doPLTB','dUserList');
									}
								},{
									iconCls : 'icon-add',
									text : '同步部门用户',
									handler : function() { 
										copyDepartUser( );
									}
								},{
									iconCls : 'icon-add',
									text : '关联用户',
									handler : function() {
										var rowsData = $("#dUserList").treegrid('getSelections');
										if (!rowsData|| rowsData.length == 0) {
											top.$.messager.alert('警告','请选择用户', 'warning');
											return;
										}else if (rowsData.length > 1) {
											top.$.messager.alert('警告','请选择一个用户进行关联','warning');
											return;
										}else{
											shoWind();
										}
									}
								},{
									iconCls : 'icon-cancel',
									text : '取消关联',
									handler : function() { 
										removeCopied('同步用户','framework/dingUser.do?doCancelUsers','dUserList');
									}
								}],
			onLoadSuccess:function(){
				//$('#dUserList').treegrid('collapseAll');
			},
			onLoadError : function() {
				alert("系统繁忙，请稍后再试！");
			}
		});
	showRoleSelect();
});
function reloadTable(){
	window.location.reload();
	//$("#dUserList").treegrid('reload');
}
function destroyDialog(){
	$("#dialog").dialog('destroy');
}
function tongbuDepart(url,gname){
	top.$.messager.confirm('提示','确定同步钉钉部门吗?', function(r) {
		if(r){
			$.ajax({
				url : url,
				type : 'post',
				data : {},
				cache : false,
				success : function(data) {
					//var d = $.parseJSON(data);
					if (data.success) {
						var msg = data.msg;
						$.messager.show({title : '同步部门成功',msg : msg});
						setTimeout('reloadTable()',2000);
						$("#"+gname).treegrid('unselectAll');
					}
				}
			});
		}
	});
}
function tongbu(title,url,gname) {
	gridname=gname;
    var ids = [];
    var rows = $("#"+gname).treegrid('getSelections');
    
    if (rows.length > 0) {
    	//$.dialog.setting.zIndex = getzIndex(true);
    	top.$.messager.confirm('确认','你确定同步吗?', function(r) {
		   if (r) {
				for ( var i = 0; i < rows.length; i++) {
					var row = rows[i];
					if(row.code=="0"){
						ids.push(row.id);
					}
				}
				if(ids.length==0){ 
					top.$.messager.alert('警告',"选择的用户已关联或者选择的不是用户",'warning');
					return;
				}
				$.ajax({
					url : url,
					type : 'post',
					data : {
						ids : ids.join(',')
					},
					cache : false,
					success : function(data) {
						//var d = $.parseJSON(data);
						if (data.success) {
							var msg = data.msg;
							//tip(msg);
							$.messager.show({title : '成功',msg : msg});
							setTimeout('reloadTable()',2000);
							$("#"+gname).treegrid('unselectAll');
							ids='';
						}
					}
				});
			}
		});
	} else {
		top.$.messager.alert('警告','请选择需要同步的用户','warning'); 
	}
}
function copyDepartUser(){
	var url = "framework/dingUser.do?copyUserOfDepart";
	var ids = [];
	var rows = $("#dUserList").treegrid('getSelections');
	if (rows.length == 0) {
		top.$.messager.alert('警告','请选择需要同步的部门','warning'); 
		return;
	}
	for ( var i = 0; i < rows.length; i++) {
		var row = rows[i];
		 console.log(row);
		 if(!row.attributes.isUser){
			 ids.push(row.id);
		 } 
	}
	if(ids.length==0){ 
		top.$.messager.alert('警告',"选择的不是部门",'warning');
		return;
	}
	 
	$.ajax({
		url : url,
		type : 'post',
		data : {
			ids : ids.join(',')
		},
		cache : false,
		success : function(data) { 
			if (data.success) {
				var msg = data.msg; 
				$.messager.show({title : '成功',msg : msg});
				setTimeout('reloadTable()',2000);
				$("#dUserList").treegrid('unselectAll'); 
			}
		}
	});
}
function removeCopied(title,url,gname){
	gridname=gname;
    var ids = [];
    var rows = $("#"+gname).treegrid('getSelections');
    
    if (rows.length > 0) {
    	//$.dialog.setting.zIndex = getzIndex(true);
    	top.$.messager.confirm('确认','你确定取消关联吗?', function(r) {
		   if (r) {
				for ( var i = 0; i < rows.length; i++) {
					var row = rows[i];
					if(row.code=="1"){
						ids.push(row.id);
					}
				}
				if(ids.length==0){ 
					top.$.messager.alert('警告',"请选择的用户已关联的用户",'warning');
					return;
				}
				$.ajax({
					url : url,
					type : 'post',
					data : {
						ids : ids.join(',')
					},
					cache : false,
					success : function(data) {
						//var d = $.parseJSON(data);
						if (data.success) {
							var msg = data.msg;
							//tip(msg);
							$.messager.show({title : '成功',msg : msg});
							setTimeout('reloadTable()',2000);
							$("#"+gname).treegrid('unselectAll');
							ids='';
						}
					}
				});
			}
		});
	} else {
		top.$.messager.alert('警告','请选择需要取消关联的用户','warning');
		//alert("请选择需要同步的数据");
	}
	
}
function shoWind() {
	$("#win").window('open');
}
function connect(gname){
	var rows = $("#"+gname).treegrid('getSelections');
	var tsuserid=$('#selUserid').text();
	var tsusername=$('#selUser').text();
	var duserid=rows[0].id;
	var dname=rows[0].text;
	//alert("tsuserid:"+tsuserid+"===tsusername:"+tsusername+" ==duserid:"+duserid);
	var obj={
			duserid:duserid,
		    tsid:tsuserid,
		    name:tsusername,
		    dname:dname
	};
	$.ajax({
			url:"framework/dingUser.do?dolink2",
			data:obj,
			type:"post",
			dataType:"json",
			success:function(data,e){
				$("#win").window('close');
				$.messager.show({title : '提示',msg : data.msg});
				setTimeout('reloadTable()',2000);
			},
			error: function(e){
				$.messager.show({title : '提示',msg : data.msg});
			}
	});
}

function showRoleSelect(){
	/* $('#tt').tree({
		url : 'lzxmdc/lzuserController.do?getUser',
		checkbox : false,
		loadFilter : function(data) {
			var q=data.data;
			//把开发测试组删除掉
			//人员组id:"402881eb597c8fe001597c9fa5730001"
			for(var i=0;i<q.length;i++){
				if(data.data[i].id=="21"){
					q.splice(i,1);
					return q;
				}
			}
		},
		onClick : function(node) {
			$('#selUserid').text(node.id);
			$('#selUser').text(node.text);
		}
	}); */
	$('#tt').tree({
		   /* url:'sfw/jtuserController.do?getUser', */
		   /* url:'lzxmdc/lzuserController.do?getUser', */
		   url:'framework/dingUser.do?getAllUser',
		   checkbox:false,
		   panelHeight:570,
		   loadFilter:function(data){//显示数据库传来的值显示到树上
			   var q=data.data;
		       var  count = 0;
			   //把开发测试组删除掉
			   for(var i=0;i<q.length;i++){
					if(data.data[i].id=="21"){
						q.splice(i,1);
						count++;
						return q;
					}
				}
			   if(count == 0){
				   return q;
			   }
		   },
		   onBeforeExpand:function(node,param){                         
		        /* $('#tt').tree('options').url = "framework/dingUser.do?getNextUser&nodeid="+node.id; */   
		        $('#tt').tree('options').url = "framework/dingUser.do?getAllUser&flag=101";
		   }, 
		   onClick:function(node){
		   		$('#selUserid').text(node.code);
				$('#selUser').text(node.text);
			   //document.getElementById("tzrform").reset();
		   }
	   });
	$("#searchput").bind("input propertychange", function() {
		 $('#tt').tree("search",$(this).val());
		 if($(this).val()==""&&null==$(this).val()){
			 $('#tt').tree("collapseAll");
		 }
	});
}

function adduser(){
	var username = $("#addusername").textbox("getValue");
	var mobile = $("#addusermobile").textbox("getValue");
	var email = $("#adduseremail").textbox("getValue");
	console.log(username,mobile ,email);
	var data={name:username,mobile:mobile,email:email,department:1};
	$.ajax({
		url:"framework/dingUser.do?addDingUser",
		data:data,
		dataType:"json",
		success:function(result){
			if(result.success){
				alert("添加成功");
			}else{
				alert(result.msg);
			}
		}
		
	})
}
</script>
</html>