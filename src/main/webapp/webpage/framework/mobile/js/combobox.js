sunz.MComboBox = function () {
	/**
	 * 下拉框，dict|data:[{textField|"text,valueField|"value"}]｜url
	 */
    var defaults = {
        readonly: null ,
        addAll:false,
        inline:true ,
        easyui:false,
		_description:{
			title:"单选组件",
			remark:"" 
		}
        //,data:[{name:"请选择",value:""},{name:"男",value:"man"},{name:"女",value:"woman"}]
    };
    var MComboBox = function (_option) {
    	this.exoption = ["width","height"];
    	/**
    	 * 整体dom
    	 */
    	this.domNode = undefined;
    	/**
    	 * form组件dom
    	 */
    	this.element = undefined;
    	/**
    	 * 监听事件dom,可以随意定义，但是本类中的所有默认监听会绑定到该dom上，如果想要触发，需要调用trigger方法
    	 * 如果想监听别的组件事件，需要自己写代码监听，在监听回调中执行本类中的trigger方法，这样就相当于把事件绑定在本类上
    	 */
    	this.listenerNode = undefined;
    	
        this.initOption(defaults, _option);
        
        /**
         * 组件基本容器
         */
        var div = document.createElement("div");
        div.className += " sunzmui-field  ";
        this.domNode = div;
        if(this.inline){
        	div.className += " sunzmui-field-inline "
        }
        this.initDom();
    },proto=MComboBox.prototype;
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
            labelNode.className += " sunzmui-field-lable   ";
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
            	labelNode.className += "    ";
            }
            labelNode.style.cssText += " ";
            this.domNode.appendChild(labelNode);
            labelNode.innerHTML = this.label;
        }

        /**
         * 输入框容器
         */
        var contentNode = document.createElement("div");
        contentNode.className += " sunzmui-field-input  ";
        if(this.inline){
        	contentNode.className += "  sunzmui-field-input-inline   ";
        	if(this.labelwidth){
        		contentNode.style.cssText += " ; width:calc(100% - "+this.labelwidth +"); "
        	}
        }else{
        	contentNode.className += "      ";
        }
        this.domNode.appendChild(contentNode);
        contentNode.style.cssText += " ";
        this.elecontentNode = contentNode;
        /**
         * 输入框
         */
        
        
        if(this.easyui){
        	var inputNode = document.createElement("input");
        	inputNode.className += "  aaa  sunzmui-form-control  input-noborder"; 
            contentNode.appendChild(inputNode);
            inputNode.style.cssText += "  ";
            this.element = inputNode;
            $(this.element).combobox({
            	valueField:this.valueField||"value",
                textField:this.textField||"text", 
                multiple:true,
                panelHeight:'auto', 
                labelPosition: 'top',
                data: [{
					label: 'java',
					value: 'Java'
				},{
					label: 'perl',
					value: 'Perl'
				},{
					label: 'ruby',
					value: 'Ruby'
				}]
            }
            );
            //this.textField||"text"], data[i][this.valueField||"value"
        }else{ 
        	var inputNode = document.createElement("select");
            inputNode.className += " sunzmui-field-input ";
            if(this.showBorder){
            	inputNode.className += "  form-control ";
            }
            else{
            	inputNode.className += "  sunzmui-form-control  input-noborder";
            }
             
            contentNode.appendChild(inputNode);
            inputNode.style.cssText += " ";

            this.element = inputNode;
            this.element.options = [];
        }
        this.listenerNode = this.element;
        sunz.applyToElement(this.element, this.option);
        //sunz.registListener(this.listenerNode, this.listener);
        this.initListener();
        this.trigger("create"); 
        if(this.dict&&D&&D.get(this.dict)){ //3D244E9C9F64C4DBE05010AC09C807A6 
        	this.data = D.get(this.dict , this.blank);
        }
        if( this.data){
        	this.setData(this.data);
        }else if( this.url){
        	this.loadData(this.url);
        }
        this._oncreate();
    }
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
     * 设置选项
     * @param data
     */
    proto.setData = function(data){
    	if(this.easyui){
    		if(this.addAll){
    			var item1 = {};
    			item1[this.textField||"text"] = "全部";
    			item1[this.valueField||"value"] = "";
    			data = [ item1 ].concat(data);
    		}
    		$(this.element).combobox({data:data})
    		this.data = data;
    	}
    	else{
    		this.element.options.length=0;
        	if(data&&data.length){
        		this.data = data;
        		if(this.addAll){
        			var option = new Option("全部", "");
        			this.element.add(option);
        		}
        		for(var i=0;i<data.length;i++){
        			var option = new Option(data[i][this.textField||"text"], data[i][this.valueField||"value"]);
        			this.element.add(option);
        		}
        		if(this.defaultValue != undefined){
        			this.setValue(this.defaultValue);
        		}
        	}
    	}
    	
    }
    /**
     * 从服务端加载选项
     * @param url
     */
    proto.loadData = function(url){
    	 var self = this;
         if (!url) { return; }
         this.url = url;
         var ajax = $.ajax(this.url).done(function (result) {
             if(self.loadFilter){
                 var data = self.loadFilter(result);
                 self.setData(data);
             }else{
            	 self.setData(result.data?result.data:result);
             }
             
         }).fail(function (e) {
             console.error(e);
         });
    }
    /**
     * 输入验证
     */
    
 
    /**
     * 获取当前值
     */
    proto.getValue = function () {
        var value = this.element.value;
        if(this.easyui){
        	return $(this.element).combobox("getValue");
        }
        return $(this.element).val( );;
    };
    proto.getValues = function () {
        var value = this.element.value;
        if(this.easyui){
        	return $(this.element).combobox("getValues");
        }
        return $(this.element).val( );;
    };
    /**
     * 设置值
     */
    proto.setValue = function (value) {
    	if(this.element){
    		$(this.element).val(value);
    		if(this.easyui){
    	    	$(this.element).combobox("setValue",value)
    	    }
    		this.trigger("change");
    	}
    };
    proto.setValues = function (value) {
    	if(this.element){
    		$(this.element).val(value);
    		if(this.easyui){
    	    	$(this.element).combobox("setValues",value)
    	    }
    		this.trigger("change");
    	}
    };  
   
    return MComboBox;
}();