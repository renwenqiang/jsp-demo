<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>密码重置</title>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<body style="overflow-y: hidden" scroll="no">
	<form id="formobj" action="" method="post">
       <input id="id" name="id" type="hidden" value='${guser.id}'>
       <table class="kv-table">
       
       <tr >
           <td class="kv-label">
               <label class="Validform_label">
                   	密码:
               </label>
           </td>
           <td class="kv-content">
               <input id="admin_password_change" name="password" type="password"  
                   style="width: 250px;" class="inputxt easyui-validatebox" data-options="required:true">
               <span class="Validform_checktip"></span>
               <label class="Validform_label" style="display: none;"></label>
           </td>
       </tr>
       
       <tr>
           <td class="kv-label">
               <label class="Validform_label">
                   	重复密码:
               </label>
           </td>
           <td class="kv-content">
                <input class="inputxt" name="mobilePhone" type="password"  id ="admin_password_change_again" data-options="require:true" style="width: 250px">
           </td>
       </tr>
            
	</table></form>
	<script type="text/javascript">
		function adminChangePass(id) {
			var pa1 = $("#admin_password_change").val();
			var pa2 = $("#admin_password_change_again").val();
			if(pa1 == null || pa1 == "") {
				alert("请输入密码");
				return false;
			}
			if(pa1 != pa2) {
				alert("两次密码输入不一致");
				return false;
			}
			$.ajax({
		        url : 'userController.do?savenewpwdforuser',
		        type : 'post',
		        cache : false,
		        data:{
		        	id:id,
		        	password:pa1
		        },
		        success : function(data) {
					top.$.messager.show({title : '提示',msg : data.msg});
					return true;
		        },
		        error : function(e) {
		        	alert(e);
		        	return false;
		        }
			});		     
		}
	</script>
</body>