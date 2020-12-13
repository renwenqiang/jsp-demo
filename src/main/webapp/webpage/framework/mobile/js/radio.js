sunz.MRadio = function () {
	/**
	 * 单选按钮,label,labelWidth,data:[{textField|"text",valueField|"value"]|url
	 */
    var defaults = {
    		type:"default",
    		inline:true,
    		_description:{
    			title:"单选按钮",
    			remark:"" 
    		}
    };
    var MRadio = function (_option) { 
    	this.exoption = ["width","height"];
    	this.initOption(defaults,_option);
        
        /**
         * 组件基本容器
         */
        var div = document.createElement("div");
        if(!this.ignoreOutSide){
        	div.className += "  sunzmui-field  ";
            if(this.inline){
            	div.className += " sunzmui-field-inline "
            }
        }
        
        this.domNode = div; 
        this.initDom();
    };
    MRadio.prototype.initDom = function(){
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
        	contentNode.className += "    ";
        	if(this.labelwidth){
        		contentNode.style.cssText += " ; width:calc(100% - "+this.labelwidth +"); "
        	}
        }else{
        	contentNode.className += "     ";
        }
        this.domNode.appendChild(contentNode);
        contentNode.style.cssText += "position:relative; ";        
        this.elecontentNode = contentNode;        
        this.listenerNode = contentNode; 
        this.initListener();
        
        if(this.dict&&D&&D.get(this.dict)){ //3D244E9C9F64C4DBE05010AC09C807A6 
        	this.data = D.get(this.dict , this.blank);
        }
        if( this.data){
        	this.setData(this.data);
        }else if( this.url){
        	this.loadData(this.url);
        }
        this._oncreate();
    };
    /**
     * 初始化完成后
     */
    MRadio.prototype._oncreate = function () {
    	if(!this.loaded){
    		this.addToParent?this.addToParent():"";
    	}    	
    	this.trigger("create", this); 
    	this.loaded = true;
    	this.onblur();
    };
   
    MRadio.prototype.onblur = function () {
    	var self = this;
    	this.bind("blur",function(){
    		self.validate?self.validate():"";
    	}) 
    };
    
    /**
     * 设置选项
     * @param data
     */
    MRadio.prototype.setData = function(data){
    	
    	if(data&&data.length){
    		this.data = data;
    		for(var i=0;i<data.length;i++){
    			var text = data[i][this.textField||"text"];
    			var value = data[i][this.valueField||"value"];
    			 
    			var item = $('<div class="sunzmui_radio_item"><input name="'+this.name+'" type="radio" value="'+value+'" />'+text+'</div>')[0];
    			$(this.elecontentNode).append(item);
    		}
    		if(this.defaultValue != undefined){
    			this.setValue(this.defaultValue);
    		}
    	}
    }
    /**
     * 从服务端加载选项
     * @param url
     */
    MRadio.prototype.loadData = function(url){
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
     * 获取和设置值，是否选中
     * @returns
     */
    MRadio.prototype.getValue = function(){
    	console.log(1111)
    	return $(":checked",this.elecontentNode).val();
    };
    MRadio.prototype.setValue = function(value){
    	 $("input",this.elecontentNode).val(value);  	
    };
    /**
     * 获取真实值
     * @returns
     */
    MRadio.prototype.getText  = function(){
    	var _value = this.getValue();
    	for(var id in this.data){
    		var _d = this.data[id];
    		var text = _d[this.textField||"text"];
			var value = _d[this.valueField||"value"];
    		if(_value==value){
    			return text;
    		}
    	}
    	return  "无";
    };
    /**
     * 设置真实值
     * @param value
     * @returns
     */
    MRadio.prototype.setTextValue = function(value){
    	return $(this.element).val(value);
    };
    
    MRadio.create = function(option){
    	return new MRadio(option);
    };
   
    return MRadio;
}();

