<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>用户管理</title>
<z:resource items="jquery,easyui,sunzui,tools,autocomplete"></z:resource>
<script type="text/javascript" src="webpage/framework/resource/js/compatible.js"></script>
<script type="text/javascript" src="webpage/system/user/js/user.js"></script>
</head>
<body>
    <div id="" title="用户管理" style="height:100%;  width:100%;  margin:0 auto;/*设置整个容器在浏览器中水平居中*/ position:absolute;">
    	<div style="height:40px;line-height:40px;background:#E3E3E3;padding-left:20px;display:block;font-size:12px;">查询条件</div>
        <div data-options="region:'north'," style="width: 100%;height: 6%;">
               <form id = 'fSearch' style="margin-top: 10px;margin-left: 1%;">
                        <table style="width:95%;" cellpadding="0" cellspacing="0" >
                        <tr>    
                            <td align="right">
                                <label class="Validform_label" style="font-size:12px">
                                                                 用户名:
                                </label>
                            </td>
                            <td class="value">
                                <input id="username" name="username" type="text" style="width: 150px" class="inputxt"  value=''>
                                <span class="Validform_checktip"></span>
                                <label class="Validform_label" style="display: none;">用户名</label>
                            </td>
                             <td align="right">
                                <label class="Validform_label" style="font-size:12px">
                                                                  真实姓名:
                                </label>
                            </td>
                            <td class="value">
                                <input id="realname" name="realname" type="text" style="width: 150px" class="inputxt"  value=''>
                                <span class="Validform_checktip"></span>
                                <label class="Validform_label" style="display: none;">真实姓名</label>
                            </td>
                            <td align="right">
                                <label class="Validform_label" style="font-size:12px">
                                   	 组织机构:
                                </label>
                            </td>
                            <td class="value">
                                <input id="departid" style="width:250px;"> 
                                <span class="Validform_checktip"></span>
                                <label class="Validform_label" style="display: none;">组织机构</label>
                            </td>
                            <td align="left">
                                <a href="javascript:void(0)" onclick="searchUser()" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >查询</a>
                                <a href="javascript:void(0)" onclick="resetform()"  style="margin-left:25px" class="easyui-linkbutton"  data-options="iconCls:'icon-reload'">重置</a>
                            </td>
                        </tr>
                        </table>
                        </form>
        </div>
         <div data-options="region:'center',"  title="" style="width: 100%;height: 86%">   
            <table style="width:100%;height: auto;"  id="userdatagrid"></table>
         </div>
    </div>
    <div id="win" class="easyui-window" title="用户操作" style="width:500px;height:360px;display:none;" data-options="modal:true" closed="true" >
		<iframe id="editwindow" style="" src="" height="260px" width="100%"></iframe>
		<div id="buttons" style="margin-top:10px;position:relative; margin:auto; text-align:center; padding:5px 25px">
			<a href="javascript:void(0);" onclick="submitForm()" class="easyui-linkbutton" id="submitForm" data-options="iconCls:'icon-save'">
				<span style="font-size:12px;"><strong>保存</strong></span>
			</a> 
			<a style="margin-left:30px" href="javascript:void(0);" onclick="closeWindow()" class="easyui-linkbutton"  data-options="iconCls:'icon-cancel'"  >
				<span style="font-size:12px;"><strong>取消</strong></span>
			</a>
		</div>
	</div>
</body>
<script type="text/javascript">
	
	$(function() {
		//组织机构下拉数据
		loadDeparttree("departid",'',true);
		//列表数据
		$("#userdatagrid").datagrid({
							url : 'framework/query.do?search&k=user_list_tb',
							title : '用户列表',
							fit : true,
							fitColumns : true,
							striped : true,
							idField : 'ID',
							//singleSelect:false,
							loadMsg : '数据加载中......',
							pagination : true,
							rownumbers : true,
							//sortName:'id',
							columns : [ [ {
								field : 'ID',
								title : 'ID',
								width : 250,
								hidden : true
							}, {
								field : 'USERNAME',
								title : '用户名',
								width : 100
							}, {
								field : 'REALNAME',
								title : '真实姓名',
								width : 100
							}, {
								field : 'DEPARTLIST',
								title : '组织机构',
								width : 100
							}, {
								field : 'ROLELIST',
								title : '角色',
								width : 250
							}, {
								field : 'ORDERINDEX',
								title : '顺序',
								width : 50
							}] ],
							toolbar : [
									{
										iconCls : 'icon-add',
										text : '新增用户',
										handler : function() {
                                           	isNew = true;
                                           	$('#editwindow').contents().find("body").html("");
											$("#editwindow").attr('src','userController.do?goAddUser');
											$("#submitForm").attr("onclick","submitForm('doAdd')");
											$('#win').window('open');
											$("#buttons").css("display","block"); 
										}
									},'-',
									{
										iconCls : 'icon-edit',
										text : '编辑用户',
										handler : function() {
											var rowsData = $("#userdatagrid").datagrid('getSelected');
											if (rowsData == null) {
												top.$.messager.alert('警告','请选择要编辑的用户', 'warning');
												return;
											}
											$('#editwindow').contents().find("body").html("");
											$("#editwindow").attr('src','userController.do?goUpdate&userid='+ rowsData.ID);
											$("#submitForm").attr("onclick","submitForm('doUpdate')");
                                            $('#win').window('open');
                                            $("#buttons").css("display","block"); 
										}
									},'-',{
	                                    text : '删除用户',
	                                    iconCls : 'icon-remove',
	                                    handler : function() {
											var rowsData = $("#userdatagrid").datagrid('getSelected');
											if (rowsData == null) {
												top.$.messager.alert('警告','请选择删除项目', 'warning');
												return;
											}
											top.$.messager.confirm('温馨提示','您确认想要删除此用户吗？',function(r){    
											    if (r){    
											       $.ajax({
												        url : 'userController.do?del',
												        type : 'post',
												        cache : false,
												        data:{
												        	id:rowsData.ID,
												        	userName:rowsData.USERNAME
												        },
												        success : function(data) {
															top.$.messager.show({title : '提示',msg : data.msg});
															reloadTable();
												        }
													}); 
											    }    
											});
	                                    }
	                                },'-',{
	                                    text : '密码重置',
	                                    iconCls : 'icon-reload',
	                                    handler : function() {
											var rowsData = $("#userdatagrid").datagrid('getSelected');
											if (rowsData == null) {
												top.$.messager.alert('警告','请选择编辑项目', 'warning');
												return;
											}
											$('#editwindow').contents().find("body").html("");
											$("#editwindow").attr('src','userController.do?changepasswordforuser&id='+ rowsData.ID);
											$("#submitForm").attr("onclick","resetPass(\'" + rowsData.ID + "\')");
                                            $('#win').window('open');
                                            $('#win').prop("title","密码重置");
                                            $('#win').css("height","270px");
                                            $("#buttons").css("display","block"); 
											
										                                   	
	                                    }
	                                } ],
							queryParams : {},
							onLoadError : function() {
								alert("系统繁忙，请稍后再试！");
							}
						});
		   $("#win").window({
		       onBeforeClose:function(){ 
		    	   closeWindow();
		       }
		   });
	});
	function reloadTable() {
		$("#userdatagrid").datagrid('reload');
	}
	function destroyDialog() {
		$("#dialog").dialog('destroy');
	}
	
	var searusername = null;
	var searrealname = null;
	var seardepartid = null;
	
	//获取查询结果
	function searchUser() {
		searusername = $("#username").val();
		searrealname = $("#realname").val();
		seardepartid = $("#departid").combotree("getValue");

		//获取表单中填写的值
		$("#userdatagrid").datagrid('load', {
			user_name : searusername,
			realname : searrealname,
			departid:seardepartid
		});
	}
	
	//查询条件的重置
	function resetform() {
		document.getElementById("fSearch").reset();
		$("#departid").combotree('clear');
	}
	//关闭弹出窗口
	function closeWindow() {
	      /* var fileIds = $("#editwindow").contents().find('#fileids').val();
		var data = $("#editwindow").contents().find('#id').val(); */
		$("#win").window('close', true);
		//$("#userdatagrid").datagrid('load');
	}
	function submitForm(action) {
		var result = $("#editwindow")[0].contentWindow.saveUser();
		if(result){
			$("#win").window('close', true);
			reloadTable();
		}
	}
	function resetPass(id) {
		var result = $("#editwindow")[0].contentWindow.adminChangePass(id);
		$("#win").window('close', true);
	}
	
</script>
</html>