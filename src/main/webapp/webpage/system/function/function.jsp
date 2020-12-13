<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>菜单编辑</title>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<z:dict items="all"></z:dict>
<style type="text/css">
.Validform_label{
FONT-SIZE: 12px;
}
.panel.window.easyui-fluid{
margin-top:400px;
}
.panel.window{
margin-top:-400px;
}

.must {
 color: red;
 border: 1px solid red;
 background-color: white;
}
</style>

<script type="text/javascript" src="webpage/system/user/js/user.js"></script>

</head>
<body>
	<form id="formobj" action="" method="post">
	<input name="id" type="hidden" value="${function.id}">
	<table class="kv-table">
		<tr>
            <td class="kv-label">
            	<label class="Validform_label">菜单名称:</label>
            </td>
		    
		    <td class="kv-content">
	        	<input name="functionName" style="width:100%" class="inputxt" value="${function.functionName}" datatype="s2-50">
	        	<span class="Validform_checktip"></span>
	                	<label class="Validform_label" style="display: none;">菜单名称</label>
	        </td>
        </tr>	    
		<tr>
	        <td class="kv-label">
            	<label class="Validform_label">菜单地址:</label>
	        </td>
	        <td class="kv-content">
	        	<input name="functionUrl" style="width:100%" class="inputxt" value="${function.functionUrl}">
	        </td>
	    </tr>
	    <tr>
	        <td class="kv-label">
            	<label class="Validform_label">图标:</label>
	        </td>
	        <td class="kv-content">
		        <select class="icon_" name="TSIcon.id" data-options="value:'${function.TSIcon.id}'"></select>
		    </td>
	    </tr>
	    
	    <tr>
            <td class="kv-label">
	        	<label class="Validform_label">菜单类型:</label>
	        </td>
	        <td class="kv-content">
		        <select name="functionType" id="functionType" datatype="*" style="width:100%">
		            <option value="0" <c:if test="${function.functionType eq 0}">selected="selected"</c:if>>
		                	菜单类型
		            </option>
		            <option value="1" <c:if test="${function.functionType>0}"> selected="selected"</c:if>>
		                	访问类型
		            </option>
		        </select>
		        <span class="Validform_checktip"></span>
	                	<label class="Validform_label" style="display: none;">菜单类型</label>
		    </td>
	    </tr>
		<tr>
	        <td class="kv-label">
            	<label class="Validform_label">父级菜单:</label>
	        </td>
	        <td class="kv-content">
		        <input id="cc" style="width:100%" <c:if test="${function.TSFunction.functionLevel eq 0}"> value="${function.TSFunction.id}"</c:if>
				<c:if test="${function.TSFunction.functionLevel > 0}"> value="${function.TSFunction.functionName}"</c:if>>
		        <input id="functionId" name="TSFunction.id" style="display: none;" value="${function.TSFunction.id}">
	        </td>
	    </tr>
		<tr>
            <td class="kv-label">
            	<label class="Validform_label">菜单等级:</label>
	        </td>
	        <td class="kv-content">
		        <select name="functionLevel" id="functionLevel" datatype="*" style="width:100%">
		            <option value="0" <c:if test="${function.functionLevel eq 0}">selected="selected"</c:if>>
		               	 一级菜单
		            </option>
		            <option value="1" <c:if test="${function.functionLevel>0}"> selected="selected"</c:if>>
		                	下拉菜单
		            </option>
		        </select>
		        <span class="Validform_checktip"></span>
	                	<label class="Validform_label" style="display: none;">菜单等级</label>
		    </td>
		</tr>
	    <tr>
	        <td class="kv-label">
            	<label class="Validform_label">桌面图标:</label>
	        </td>
	        <td class="kv-content">
		        <select class="icon_"  name="TSIconDesk.id" data-options="value:'${function.TSIconDesk.id}'"></select>
		    </td>
	    </tr>

		<tr id="funorder">
			<td class="kv-label">
            	<label class="Validform_label">菜单顺序:</label>
	        </td>
	        <td class="kv-content">
	        	<input name="functionOrder" style="width:100%" class="inputxt" value="${function.functionOrder}" datatype="n1-3">
	    	</td>
	    </tr>
	    
	    <tr id="funremark">
			<td class="kv-label">
            	<label class="Validform_label">备注:</label>
	        </td>
	        <td class="kv-content">
	        	<textarea name="remark" style="width:100%;height:120px" class="inputxt" value="${function.remark}" datatype="s0-255"></textarea>
	    	</td>
	    </tr>
	    
	</table>
	</form>
</body>

</html>

<script type="text/javascript">
$(function() {
	$('#cc').combotree({
		fit:true,
		url : 'framework/query.do?search&k=sys_menu_parents&selfId=${function.id}',
		panelHeight: 200,
		onClick: function(node){
			$("#functionId").val(node.id);
		}
	});
	
	$(".icon_").combogrid({
		url:'framework/query.do?search&k=queryForIcon',fit:true,fitColumns:true,
	  	columns:[[
		  		{field:'path',title:'预览',width:64,align:"center",formatter:function(v){return '<img src="'+v+'"/>'}},
		  		{field:'iconClas',title:'css样式类名',width:80},
	            {field:'name',title:'名称或描述',width:120},
	            {field:'category',title:"分类",width:80,formatter:function(v){return D.getText(v);}},
	            {field:'extend',title:'格式',width:48}
               ]],
	  	idField:"id",textField:"name",delay:1000,mode:'remote',pagination:true
	});
	
	if($('#functionLevel').val()=='1'){
		$('#pfun').show();
	}else{
		$('#pfun').hide();
	}	
});

function  checkForm(){	
	return $("#formobj").form('validate');
}

function saveFunction() {
	var result = checkForm(); 
	if(!result){
		return;
	}
	var url = 'functionController.do?saveFunction';
	var data=$('#formobj').serializeArray();
	var jsondata = serializeObject(data);

	$.ajax({
		async : false,
		cache : false,
		type : 'POST',
		url : url,// 请求的action路径
		data : jsondata,
		error : function() {// 请求失败处理函数
		},
		success : function(data) {
			if (data.success) {
				top.$.messager.show({
					title : '成功',
					msg : data.msg
				});
				
				result = true;

			} else {
				result = false;
				top.$.messager.alert('失败', data.msg, 'warning');
			}
		}
	});
	
	return result;
}
</script>