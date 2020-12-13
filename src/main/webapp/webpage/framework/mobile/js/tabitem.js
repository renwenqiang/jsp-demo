sunz.MTabItem = function () {
	/**
	 * tab项，主参数为template,iconCls,activetemplate,activeCls
	 * template为tplReplace，参数为option本身
	 */
    var defaults = {
    		template:'<img src="{imgsrc}"/>{title}',
    		activetemplate: '<img src="{activeimgsrc}"/><span style="color:red;"">{title}</span>',
    		inited:false,
    		tab:null,
    		activeCls:null,
    		iconCls:null
    };
    var MTabItem = function (_option) {  
    	this.initOption(defaults,_option);
    	
        /**
         * tab项的按钮
         *  
         */
        
        var div = document.createElement("div");
        div.className += " sunzmui-tab-nav-item "  ;
        if(this.iconCls){
        	div.className += " "+this.iconCls+" "  ;
        }
        this.domNode = div;
        this.element = div;
        this.listenerNode = div; 
        
        this.initDom();
        sunz.applyToElement(this.element, this.option);
        this.initListener(); 
        this._oncreate();
        
    },proto=MTabItem.prototype;
    
    proto.initDom = function(){ 
    	this.domNode.innerHTML = sunz.tplReplace(this.template,this.option); 
    	
    	this.initContent();
    };
    
    
    proto.active = function(){
    	if(this.tab){
    		this.tab.showItemA(this);
    	}
    	
    };
    proto.deactive = function(){
    	this.hide();
    	
    };
    proto.show = function(){
    	if(this.viewloaded){
    		//$(this.viewDom).show();
    	}else{
    		if(!this.viewDom){
    			this.initContent(); 
        	}
    		$(this.tab.contentNode).append(this.viewDom);
    		this.viewloaded = true;
    	} 
    	if(this.activeCls){
    		$(this.domNode).addClass(this.activeCls);
    	}
    	if(this.activetemplate){
    		this.domNode.innerHTML = sunz.tplReplace(this.activetemplate,this.option); 
    		
    	}
    	var dn =window.dn= this.domNode;
    	setTimeout(function(){dn.scrollIntoView( );},200)
    	 
    	this.trigger("show");
    	this.trigger("active");
    };
    
    proto.hide = function(){
    	if(this.viewloaded){
    		//$(this.viewDom).hide();
    	} 
    	if(this.activeCls){
    		$(this.domNode).removeClass(this.activeCls);
    	}
    	
    	if(this.activetemplate){
    		this.domNode.innerHTML = sunz.tplReplace(this.template,this.option); 
    	}
    	this.trigger("hide");
    	this.trigger("deactive");
    };
    
    
    proto.initContent = function(){
    	this.viewDom = document.createElement("div");
    	this.viewDom.className += "  content-item "; 
    	$(this.viewDom ).width( this.viewWidth);
    	if(this.ele){
    		$(this.viewDom).append(this.ele);
    	}
    	$(this.tab.contentNode).append(this.viewDom);
		this.viewloaded = true;
    	
    };
    /**
     * 初始化完成后
     */
    proto._oncreate = function () { 
    	var self = this;
    	if(!this.loaded){
    		this.addToParent?this.addToParent():"";
    	}
    	this.trigger("create", this); 
    	this.bind("click", function(){
    		//self.tab.showItem(self);
    		self.active();
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
   
    return MTabItem;
}();

