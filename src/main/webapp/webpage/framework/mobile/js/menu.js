sunz.MMenu = function () {
	/**
	 * 菜单，主参数为：data:[],template:function|string|dom，itemclick
	 */
    var defaults = {
    		type:"default",
    		parent:"body",
    		target:null,
    		event:null,
    		autoHidden:true,
    		width:"180px",
			template:"<a>{title}</a>",
    		_description:{
    			title:"弹出菜单",
    			remark:"" 
    		}
    };
    var MMenu = function (_option) { 
    	
    	this.exoption = ["parent"];
    	this.initOption(defaults,_option);
    	
        /**
         * 组件基本容器
         *  
         */
        
        var div = document.createElement("div");
        div.className += " sunzmui-menu ";
        
        if(this.text){
        	div.innerHTML = this.text;
        }
        
        this.domNode = div;
        this.element = div;
        this.listenerNode = div;
        
        this.initDom();
    },proto=MMenu.prototype;
    
    proto.initDom = function(){
    	var self = this;
    	this.domNode.innerHTML = "";
    	
    	var pointNode = this.pointNode = document.createElement("div");
    	
    	if(this.type=="default"||this.type=="bottom"){
			pointNode.className += " sunzmui-menu-pointer ";
    	}else if(this.type=="right"){
			pointNode.className += " sunzmui-menu-pointer-right ";	 
    	}
    	this.domNode.appendChild(pointNode);
    	
    	
    	//列表
    	
    	var list = sunz.MList.create({data:this.data,template:this.template,itemclick:function(){
    		self.itemclick.apply(this, arguments);
    	},
    	parent:this.domNode});
    	
    	sunz.applyToElement(this.element, this.option); 
        //sunz.registListener(this.listenerNode, this.listener); 
    	
    	
    	
    	
        this.initListener(); 
        this.initPosition();
        this._oncreate();
    };
    
    
    proto.initPosition = function () {
    	if(this.event){
    		if(!this.target){
    			this.target = this.event.target;
    		}
    		var width = parseInt(this.width);
			var bodyWidth = $("body").width();
    		var top = this.target.offsetTop+this.target.clientHeight+10;
			var left = this.target.offsetLeft + this.target.clientWidth/2 - width/2;
    		if(this.type=="default"||this.type=="bottom"){ 
				this.pointNode.style.left = width/2 - 10  + "px";
				if(left<0){
					this.pointNode.style.left = width/2 - 10 + left  + "px";
					left=0;
				}
				else if(left > bodyWidth - width){
					
					this.pointNode.style.left =  left- ( bodyWidth - width - 5)  + width/2 +"px";
					left = bodyWidth - width - 5;
				}
    			this.domNode.style.top = top +"px";
        		this.domNode.style.left = left +"px";
    		}else if(this.type=="right"){
				 
				top = $(this.target).offset().top + 20;
    			left = $(this.target).offset().left + this.target.clientWidth + 30 ;
				this.domNode.style.top = top +"px";
        		this.domNode.style.left = left +"px";
    		}
    		
    	}
    };
    /**
     * 初始化完成后
     */
    proto._oncreate = function () {
    	var self = this;
    	if(this.autoHidden){
    		setTimeout(function(){
    			$("body").one("click",function(e){
        		 
        			self.hide();
        		});
    		},50 );
    	}
    	this.addToParent?this.addToParent():"";
    	this.trigger("create", this); 
    };
    proto.hide = function(){
    	 
    	this.destroy();
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
   
    return MMenu;
}();

