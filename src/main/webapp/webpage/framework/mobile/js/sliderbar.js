sunz.MSliderBar = function() {    
    /**
     *  数值范围选择组件，主参数：min,max,minText,maxText,value,type:"h"|"v"(未实现？）,showSideText,emphasisColor,normalColor
     *
     */
	
    var defaults = {
        type: "h",
        value:0,
        emphasisColor:"#64bd63",
        normalColor:"#ccc",
        min:1,
        max:100,
        showSideText:true,
        minText:"1",
        maxText:"100",
		_description:{
			title:"数值范围选择组件",
			remark:"" 
		}
    };
    var MSliderBar = function(option) {
        this.initOption(defaults,option);
        var div = document.createElement("div");
        div.className +=" sunzmui-sliderbar  ";
        this.domNode = div;
        this.initDom();
        this.setValue(this.value);
    },proto=MSliderBar.prototype;
 
 
    proto.initDom = function(){
    	/**
         * 标题
         */
         if(this.title){
         	 this._initTitleDom();
         }
        
         var barNode = this.barNode = document.createElement("div");
         barNode.className+=" sunzmui-sliderbar-bar ";
         barNode.style.cssText+= " background-color:" + this.normalColor + "; "
         this.domNode.appendChild(barNode);
         
         var activebarNode = this.activebarNode = document.createElement("div");
         activebarNode.className+=" sunzmui-sliderbar-bar  sunzmui-sliderbar-bar-active ";
         activebarNode.style.cssText+= " background-color:" + this.emphasisColor + "; "
         this.domNode.appendChild(activebarNode); 
         
         var buttonNode = this.buttonNode = document.createElement("div");
         buttonNode.className+=" sunzmui-sliderbar-button ";
         buttonNode.style.cssText+= " background-color:" + this.emphasisColor + "; "
         this.domNode.appendChild(buttonNode);
        
         var leftText = this.leftTextNode = document.createElement("div");
         leftText.className+=" sunzmui-sliderbar-text sunzmui-sliderbar-lefttext ";
         leftText.innerHTML += this.minText;
         var rightText = this.rightTextNode = document.createElement("div");
         rightText.className+=" sunzmui-sliderbar-text sunzmui-sliderbar-righttext ";
         rightText.innerHTML += this.maxText;
         if(this.showSideText){
        	 this.domNode.appendChild(leftText);
        	 this.domNode.appendChild(rightText);
         }
         
         
         this.contentNode = this.domNode;
         this.listenerNode = this.domNode;
         sunz.applyToElement(this.contentNode, this.option); 
         
         this.initListener();
         this._oncreate();
         this.initSwipe();
    };
    proto.initSwipe = function(){
    	var self = this;
    	var swipe = sunz.Swipe.create(this.buttonNode,{
 			moveWithTouch:true,
 			isStopPropagation:true,
 			notClearMoveAfterEnd:true,
 			moveDirection:"leftorright",
 			maxMove:1000,
 			tolerance:0,
 			onMoveStart: function(){ 
 				console.log(this)
 				self.startSwip = true;
 				//console.log("onStart");
 				//self.baseButtonLeft = $(self.buttonNode).offset().left;
 			},
 			eleMoveEnd:function(a,b,c){ 
 				if(self.startSwip){ 
 					//console.log(a,b,c,self.baseButtonLeft)
 					var left = parseInt(self.buttonNode.style.left);
 					self.changeLength(left);
 				}
 				self.startSwip= false;
 				//console.log("end")
 			},
 			eleMove:function(a,b,c){
 				if(self.startSwip){ 
 					//console.log(a,b,c,self.baseButtonLeft)
 					var left = parseInt(self.buttonNode.style.left);
 					self.changeLength(left);
 				}  
 			}
 		});
    };
    proto.changeLength = function(left,issetValue){
    	var self = this;
    	var total = $(this.barNode).width() - 16;
    	if(total<0){
    		//dom没有初始化完成
    		setTimeout(function(){
    			self.changeLength(left,issetValue);
    		},100); 
    		return;
    	}
    	if(left<0){
    		left = 0;
    	}
    	else if(left>total){
    		left = total;
    	} 
    	$(self.activebarNode).width(left);
    	$(self.buttonNode).css("left",left );
    	if(!issetValue){
    		//console.log("calcValue")
    		this.calcValue();
    	}
    };
    proto.setValue = function(value){
    	var total = $(this.barNode).width() - 16;
    	var self = this;
    	if(total<0){
    		//dom没有初始化完成
    		setTimeout(function(){
    			self.setValue(value);
    		},100); 
    		return;
    	}
    	var oldValue = this.value;
    	this.value = value; 
    	if(value<this.min){
    		this.value = this.min;
    	}else{
    		if(value>this.max){
        		this.value = this.max;
        	}
    	}
    	var left =total/(this.max - this.min) * (this.value -this.min) ; 
    	this.changeLength(left,true);
    	if(this.value!= oldValue){
    		this.trigger("change", this.value, oldValue);
    	}
    };
    proto.getValue = function(value){
    	return this.value;
    };
    proto.calcValue = function(){
    	var  left =  parseInt(this.buttonNode.style.left) ;
    	var total = $(this.barNode).width() - 16;
    	var oldValue = this.value;
    	var value = this.value = left/total * (this.max - this.min) + this.min;
    	if(value<this.min){
    		this.value = this.min;
    	}else{
    		if(value>this.max){
        		this.value = this.max;
        	}
    	}
    	if(this.value!=oldValue){
    		this.trigger("change", this.value, oldValue);
    	}
    	
    }; 
    /**
     * 初始化完成后
     */
    proto._oncreate = function () {
    	//this.initListener();
    	if(!this.loaded){
    		this.addToParent?this.addToParent():"";
    	}    	
    	this.loaded = true;
    	this.trigger("create", this); 
    	
    };
 
    return MSliderBar;
}();
 