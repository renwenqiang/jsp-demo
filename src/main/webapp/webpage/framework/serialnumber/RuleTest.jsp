<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<script type="text/javascript" >
$(function(){
	for(var i=0;i<1000;i++){
		var url="framework/serial.do?goSerialNumber&userid=1&jobid=af9c0058287949c984dba043216190be&rulekey=xxxx"; 
		$.ajax({
			type : 'POST',
			dataType : 'json',
			url : url,// 请求的action路径
			error : function() {// 请求失败处理函数
				alert();
			},
			success : function(data) {
				console.log(data);
			}
		});
	}
});
</script>