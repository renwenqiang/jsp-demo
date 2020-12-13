<%@ taglib prefix="z" uri="/sunz-tags"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<z:dict items="${setting.DICTS}"></z:dict>
<c:if test="setting.CONFIGS !=null && setting.CONFIGS!=''"><z:config items="${setting.CONFIGS}"></z:config></c:if>
<z:resource items="${setting.INNERRESOURCES}"></z:resource>