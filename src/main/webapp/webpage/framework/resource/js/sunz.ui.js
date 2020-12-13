/**
 * 支持 new 格式写法
 */
(function(){
	var config={
			"textbox":{tag:"input",event:"change"}
			,"linkbutton":	{tag:"a",event:"click"}
			,"pagination":	{tag:"div",event:""}
			,"tree":		{tag:"ul",event:""}
			,"progressbar":	{tag:"div",event:""}
			,"panel":		{tag:"div",event:""}
			,"window":		{tag:"div",event:""}
			,"dialog":		{tag:"div",event:""}
			,"accordion":	{tag:"div",event:""}
			,"tabs":		{tag:"div",event:""}
			,"layout":		{tag:"div",event:""}
			,"menu":		{tag:"div",event:"click"}
			,"menubutton":	{tag:"a",event:""}
			,"searchbox":	{tag:"input",event:""}
			,"validatebox":	{tag:"input",event:"validate"}
			,"form":		{tag:"form",event:"submit"}
			,"numberbox":	{tag:"input",event:"change"}
			,"calendar":	{tag:"div",event:"change"}
			,"spinner":		{tag:"input",event:"change"}
			,"numberspinner":{tag:"input",event:"change"}
			,"timespinner":	{tag:"input",event:"change"}
			,"datagrid":	{tag:"table",event:""}
			,"propertygrid":{tag:"table",event:""}
			,"treegrid":	{tag:"table",event:""}
			,"combo":		{tag:"input",event:"change"}
			,"combobox":	{tag:"select" /*"select,input"*/,event:"change"}
			,"combotree":	{tag:"select"/*"select,input"*/ ,event:"change"}
			,"combogrid":	{tag:"select" /* "select,input"*/,event:"change"}
			,"combotreegrid":{tag:"select" /* "select,input"*/,event:"change"}
			,"datebox":		{tag:"input",event:"change"}
			,"datetimebox":	{tag:"input",event:"change"}
			,"slider":		{tag:"input",event:"change"}
			,"dictcombo":	{tag:"select",event:"change"}
			,"datalist":	{tag:"ul",event:""}
			,"switchbutton":{tag:"input",event:"change"}
			,"datetimespinner":{tag:"input",event:"change"}
			,"filebox":{tab:"input",event:"change"}
	},
	getEle=function(type,pele){
		var ele=document.createElement(config[type].tag);		
		ele.each=function(fn){fn.call(ele,0,ele);};
		$(pele||document.body).append(ele);
		//var ele=$("<"+config[type]+"></"+config[type]+">",pele)[0]
		return ele;
	},	
	applyConfig=function(cfg,fn){
		cfg=sunz.parseStyle(cfg);
		$.each(cfg,function(n,v){
			 fn(n,v);
		});
	},
	sunz={parseStyle:function(style){
			if("string" == typeof style){
				var attrs=style.split(";");
				style={};
				$.each(attrs,function(i,o){
					if(!o)return;
					var attr=o.split(":");
					style[attr[0]]=attr[1];
				});
			}
			return style;
		},
		applyAttributes:function(ele,opt){
			// 支持style ,attrs/attributes
			if(opt.style) applyConfig(opt.style,function(n,v){ele.style[n]=v;});
			if(opt.attrs||opt.attributes) applyConfig(opt.attrs||opt.attributes,function(n,v){if(n&&n.toLowerCase()=="class")$(ele).addClass(v); else $(ele).attr(n,v);});
		}
	};
	$.each(config,function(n){
		var rn=n.substr(0,1).toUpperCase()+n.substr(1);
		sunz[rn]=function(opt){
			opt=opt||{};
			var ele=getEle(n,opt.parent),jele= $(ele);
			sunz.applyAttributes(ele, opt);
			
			jele["as"+rn](opt);
			// 直接支持事件
			if(opt.handler && config[n].event){
				jele.on(config[n].event,opt.handler);
			}
			/*$.each(opt,function(o,p){// 以on开头的
				if("string"===typeof o && "on"===o.substr(0,2)){
					jele.on(o.substr(2),p);
				}
			});*/
			var ons=opt.on||opt.events||opt.listeners; // 定义在on/events/listeners下的
			if(ons){
				$.each(ons,function(o,p){
					jele.on(o,p);
				});
			}
			// ////////////
			
			ele.addx=jele.addx=function(o,p){
				var xtype=o, opt=p;
				if('object'===typeof o ) xtype=o.xtype||'Panel';
				if(!p)opt=o;
				if('string'===typeof opt )opt={xtype:opt};
				
				opt.parent=ele;
				//var xtype=xtype.substr(0,1).toUpperCase()+xtype.substr(1);
				var nele=new sunz[xtype](opt);
				jele.add(nele);
				
				return nele;
			};
			
			return jele;
		};
	});
	var addBars=function(bars,pele){
		$.each(bars,function(i,bar){
			var opt={parent:pele,xtype:"Linkbutton"};
			if(!bar.style)
				opt.style="margin-left:15px";
			
			new sunz[opt.xtype]($.extend(opt,bar));
		});
	},oldW=sunz.Window;
	
	sunz.Window=function(opt){
		opt=opt||{};
		if(opt.maximized==null /*&& (!opt.width||!opt.height)*/){
			opt.width=opt.width||550;
			opt.height=opt.height||400;
			//opt.maximized=true;
		}
		var tbars=opt.tbars,
			bbars=opt.bbars;
		
		var win=new oldW(opt);
		if(tbars || bbars){
			var origContent=opt.content,
				content='<div class="easyui-layout" data-options="fit:true,border:false">'
				+(tbars?'<div class="_w_tbar" data-options="region:\'north\',height:34,border:false"></div>':'')
				+'<div class="_w_content" data-options="region:\'center\',border:false"></div>'
				+(bbars?'<div data-options="region:\'south\',height:45,border:false"><div class="_w_bbar" style="float:right;padding:5px 25px"></div></div>':'')
				+'</div>';
			opt.content=content;
			if(origContent){
				$(origContent).appendTo(win.find("._w_content"));
			}
			if(tbars){
				addBars(tbars,win.find("._w_tbar"));
			}
			if(bbars){
				addBars(bbars,win.find("._w_bbar"));
			}
		}
		return win;
	}
	window.sunz=sunz;
})();