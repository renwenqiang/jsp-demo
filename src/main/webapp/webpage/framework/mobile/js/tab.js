sunz.MTab = function () {
	/**
	 * Tbas组件，data：[]|url
	 */
    var defaults = {
    		type: "top",
    		fixed: false,
    		loop:false,
    		activeBarTemplate:'<span class="tab-common-activeBar"></span>'
    };
    var MTab = function (_option) { 
    	this.items = [];
    	this.currentIndex = -1;
    	this.initOption(defaults,_option);
    	
        /**
         * 组件基本容器
         *  
         */
        
        var div = document.createElement("div");
        div.className += "  sunzmui-tab sunzmui-tab-"+this.type ;
        
        this.domNode = div;
        this.element = div;
        this.listenerNode = div;
        //this.contentDom = div;
        
        this.initDom();
        sunz.applyToElement(this.element, this.option);  
        
        this.parentWidth = $(this.parent).width();

        this.initListener(); 
        this._oncreate();
    },proto=MTab.prototype;
    
    proto.initDom = function(){
    	/**
    	 * 创建tab标签容器
    	 */
    	var navDom = this.navDom = document.createElement("div");
    	navDom.className += " tab-nav ";
    	if(!this.fixed){
    		navDom.style.cssText += " position:absolute;";
    	}else{
    		navDom.style.cssText += " position:fixed;";
    	}
    	$(this.domNode).append(navDom);
    	
    	var navContainer = this.navContainer = document.createElement("div");
    	navContainer.className += " tab-nav-container ";
    	$(this.navDom).append(navContainer);
    	
    	var contentNode = this.contentNode = document.createElement("div");
    	contentNode.className += " tab-content ";
    	contentNode.style.cssText += ' height:calc(100% - '+$(navDom).height()+'px)';
    	console.log(' height:(100% - '+$(navDom).height()+'px)')
    	$(this.domNode).append(contentNode);
    	
    	if(this.activeBarTemplate){
    		console.log(1)
    		var activeBar = null;
        	if(typeof this.activeBarTemplate == "string"){
        		activeBar = $(sunz.tplReplace(this.activeBarTemplate,this.option))[0]; 
        	}
        	this.activeBar  = activeBar;
        	$(this.navContainer).append(activeBar);
    	}
    	
    	
    	this.initSwipe();
    	this.resizeContentHeight();
    };
    
    
    proto.resizeContentHeight = function () {
    	var self = this;
    	this.bind("addToParent",function(){
    		self._resizeContentHeight();
    	})
    }
    proto._resizeContentHeight = function(){
    	var self = this;
		function resize(){
			self.contentNode.style.cssText += ' height:calc(100% - '+$(self.navDom).height()+'px)';
		}
		setTimeout(resize, 100);
	}
    /**
     * 初始化完成后
     */
    proto._oncreate = function () {
    	this.addToParent?this.addToParent():"";
    	this._resizeContentHeight();
    	this.trigger("create", this); 
    	if (this.data) {
            this.setData( this.data);
        }else if(this.url){
            this.loadData(this.url);
        }
        
    };
    /**
     * 加载tab页列表
     * @param data
     */
    proto.setData = function(data){
    	if(data){
    		this.data = data;
    	}
    	if(!this.data) return;
    	for(var i = 0;i<this.data.length;i++){  
    		this.add(this.data[i]);
    	}
    	this.showItem(0);
    	
    };
    proto.add = function(data){ 
    	var config = $.extend({tab:this, parent: this.navContainer,data:data,viewWidth:this.parentWidth},this.itemOption,data);
    	var item = sunz.MTabItem.create(config);
		this.items.push(item);
		//$(this.contentNode ).width(this.items.length * this.parentWidth);
		this.resizeContent();
		return item;
    }
    proto.resizeContent = function(){
    	$(this.contentNode ).css("width",'calc(100% * '+this.items.length +')');
    	for(var i=0;i<this.items.length;i++){
    		var _item = this.items[i];
    		$(_item.viewDom).css("width",'calc(100% / '+this.items.length +')');
    	}
    }
    
    proto.showItemA = function(item){
    	this.contentNode.style.transition = "margin-left 0s  ,left 0s ";
    	this.showItem(item);
    };
    proto.showItem = function(item){
    	
    	var index = 0;
    	if(typeof item =="number"){
    		index = item;
    		
    		if(this.loop){
    			if(index>=this.items.length ){
        			index = 0;
        		}
        		if(index<0){
        			index = this.items.length-1;
        		}
    		}else{
    			if(index>=this.items.length ){
        			index = this.items.length-1;
        		}
        		if(index<0){
        			index = 0;
        		}
    		}
    		item = this.items[index];
    	}else{
    		index = this.items.indexOf(item);
    	}
    	if(index == this.currentIndex){
    		//this.contentNode.style.cssText += "margin-left:-"+ (this.parentWidth*index) + "px;"/ "+ (this.items.length) + "
    		this.contentNode.style.cssText += "margin-left:calc(-100%  * "+index+")"
    		return; 
    	}
    	for(var i=0;i<this.items.length;i++){
    		var _item = this.items[i];
    		_item.hide();
    	}
    	if(this.currentIndex>=0){
    		this.trigger("hideItem",this.items[this.currentIndex]);
    	}
    	if(item.viewDom){
    		//this.contentNode.style.cssText += "margin-left:-"+ (this.parentWidth*index) + "px;"
    		this.contentNode.style.cssText += "margin-left:calc(100% / "+ (this.items.length) + " * "+index+")";
    		this.contentNode.style.cssText += "margin-left:calc(-100%  * "+index+")";
    		item.show();
        	this.currentIndex = index;
        	this.trigger("showItem",this.items[this.currentIndex]);
    	}
    	
    	if(this.activeBar){ 
    		this.activeBar.style.left = item.domNode.offsetLeft +"px"; 
    		$(this.activeBar).width( $(item.domNode).width());
    	}
    	
    };
    
    proto.showNext = function(){
    	if(this.currentIndex>=0){
    		this.showItem(this.currentIndex + 1)
    	}
    }
    proto.showPre = function(){
    	if(this.currentIndex>=0){
    		this.showItem(this.currentIndex - 1)
    	}
    }

    proto.initSwipe = function(){
    	var self = this;
    	
        var swipe = sunz.Swipe.create(this.contentNode,{
 			moveWithTouch:true,
 			moveDirection:"leftorright",
 			maxMove:self.parentWidth,
 			onMoveStart: function(){ 
 				console.log(this)
 				self.startSwip = true;
 				console.log("onStart")
 			},
 			eleMoveEnd:function(a,b,c){
 				self.parentWidth = $(self.parent).width();
 				self.contentNode.style.transition = "margin-left 0.5s  ,left 0.5s ";
 				if(Math.abs(c)>self.parentWidth/3){
 					self.contentNode.style.cssText += "left:0px;margin-left:-"+ (self.parentWidth*self.currentIndex-c) + "px;"
 	 				if(c<0 ){
 	 					self.showNext();
 	 				}else{
 	 					self.showPre();
 	 				}
 				} else{
 					self.contentNode.style.cssText += "left:0px; " 
 				}
 				self.startSwip= false;
 				console.log("end")
 			},
 			eleMove:function(a,b,c){
 				if(self.startSwip){ 
 				}  
 			}
 		});
        return;
        var swipe = sunz.Swipe.create(this.contentNode,{
 			moveWithTouch:true,
 			moveDirection:"right",
 			maxMove:150,
 			onMoveStart: function(){ 
 				console.log(this)
 				self.startSwip = true;
 				console.log("onStart")
 			},
 			eleMoveEnd:function(a,b,c){
 				console.log(111)
 				self.contentNode.style.transition = "margin-left 0s ,left 0.5s ";
 				if(Math.abs(c)>self.parentWidth/3 *2){
 					self.contentNode.style.cssText += "left:0px;margin-left: "+ (self.parentWidth*self.currentIndex+c) + "px;"
 	 				self.showNext();
 				} else{
 					self.contentNode.style.cssText += "left:0px; " 
 				}
 				self.startSwip= false;
 				console.log("end")
 			},
 			eleMove:function(a,b,c){
 				if(self.startSwip){ 
 				}  
 			}
 		});
    };
    /**
     * 重新加载option
     */
    proto.update = function(option){
    	this.initOption(defaults,option);
    	if(this.text){
        	this.domNode.innerHTML = this.text;
        }
    }
   
    return MTab;
}();

