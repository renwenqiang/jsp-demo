/**
 * {text/title,cls,iconCls,textCls,iconStyle,textStyle,img,activeImg,model,params}
 */
define([],function(){
	let TabContainer=function(tabs,config){
		let jele=$('<div style="width:100%;height:100%;overflow-y:scroll;position:relative;"></div>');
		$.extend(this,config);
		
		this.title=tabs[0].title;
		this.panel={domNode:jele};		
		let activeModel=null,activeTab=null;
		let bubble=function(){
			viewport.setTitle(activeModel.title);
			viewport.setButtons(activeModel.buttons);
			//viewport.setMenus(activeModel.menus);
		};
		
		this.bbars=$.map(tabs,function(tab){
			let model=tab.model,params=tab.params;
			tab.handler=function(){
				if(activeTab==model)
					return;
				
				if(tab.detached){
					return require([model],function(Model){
						viewport.add(Model);
						Model.setParam(params);
					});
				}	
				let show=function(){
					require([model],function(Model){
						if(activeModel)
							$(activeModel.panel.domNode).fadeOut();
						
						$(Model.panel.domNode).appendTo(jele).fadeIn();
						Model.setParam(params);
						Model.onActive && Model.onActive({deta:1,pre:activeModel});
						activeTab=model;
						activeModel=Model;
						bubble();
					});
				};
				if(activeModel){
					if(activeModel.onDeactive){
						let eventArg={deta:1,target:model,handled:false,goingon:show};
						let r=activeModel.onDeactive(eventArg);
						if(r===false || eventArg.handled)
							return;
					}
				}				
				show();
			};	
			return tab;
		});
		this.onActive=function(){
			if(!this.onceActived){	// 引擎在add时会setBBar，此时会先于此Active事件调用一些
				this.onceActived=true;
				return;
			}
			if(!activeModel){				
			}else{
				bubble();
				activeModel.onActive && activeModel.onActive.apply(activeModel,arguments);
			}
		};
		this.onDeactive=function(){
			viewport.hideBBar();
			activeModel.onDeactive && activeModel.onDeactive.apply(activeModel,arguments);
		}
	};	
	
	return TabContainer;
});