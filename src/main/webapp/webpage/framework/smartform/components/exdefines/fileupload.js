/**
 * 
 */
(function(){
	// ************** 移动端代码适配--基于移动端的filelist ***************
	if(!window.smartformHelper)$('<script src="webpage/framework/query/smartformHelper.js"></script>').appendTo("head");
	if(!$.tip){
		$('<style type="text/css">.mprogress{padding:15px 0}.mprogress-msg{line-height:36px;text-align:center}.wincontainer{width:100%;height:100%}.sunzmui-limg-div,.sunzmui-lvideo-div{width:110px !important;height:110px !important}</style>').appendTo("head");
		$.prompt=function(t,m,b,fn){
			return $.messager.confirm(t,m,fn);
		};
		$.tip=function(msg){$.messager.show({title:"友情提示",msg:msg})};
		$.hideTip=function(){};
		sunz.MProgress=sunz.MProgress||function(opt){
			var win=new sunz.Dialog({attrs:{"class":"mprogress"},width:360,closable:false,modal:true});
			win.header().remove();
			var jt=$('<div class="mprogress-msg">正在上传...</div>').appendTo(win);
			var p=new sunz.Progressbar({parent:win});
			this.setValue=function(v){p.setValue(Math.round(100*v))};
			this.setInfo=function(msg){jt.text(msg)};
			this.destroy=win.close;
			if(opt){
				var t=opt.msg||opt.info;
				t && this.setInfo(t);
				opt.value && this.setValue(opt.value);
			}
		}
		var win,dContainer,last,deactive=function(){
			if(last){
				last.onDeactive && last.onDeactive(-1,{});
				if(last.destroyOnback)dContainer.empty();
			}};
		window.viewport={
				container:$("body"),
				add:function(config){
					if(!win){
						var c={title:"-",modal:true,content:'<div class="wincontainer"></div>',onClose:deactive};
						var hp=smartformHelper,w=hp.parseSize(setting.formWidth, hp.offsetWidth),h=hp.parseSize(setting.formHeight, hp.offsetHeight);
						if(!w||!h){c.fit=true}
						else{c.width=w;c.height=h;}
						win=new top.sunz.Window(c);
						dContainer=win.find(".wincontainer");
					}
					if(last){
						last.onDeactive && last.onDeactive(1,{});
					}
					last=config;
					$(config.panel.domNode).appendTo(dContainer.empty());
					this.setTitle(config.title);
					this.setButtons(config.buttons);
					win.open();
					config.onActive && config.onActive({deta:1});
				},
				back:function(){
					win.close();
				},
				setTitle:function(t){
					win.setTitle(t);
				},
				setButtons:function(){
					
				}
					
			};
	}
	///////////////////////////////////////////////////////////////////////
	
	smart.define("Fileupload",{
		name:"附件上传",
		defaults:{width:"100%",height:"120px",editable:true},
		customAdd:function(pele,params){
			var ele=$('<div class="filelist-container" style="overflow-y:scroll"></div>')[0];
			pele.insertPoint?pele.insertBefore(ele,pele.insertPoint):pele.appendChild(ele);
			$(ele).addClass(smart.selectProxyClass);
			ele.setting=params;
			// 异步加载
			ele.master=ele;
			ele.masterLoaded=$.Callbacks();
			
			this.customUpdate(ele,params);			
			return ele;
		},
		customUpdate:function(ele,setting){
			smart.applyToElement(ele,setting||ele.setting);

			if(ele.uploader && ele.uploadType==setting.type)
				return;
			// 异步加载
			var setVal=new $.Deferred(),changeMode=new $.Deferred();
			ele.setFormValue=function(){setVal.resolve.apply(setVal,arguments)}
			ele.changeMode=function(editable){changeMode.resolve.apply(changeMode,arguments);};
			if(!window.require)C.requireResources("requirejs",true);
			require(["mobile/framework/util/filelist"],function(filelist){
				var uploader=ele.master=ele.uploader=componentManager[setting.id]=filelist[ele.uploadType=setting.type](setting);
				uploader.setting=setting;
				uploader.setFormValue=ele.setFormValue=function(v,o){
					uploader.update({bid:v||o.id||o.ID||null});
				}
				uploader.getFormValue=function(){
					return $.map(uploader.getAttachments(),function(a){return a.id});
				}
				uploader.changeMode=function(b){
					uploader.update({editable:b},true);
					uploader.find(".file-proxy")[b?"show":"hide"]();
				};
				ele.save=function(pdata,fn,skip){	// 伪造子表单，以便被调用
					if(uploader.getDirties().length){
						uploader.upload(fn,setting.name?pdata[setting.name]:(pdata.id||pdata.ID));
					}else{
						fn && fn();
					}
				}				
				uploader.appendTo($(ele).empty());
				ele.masterLoaded.fire(uploader);
				setVal.then(ele.setFormValue=uploader.setFormValue);
				changeMode.then(ele.changeMode=uploader.changeMode);
			})
		}
	});
})();