
if(sunz){
	for(var id in sunz.charts){
		if(true){
			var mw = sunz.charts[id];
			if(!mw.prototype) continue;
				
			
		    if(!mw.prototype.initOption){
		    	/**
				 * 初始化配置
				 */
		    	mw.prototype.initOption = function(defaults,_option){
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
		            
		            if(option&&option.listener){
		        		var lis = this.listener = option.listener?option.listener:{};
		        		delete(option.listener);
		        	}
		            $.extend(this, option);
		            this.option = option;
		            
		    	}
		    }
		     
		    
		    if(!mw.prototype.initSeriesOption){
		    	mw.prototype.initSeriesOption = function( _option){
		    		var self = this;
		    		try{
		    			//var option = [];
			    		for(var i=0;i<_option.length;i++){
			    			var _c = _option[i];
			    			if(_c&&_c.type){
			    				var typeinfo = mw.seriesType[_c.type];
			    				var c = typeinfo.config;
			    				if(!c){
			    					continue
			    				}
			    				var sconfig = $.extend({}, c, _c);
			    				if(this.chartOption&&this.chartOption.series){
			    					this.chartOption.series.push(sconfig);
			    					if(sconfig.dataConfig&&sconfig.dataConfig.url){
			    						getRomoteData(sconfig, function(){
			    							self.reSetChartOption();
			    						});
			    					}
			    				}
			    			}
			    			
			    		} 
			    		
		    		}catch(e){
		    			console.log(e)
		    		}
		    		function getRomoteData(config, callback){
		    			var url = config.dataConfig.url; 
		    			$.get(url).then(function(result){
		    				var data;
		    				if(config.dataConfig.filter){
		    					data = config.dataConfig.filter(result);
		    				}else{
		    					data = result.data?result.data:result;
		    				}
		    				config.data = data;
		    				callback?callback():"";
		    			});
		    		}
		    		
		    	}
		    }
		    if(!mw.prototype.initChartOption){
		    	mw.prototype.initChartOption = function(defaults, _option){
		    		var option = $.extend({}, defaults, _option,mw.getDefaultOption());
		    		this.chartOption = option; 
		    	}
		    }
		    if(!mw.prototype.reSetChartOption){
		    	mw.prototype.reSetChartOption = function(){
		    		var option = this.chartOption;
		    		if(option&&this.chart){
		    			this.chart.setOption(option);
		    		}
		    	}
		    }
		    if(!mw.prototype.setChartOption){
		    	mw.prototype.setChartOption = function(option){
		    		 
		    		if(option&&this.chart){
		    			this.chart.setOption(option);
		    		}
		    	}
		    }
		    if(!mw.prototype.config){
		    	mw.prototype.config = function(oname){
		    		if(oname){
	            		return this.option[oname];
	            	} 
		    	}
		    }
		    if(!mw.prototype.initListener){
			    /**
			     * 初始化事件
			     */
				mw.prototype.initListener = function(){
					var self = this;
			    	if(this.listener){
			    		for(var id in this.listener){
			    			var fun = this.listener[id];
			    			this.bind(id, fun);
			    		}
			    	}
			    }
			}
			if(!mw.prototype.trigger){
				/**
			     * 触发监听事件
			     * @param fName
			     */
				mw.prototype.trigger = function(fName){ 
					var args = [];//.slice(1);
					for(var i=0;i<arguments.length;i++){
						if(i>0){
							args.push(arguments[i]);
						}
					}
			    	$(this.listenerNode).trigger(fName,args);
			    }
			}
			if(!mw.prototype.bind){
			    /**
			     * 监听事件
			     */
				mw.prototype.bind = function(fName, func){
					var self = this;
			    	$(this.listenerNode).bind(fName, function(e){
			    		if(func&&func.call){
			    			func.call(self, e, self, arguments);
			    			//console.log(fName);
			    		}
			    	});
			    }
			}
			if(!mw.prototype.destroy){
			    /**
			     * 销毁对象
			     */
				mw.prototype.destroy = function(){
			    	this.trigger("beforeDestroy");
			    	try{
			    		$(this.domNode).remove();
			    		this.destroyed = true;
			    	}catch(e){
			    		console.error(e);
			    	} 
			    }
			 }
			
			if(!mw.prototype.onCreate){
		    	/**
		    	 * 添加到父元素
		    	 */
		    	mw.prototype.onCreate = function(){
		    		
		    		this.addToParent();
		    		this.initListener();
		        } 
		    }
		    
		    if(!mw.prototype.addToParent){
		    	/**
		    	 * 添加到父元素
		    	 */
		    	mw.prototype.addToParent = function(){
		    		if(this.listenerNode){
		    			this.listenerNode.sunzObject = this;
		    		}
		        	if(this.parent){
		        		$(this.parent).append(this.domNode);
		        		this.loaded =true
		        	}else{
		        		this.loaded =false;
		        		//$("body").append(this.domNode);
		        	}
		        } 
		    }
		    
		    if(!mw.prototype.update){
			    /**
			     * 重新设置参数
			     */
		    	mw.prototype.update = function (option) {
			    	this.initOption(this.option, option);
			    	this.initChartOption(this.chartOption, option.option); 
			    	this.initDom();
			    };
		    }
		    
		    var asName = "as" + mw.name;
		    if(!mw.prototype[asName]){
			    /**
			     * 重新设置参数
			     */
		    	mw.prototype[asName] = function (option) { 
			    	this.update(option);
			    };
		    }
		    
		    if(!mw.prototype.hide){ 
		    	mw.prototype.hide = function () {
		    		$(this.domNode).hide();
			    };
		    }
		    if(!mw.prototype.show){ 
		    	mw.prototype.show = function () {
		    		$(this.domNode).show();
			    };
		    }
		}
	}
}