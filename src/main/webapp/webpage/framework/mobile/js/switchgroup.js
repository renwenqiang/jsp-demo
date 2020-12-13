sunz.MSwitchGroup = function () {
	/**
	 * 开关组
	 */
	var defaults ={
		type : "v",
		issingle:false,
		_description:{
			title:"开关组",
			remark:"" 
		}
	}
    var MSwitchGroup = function (option) {
        
        this.data = [];
        this.items = [];
        
        this.initOption(defaults, option);  
        this.initDomNode();  
        this.initDom();
       
        
    },proto=MSwitchGroup.prototype;
    
    proto.initDomNode = function () {
    	/**
         * 组件基本容器
         */
        var div = document.createElement("div");
        div.className += "  sunzmui-field  ";
       
        if(this.inline){
        	div.className += " sunzmui-field-inline "
        } 
        this.domNode = div;  
    };
    proto.initDom = function(){ 
    	 this.domNode.innerHTML = '';
     	/**
          * 文字
          */
         if (this.label) {
             var labelNode = document.createElement("label");
             labelNode.className += " sunzmui-field-lable   ";
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
             	labelNode.className += "  ";
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
         	contentNode.className += "   col-xs-9  col-sm-9    ";
         }
         this.domNode.appendChild(contentNode);
         contentNode.style.cssText += "position:relative; ";
         
       

         
         this.elecontentNode = contentNode;
         this.contentNode = contentNode;
         this.element = contentNode;
         this.listenerNode = contentNode;
         
         sunz.applyToElement(this.element, this.option);
         //sunz.registListener(this.listenerNode, this.listener);
         this.initListener();  
    	
    	
    	if(this.dict&&D&&D.get(this.dict)){ //3D244E9C9F64C4DBE05010AC09C807A6
        	this.data = D.get(this.dict);
        }
        if (this.data) {
            this.setData( this.data);
        }else if(this.url){
            this.loadData(this.url);
        }
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
     * 装载数据
     */
    proto.setData = function (_data) {
        if (_data &&  _data.length >= 0) {
            this.data = _data;
            this.render();
        } else {
            
        }
    };
    /**
     * 
     */
    proto.render = function () {
    	var self = this;
        this.clear();
        for (var i = 0; i < this.data.length; i++) {
        	var datai = this.data[i];
            var item = new sunz.MSwitch({
            	name:self.name,
            	value:datai[this.valueField]||datai.value,
            	label:datai[this.textField]||datai.text,
                parent:this.elecontentNode,
                listener:{
                	change:function(){
                		if(self.issingle&&this.getValue()){
                			self.changeItem(this);
                		}
                		
                	}
                }
            }); 
            this.items.push(item); 
        }
    };
    proto.changeItem = function(item){
    	this.setValue(item.getTextValue());
    };
    proto.loadData = function (url) {
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
    };
    proto.clear = function () {
        this.contentNode.innerHTML = "";
        this.items = [];
    };
    proto.toString = function () {
        return "HI this is a swich group";
    };
    
    /**
     * 获取值
     */
    proto.getValue = function(){ 
    	var value = ""
    	for(var id in this.items){
    		 var item = this.items[id];
    		 if(item.getValue()){
    			 if(value==""){
    				 value = item.getTextValue();
    			 }else{
    				 value += ","+item.getTextValue();
    			 }
    		 }
    	}
    	return value;
    };
    /**
     * 获取值
     */
    proto.setValue = function(value){
    	if(!value||typeof value!="string"){
    		return;
    	}
    	var values = value.split(",");
    	for(var id in this.items){
    		 var item = this.items[id];
    		 var itemvalue = item.getTextValue();
    		 if(values.indexOf(itemvalue)>=0){
    			 item.setValue(true);
    		 }else{
    			 item.setValue(false);
    		 }
    	}
    };

    return MSwitchGroup;
}();