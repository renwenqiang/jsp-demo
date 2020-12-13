sunz.MButton = function () {
	/**
	 * 按钮，基本参数：text，iconCls,handler；type相当于"btn-"+iconCls
	 */
    var defaults = {
    		type:"default",
    		text:"",
    		iconCls:"",
    		handler:null,
    		_description:{
    			title:"按钮",
    			remark:"" 
    		}
    };
    var MButton = function (_option) { 
    	
    	this.initOption(defaults,_option);
    	
        /**
         * 组件基本容器
         *  
         */
        
        var div = document.createElement("button");
        div.className += "  sunzmui-button  btn-" + this.type;
        if(this.iconCls){
        	div.className += " " + this.iconCls
        }
        
        if(this.text){
        	div.innerHTML = this.text;
        }
        
        this.domNode = div;
        this.element = div;
        this.listenerNode = div;
        
        sunz.applyToElement(this.element, this.option); 
        //sunz.registListener(this.listenerNode, this.listener); 
        this.initListener();
        if(this.handler){
        	this.bind("click",this.handler);
    	}
         
        this._oncreate();
    },proto=MButton.prototype;
    
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
   
    return MButton;
}();