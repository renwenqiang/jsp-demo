sunz.MListItem = function () {
    /**
     * 列表项,通常用于list内部使用
     *  
     * @param data ： *
     * @param template： function|string|dom
     */
	var defaults = { 
    		_description:{
    			title:"列表项",
    			remark:"一般情况下不单独使用" 
    		}};
    var ListItem = function (option) { 
        this.template = "<a>{title}</a>";
        this.activetemplate = "<a style='color:blue;'>{title}</a>";
        this.initOption(defaults, option); 
        this.html = "";
        this.setObj(option.data);
        this.setTemplate(option.template);
        
        this.domNode = document.createElement("li");
        this.listenerNode = this.domNode;
        if (this.type == "h") {
            this.domNode.className += " sunzmui-h-list-group-item  ";
        } else {
            this.domNode.className += " list-group-item  ";
        }
        
        this.initDom();
    },proto=ListItem.prototype;
    
    proto.initDom = function(){
    	var option = this.option;
    	this.domNode.innerHTML = "";
    	if(option.data&&option.data.__iconCls){
       	 	//glyphicon glyphicon-arrow-left
    		this.domNode.className += " glyphicon " + option.data.__iconCls + " ";
        }
    	this.render();
    	sunz.applyToElement(this.element, this.option); 
        //sunz.registListener(this.listenerNode, this.listener); 
    	this.initListener();
    	this._oncreate();
    	
    };
     
    /**
     * 初始化完成后
     */
    proto._oncreate = function () {
    	if(!this.loaded){
    		this.addToParent?this.addToParent():"";
    	}    	
    	this.trigger("create", this); 
    	this.loaded = true;
    };
    proto.setTemplate = function (template) {
        if (template)
            this.template = template;
    };
    proto.setObj = function (obj) {
        if (typeof (obj) == "string") {
            this.data = this.obj = { title: obj };
        } else {
        	this.data = this.obj = obj;
        }
    };
    proto.setData = function (obj) {
        if (typeof (obj) == "string") {
            this.data = this.obj = { title: obj };
        } else {
        	this.data = this.obj = obj;
        }
    };
    proto.render = function ( ) {
        var self = this;   
        if(typeof this.template == "function"){
        	this.html = this.template.call(this, this.obj);
        }else{
        	 if (this.obj) {
                 this.html = sunz.tplReplace(this.template, this.data);
             } else {
                 this.html = sunz.tplReplace(this.template, {});
             }
        }
       
        if ( this.html) {
            $(this.domNode).append(this.html);
        }
    }; 
   
    return ListItem;
}();