<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>角色管理</title>
<z:resource items="jquery,easyui,sunzui,tools,autocomplete"></z:resource>
<script type="text/javascript" src="webpage/framework/resource/js/compatible.js"></script>
<script type="text/javascript" src="webpage/system/user/js/user.js"></script>
</head>
<body>
	<div id="cc" class="easyui-layout" style="height:100%;  width:100%;  margin:0 auto;/*设置整个容器在浏览器中水平居中*/ position:absolute;">
		<div data-options="region:'center',title:'角色管理'">
		    <div data-options="region:'north'," style="width: 100%;height:6%;">
		         <form id = 'fSearch' style="margin-top: 10px;margin-left: 1%;">
		             <table style="width:95%;" cellpadding="0" cellspacing="0" >
		             	<tr>    
		                	<td align="right">
		                    	<label class="Validform_label" style="font-size:12px">
		                                                 角色名称:
		                        </label>
		                    </td>
		                    <td class="value">
		                        <input id="rolename" name="rolename" type="text" style="width: 150px" class="inputxt"  value=''>
		                        <span class="Validform_checktip"></span>
		                        <label class="Validform_label" style="display: none;">角色名称</label>
		                    </td>
		                    <td align="right">
		                        <label class="Validform_label" style="font-size:12px">
		                                                角色编码:
		                        </label>
		                    </td>
		                    <td class="value">
		                        <input id="rolecode" name="rolecode" type="text" style="width: 150px" class="inputxt"  value=''>
		                        <span class="Validform_checktip"></span>
		                        <label class="Validform_label" style="display: none;">角色编码</label>
		                    </td>
		                    <td align="left">
		                        <a href="javascript:void(0)" onclick="searchRole()" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >查询</a>
		                        <a href="javascript:void(0)" onclick="resetForm('fSearch')"  style="margin-left:25px" class="easyui-linkbutton"  data-options="iconCls:'icon-reload'">重置</a>
		                    </td>
		                </tr>
		             </table>
		           </form>
		       	</div>
		         <div data-options="region:'center',"  title="" style="width: 100%;height: 90%">   
		            <table style="width:100%;height: auto;"  id="roledatagrid"></table>
		         </div>
			    <div id="win" class="easyui-window" title="角色操作" style="width:520px;height:300px;" data-options="modal:true" closed="true" >
					<iframe id="editwindow" style="" src="" height="80%" width="100%"></iframe>
					<div id="buttons" style="margin:auto;margin-top:10px;position:relative;text-align:center ">
						<a href="javascript:void(0);" onclick="submitForm()" class="easyui-linkbutton" id="submitForm" data-options="iconCls:'icon-save'">
							<span style="font-size:12px;"><strong>保存</strong></span>
						</a> 
						<a style="margin-left:30px" href="javascript:void(0);" onclick="closeWindow()" class="easyui-linkbutton"  data-options="iconCls:'icon-cancel'"  >
							<span style="font-size:12px;"><strong>取消</strong></span>
						</a>
					</div>
				</div>
		</div> 
    	<div id="role_users" class="easyui-panel" data-options="region:'east',title:'关联用户',split:true,collapsed:true" style="width:50%;">
    		
    	</div>
    </div>
</body>
<script type="text/javascript">
	$(function() {
		//列表数据
		$("#roledatagrid").datagrid({
							url : 'framework/datatable.do?likely&t=t_s_role',
							title : '角色列表',
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
								field : 'ROLENAME',
								title : '角色名称',
								width : 100
							}, {
								field : 'ROLECODE',
								title : '角色编码',
								width : 100
							},{
								field : 'ID',
								title : '操作',
								width : 100,
								formatter:function(v,r,i){
									return '<a style="text-decoration:none;cursor:pointer;" onclick="goRoleUsers(\''+v+'\')">[查看用户]</a><a style="text-decoration:none;cursor:pointer;" onclick="goRoleAuths(\''+v+'\')">[权限设置]</a>';
								}
							} ] ],
							toolbar : [
									{
										iconCls : 'icon-add',
										text : '新增角色',
										handler : function() {
                                                	isNew = true;
                                                	$('#editwindow').contents().find("body").html("");
													$("#editwindow").attr('src','roleController.do?addorupdate');
													$("#submitForm").attr("onclick","submitForm('doAdd')");
													$('#win').window('open');
													$("#buttons").css("display","block"); 
										}
									},'-',
									{
										iconCls : 'icon-edit',
										text : '编辑角色',
										handler : function() {
											var rowsData = $("#roledatagrid").datagrid('getSelected');
											if (rowsData == null) {
												top.$.messager.alert('警告','请选择要编辑的角色', 'warning');
												return;
											}
											$('#editwindow').contents().find("body").html("");
											$("#editwindow").attr('src','roleController.do?addorupdate&id='+ rowsData.ID);
											$("#submitForm").attr("onclick","submitForm('doUpdate')");
                                            $('#win').window('open');
                                            $("#buttons").css("display","block"); 
										}
									},'-',{
	                                    text : '删除角色',
	                                    iconCls : 'icon-remove',
	                                    handler : function() {
											var rowsData = $("#roledatagrid").datagrid('getSelected');
											if (rowsData == null) {
												top.$.messager.alert('警告','请选择要删除的角色', 'warning');
												return;
											}
											top.$.messager.confirm('温馨提示','您确认想要删除此角色吗？',function(r){    
											    if (r){    
											       $.ajax({
												        url : 'roleController.do?delRole',
												        type : 'post',
												        cache : false,
												        data:{
												        	id:rowsData.ID,
												        	roleName:rowsData.ROLENAME
												        },
												        success : function(data) {
															top.$.messager.show({title : '提示',msg : data.msg});
															reloadTable();
												        }
													}); 
											    }    
											});
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
		$("#roledatagrid").datagrid('reload');
	}
	function destroyDialog() {
		$("#dialog").dialog('destroy');
	}
	var searrolename = null;
	var searrolecode = null;
	
	//获取查询结果
	function searchRole() {
		searrolename = $("#rolename").val();
		searrolecode = $("#rolecode").val();
		
		//获取表单中填写的值
		$("#roledatagrid").datagrid('reload', {
			rolename : searrolename,
			rolecode : searrolecode
		});
	}
	
	
	//关闭弹出窗口
	function closeWindow() {
	    
		$("#win").window('close', true);
		//$("#roledatagrid").datagrid('load');
	}
	function submitForm(action) {
		var result = $("#editwindow")[0].contentWindow.saveRole();
		if(result){
			$("#win").window('close', true);
			reloadTable();
		}
	}
	//对象序列化功能
	function serializeObject(a){  
	    var o,h,i,e;  
	    o={};  
	    h=o.hasOwnProperty;  
	    for(i=0;i<a.length;i++){  
	        e=a[i];  
	        if(!h.call(o,e.name)){  
	            o[e.name]=e.value;  
	        }  
	    }  
	    return o;  
	};
	function goRoleUsers(rowid){
		$(".layout-button-left").click();
		$('#role_users').panel({
			title:'关联用户',
			href: 'roleController.do?roleUsers&roleid='+rowid
		});
		
	}
	function goRoleAuths(rowid) {
		$(".layout-button-left").click();
		$('#role_users').panel({
			title:'权限设置',
			href: 'roleController.do?fun&roleId='+rowid
		});
		
	}
</script>
</html>