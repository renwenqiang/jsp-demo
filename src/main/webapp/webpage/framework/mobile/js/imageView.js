sunz.MImageView = function(){
	/**
	 * 图片预览组件，主参数imageList：[imageUrl]
	 */
	var defaults = {
		deep:1,
		currentIndex:0,
		_description:{
			title:"图片预览组件",
			remark:"" 
		}
	};
	var MImageView = function(option) {
        this.type = "h"; //v垂直 h 方块水平浮动 
        this.items = [];
        this.navItems = [];
        this.initOption(defaults, option);
        this.initDomNode(); 
        
    },proto=MImageView.prototype;
	
    proto.initDomNode = function(){
    	var alphaDiv = $('<div class="sunzmui-window-alphadiv"><div class="imagev-close-btn">×</div><div class="imagev-imagecontainer"></div><div class="imagev-next imagev-left"><</div><div class="imagev-next imagev-right">></div><div class="imagev-toptitle"></div><div class="imagev-nav"></div></div>')[0];
        
        this.domNode = alphaDiv;
    	this.initDom();
    };
    
    proto.initDom = function(){
    	var self = this;
        this.closeBtn = $(".imagev-close-btn", this.domNode);
        this.imgContainer = $(".imagev-imagecontainer", this.domNode);
        this.navContainer = $(".imagev-nav", this.domNode);
        
        if(this.imageList&&this.imageList.length>0){
        	for(var id in this.imageList){
        		var iurl =  this.imageList[id];
        		var m = - this.currentIndex + parseInt(id);
        		var item = $('<div class="imagev-image" style="left: calc(100% * '+m+');"> <img src="'+ iurl +'"/> </div>');
        		this.imgContainer.append(item);
        		this.items.push(item);
        		
        		var itemNav = $('<div class="imagev-nav-image" style=" "> <img src="'+ iurl +'"/> </div>')[0];
        		this.navContainer.append(itemNav);
        		this.navItems.push(itemNav);
        	}
        }
        $(".imagev-left", this.domNode).bind("click", function(){
        	self.last();
        });
        $(".imagev-right", this.domNode).bind("click", function(){
        	self.next();
        });
        $(".imagev-nav-image", this.domNode).bind("click", function(e){
        	self.navClick(e.currentTarget);
        });
        this.reSetTitle();
        this._oncreate(); 
        this.showIndex(this.currentIndex);
    };
    proto.navClick = function(node){
    	var self = this;
    	 
    	self.showIndex(self.navItems.indexOf(node));
    };
    proto.reSetTitle = function(){
    	var self = this;
    	var title = "" +(this.currentIndex + 1) + "/" + this.items.length;
    	this.setTitle(title);
    };
    
    proto.setTitle = function(title){
    	var self = this;
    	$(".imagev-toptitle", this.domNode).html(title);
    };
    proto.next = function(){
    	var index = this.currentIndex + 1;
    	this.showIndex(index);
    };
    proto.last = function(){
    	var index = this.currentIndex - 1;
    	this.showIndex(index);
    };
    /**
     * 切换图片
     * @param index
     */
    proto.showIndex = function(index){
    	if(index < 0 ){
    		index = this.items.length -1;
    	}
    	if(index > (this.items.length -1)){
    		index = 0;
    	}
    	for(var id in this.items){
    		var item = this.items[id];
    		var m = - index + parseInt(id);
    		$(item).css("left" , 'calc(100% * '+ m +')');
    	} 
    	for(var id in this.navItems){
    		var item = this.navItems[id];
    		if(id != index)
    			$(item).removeClass("imagev-nav-image-active");
    	} 
    	$(this.navItems[index]).addClass("imagev-nav-image-active");
    	this.currentIndex = index;
    	this.reSetTitle();
    };
    /**
     * 初始化完成后
     */
    proto._oncreate = function() {
    	var self = this;
    	 $(this.closeBtn).bind("click",function(e) {
          
             self.destroy();
         })
        if (!this.loaded) {
            this.addToParent ? this.addToParent() : "";
        }
        this.trigger("create", this);
        this.loaded = true;
    };
	
	return  MImageView;
}();