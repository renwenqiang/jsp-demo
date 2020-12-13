/**
 * 让List支持预定义模板
 */
define(["_listtemplates"],function(templates){
	let oldInit=sunz.MList.prototype.initOption;
	sunz.MList.prototype.initOption=function(defaults,opt){
		opt=opt||{};
		if(!opt.template){
			let tplid=opt.tplcode||opt.tempatecode||opt.tplid||opt.templateid;
			if(tplid){
				this.tplcode=tplid;
				opt.template=function(d){					
					let context=opt.tplContext;
					if(context&&$.isFunction(context)){
						context=context(d);
					}
					if($.isFunction(tplid)){						
						return templates[tplid(d)](d,context);
					}
					return templates[tplid](d,context);
				};
			}
		}
		if(!opt.url){
			var k=opt.key||opt.k||opt.querykey||opt.queryKey;
			if(k)opt.url="framework/query.do?search&k="+k;
		}
		
		return oldInit.apply(this,arguments);
	};
});