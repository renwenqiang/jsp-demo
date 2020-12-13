/**
 * 前端错误向后台数据库记录
 */
(function(){
	let debugX= window.onerror = function(message, source, lineno, colno, error,innerSource) { 
		$.post("framework/datatable.do?add",{
			t:"T_S_JSEXCEPTION",
			message_:message,
			source_:source,
			lineno_:lineno,
			colno_:colno,
			error_:error,
			innerSource:innerSource,
			ipFields:"IP"
		});
	};
	window.addEventListener('error', function(e){
		debugX(e.message,e.filename,e.lineno,e.colno,e.error);
	}, false);
	console.debug=function(){
		arguments.length=6;
		arguments[3]="console";
		arguments[4]="debug";
		let cller=arguments.callee.caller,innerS=cller?cller.toString():"";
		arguments[5]=(innerS||"").substr(0,1000);
		window.onerror.apply(window,arguments);
	}
	console.error=function(){
		arguments.length=6;
		arguments[3]="console";
		arguments[4]="error";
		let cller=arguments.callee.caller,innerS=cller?cller.toString():"";
		arguments[5]=(innerS||"").substr(0,1000);
		window.onerror.apply(window,arguments);
	}
	console.log=function(){
		arguments.length=6;
		arguments[3]="console";
		arguments[4]="log";
		let cller=arguments.callee.caller,innerS=cller?cller.toString():"";
		if(innerS.indexOf("catch")<0)
			return;
		arguments[5]=(innerS||"").substr(0,1000);
		window.onerror.apply(window,arguments);
	}
})();