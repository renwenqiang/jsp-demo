<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>部门操作</title>
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
<script type="text/javascript">
	var alldepart=[],dictdepart={};
	
	var adCombotree=function(id,code,text,pid){
		var item=dictdepart[id]=$.extend(dictdepart[id]||{},{id:id,code:code,text:text,pid:pid});
		var pItem=dictdepart[pid]=dictdepart[pid]||{}
			,list=pItem.children=pItem.children||[];
		list.push(item);
		alldepart.push(item);
	};
	
	var fnGrepDepart=function(items,state){
		return $.grep(items,function(item){if(item.children)item.state=state; 
		return item.pid==null;}); //item.children!=null;});
	};
	
	$(function() {
		$.each(["departname","orderIndex"],function(i,n){$("input[name="+n+"]").addClass("must").validatebox({required:true});});
		//区分加载方式，优化部分加载的速度
		if(!($("#id").val()) && '${empty pid}' == 'true' ) {//新增并且没有父节点的延迟加载树
			loadDeparttree("dd",'',false);
		} else {//全部加载
			
			$('#dd').combotree({
//				url : 'departController.do?setPFunction&selfId=${depart.id}',
	            width: 250,
	            onSelect : function(node) {
//	                alert(node.text);
	                //changeOrgType();
	            }
	        });
		
			$.ajax({
				async: true,
			    type: "POST",
			    url: "framework/query.do?search&k=user_org_tree",
			    contentType: "application/json; charset=utf-8",
			    dataType: "json",
			    success: function (data) {
			    	alldepart=[],dictdept={};
			        $.each(data.data,function(i,d){
			        	adCombotree(d.ID,d.CODE,d.TEXT,d.PID);
					});
			        var treedata=fnGrepDepart(alldepart,"closed");
			        $('#dd').combotree({
			        	data: treedata
			        });
			        
			        if('${empty pid}' == 'false') { // 设置新增页面时的父级
			            $('#dd').combotree('setValue', '${pid}');
			        }
			    },
			    error: function (msg) {
			        alert("组织机构数据加载失败");
			    }
			});
		    
			if($("#id").val()) {// || '${empty pid}' == 'false'
	            $('#dd').combotree({ readonly: true });  
	        }
			
		}
		
	});
    
    function  checkForm(){	
    	return $("#formobj").form('validate');
    }
    function saveDept() {
    	var result = checkForm(); 
		if(!result){
			return;
		}
    	var url = 'systemController.do?saveDepart';
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
</head>
<body>
	<form id="formobj" action="" method="post">
		<input id="id" name="id" type="hidden" value="${depart.id }">
		<table class="kv-table">
	        <tr>
	            <td class="kv-label">
	                <label class="Validform_label">组织机构名称:</label>
	            </td>
	            <td class="kv-content">
		            <input name="departname" style="width:250px;" class="inputxt" type="text" value="${depart.departname }"  datatype="s1-20">
		            <span class="Validform_checktip"></span>
                	<label class="Validform_label" style="display: none;">组织机构名称</label>
	        	</td>
	        </tr>
	        <tr>
	            <td class="kv-label">
	            	<label class="Validform_label">组织机构描述:</label>
	            </td>
	            <td class="kv-content">
	            	<input name="description" style="width:250px;"  class="inputxt" value="${depart.description }">
	        	</td>
	        </tr>
	        <tr>
		        <td class="kv-label">
	            	<label class="Validform_label">上级组织机构:</label>
	            </td>
	            <td class="kv-content">
	            	<input id="dd" name="TSPDepart.id" style="width:250px;"  value="${depart.TSPDepart.id}">
	            </td>
	        </tr>
        	<tr>
            	
            	<td class="kv-label">
            		<input type="hidden" name="orgCode" style="width:250px;"  value="${depart.orgCode }">
            		<label class="Validform_label">机构类型:</label>
            	</td>
            	<td class="kv-content">
		            <select name="orgType" id="orgType" style="width:250px;font-size:14px;" >
		                <option value="1" <c:if test="${depart.orgType=='1'}">selected="selected"</c:if>>公司</option>
		                <option value="2" <c:if test="${depart.orgType=='2'}">selected="selected"</c:if>>部门</option>
		                <option value="3" <c:if test="${depart.orgType=='3'}">selected="selected"</c:if>>其他</option>
		            </select>
	            </td>
	         </tr>
	         <tr>
	         
	         <tr>
	         	<td class="kv-label">
            		<label class="Validform_label">电话:</label>
            	</td>
            	<td class="kv-content">
            		<input name="mobile" style="width:250px;"  class="inputxt" value="${depart.mobile }">
            	</td>
            </tr>
            <tr>
            	<td class="kv-label">
            		<label class="Validform_label">传真:</label>
            	</td>
            	<td class="kv-content">
            		<input name="fax" style="width:250px;"  class="inputxt" value="${depart.fax }">
            	</td>
            </tr>
        	<tr>
        		<td class="kv-label">
        			<label class="Validform_label">地址:</label>
        		</td>
        		<td class="kv-content">
           	 		<input name="address" style="width:250px;"  class="inputxt" value="${depart.address }" datatype="s1-50">
           	 		<label class="Validform_label" style="display: none;">地址</label>
           	 	</td>
            </tr>
        	<tr>
        		<td class="kv-label">
        			<label class="Validform_label">顺序:</label>
        		</td>
            	<td class="kv-content">
            		<input name="orderIndex" style="width:250px;"  class="inputxt" value="${depart.orderIndex}" datatype="n1-3" data-option:"required:true">
            	</td>
        	</tr>
		</table>
	</form>
</body>
</html>
