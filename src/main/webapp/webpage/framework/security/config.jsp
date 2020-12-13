<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<html>
<head>
<title>系统安全控制配置</title>
</head>
<body>

</body>

<script type="text/javascript">
	var grid=window.grid=new sunz.Datagrid({
		columns:[{}
			],
		singleSelect:!multiSelect,
		fit:true,fitColumns:true,
		autoRowHeight:true,
		pagination:true,rownumbers:true,striped:true,
	});
</script>

</html>