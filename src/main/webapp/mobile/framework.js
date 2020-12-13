/**
 * 移动框架核心引擎
 */
$(function(){	
	let view=window.viewport=$('<div id="viewport" class="viewport">\
			<div class="navbar">\
				<div class="nav-navigate"></div>\
				<div class="nav-title">'+document.title+'</div>\
				<div class="nav-menu"></div>\
				<div class="nav-button"></div>\
			</div>\
			<div class="main-container"></div>\
			<div class="nav-bbar" style="display:none"></div>\
		</div>').appendTo(document.body),
		pTbar=view.find(".navbar"),
		pOperate=view.find(".nav-navigate")[0],
		pTitle=view.find(".nav-title"),
		pButton=view.find(".nav-button"),
		pMenu=view.find(".nav-menu"),
		pContainer=view.find(".main-container"),
		pBbar=view.find(".nav-bbar");

	view.navbar=pTbar;
	view.container=pContainer;
	view.bbar=pBbar;
	
	var btnBack=new sunz.MButton({parent:pOperate,hidden:true,iconCls:"tbar glyphicon glyphicon-arrow-left",width:36,handler:function(){view.back();}});
	var btnForward=new sunz.MButton({parent:pOperate,hidden:true,iconCls:"tbar glyphicon glyphicon-arrow-right",width:36,handler:function(){view.forward();}});
	view.stack=[];
	view.current=-1;
	var setTitle=view.setTitle=function(t){pTitle.html(t);};
	view.layout=function(){pContainer.css("height","calc(100% - "+(pTbar[0].offsetHeight+pBbar[0].offsetHeight)+"px)")}
	view.hideNavibar=function(){pTbar.hide();this.layout();this.setTitle=function(t){document.title=t}}
	view.showNavibar=function(){pTbar.show();this.layout();this.setTitle=setTitle}
	view.hideBBar=function(){pBbar.hide();this.layout();};
	view.showBBar=function(){pBbar.show();this.layout();};
	view.setBbars=(tplBarItem='<div style="{style}" class="bbar-item {cls}" aimg="{activeImg}"><img class="bbar-item-icon {iconCls}" style="{iconStyle}" src="{img}" dimg="{img}"/><span class="bbar-item-text {textCls}" style="{textStyle}" >{title}{text}</span></div>',
		switchBbar=function (jme){
			var jall=pBbar.find(".bbar-item"),jimg=jme.find("img");
			jall.removeClass("bbar-active");
			jall.find("img").attr("src",function(){return $(this).attr("dimg");});
			jme.addClass("bbar-active");
			jimg.attr("src",jme.attr("aimg"));
	  },
	  function(bbars,keep){
		if(!bbars||bbars.length==0)
			if(keep!=false)return;
			else view.hideBBar();
			
		pBbar.html("");
		$.each(bbars,function(i,btn){
			if(btn.detached)btn.dimg=btn.img;
			var jitem=$(tplReplace(tplBarItem,btn)).on("click",function(){
				if(btn.handler){
					btn.handler();
				}else if(btn.onclick){
					btn.onclick();
				}
				if(btn.detached)
					return;
				$.each(bbars,function(k,b){delete b._actived;});
				btn._actived=true;
				switchBbar($(this));				
			}).appendTo(pBbar);
			if(btn._actived && !btn.detached){
				switchBbar(jitem);	
			}
		});
		if($.grep(bbars,function(btn){return btn._actived;}).length==0)
			$(pBbar.find(".bbar-item")[0]).click();
		
		view.showBBar();
	});
	view.setButtons=function(buttons,keep){
		if(keep)return;
		pButton.html("");
		if(buttons==null||!buttons.length)
			return;
		
		var count=0;
		$.each(buttons,function(i,btn){count+=(btn.text||"").length;});
		if(buttons.length>2||count>4){
			new sunz.MButton({parent:pButton[0],text:"· · ·",iconCls:"tbar",handler:function(){
				var c=$('<div class="nav-menu-mask"></div>').on("click",function(){c.remove();}).appendTo(document.body).show();
				var d=$('<div class="nav-menu-container"></div>').appendTo(c);
				$.each(buttons,function(i,btn){
					$('<div class="nav-menu-item"></div>').html(btn.text).addClass(btn.iconCls).appendTo(d)
					.on("click",btn.handler||btn.onclick);					
				});
			}});
		}else{
			$.each(buttons,function(i,btn){ new sunz.MButton($.extend({parent:pButton[0]},btn));});		
		}
	};
	view.setMenus=(tplMenuItem='<li role="presentation"><span class="{icon}"></span><a role="menuitem" href="javascript:void(0);">{text}</a></li>'
			,tplDivider='<li role="presentation" class="divider"></li>',
			function(menus,keep){
				if(keep) return;
				pMenu.html("");
				if(menus==null || menus.length==0){return pMenu.hide();}
				pMenu.show();
				var html=$('<button type="button" class="tbar btn dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-list"></span></button><ul style="left:auto;right:0 !important" class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1"></ul>')
					,mContainer=$(html[1]);
				$.each(menus,function(i,menu){
					$(tplReplace((menu=="-"||menu.separator)?tplDivider:tplMenuItem,menu)).on("click",menu.handler||menu.click||menu.onclick).appendTo(mContainer);
				});
				html.dropdown().appendTo(pMenu);
	});
	view.enableBack=function(f){if(f==false)btnBack.hide();else btnBack.show();};
	view.enableForward=function(f){if(f==false)btnForward.hide();else btnForward.show();};
	view.hasBack=function(){return view.current>0;};
	view.hasForward=function(){return view.stack[view.current+1]!=null;};
	view.stackChanged=function(){
		view.enableBack(view.hasBack());
		//view.enableForward(view.hasForward());
	};
	view.getActive=function(){return view.current<0?null:view.stack[view.current];}
	view.showAcitivity=function(config,deta,fn){
		var current=view.getActive(),		
		realShow=function(){
			if(current) {
				current.panel.hide();
				if(current.destroyOnback)current.panel.domNode.remove();
			}
			view.current+=deta;
			view.setTitle(config.title);
			view.setButtons(config.tbars||config.buttons);
			view.setBbars(config.bbars||config.navbars||config.navbuttons);
			view.setMenus(config.menus);
			var panel=config.panel;
			if(pContainer[0]!=$(panel.domNode).parent()[0]){ // 已经是子节点不用动
				pContainer.append(panel.domNode);
			}
			panel.show();
			view.stackChanged();
			if(config.onActive)
				config.onActive({deta:deta,pre:current});
			fn&&fn();
		}
		if(current) {
			if(current.onDeactive){
				var eventArg={deta:deta,target:config,handled:false,goingon:realShow};
				var r=current.onDeactive(eventArg);
				if(r===false || eventArg.handled)
					return false;
			}
		}
		if(config.onBeforeActive){
			var eventArg={deta:deta,pre:current,handled:false,goingon:realShow};
			var r=config.onBeforeActive(eventArg);
			if(r===false || eventArg.handled)
				return false;
		}
		realShow();		
	};
	let back=function(){
		var pre=view.current-1;
		if(pre<0){
			if(!window.app){
				$.tip("再按一次退出",1500);
				setTimeout(function(){window.history.pushState('forward', null, window.location.href);},1500);
			}
			return;
		}
		var config=view.stack[pre];
		view.showAcitivity(config,-1);
	};
	window.addEventListener("popstate", function (e) {/*e.preventDefault();e.stopPropagation();*/back();}, false);
	view.back=function(){
		history.back();
	}
	view.forward=function(){
		var next=view.current+1;
		var config=view.stack[next];
		view.showAcitivity(config,1);
	};	
	view.add=function(config,title,menus,buttons,onActive,onDeactive,destroyOnback){
		if(typeof config=="string"){
			return require([config],function(c){view.add(c);c.setParam(title);});
		}
		var panel=config;
		if(config instanceof HTMLElement){
			panel.domNode=panel;
			config={panel:panel};
		}else{
			config=$.extend({},config);
			panel=config.panel||(config.panel=config);
		} 
		panel.show=panel.show||function(){$(panel.domNode).addClass("panel-domNode").show();};
		panel.hide=panel.hide||function(){$(panel.domNode).hide();};			
		$.extend(config,{title:title,menus:menus,buttons:buttons,onActive:onActive,onDeactive:onDeactive});
		
		view.stack[view.current + 1]=config;
		view.stack[view.current+2]=null;
		
		if(view.showAcitivity(config,1,function(){window.history.pushState('forward', null, window.location.href);})!=false)
			return panel;
	};
}); 