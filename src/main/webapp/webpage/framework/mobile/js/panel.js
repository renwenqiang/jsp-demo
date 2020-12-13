sunz.MPanel = function() {
    /**
     * 面板容器，主参数content，title:html，bottom:html
     *
     */
	
    var defaults = {
        type: "h",
		_description:{
			title:"面板容器",
			remark:"" 
		}
    };
    var MPanel = function(option) {
        this.initOption(defaults,option);
        var div = document.createElement("div");
        div.className +=" sunzmui-panel  ";
        this.domNode = div;
        this.initDom();
    },proto=MPanel.prototype;
    
    proto._initTitleDom = function(){
    	 var titleDom = this.titleDom = document.createElement("div");
         titleDom.className +=" sunzmui-panel-heading ";
         titleDom.innerHTML = this.title;
         if(this.contentNode){
        	 this.domNode.insertBefore(titleDom, this.contentNode);
         }else{
        	 this.domNode.appendChild(titleDom);
         }
         
    };
    proto._initBottomDom = function(){
    	var bottomDom = document.createElement("div");
     	bottomDom.className+=" sunzmui-panel-footer ";
     	bottomDom.innerHTML += this.bottom;
     	this.domNode.appendChild(bottomDom);
        this.bottomNode = bottomDom;
   }
    proto.initDom = function(){
    	/**
         * 标题
         */
         if(this.title){
         	 this._initTitleDom();
         }
         /**
          * 主体
          */
         var contentDom =  document.createElement("div");
         contentDom.className+=" sunzmui-panel-body ";
         this.domNode.appendChild(contentDom);
         if(this.content){
        	 $(contentDom).append(this.content);
         	//contentDom.innerHTML = this.content;
         }
         
         /**
          * 注脚
          */
         if(this.bottom){
         	this._initBottomDom();
         }
         
         if (this.type == "h") {
         	contentDom.className += " sunzmui-panel-h ";
         } else {
         	contentDom.className += " sunzmui-panel-v ";
         }
         
         
         this.contentNode = contentDom;
         this.listenerNode = contentDom;
         sunz.applyToElement(this.contentNode, this.option);
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
    /**
     * 重置参数
     * @param _option
     */
    proto.update = function(_option){
    	this.initOption(defaults,_option);
    	sunz.applyToElement(this.element, this.option);
    	this.initListener(this.listenerNode, this.listener);
    	this.setTitle(this.title);
    	this.setBottom(this.bottom);
    	
    };
    proto.setTitle = function(title){
    	this.title = title;
    	if(!this.titleDom){
    		this._initTitleDom();
    	}
    	this.titleDom.innerHTML = this.title;
    };
    proto.setBottom = function(bottom){
    	this.bottom = bottom;
    	if(!this.bottomNode){
    		this._initBottomDom();
    	}
    	if(this.bottomNode.innerText =="")
    		this.bottomNode.innerHTML = this.bottom;
    }
    MPanel.create = function(_option){
    	return new MPanel(_option);
    };
    return MPanel;
}();