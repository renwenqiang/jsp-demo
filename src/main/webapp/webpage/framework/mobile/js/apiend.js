if(sunz){
	for(var id in sunz){
		if(id.indexOf("M")==0){
			var mw = sunz[id],proto=mw.prototype; //
			mw.create=mw.create||function(opt){	// 统一宝义create，仅支持1个参数，如多于1个，需自定义此方法
				return new this(opt);
			}
			if(!proto) continue;
			proto.class=mw;	// var 作用域问题，使用实例变量保存替代let方式
				
			if(!proto.validate)proto.validate=sunz.validate;
		    if(!proto.initOption){
		    	/**
				 * 初始化配置
				 */
		    	proto.initOption = function(defaults,__option){
		    		// 2019-08-06 允许使用sunz.XXX.defaults进行修改
		    		var mw=this.class;
		    		if(!mw.defaultsInited){
						mw.defaults=$.extend(defaults,mw.defaults);
						mw.defaultsInited=true;
					}
		    		/**
		        	 * 整体dom
		        	 */
		        	//this.domNode = undefined;
		        	/**
		        	 * 验证时标记变红的dom，应当为 this.element的上一级
		        	 */
		        	//this.contentNode = undefined;
		        	/**
		        	 * form组件dom
		        	 */
		        	//this.element = undefined;
		        	/**
		        	 * 监听事件dom,可以随意定义，但是本类中的所有默认监听会绑定到该dom上，如果想要触发，需要调用trigger方法
		        	 * 如果想监听别的组件事件，需要自己写代码监听，在监听回调中执行本类中的trigger方法，这样就相当于把事件绑定在本类上
		        	 */
					var _option = __option;//JSON.parse(JSON.stringify(__option));
		        	//this.listenerNode = undefined;
		        	if(this.exoption){
		        		for(var i=0;i<this.exoption.length;i++){
		        			var oname = this.exoption[i];
		        			if(_option&&_option[oname]){
		        				delete(_option[oname]);
		        			}
		        		}
		        	}
		            var option = $.extend({}, defaults, _option);
		            
		            $.extend(this, option);
		            this.option = option;
		            this.options =  function(){
		            	return option;
		            }
		            
		            var tempoption = $.extend({}, defaults, __option);
		            if(tempoption&&tempoption.listener){
		        		var lis = this.listener = tempoption.listener?tempoption.listener:{};
		        		delete(tempoption.listener);
		        	}
		            if(tempoption.parent){
		            	this.parent = tempoption.parent;
		            }
		    	}
		    }
		    proto.initOption._description = {
		    		text:"初始化配置"
		    }
		    if(!proto.config){
		    	proto.config = function(oname){
		    		if(oname){
	            		return this.option[oname];
	            	} 
		    	}
		    }
		    if(!proto.initListener){
			    /**
			     * 初始化事件
			     */
				proto.initListener = function(){
					var self = this;
					if(self._preEvents){
						$.each(self._preEvents, function(id, item){
							$(self.listenerNode).unbind(id, item);
							delete self._preEvents[id] ;
						});
					}else{
						self._preEvents = {};
					}
					if(this.listenerNode){
						//$(this.listenerNode).unbind();
					}
					
			    	if(this.listener){
			    		for(var id in this.listener){
			    			var fun = this.listener[id];
			    			var event = this.bind(id, fun);
			    			self._preEvents[id] = event;
			    		}
			    	}
			    	this.bind("create", function(){
			    		return;
			    		if(self.option.value){
			    			alert(self.option.value);
			    			if(self.setValue){
			    				self.setValue(self.option.value)
			    			}
			    		}
			    	});
			    }
			}
		    proto.initListener._description = {
		    		text:"初始化事件"
		    }
			if(!proto.trigger){
				/**
			     * 触发监听事件
			     * @param fName
			     */
				proto.trigger = function(fName, data){ 
					var args = []; 
			    	$(this.listenerNode).trigger(fName,arguments);
			    	//$(this.listenerNode).trigger.apply($(this.listenerNode), arguments);
			    }
			}
		    proto.trigger._description = {
		    		text:"触发监听事件"
		    }
			if(!proto.bind){
			    /**
			     * 监听事件
			     */
				proto.bind = function(fName, func){
					var self = this;
					var fun =  function(e){
			    		if(func&&func.call){ 
			    			//func.call(self, e, self, arguments);
			    			//console.log(fName);
			    			 
			    			arguments[arguments.length] = e;
			    			arguments[arguments.length+1] = self;
			    			func.apply(self, arguments);
			    		}
			    	};
			    	$(this.listenerNode).bind(fName, fun);
			    	if(!this._eventsName){
			    		this._eventsName=[];
			    	}
			    	this._eventsName.push(fName);
			    	return fun;
			    }
			}
		    proto.bind._description = {
		    		text:"绑定监听事件"
		    }
			if(!proto.destroy){
			    /**
			     * 销毁对象
			     */
				proto.destroy = function(){
			    	this.trigger("beforeDestroy");
			    	try{
			    		$(this.domNode).remove();
			    		this.destroyed = true;
			    	}catch(e){
			    		console.error(e);
			    	} 
			    }
			 }
		    proto.destroy._description = {
		    		text:"销毁对象"
		    }
			if(!proto.markInvalid){
			    /**
			     * 标记验证未通过
			     */
				proto.markInvalid = function (msg) {
			    	sunz.markInvalid(this.contentNode||this.elecontentNode);
			    };
			}
		    proto.markInvalid._description = {
		    		text:"标记验证未通过"
		    }
		    if(!proto.clearInvalid){
			    /**
			     * 取消标记验证未通过
			     */
		    	proto.clearInvalid = function () {
			    	sunz.clearInvalid(this.contentNode);
			    };
		    }
		    proto.clearInvalid._description = {
		    		text:"取消标记验证未通过"
		    }
		    if(!proto.addToParent){
		    	/**
		    	 * 添加到父元素
		    	 */
		    	proto.addToParent = function(){
		    		if(this.listenerNode){
		    			this.listenerNode.sunzObject = this;
		    		}
		        	if(this.parent){
		        		$(this.parent).append(this.domNode);
		        		this.loaded =true;
		        		this.trigger("addToParent")
		        	}else{
		        		this.loaded =false;
		        		//$("body").append(this.domNode);
		        	}
		        } 
		    }
		    proto.addToParent._description = {
		    		text:" 添加到父元素"
		    }
		    if(!proto.update){
			    /**
			     * 重新设置参数
			     */
		    	proto.update = function (option) {
		    		if(this.beforeUpdate){
		    			this.beforeUpdate(option);
		    		}
			    	this.initOption(this.option, option);
			    	this.initDom();
			    };
		    }
		    proto.update._description = {
		    		text:"  重新设置参数"
		    }
		    var asName = "as" + mw.name;
		    if(!proto[asName]){
			    /**
			     * 重新设置参数
			     */
		    	proto[asName] = function (option) { 
			    	this.update(option);
			    };
		    }
		    proto[asName]._description = {
		    		text:"  重新设置参数"
		    }
		    
		    if(!proto.disable){
			    /**
			     *  
			     */
		    	proto.disable = function () {
			    	if(this.element)
			    		this.element.disabled = true;
			    };
		    }
		    proto.disable._description = {
		    		text:"设置不可编辑"
		    }
		    if(!proto.enable){
			    /**
			     *  
			     */
		    	proto.enable = function () {
		    		if(this.element)
		    			this.element.disabled = false;
			    };
		    }
		    proto.enable._description = {
		    		text:"设置可编辑"
		    }
		    if(!proto.readOnly){ 
		    	proto.readOnly = function () {
		    		if(this.element)
		    			this.element.readOnly  = "readonly";
			    };
		    }
		    proto.readOnly._description = {
		    		text:"设置只读"
		    }
		    if(!proto.editable){ 
		    	proto.editable = function () {
		    		if(this.element)
		    			this.element.readOnly  = "";
			    };
		    }
		    proto.readOnly._description = {
		    		text:"设置非只读"
		    }
		    if(!proto.hide){ 
		    	proto.hide = function () {
		    		$(this.domNode).hide();
			    };
		    }
		    proto.hide._description = {
		    		text:"隐藏元素"
		    }
		    if(!proto.show){ 
		    	proto.show = function () {
		    		$(this.domNode).show();
			    };
		    }
		    proto.show._description = {
		    		text:"显示元素"
		    }
		}
	}
}