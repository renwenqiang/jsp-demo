<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<html>
<head>
<z:resource items="jquery,easyui,sunzui,datepicker"></z:resource>
</head>
<body class="easyui-layout" fit="true">   
    	<div id="tab" class="easyui-tabs" style="width:100%;height:100%;">   
		    <div title="同步用户" style="display:none;">   
		        <iframe id="tbframe" name="tbframe" style="width: 100%;height: 100%" scrolling="no"></iframe>
		    </div>   
		    <div title="取消关联" style="overflow:auto;display:none;">   
		    	<iframe id="glframe" name="glframe" style="width: 100%;height: 100%" scrolling="no"></iframe>
		    </div> 
		</div> 
</body>  
<script type="text/javascript">
$(function() {
	$('#tbframe').attr('src','framework/dingUser.do?list1');
	$('#glframe').attr('src','framework/dingUser.do?list3');
});
</script>
</html>