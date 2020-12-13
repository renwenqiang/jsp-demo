 
/**
 * sunz.MSwitchGroup
 */
sunz.MSwitchGroup = function () {
    /**
     * 列表
     * @param {*} container 
     */
	var defaults ={
		type : "v",
		issingle:false
	}
    var MSwitchGroup = function (option) {
        
        this.data = [];
        this.items = [];
        
        this.initOption(defaults, option);  
        this.initDomNode();  
        this.initDom();
       
        
    };
    MSwitchGroup.prototype.initDomNode = function () {
    	var panel1 = sunz.MPanel.create({title:this.option.label,type:"v",parent:this.option.parent});
    	this.contentNode = panel1.contentNode;
        this.domNode = panel1.domNode;
        this.listenerNode = this.domNode;
        //sunz.registListener(this.listenerNode, this.listener);
        this.initListener();
    };
    MSwitchGroup.prototype.initDom = function(){ 
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
    MSwitchGroup.prototype._oncreate = function () {
    	if(!this.loaded){
    		this.addToParent?this.addToParent():"";
    	}    	
    	this.trigger("create", this); 
    	this.loaded = true;
    	this.onblur();
    };
   
    MSwitchGroup.prototype.onblur = function () {
    	var self = this;
    	this.bind("blur",function(){
    		self.validate?self.validate():"";
    	}) 
    };
    
 
    /**
     * 装载数据
     */
    MSwitchGroup.prototype.setData = function (_data) {
        if (_data && _data.length && _data.length >= 0) {
            this.data = _data;
            this.render();
        } else {
            throw ("data must be a array");
        }
    };
    /**
     * 
     */
    MSwitchGroup.prototype.render = function () {
    	var self = this;
        this.clear();
        for (var i = 0; i < this.data.length; i++) {
        	var datai = this.data[i];
            var item = new sunz.MSwitch({
            	name:self.name,
            	value:datai[this.valueField]||datai.value,
            	label:datai[this.textField]||datai.text,
                parent:this.contentNode,
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
    MSwitchGroup.prototype.changeItem = function(item){
    	this.setValue(item.getTextValue());
    };
    MSwitchGroup.prototype.loadData = function (url) {
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
    MSwitchGroup.prototype.clear = function () {
        this.contentNode.innerHTML = "";
        this.items = [];
    };
    MSwitchGroup.prototype.toString = function () {
        return "HI this is a swich group";
    };
    
    /**
     * 获取值
     */
    MSwitchGroup.prototype.getValue = function(){ 
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
    MSwitchGroup.prototype.setValue = function(value){
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
    
    MSwitchGroup.create = function(option){
    	return new MSwitchGroup(option);
    }
    return MSwitchGroup;
}();