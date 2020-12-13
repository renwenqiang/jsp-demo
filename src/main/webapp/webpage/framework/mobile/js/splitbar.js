sunz.MSplitBar = function () {
	/**
	 * 分割线，主参数：text
	 */
    var defaults = {
    		type:"default",
    		_description:{
    			title:"分割线",
    			remark:"" 
    		}
    };
    var MSplitBar = function (_option) { 
    	
    	this.initOption(defaults,_option);
        /**
         * 组件基本容器
         *  
         */
        
        var div = document.createElement("div");
        div.className += "  sunzmui-splitbar " ;
        div.style.backgroundColor = document.body.style.backgroundColor;
        if(this.text){
        	div.innerHTML = this.text;
        }
        
        this.domNode = div;
        this.element = div;
        this.listenerNode = div;
        
        sunz.applyToElement(this.element, this.option); 
        //sunz.registListener(this.listenerNode, this.listener); 
        this.initListener();
        this._oncreate();
    },proto=MSplitBar.prototype;
    
    /**
     * 初始化完成后
     */
   proto._oncreate = function () {
    	this.addToParent?this.addToParent():"";
    	this.trigger("create", this); 
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
   
    return MSplitBar;
}();

