<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>用户操作</title>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<script type="text/javascript" src="webpage/system/user/js/user.js"></script>
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
</head>
<body>
    <form id="formobj" action="" method="post">
        <input id="id" name="id" type="hidden" value='${guser.id}'>
        <table class="kv-table">
        <tr>
            <td class="kv-label">
                <label class="Validform_label">用户名:</label>
            </td>
            <td class="kv-content">
            	<c:if test="${guser.id==''||guser.id==null}">
                	<input id="userName" name="userName" data-options="required:true" type="text" style="width: 250px" class="easyui-validatebox inputxt" value='${guser.userName}'>
                </c:if>
                <c:if test="${guser.id!=''&&guser.id!=null}">
                	${guser.userName}
                </c:if> 
                <span class="Validform_checktip"></span>
                <label class="Validform_label" style="display: none;">用户名</label>
            </td>
        </tr>
        <tr >
            <td class="kv-label">
                <label class="Validform_label">
                    	真实姓名:
                </label>
            </td>
            <td class="kv-content">
                <input id="realName" name="realName" type="text"  
                    style="width: 250px;" class="inputxt easyui-validatebox" data-options="required:true" value='${guser.realName}'>
                <span class="Validform_checktip"></span>
                <label class="Validform_label" style="display: none;">真实姓名</label>
            </td>
        </tr>
        <tr>
            <td class="kv-label">
                <label class="Validform_label">
                    	组织机构:
                </label>
            </td>
            <td class="kv-content">
                 <input  name="gorgIds" id ="gorgIds"  style="width: 250px" value="${gdepartid}">
                 <input  name="orgIds" id ="orgIds"   type="hidden"  style="width: 250px" value="${gdepartid}">
            </td>
        </tr>
        <tr>
            <td class="kv-label">
                <label class="Validform_label">
                    	角色:
                </label>
            </td>
            <td class="kv-content">
                <input id="groleid" class="easyui-combobox" name="groleid" style="width: 250px"  value="${groleid}" >
                <input id="roleid" name="roleid" type="hidden" style="width: 250px"  value="${groleid}" >
                <span class="Validform_checktip"></span>
                <label class="Validform_label" style="display: none;">角色</label>
            </td>
        </tr>  
        <tr>            
            <td class="kv-label">
                <label class="Validform_label">
                    	顺序:
                </label>
            </td>
            <td class="kv-content">
                <input id="orderIndex" name="orderIndex" id="orderIndex" type="text"
                    style="width: 250px" class="inputxt easyui-validatebox"  data-options="required:true" value='${guser.orderIndex}'>
                <span class="Validform_checktip"></span>
            </td>
        </tr>
		</table></form>
</body>
<script type="text/javascript">
	
    function loadRoletree(){
        $.ajax({
            url : 'framework/query.do?search&k=user_role_tree',
            type : 'post',
            cache : false,
            success : function(data) {
            	//加载树
            	$('#groleid').combobox({
            		data:toLowerCaseData(data.data),
					editable:true,
            		required: true,
            		valueField:'id',
            		textField:'text',
            		multiple:true
            	});
            }
   		});
    }
    //检查表单必填项
    function  checkForm(){	
    	return $("#formobj").form('validate');
    }
    
    //传入下拉列表数据
    loadDeparttree("gorgIds",'',true);
	loadRoletree();
	
	
	function saveUser(){
		//利用easyui规则进行校验
		var result = checkForm(); 
		if(!result){
			return;
		}
		var url = "userController.do?saveUser";
		//获取角色树的数据
		 var roleid = $('#groleid').combobox('getValues').join(",");
		 $('#roleid').val(roleid);
		 
		 var orgid = $('#gorgIds').combotree('getValues').join(",");
		 $('#orgIds').val(orgid);
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
</html>
