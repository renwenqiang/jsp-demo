 
/**
 * sunz.MButton
 */
sunz.MButton = function () {
    var defaults = {
    		type:"default"
    };
    var MButton = function (_option) { 
    	
    	this.initOption(defaults,_option);
        /**
         * 组件基本容器
         *  
         */
        
        var div = document.createElement("button");
        div.className += "  btn btn-" + this.type;
        
        if(this.text){
        	div.innerHTML = this.text;
        }
        
        this.domNode = div;
        this.element = div;
        this.listenerNode = div;
        
        sunz.applyToElement(this.element, this.option); 
        sunz.registListener(this.listenerNode, this.listener); 
        this._oncreate();
    };
    
    /**
     * 初始化完成后
     */
    MButton.prototype._oncreate = function () {
    	this.addToParent?this.addToParent():"";
    	this.trigger("create", this); 
    };
    
    /**
     * 重新加载option
     */
    MButton.prototype.update = function(option){
    	this.initOption(defaults,option);
    	if(this.text){
        	this.domNode.innerHTML = this.text;
        }
    }
    MButton.create = function(option){
    	return new MButton(option);
    };
   
    return MButton;
}();

