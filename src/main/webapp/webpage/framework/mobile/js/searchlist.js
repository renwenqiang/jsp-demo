sunz.MSearchList = function(){
	/**
	 * 带搜索框的列表，参数见sunz.MList
	 */
	var defaults = {
			searchField:"q",
			height:"100%",
    		_description:{
    			title:"搜索列表",
    			remark:"" 
    		}
	};
	var MSearchList = function(option){
		this.initOption(defaults,option);
		
		this.initDomNode();
		this.initDom();
	},proto=MSearchList.prototype;
	
	proto.initDomNode = function(){
		var self = this;
		var domNode = document.createElement("div");
		domNode.className += " sunzmui-searchlist "; 
		this.domNode =  domNode;
		
		this.element = domNode;
	};
	
	
	proto.initDom = function(){
		var self = this;
		this.domNode.innerHTML = "";
		this.headDom = document.createElement("div");
		this.headDom.className += " sunzmui-searchlist-head "; 
		this.domNode.appendChild(this.headDom);
		
		this.searchBtn = document.createElement("div");
		this.searchBtn.className += " sunzmui-searchlist-searchbtn sunzmui-searchlist-searchbtn-active"; 
		this.headDom.appendChild(this.searchBtn);
		$(this.searchBtn).bind("click", function(){
			self.search();
		})
		/**
		 * 搜索框
		 */
		var searchInput = this.searchInput = document.createElement("input");
		searchInput.type = "search";
		searchInput.className += " sunzmui-searchlist-input ";
		if(this.option.placeholder){
			searchInput.placeholder = this.option.placeholder;
		}
		
			//this.element = sunz.MTextbox.create({name:"searchtext",showBorder:true,inline:false,placeholder:this.placeholder||"请输入",parent:this.headDom})
		searchInput.addEventListener("keypress",function(e){ 
			if(e.which==13){
				self.search();
			}
		});
		$(searchInput).bind("focus", function(){
			//self._changSearchBtnPosition(true);
		});
		$(searchInput).bind("focusout", function(){
			//self._changSearchBtnPosition(false);
		});
		searchInput.getValue = function(){
			return $(searchInput).val()
			};
		this.headDom.appendChild(searchInput);
		//var split = sunz.MSplitBar.create({parent:this.headDom});
		/**
		 * 列表
		 */
		var listoption = $.extend({},this.option);
		listoption.parent = this.domNode;
		if(listoption.height){
			var h = listoption.height;
			listoption.height = "calc(" + h +" - 50px)";
		}
		this.list = sunz.MList.create(listoption);
		
		this._oncreate();
    };
    proto._changSearchBtnPosition = function(isactive){
    	var self = this;
    	if(isactive){
    		$(self.searchBtn).addClass("sunzmui-searchlist-searchbtn-active");
    	}else{
    		$(self.searchBtn).removeClass("sunzmui-searchlist-searchbtn-active");
    	}
    	
    };
    proto.research =proto.reload = function(){
    	var fieldValue = this.searchInput.getValue();
    	if(this.searchField){
    		var data = {};
    		if(fieldValue!=""){
    			//var where = "&"+this.searchField+"="+fieldValue;
    			data[this.searchField]  = fieldValue;
    		} 
    		this.list.beforeUpdate();
    		this.list.loadData(this.url,data);
    		
    	}
    	//sunz.alert(this.element.getValue());
    };
    proto.search =proto.load =function(queryParam){
    	var list=this.list;
    	list.searchParams=list.option.searchParams=$.extend(list.searchParams||{},queryParam);
    	this.research();
    }
    /**
     * 初始化完成后
     */
    proto._oncreate = function () {
		sunz.applyToElement(this.element, this.option);
    	if(!this.loaded){
    		this.addToParent?this.addToParent():"";
    	}    	
    	this.trigger("create", this); 
    	this.loaded = true;
    };
	
	return MSearchList;
}();

