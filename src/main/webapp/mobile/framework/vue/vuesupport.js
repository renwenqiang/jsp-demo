/**
 * 对单个vue组件的支持
 */
(function(){
	if(!window.Vue)	C.requireResources("vue.mobileResources",true)
	
	let unsupport=()=>{throw new Error("Vue组件间的直接路由跳转不被支持！");}
	Vue.prototype.$route={}
	Vue.prototype.$router={
		push:unsupport,
		go:unsupport
	}
	let vueModuleMap={}; // 存一份映射，以防文件名相同（路径不同）
	window.renderVueComponent=function(module,param,pele,callback){
		/*将组件module在参数params下渲染到pele*/
		let m=module,path="mobile/vue/"+m;
		if(m.lastIndexOf(".vue")==m.length-4)
			m=m.substring(0,m.length-4);
		
		require([path],(M)=>{
			let ms=m.split("/");
			M=vueModuleMap[path]=(vueModuleMap[path]||window[ms[ms.length-1]]["default"]);
			
			Vue.prototype.$route.params=param;
			let vue=window.vue=new Vue({
				el:$('<div/>').appendTo($(pele))[0],
				render:h=>h(M)
			}),comp=vue.$children[0];
			comp.$route={params:param};	// 初始化后将参数绑定给实例
			callback&&callback(comp,M,vue);
		});
	}
	let getActivity=()=>{
		let jele=$('<div class="mcontainer"></div>');
		return {
			title:"",
			panel:{domNode:jele}
		};
	},proxy=(activity,comp)=>{
		let ms=comp;
		ms.getTitle		&& (activity.title=ms.getTitle());
		ms.getMenus		&& (activity.menus=ms.getMenus());
		ms.getButtons	&& (activity.buttons=ms.getButtons());
		ms.onActive		&& (activity.onActive=ms.onActive);
		ms.onDeactive	&& (activity.onDeactive=ms.onDeactive);
		ms.setParam		&& (activity.setParam=ms.setParam);
	}
	window.addVue=viewport.addVue=function(module,params,extend,callback){
		/* 将vue组件直接作为一屏显示  */
		$.isFunction(extend) && (callback=extend,extend=null);
		let activity=getActivity(),jele=activity.panel.domNode;
		renderVueComponent(module,params,jele,(comp,M,vue)=>{
			proxy(activity,comp);
			let deactive=activity.onDeactive;
			activity.onDeactive=(e)=>{
				deactive && deactive(e);
				e.deta<0 && setTimeout(()=>jele.remove(),1);
			};
			extend && $.extend(activity,extend);
			viewport.add(activity);
			callback && callback(activity);
		});
		
		return activity.panel;
	}
	
	/******* 直接解析vue文件（部分特性如import不支持） ********/
	let regM=/,/g,getM=(a,b)=>{let bs=b.split(regM);return $.map(a.split(regM),p=>$.map(bs,sb=>p+" "+sb).join(",")).join(",");};
	let astToCss=function(){
		this.key=this.k?getM(this.parent&&this.parent.key?this.parent.key:"",this.k):"";
		return (this.v?(this.key+"{"+this.v+"}"):"")+$.map(this.children,sub=>astToCss.call(sub)).join("")
	},parseCss=(css)=>{
		let root={toCss:astToCss},k0="{",k1="}",kx=";",line="",o=root;
		for(let i=0,len=css.length;i<len;i++){
			let c=css.charAt(i);
			if(kx==c){ 			// o
				o.v +=line+";";
				line="";
			}else if(k0==c){	// child
				let sub={k:line.trim(),v:"",parent:o};
				o=(o.children||(o.children=[]),o.children.push(sub),sub);
				line="";
			}else if (k1==c){	// child end
				o.v +=line;		// 以防最后一个未写封号
				o=o.parent;
				line="";
			}else{
				(line || c.charCodeAt(0)>32) && (line +=c);
			}
		}
		return root;
	},parseStyle=(style,scopeid)=>{
		let css=style.text(),cssAst;
		if(style.attr("lang")=="scss")
			cssAst=parseCss(css);
		
		if(style.attr("scoped")!==undefined){
			cssAst=cssAst||parseCss(css);
			cssAst.k=scopeid;
		}
		return cssAst?cssAst.toCss():css;	//压缩，然而并无用.replace(/\s*([\(\)\{\}:,;\s])\s*/g,"$1")
	},xCache={},_render=(vm,param,pele,callback)=>{
		Vue.prototype.$route={params:param};
		let vue=new Vue($.extend({},vm.Module,{
			el:$('<div/>').appendTo($(pele))[0],
			render:vm.template.render,
		    staticRenderFns:vm.template.staticRenderFns
		})),comp=vue/*此种方式创建的vue就是comp*/;
		$(vue.$el).addClass(vm.cssScopeid);
		comp.$route={params:param};
		callback&&callback(comp,vm.Module,vue);
	},parseJs=(js,path,callback)=>{
		//path.substr(0,1) !="." && (path="./"+path);
		path=path.replace(/\w+(\.\w+)?$/,"");
		let r=/\bimport\s+([^'"]*)['"]([^'"]+)['"]\s*;?/g,k="import",kx="*";
		let imports=[];
		js=js.replace(r,(m,vars,url)=>{
			let imp={url:url,vars:[]};
			$.each(vars.replace(/(\bfrom\b)|\{|\}/g,"").split(","),(i,v)=>{
				let kv=$.map(v.split(/\s+as\s+/),v=>v.trim());
				imp.vars.push({field:kv[0],name:kv[1]||kv[0]});
			});
			imports.push(imp);
			return "";
		})
		let args=[],argNames=["_module"],index=0;
		let resolveImport=(m,M)=>{
			$.each(m.vars,(i,o)=>{
				args.push(o.field==kx?M:M[o.field]);
				argNames.push(o.name==kx?m.url.replace(/\.\w+/g,"").split(/\\|\//).pop():o.name);
			});
			resolveNext();
		},resolveNext=()=>{
			let m=imports[index++];
			if(m){
				let global=window[m.url];
				if(global){
					resolveImport(m,global);
				}else{
					let url=path+m.url;
					if(/\.\w+$/.test(m.url)&&m.url.substr(-3).toLowerCase()!=".js") // 非js资源
						return resolveImport(m,(m[url]=url,m));
					//let start=m.url.substr(0,2),url=start=="./"?path+m.url.substr(2):(start==".."?path.replace(/\w+[\\\/]$/,"")+m.url.substr(3):(path+m.url));
					import("../../../"+url+".js").then(M=>resolveImport(m,M));
				}
			}else{
				let fnTemp=new Function(argNames,js.replace(/\bexport\s+(\w+)\s*/g,"_module['$1']=")),_module={};
				fnTemp.apply(window,[_module].concat(args));
				let vm=_module["default"];
				callback && callback(vm.data?vm:{data:vm});
			}
		}
		resolveNext();		
	},_addXVue=(activity,params,extend,callback)=>{
		if(extend)
			activity=$.extend({},activity,extend);
		
		viewport.add(activity);
		activity.setParam && activity.setParam(params);
		callback && callback(activity);
	};
	window.renderXVueComponent=function(module,param,pele,callback){
		if(module.lastIndexOf(".vue") !=module.length-4)
			module=module+".vue";
		if(xCache[module])	// 缓存以保证css仅加载一次，并大大减小运算量
			return _render(xCache[module],param,pele,callback);
		
		require(["text!"+module],function(VText){
			let j=$("<v>"+VText+"</v>"),
				tpl=j.find("template").html(),
				js=j.find("script").text(),
				jstyle=j.find("style");
			let cssScopeid="v-"+module.replace(/\.\w+/g,"").split(/\\|\//).pop();
			xCache[cssScopeid]?(cssScopeid=cssScopeid+"-"+xCache[cssScopeid]++):(xCache[cssScopeid]=1);
			let css=$.map(jstyle,(style)=>parseStyle($(style),"."+cssScopeid)).join("");
			$('<style type="text/css">'+css+'</style>').appendTo(document.head);
			let template=Vue.compile(tpl);
			parseJs(js,module,(vm)=>{
				_render(xCache[module]={
						cssScopeid:cssScopeid,
						template:template,
						Module:vm
				},param,pele,callback);
			})
			
		})
	};
	window.addXVue=viewport.addXVue=function(module,params,extend,callback){
		$.isFunction(extend) && (callback=extend,extend=null);
		
		let k="proxy-"+module;
		if(xCache[k])
			return _addXVue(xCache[k],params,extend,callback);
		
		let activity=getActivity();
		renderXVueComponent(module,params,activity.panel.domNode,(comp,M,vue)=>{
			proxy(activity,comp);
			_addXVue(xCache[k]=activity,params,extend,callback);
		});
		return activity.panel;
	}
	
})()