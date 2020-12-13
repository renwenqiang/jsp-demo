sunz.MForm = function () {
	/**
	 * 表单
	 */
    var defaults = {  
    		_description:{
    			title:"表单组件",
    			remark:"" 
    		}
    };
    var MForm = function (_option) {
    	/**
    	 * 整体dom
    	 */
    	this.domNode = undefined;
    	/**
    	 * 验证时标记变红的dom，应当为 this.element的上一级
    	 */
    	this.contentNode = undefined;
    	/**
    	 * form组件dom
    	 */
    	this.element = undefined;
    	/**
    	 * 监听事件dom,可以随意定义，但是本类中的所有默认监听会绑定到该dom上，如果想要触发，需要调用trigger方法
    	 * 如果想监听别的组件事件，需要自己写代码监听，在监听回调中执行本类中的trigger方法，这样就相当于把事件绑定在本类上
    	 */
    	this.listenerNode = undefined;
    	
    	this.initOption(defaults,_option);
    	
        /**
         * 必须有DOMNode
         */
        var div = document.createElement("div");
        div.className += " sunzmui-form form-horizontal";
        div.style.cssText += "  width:100%; ";
        this.domNode = div;

       this.initDom();
    },proto=MForm.prototype;
    proto.initDom = function(){
    	 /**
         * 输入类组件必须有element，为原生表单控件
         */
        this.element = document.createElement("form");
        this.element.role = "form";
        sunz.applyToElement(this.element, this.option);
        
        
        
       
        this.contentNode = this.element;
        this.listenerNode = this.element;
        this.domNode.appendChild( this.element);
        
        sunz.applyToElement(this.element, this.option);
        //sunz.registListener(this.listenerNode, this.listener);
        this.initListener();
        this._oncreate();
    };
    /**
     * 初始化完成后
     */
    proto._oncreate = function () {
    	this.addToParent?this.addToParent():"";
    	this.trigger("create", this); 
    };
    
    /**
     * 获取当前值
     */
    proto.getValue =function(){
        
    };
    /**
     * 设置值
     */
    proto.setValue =function(){
        
    };
    proto.update = function(_option){
    	this.initOption(defaults,_option);
    	sunz.applyToElement(this.element, this.option);
    	sunz.registListener(this.listenerNode, this.listener);
    };
    return MForm;
}();





