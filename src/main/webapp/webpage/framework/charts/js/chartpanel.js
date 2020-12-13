sunz.charts.ChartPanel = function(){
	
	var defaults = {
	    	type:"default" 
	    };
	var chartPanel = function(_option){
		this.initOption(defaults, _option);
		
		this.initDomNode(); 
		this.initDom();
	};
	
	chartPanel.prototype.initDomNode =  function(){
		var div = this.domNode = $('<div class="chartpanel"><div class="chartpanel-title">标题</div><div class="chartpanel-content"></div></div>')[0]; 
		var titlediv = this.titleNode = $(".chartpanel-title", div);
		var contentDiv = this.contentNode = $(".chartpanel-content", div);
		
		if(this.title){
			$(titlediv).html(this.title);
		}
		
		
		this.listenerNode = div;
		this.element = div;
		sunz.applyToElement(this.element, this.option);
	}
	
	chartPanel.prototype.initDom =  function(){ 
		this.onCreate();
		this.initContent();
	}
	chartPanel.prototype.initContent = function(){
		if(this.sunzclazz){
			this.chartOption.parent = this.contentNode;
			this.sunzclazz.create(this.chartOption);
		}
	}
	chartPanel.create = function(option){
		return new chartPanel(option);
	}
	return chartPanel;
}();