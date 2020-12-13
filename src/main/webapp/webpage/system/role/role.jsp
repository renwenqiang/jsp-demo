<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>角色操作</title>
<z:resource items="jquery,easyui,sunzui"></z:resource>
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
        <input id="id" name="id" type="hidden" value='${role.id}'>
        <table class="kv-table">
        <tr>
            <td class="kv-label">
                <label class="Validform_label">角色名称:</label>
            </td>
            <td class="kv-content">
                <input id="roleName" name="roleName" data-options="required:true,validType:['length[1,255]']" type="text" style="width: 250px" class="easyui-validatebox inputxt" value='${role.roleName}'>
                <span class="Validform_checktip"></span>
                <label class="Validform_label" style="display: none;">角色名称</label>
            </td>
        </tr>
        <tr >
            <td class="kv-label">
                <label class="Validform_label">
                                角色编码:
                </label>
            </td>
            <td class="kv-content">
                <input id="roleCode" name="roleCode" type="text"  
                    style="width: 250px;" class="inputxt easyui-validatebox" data-options="required:true,validType:['length[1,255]']" value='${role.roleCode}'>
                <span class="Validform_checktip"></span>
                <label class="Validform_label" style="display: none;">角色编码</label>
            </td>
        </tr>
		</table></form>
</body>
<script type="text/javascript">
    //检查表单必填项
    function  checkForm(){	
    	return $("#formobj").form('validate');
    }
	$(function(){
		
	});
	function saveRole(){
		//利用easyui规则进行校验
		var result = checkForm(); 
		if(!result){
			return;
		}
		var url = "roleController.do?saveRole";
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

</script>
</html>
