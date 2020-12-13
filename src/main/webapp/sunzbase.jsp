<%@ taglib prefix="z" uri="/sunz-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<base href="<%=request.getContextPath()%>/"/>
<script type="text/javascript">
	var rootPath=(a=document.createElement("a"),a.href="",a.href),webRoot=rootPath,basePath=rootPath;
	var tplReplace= function (s,o,f) {s=s||'';f=f!=true; return s.replace(/\{\s*(\w+)\s*\}/g, typeof o ==="string" ? o : function (m, k) { var v=o[k]; return v == null?"":(f && typeof v=="string"?v.replace(/</g,"&lt;"):v);});};
	var getQueryParam=function(n,str){var b=getQueryParam,c=str,a=c?null:b.cache; if(!a && (a={},c?1:b.cache=a)){var i=1,s=((c&&(i=c.indexOf("?")>-1?1:0),c)||location.search).split(/\?|&/g);for(;i<s.length;i++){var m=s[i].split("="),k=m[0],e=a[k],v=decodeURIComponent(m[1]||"");if(e==null){a[k]=v;}else if($.isArray(e)) e.push(v);else a[k]=[e,v];}}return n?a[n]:a;};
	var getServerTime=(serverTime=<%=java.lang.System.currentTimeMillis()%>,clientTime=new Date().getTime(),function(){return serverTime+new Date().getTime()-clientTime;});
	var loginUser=top.loginUser;
	window.onload=function(){
		if(!window.onLoginRequired && window.$ && $.Callbacks){
			window.onLoginRequired=$.Callbacks();
			window.onLoginRequired.add(function(){window.location.href="framework/login.do?login&origUrl="+encodeURIComponent(location.href.replace(rootPath,""));});
		}
		if(!loginUser && !(loginUser=top.loginUser)){
			if(window.$ && $.post){$.post("framework/login.do?loginInfo",function(jr){loginUser=top.loginUser=jr.data})}else{console.log("当前页面未获取到登录信息")}
		}
	};
</script>