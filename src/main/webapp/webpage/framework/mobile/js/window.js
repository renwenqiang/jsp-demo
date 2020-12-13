sunz.MWindow = function() {
	/**
	 * 弹出窗口（移动端不常用)
	 */
    var defaults = {
    		height: "fit-content",
	        parent: "body",
	        title: "提示",
	        showBottom: false,
	        showClose: true,
	        destroyOnHide: true,
	        contentFit: false ,
	        hideWhenBtnClick: true,
	        onlyOkBtn: false,
	        modal:true,
	        moveable:false,
	        okText:"确定",
	        cancelText:"取消"
    }
    var MWindow = function(option) {
        this.type = "v"; //v垂直 h 方块水平浮动 
        this.initOption(defaults, option);
        this.initDomNode();
        this.initDom();
    },proto=MWindow.prototype;
    
    proto.initDomNode = function() {
        var alphaDiv = document.createElement("div");
        alphaDiv.className += " sunzmui-window-alphadiv ";
        this.domNode = alphaDiv;
        if(this.modal){
        	
        }else{
        	alphaDiv.style.cssText += ' ;pointer-events: none; background-color:transparent;'
        }
    };
    proto.initDom = function() {
        var self = this;
        var panel1 = sunz.MPanel.create({
            title: this.option.title,
            content: this.content,
            type: "v",
            bottom: this.showBottom ? " " : null,
            parent: this.domNode
        });
        this.panel = panel1;
        this.panelNode = panel1.domNode;
        this.contentNode = panel1.contentNode;
        this.titleDom = panel1.titleDom;
        this.bottomNode = panel1.bottomNode;
        if (this.showClose&&this.title) {
            var closeBtn = this.closeBtn = document.createElement("div");
            closeBtn.className += "sunz-panel-closebtn  ";
            var li = document.createElement("li");
            li.className += "glyphicon glyphicon-remove ";
           
            
            closeBtn.appendChild(li);
            this.titleDom.appendChild(closeBtn);
             
           
        }
        
        this.listenerNode = this.panel.domNode;
        sunz.applyToElement(this.panelNode, this.option);
        if (this.height) { 
            //this.panel.domNode.style.cssText +=  "; height:" + (parseInt(this.height) + contenttop + contentbottom) + "px;";
        }
        if (this.showBottom) {
            if(!this.onlyOkBtn){
            	var cancelbtn = this.cancelbtn = sunz.MButton.create({
                    text: this.cancelText||"取消",
                    parent: panel1.bottomNode,
                    listener: {
                        "click": function(e) {
                        	 
                            self._onCancel(e);
                        }
                    }
                });
            }
            
            var okbtn = this.okbtn =  sunz.MButton.create({
                text: this.okText||"确定",
                parent: panel1.bottomNode,
                listener: {
                    "click": function(e) {
                        self._onOk(e);
                    }
                }
            });
            if (this.height) {
               // this.panel.domNode.style.cssText +=  "height:" + (parseInt(this.height) +  contenttop + contentbottom) + "px";
                
            }
            if(this.bottomNode)
            	this.bottomNode.style.cssText += " bottom:0px;position:relative;width:100%;";
        }
        if (this.contentPadding) {
            this.contentNode.style.cssText += "padding:" + this.contentPadding + ";"
        }
        //sunz.registListener(this.listenerNode, this.listener);
        this.initListener();
        this._oncreate();
    };
    proto.onOk = function(fn) {
        this.bind("ok", fn);
        return this;
    };
    proto.onCancel = function(fn) {
        this.bind("cancel", fn);
        return this;
    };
    proto._onOk = function(e) {
    	
    	try{
    		this.trigger("ok", e, true);
    	}catch(e){
    		
    	}
    	if(this.hideWhenBtnClick){
    		this.hide();
    	}
    };
    proto._onCancel = function(e) { 
        try{
        	this.trigger("cancel", e, false);
    	}catch(e){
    		
    	}
    	if(this.hideWhenBtnClick){
    		this.hide();
    	}
    };
    /**
     * 初始化完成后
     */
    proto._oncreate = function() {
    	var self = this;
    	 $(this.closeBtn).bind("click",function(e) {
          
             self.hide(e);
         })
        if (!this.loaded) {
            this.addToParent ? this.addToParent() : "";
        }
    	this._resize();
        this.trigger("create", this);
        this.loaded = true;
    
        //window.history&&history.pushState("","")
        window.addEventListener("popstate", function (e) {
        	if(self.isShow){
        		self.trigger("cancel", e, false);
        		self.close(true); 
        	}
        		
        }, false);
        
        this.isShow = true;
        var titleclassName = "sunzmwindowtitle_move_" + parseInt(Math.random() * 1000);
        $(this.titleDom).addClass(titleclassName);
        if(this.moveable&&$.prototype.draggable){
        	$(this.panelNode).draggable({
        	    handle: "." + titleclassName 
        	});
        }
    };
    proto._resize = function(){
    	var self = this;
    	//内容上下边界
        var contenttop = 0;
        var contentbottom = 0;
        if (this.showBottom) {
        	contentbottom = 55;
        }
        if (this.title) {
        	contenttop = 40;
        }
        
        if (this.contentFit) {
            this.contentNode.style.cssText += "position: absolute;padding:0px;overflow: auto;  top: " + contenttop + "px;    bottom: " + contentbottom + "px;    left: 0px;    right: 0px;";
            if(this.bottomNode)
            	this.bottomNode.style.cssText += " bottom:0px;position:absolute;width:100%;";
        }
    	if (this.showBottom){
    		var titleHeight = $(this.titleDom).outerHeight()||0;
    		var bottomHeight = $(this.bottomNode).outerHeight()||0;
    		var height = 100;
    		if (this.height&&(parseInt(this.height))) {
    			height = parseInt(this.height);
            }
    		this.panel.domNode.style.cssText +=  "height:" + (parseInt(height) +  titleHeight + bottomHeight) + "px";
    		this.contentNode.style.cssText +=  "height:" + height + "px";
    	}else{
    		var titleHeight = $(this.titleDom).outerHeight()||0;
    		var bottomHeight = 0;
    		var height = 100;
    		if (this.height&&(parseInt(this.height))) {
    			height = parseInt(this.height);
            }
    		this.panel.domNode.style.cssText +=  "height:" + (parseInt(height) +  titleHeight + bottomHeight) + "px";
    		this.contentNode.style.cssText +=  "height:" + height + "px";
    	}
    	
    	
    };
    proto.open = function() {
       this.show();
    };
    proto.close = function(fromEvent) {
        this.hide(fromEvent);
    };
    proto.hide = function(fromEvent) {
    	this.isShow = false;
        $(this.domNode).hide();
        if (this.destroyOnHide) {
            this.destroy();
        }
        if(!fromEvent){
        	//history.back()
        }
    };
    proto.show = function() {
    	this.isShow = true;
        $(this.domNode).show();
       
    };
    
    return MWindow;
}();
sunz.Window =sunz.Window||sunz.MWindow;
sunz.alert = function(title, content) {
    var option = typeof title == "object" ? title :{
        title: content ? title : "",
        content: content ? content : title,
        parent: "body",
        showBottom: true,
        showClose: false,
        destroyOnHide: true,
        class:"mobileAlert"
    };
    return new sunz.MWindow(option);
};
sunz.alertw = function(title, content) {
    var option =  typeof title == "object" ? title :{
        title: content ? title : "提示",
        content: content ? content : title,
        parent: "body",
        showBottom: true,
        showClose: false,
        width: "300px",
        destroyOnHide: true 
    };
    return new sunz.MWindow(option);
};
sunz.alert0 = function(obj, msg, icon, fun, cancle) {
    var opts = typeof obj == "object" ? obj : {
        title: obj,
        content: msg,
        icon: icon,
        parent: "body",
		showBottom: true,
        class:"mobileAlert"
    };
    return (new sunz.MWindow(opts)).onOk(fun).onCancel(cancle);
};
$.alert = sunz.alert;
$.prompt = sunz.alert0;