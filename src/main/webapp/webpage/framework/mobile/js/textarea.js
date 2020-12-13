sunz.MTextArea = function () {
	/**
	 * 多文本输入框（textarea）
	 */
    var defaults = {
        readonly: null,
        type: "text",
        inline:false,
        noborder:false,
		_description:{
			title:"多行文本",
			remark:"" 
		}
    };
    var MTextArea = function (_option) {
    	/**
    	 * exoption  如果在option中配置的这些属性则option中的这些配置不生效
    	 */
    	this.exoption = ["width" ];
    	this.initOption(defaults,_option);
    	
        
        /**
         * 组件基本容器
         */
        var div = document.createElement("div");
        div.className += "  sunzmui-field form-group ";
       
        if(this.inline){
        	div.className += " sunzmui-field-inline "
        }
        if(this.noborder){
        	div.style.cssText += "border:0px;"
        }
        this.domNode = div;
        this.initDom();
    },proto=MTextArea.prototype;

    proto.initDom = function(){
    	this.domNode.innerHTML = "";
    	if(this.inline){
    		$(this.domNode).addClass("sunzmui-field-inline");
    	}else{
    		$(this.domNode).removeClass("sunzmui-field-inline");
    	}
        /**
         * 文字
         */
        if (this.label) {
            var labelNode = document.createElement("label");
            labelNode.className += " sunzmui-field-lable control-label ";
            if(this.inline){
            	labelNode.className += "  sunzmui-field-lable-inline   ";
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
            	labelNode.className += "    col-sm-3   ";
            }
            labelNode.style.cssText += " ";
            this.domNode.appendChild(labelNode);
            labelNode.innerHTML = this.label;
        }

        /**
         * 输入框容器
         */
        var contentNode = document.createElement("div");
        contentNode.className += " sunzmui-field-input ";
        if(this.inline){
        	contentNode.className += "  sunzmui-field-input-inline   ";
        	if(this.labelwidth){
        		contentNode.style.cssText += " ; width:calc(100% - "+this.labelwidth +"); "
        	}
        }else{
        	contentNode.className += "    col-sm-9    ";
        }
        this.domNode.appendChild(contentNode);
        contentNode.style.cssText += "position:relative; ";
        
        /**
         * 输入框
         */
        var inputNode = document.createElement("textarea");
        inputNode.className += " sunzmui-field-input form-control ";
        inputNode.type = this.option.type;
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
     * 获取当前值
     */
    proto.getValue = function () {
        var value = this.element.value;
        if (this.type == "number") {
            if (value.indexOf(".") >= 0) {
                value = parseFloat(value);
            } else {
                value = parseInt(value);
            }
        } else if (this.type == "date") {

        }
        return value;
    };
    
    /**
     * 设置值
     */
    proto.setValue = function (value) {
    	if(this.element){
    		$(this.element).val(value);
    	}
    };
    
    return MTextArea;
}();

