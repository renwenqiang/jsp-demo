/**
 * 处理缓存
 */
// 1.保证最先处理，2.必须在require加载之后重写
$.ajax("framework/cache.do?lastTime",{dataType:"json",async:false,cache:false,data:{target:"mobile",recordFiles:true,mode:window.app?0:2},success:function(jr){
	if(jr.success&&jr.data){
		if(window.app){
			let _mversion=localStorage.getItem("_mversion");
			if(jr.data>_mversion){
				app.clearCache();
				localStorage.setItem("_mversion",jr.data);
			}				
		}else{
			let mapTime={};
			$.each(jr.data,function(n,v){mapTime[n.replace(/\\/g,"/")]=v;}); // url应都是/
			if(require.load){ //requirejs
				let orgLoad=require.load;
				require.load=function(context, moduleName, url){
					let file=url;
					if(file.substr(0,1)==".")
						file=file.substr(1);
					if(file.substr(0,1)=="/")
						file=file.substr(1);
					url=url+"?__t="+mapTime[file];
					return orgLoad(context, moduleName, url);
				};
			}
			if(require.getText){ // dojo require
				let orgGText=require.getText,root=window.rootPath||window.webRoot;
				require.getText=function(url,async,callback){
					if(url.indexOf(root)==0)
						url=url.substr(root.length);
					url=url+"?__t="+mapTime[url];
					return orgGText(url,async,callback);
				};
			}
		}
	}
}});
