<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
	<%@include file="/sunzbase.jsp"%>
	<title>${applicationScope.title}</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0,user-scalable=no" />
	<z:resource items="jquery"></z:resource>
	<script type="text/javascript">
		window.onload=null;
		window.onLoginRequired=$.Callbacks();
		window.onUserLogin=$.Callbacks();
	</script>
	<z:config items="all"></z:config>
	<z:config groups="mobileExtendConfigs"></z:config>
	<z:dict items="all"></z:dict>
	<z:resource items="sunzmobile,requirejs,mobile.overload"></z:resource>
	<script type="text/javascript" src="mobile/framework.js"></script>
	<script type="text/javascript" src="mobile/util.js"></script>
	<script type="text/javascript" src="mobile/startup.js"></script>
</head>
<body>	
</body>
</html>