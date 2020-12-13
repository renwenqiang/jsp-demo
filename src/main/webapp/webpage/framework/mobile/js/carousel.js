sunz.MCarousel = function () {
	/**
	 * 轮播组件,主参数为children:[dom]，time:number(milliseconds)
	 */
    var defaults = {
    		type:"default" , 
    		auto:true,
    		time:2500,
    		children:["1","2","3"],
    		loop: true,
    		_description:{
    			title:"轮播组件",
    			remark:"" 
    		}
    };
    var MCarousel = function (_option) { 
    	var self = this;
    	this.exoption = [];
    	//子元素
    	this.items = [];
    	this.currentIndex = 0;
    	this.initOption(defaults,_option); 
    	this.id = "carousel" + parseInt(Math.random()*10000);
        /**
         * 组件基本容器
         */
        var div = document.createElement("div");
        div.className += "  sunzmui-carousel "; 
        div.id = this.id;

        
        this.domNode = div; 
        var touchLis = sunz.Swipe.create(div,{
        	onSwipeRight:function(){
        		 
        		self.lastPage();
        	},
        	onSwipeLeft:function(){ 
        		self.nextPage();
        	}
        });
        this.initDom();
    },proto=MCarousel.prototype;
    proto.initDom = function(){
    	var self = this;
    	this.domNode.innerHTML = '';
    	 
    	var ol = document.createElement("ol"); 
    	ol.className += " sunzmui-carousel-tip ";
    	this.domNode.appendChild(ol);
    	this.topNode = ol;
    	
    	//<!-- 轮播（Carousel）项目 -->
        var ul = document.createElement("div"); 
        ul.className += "  sunzmui-carousel-inner "; 
        this.domNode.appendChild(ul);
        this.contentNode = ul;
  
        this.element = this.domNode;
        this.listenerNode = this.domNode;
        
        sunz.applyToElement(this.element, this.option); 
        this.initListener();
        this.initChildren();
        this._oncreate();
    };
    proto.initChildren = function(){
    	for(var i=0;i<this.children.length;i++){
    		var dom = document.createElement("div");
    		dom.className+=" sunzmui-carousel-item   ";
    		var child = this.children[i];
    		if(typeof child =="string" ){
    			if(child.trim().indexOf("<")==0){
    				$(this.children[i]).appendTo(dom); 
        		} 
    			else{
    				var imghtml = "<img src='"+this.children[i]+"'/>"
    				$(imghtml).appendTo(dom);
    			}
    		} else{
				$(this.children[i]).appendTo(dom);
			}
    		//dom.style.left = "calc(100% * "+i+")";
    		this._addItem(dom); 
    	}
    };
    proto.addItem = function(){
    	
    };
    /**
     * 添加新的页面
     */
    proto._addItem = function(item){
    	var self = this;
    	this.items.push(item);
    	var index = this.items.indexOf(item);
    	item.style.left = "calc(100% * "+index+")";
    	$(item).appendTo(this.contentNode); 
    	$(item).click(function(e){
			self.trigger("itemClick",index, item);
		});
    	if(index==0){
    		item.className+=" active "
    		$("<li class='sunzmui-carousel-tip-item active' data-target='"+this.id+"'  data-slide-to='"+(index)+"'></li>").appendTo(this.topNode);
        }else{
    		$("<li class='sunzmui-carousel-tip-item' data-target='"+this.id+"' data-slide-to='"+(index)+"'></li>").appendTo(this.topNode);
        } 
    };
    proto.nextPage = function(){ 
    	//$(this.domNode).carousel('next');
    	this.toPage(this.currentIndex + 1)
    };
    proto.lastPage = function(){ 
    	//$(this.domNode).carousel('prev');
    	this.toPage(this.currentIndex - 1)
    	 
    };
    proto.toPage = function(index){
    	if(index>=0&&index<this.items.length){
    		this.currentIndex = index; 
    	}
    	else if(index<0){ 
    		if(this.loop){
    			this.currentIndex = this.items.length-1; 
    		}else{
    			this.currentIndex = 0; 
    		} 
    	}
    	else if(index>=this.items.length){
    		if(this.loop){
    			this.currentIndex = 0; 
    		}else{
    			this.currentIndex = this.items.length -1; 
    		} 
    	}
    	for(var i=0;i<this.items.length;i++){
			var item = this.items[i];
			item.style.left = "calc(100% * "+(i-this.currentIndex)+")";	
			$(item).removeClass("active");
		}
    	$(this.items[this.currentIndex]).addClass("active");
    	
    	$(this.domNode).find(".sunzmui-carousel-tip-item").removeClass("active");
    	$($(this.domNode).find(".sunzmui-carousel-tip-item")[this.currentIndex]).addClass("active");
    	this.autoPlay();
    }
    
    /**
     * 初始化完成后
     */
    proto._oncreate = function () {
    	var self = this;
    	//$( this.domNode).carousel('cycle')
    	 
    	if(!this.loaded){
    		this.addToParent?this.addToParent():"";
    	}
    	if(this.auto){
    		this.autoPlay();
    	}
    	this.trigger("create", this); 
    	this.loaded = true; 
    };
    proto.autoPlay = function(){
    	var self = this;
    	if(this.auto){
    		clearTimeout(this.Timeout);  
    		this.Timeout = setTimeout(function(){
    			self.nextPage();
    		}, this.time);
    	}
    };
    proto.stopAutoPaly = function(){
    	clearTimeout(this.Timeout);  
    	this.auto = false;
    };
 
    return MCarousel;
}();

