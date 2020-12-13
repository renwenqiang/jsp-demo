 
sunz.charts.Map = function(){
	
	var defaults = {
    	type:"default" 
    };
	var chartDefault = {
			
	};
	var Map = function(_option){
		this.initOption(defaults, _option);
		this.initChartOption(chartDefault, _option.option); 
		if(_option.seriesConfig){
			this.initSeriesOption(_option.seriesConfig);
		}
		
		//this.chartOption =  _option.option
		this.initDomNode();
		this.initDom();
	}
	  
	Map.prototype.initDomNode = function(){
		var div = this.domNode = document.createElement("div"); 
		this.listenerNode = div;
		this.element = div;
		sunz.applyToElement(this.element, this.option);
	}
	
	Map.prototype.initDom = function(){
		this.domNode.innerHTML = ""; 
		this.onCreate();
		this.initChart();
	}
	Map.prototype.initChart = function(){
		var cahrt = this.chart = echarts.init(this.domNode,  'dark');
		this.getChartOption();
	}
	
	Map.prototype.getChartOption = function(){
		var option = this.chartOption;
		this.setChartOption(option);
	}
	var data = [
	            //{name: '海门', value: 9} 
	            ];
	
	var GZData = [
	             // [{name:'广州'},{name:'福州',value:95}] 
	          ];
	Map.seriesType = {
			"scatter":{
				config:{
	            name: 'scatter',
	            type: 'scatter',
	            coordinateSystem: 'bmap',
	            data: convertPointData(data),
	            symbolSize: function (val) {
	                return val[2]  ;
	            },
	            label: {
	                normal: {
	                    formatter: '{name}',
	                    position: 'right',
	                    show: false
	                },
	                emphasis: {
	                    show: true
	                }
	            },
	            itemStyle: {
	                normal: {
	                    color: '#ddb926'
	                }
	            }}
	        },
	        "lines":{
	        	
	        	config:{
	            name: 'Top10',
	            type: 'lines',
	            coordinateSystem: 'bmap',
	            zlevel: 1,
	            symbol: ['none', 'arrow',"line"],
	            symbolSize: 10,
	            effect: {
	                show: true,
	                period: 5,
	                trailLength: 0.8,
	                color: '#fff',
	                symbolSize: 3
	            },
	            lineStyle: {
	                normal: {
	                    color: color[1],
	                    width: 0,
	                    curveness: 0.2
	                }
	            },
	            data: convertLineData(GZData)
	        }}
	};
	
	Map.getDefaultOption = function(){
		return {
			backgroundColor: '#044090',
			bmap: {
		        center: [104.114129, 37.550339],
		        zoom: 5,
		        roam: true,
		        mapStyle: geoMapStyle.blue
		    },
		    series : [ ]
		}
	};
	
	Map.create = function(option){
		var map001 =  new Map(option);
		 
		return map001;
	}
	return Map;
}()

