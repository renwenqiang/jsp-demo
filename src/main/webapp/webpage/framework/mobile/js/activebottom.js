sunz.MActiveBottom = function () {
	/**
	 * 【容器】底部弹出的菜单
	 */
    var defaults = { 
    		parent:"body",
    		_description:{
    			title:"底部弹框",
    			remark:"" 
    		}
    };
    var MActiveBottom = function (_option) { 
    	
    	this.initOption(defaults,_option);
    	
        /**
         * 组件基本容器
         *  
         */
        this.initDom();
        this.element = this.domNode;
        this.listenerNode = this.domNode;
        sunz.applyToElement(this.element, this.option); 
        //sunz.registListener(this.listenerNode, this.listener); 
        this.initListener();
        if(this.handler){
        	this.bind("click",this.handler);
    	}
        this._oncreate();
    },proto=MActiveBottom.prototype;
    
    proto.initDom = function(){
    	var self = this;
    	var domNode = this.domNode = document.createElement("div");
    	domNode.className += "sunzmobile-fullwindow sunzmobile-alpha"; 
    	 
    	var bodyNode = this.bodyNode = document.createElement("div"); 
    	bodyNode.className += "sunzmobile-activebottom sunzmobile-transition";
    	domNode.appendChild(bodyNode);
    	
    	var titleNode =    this.titleNode = document.createElement("div"); 
    	titleNode.className += "sunzmobile-activebottom-title";
    	bodyNode.appendChild(titleNode);
    	if(this.title){
    		$(titleNode).append(this.title); 
    	}
    	
    	var contentNode =   this.contentNode = document.createElement("div"); 
    	contentNode.className += "sunzmobile-activebottom-content";
    	bodyNode.appendChild(contentNode);
    	if(this.content){
    		$(contentNode).append(this.content); 
    	}
    	
    	var bottomNode =    this.bottomNode = document.createElement("div"); 
    	bottomNode.className += "sunzmobile-activebottom-bottom";
    	bodyNode.appendChild(bottomNode);
    	 
    	var buttom = sunz.MButton.create({text:"取消",parent:bottomNode,class:"sunzmobile-activebottom-bottom-buttom",onclick:function(){
	    		   self.destroy()
	 	   } });
    	
    };
    /**
     * 初始化完成后
     */
    proto._oncreate = function () {
    	this.addToParent?this.addToParent():"";
    	this.trigger("create", this); 
    	this._resize();
    };
    proto._resize = function(){
    	var titleHeight = $(this.titleNode).outerHeight();
		var bottomHeight = $(this.bottomNode).outerHeight();
		var contentHeight = $(this.contentNode).outerHeight();
    	var height = titleHeight + bottomHeight + contentHeight;
    	this.bodyNode.style.cssText += "height:"+height+"px;";
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
    return MActiveBottom;
}();