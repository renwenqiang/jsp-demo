sunz.MTextbox = function () {
	/**
	 * 文本输入框，
	 */
    var defaults = {
        readonly: null,
        type: "text",
        inline:true,
        placeholder:"请输入",
        noborder:false,
		_description:{
			title:"单行文本",
			remark:"" 
		}
        //,rightButton:"菜单",
       // rightButtonWidth: "40px",
        //rightButtonClass:""
    };
    var MTextbox = function (_option) {
    	/**
    	 * exoption  如果在option中配置的这些属性则option中的这些配置不生效
    	 */
    	this.exoption = ["width","height"];
    	this.initOption(defaults,_option);
    	
        /**
         * 组件基本容器
         */
        var div = document.createElement("div");
        div.className += " sunzmui-field ";
       
        if(this.inline){
        	div.className += " sunzmui-field-inline "
        }
        if(this.noborder){
        	div.style.cssText += "border:0px;"
        }
        this.domNode = div;
        this.initDom();
    },proto=MTextbox.prototype;

    proto.initDom = function(){
    	var self = this;
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
            labelNode.className += " sunzmui-field-lable ";
            if(this.inline){
            	labelNode.className += " sunzmui-field-lable-inline ";
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
        	if(this.label){
        		contentNode.className += "    col-sm-9    ";
        	}else{
        		contentNode.className += "    col-sm-12    ";
        	}
        	
        }
        this.domNode.appendChild(contentNode);
        contentNode.style.cssText += "position:relative; ";
        
        /**
         * 输入框
         */
        var inputNode = document.createElement("input");
        inputNode.className += " sunzmui-field-input ";
        if(this.showBorder){
        	inputNode.className += "   ";
        }
        else{
        	inputNode.className += "  sunzmui-form-control input-noborder";
        }
        inputNode.type = this.option.type;
        contentNode.appendChild(inputNode);
        inputNode.style.cssText += " ";

        
        //右侧按钮
        if(this.rightButton){
        	this.rightNode = $('<div style=""></div>')[0];
        	$(this.rightNode).append(this.rightButton.html||"");
        	$(this.rightNode).addClass(this.rightButton.rightButtonClass||"");
        	$(this.rightNode).css("width", this.rightButton.rightButtonWidth ||"40px");
        	this.rightNode.style.cssText +=    "display: inline-block;";
        	contentNode.appendChild(this.rightNode );
        	$(inputNode).css("width","calc(100% - " +(this.rightButton.rightButtonWidth||"40px")+")");
        	inputNode.style.cssText += "display: inline-block; ";
        	$(this.rightNode).bind("click",function(e){
        		 
        		self.trigger("rightButtonClick")
        	});
        }
        
        
        this.elecontentNode = contentNode;
        this.element = inputNode;
        this.listenerNode = inputNode;
        
        //sunz.registListener(this.listenerNode, this.listener);
        
        
        
        
        sunz.applyToElement(this.element, this.option);
        this.initListener();
        this._oncreate();
    };
    /**
     * 初始化完成后
     */
    proto._oncreate = function () {
    	if(this.rightButton){
    		this.bind("rightButtonClick",this.rightButton.onclick||this.rightButton.handler||function(e){})
        	
    		
    	}
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
        	value = new  Date(value)
        } 
        return value;
    };
    
    /**
     * 设置值
     */
    proto.setValue = function (value) {
    	
    	if(this.element){
    		if (this.type == "date") {
        		if( value instanceof Date){
        			value = value.format("yyyy-MM-dd");
        		}else{
        			value = new Date(value);
        			value = value.format("yyyy-MM-dd");
        		}
        	} 
    		$(this.element).val(value);
    	}
    };
    
    return MTextbox;
}();
sunz.MTextBox = sunz.MTextbox;

