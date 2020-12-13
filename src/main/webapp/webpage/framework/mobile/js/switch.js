sunz.MSwitch = function () {
	/**
	 * 开关组件（二选一、checkbox组件)
	 */
    var defaults = {
    		type:"default",
    		inline:true,
    		_description:{
    			title:"开关组件",
    			remark:"" 
    		}
    };
    var MSwitch = function (_option) { 
    	this.exoption = ["width","height"];
    	this.initOption(defaults,_option);
        /**
         * 组件基本容器
         */
        var div = document.createElement("div");
        div.className += "  sunzmui-field  ";
       
        if(this.inline){
        	div.className += " sunzmui-field-inline "
        }
        this.domNode = div; 
        this.initDom();
    },proto=MSwitch.prototype;
    
    proto.initDom = function(){
    	 this.domNode.innerHTML = '';
    	/**
         * 文字
         */
        if (this.label) {
            var labelNode = document.createElement("label");
            labelNode.className += " sunzmui-field-lable  ";
            if(this.inline){
            	labelNode.className += "  sunzmui-field-lable-inline-switch   ";
            	if(this.labelwidth){
            		if(typeof this.labelwidth =="number"){
            			this.labelwidth = ""+this.labelwidth+"px";
            		}
            		else if(typeof this.labelwidth =="string"){
            			if(this.labelwidth.indexOf("px")>=0||this.labelwidth.indexOf("%")>=0||this.labelwidth.indexOf("rem")>=0){
            				
            			}else{
            				this.labelwidth +="px";
            			}
            		}
            		labelNode.style.cssText += " ; width:"+this.labelwidth +"; "
            	}
            }else{
            	labelNode.className += "   ";
            }
            labelNode.style.cssText += " ";
            this.domNode.appendChild(labelNode);
            labelNode.innerHTML = this.label;
        }

        /**
         * 输入框容器
         */
        var contentNode = document.createElement("div");
        contentNode.className += " sunzmui-field-input  col-xs-pull-right col-sm-pull-left";
        if(this.inline){
        	contentNode.className += "  sunzmui-field-input-inline-switch ";
        	if(this.labelwidth){
        		contentNode.style.cssText += " ; width:calc(100% - "+this.labelwidth +"); "
        	}
        }else{
        	contentNode.className += "     ";
        }
        this.domNode.appendChild(contentNode);
        contentNode.style.cssText += "position:relative; ";
        
        /**
         * 输入框
         */
        var inputNode = document.createElement("input");
        inputNode.className += " sunzmui-switch sunzmui-switch-animbg ";
        inputNode.type =   "checkbox";
        contentNode.appendChild(inputNode);
        inputNode.style.cssText += " ";

        
        this.elecontentNode = contentNode;
        this.element = inputNode;
        this.listenerNode = inputNode;
        
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
    	this.onblur();
    };
   
    proto.onblur = function () {
    	var self = this;
    	this.bind("blur",function(){
    		self.validate?self.validate():"";
    	}) 
    };
    
    
    /**
     * 获取和设置值，是否选中
     * @returns
     */
    proto.getValue = function(){
    	return this.element.checked;
    };
    proto.setValue = function(value){
    	try{
    		this.element.checked = !!value;
    	}catch(e){
    		throw("参数 value 的值不合法");
    	}    	
    };
    /**
     * 获取真实值
     * @returns
     */
    proto.getTextValue = function(){
    	return $(this.element).val();
    };
    /**
     * 设置真实值
     * @param value
     * @returns
     */
    proto.setTextValue = function(value){
    	return $(this.element).val(value);
    };

    return MSwitch;
}();