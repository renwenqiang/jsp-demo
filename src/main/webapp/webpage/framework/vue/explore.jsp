<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
	<head>
		<%@include file="/sunzbase.jsp"%>
		<title>${title}</title>
		<z:resource items="jquery,vue.resources,${innerResources}"></z:resource>
		<z:dict items="all"></z:dict>
		<z:config items="all"></z:config>
		${resources}
		<z:resource items="requirejs,fileHelper"></z:resource>
	</head>
	<body>
		<div id="app" ></div>
	</body>
</html>


<script type="text/javascript">
Vue.prototype.$route={}
Vue.prototype.$router={
	push:function(target){
		loadModule(target.path,target.query);
	},
	go:function(i){
		var r=routeHistory[i];
		routeIndex=i-1;
		loadModule(r.path,r.params);
	},
	replace:function(){
		
	}
}
var routeHistory=[],routeIndex=-1;
var loadModule=function(m,param){
	if(m.lastIndexOf(".vue")==m.length-4)
		m=m.substring(0,m.length-4);
	
	var path="vue/"+m;
	require([path],(M)=>{
		var ms=m.split("/");
		// 以防文件名相同（路径不同）
		M=window.vueModule=loadModule[path]=(loadModule[path]||window[ms[ms.length-1]]["default"]);
		
		Vue.prototype.$route.params=param;
		var v=window.vue=new Vue({
			el:$('<div></div>').appendTo($("#app").empty())[0],
			render: function(h){return h(M);}
		}),comp=window.vueComponent=v.$children[0];
		comp.$route={params:param};		
		window.save=function(){
			if(comp.save)
				return comp.save.apply(comp,arguments);
			
			console.error("当前vue组件未提供save方法");
		}
		routeHistory[++routeIndex]={path:m,params:param};
		
		// title
		if(document.title=="${title}"){
			var t=(M.getTitle&&M.getTitle())||"${vtitle}";
			if(t)document.title=t;
		}
	});
}

$(function(){
	var params=getQueryParam();
	loadModule("${vuePath}"||params.explore||params.module,params);
})
</script>