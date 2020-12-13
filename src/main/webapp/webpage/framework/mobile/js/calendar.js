sunz.MCalendar = function(){
	/**
	 * 日历
	 */
	var defaults = {
    		type:"default", 
    		isShrink:false,
    		weekday: [{name:"日"},{name:"一"},{name:"二"},{name:"三"},{name:"四"},{name:"五"},{name:"六"}],
    		//activeDays: [{y:2018,m:4,d:5} , {y:2018,m:4,d:7} ,{y:2018,m:4,d:23}, {y:2018,m:4,d:30}],
    		activeDays: [ ],
    		_description:{
    			title:"日历",
    			remark:"日历组件" 
    		}
    };
	var Calendar = function(_option){
		var self = this;
		this.initOption(defaults, _option);
		
		var tempalte = '<div class="sunzmui-calendar"><div class="hidden_input" style=""><input class="sunzmui-calendar-input" type="date"/></div><div class="sunzmui-calendar-background"></div><div class="sunzmui-calendar-week"></div><div class="sunzmui-calendar-content"></div></div>';
		var div = $(tempalte)[0]; 
	    
	    var weekNode = this.weekNode = $(".sunzmui-calendar-week", div)[0];
	    var contentNode = this.contentNode = $(".sunzmui-calendar-content", div)[0];
	    var backNode = this.backNode = $(".sunzmui-calendar-background", div)[0];
	    var inputNode = this.inputNode = $(".sunzmui-calendar-input", div)[0];
	    
	    this.domNode = div;
        this.element = div;
        this.listenerNode = div;
        
        this.initDom();
        this._oncreate(); 
	},proto=Calendar.prototype;
	proto.initDom = function(){
		var self = this;
		this.setDate(this.date,true);
	    this.initWeek();
	    $(this.inputNode).on("change", function(e,a,b){
	        var value = inputNode.value ;
	        if(!value){
	        	return;
	        }
	        var newDate = new Date(value);
	        self.setDate(newDate);
	    })
	    sunz.applyToElement(this.element, this.option); 
	    this.initListener();
	}
	
	proto.initWeek = function () {
		this.weekNode.innerHTML = "";
    	var weekList = sunz.MList.create({parent:this.weekNode, data:this.weekday, template:"<div class=\"dateItem\">{name}</div>",type:"panel"});
    };
    var isSameday=function(date,y,m,d){
    	if(!date) return false;
    	if(y instanceof Date)
    		return date.getYear()==y.getYear() && date.getMonth()==y.getMonth() && date.getDate()==y.getDate();
    	if(typeof y=="object"){
    		m=y.month;
    		d=y.date;
    		y=y.year;
    	}
    	return date.getFullYear() == y && (date.getMonth()+1) == m && date.getDate() == d;
    }
    proto.initContent = function () { 
    	this.contentNode.innerHTML  = "";
    	var self  = this;
    	if(typeof this.date == "string"){
    		this.date = new Date(this.date);
    	}
    	var today=self.today=new Date(); // 每次更新
    	var centerDate =self.centerDate=self.getCenterDate();
    	var days = this.getMonthDaysByDate(centerDate);
    	var templateFun = function(data){
    		var lunar = Calendar_plugin.solar2lunar(data.year, data.month , data.date);
    		var html = sunz.tplReplace('<div class="dateItem" date="{year}-{month}-{date}"><span class="dateItem-date">{date}</span><span class="dateItem-nldate">' + (lunar.Term||lunar.IDayCn)+ '</span></div>', data);
    		var dom = $(html);
    		if(data.month-1<centerDate.getMonth()){
    			dom.addClass("dateItem-before");
    		}else if(data.month-1>centerDate.getMonth()){
    			dom.addClass("dateItem-after");
    		}
    		if(self.templateFilter){
    			dom = $(self.templateFilter(data, dom[0])||dom);
    		}
    		return dom;
    	} 
    	var daysList = this.daysList = sunz.MList.create({
    		parent:this.contentNode,
    		type:"panel",
    		data: days,
    		template:templateFun,
    		itemclick:function(){
    			var data = this.data;
    			var ndate = new Date(data.year, data.month - 1 , data.date);
    			self.setDate(ndate);    			
    		}
    	});

    	this.initBackground();
    	if(this.isShrink){
 	    	this.shrink();
 	    }
    };
    /**
     * 判断是否是当前设置的日期
     * @param year
     * @param month
     * @param ddate
     * @returns {Boolean}
     */
    proto.isCurrentDay = function(y, m, d){
    	return isSameday(this.date,y,m,d);
    };

    
    /**
     * 
     */
    proto.isDayActive2 = function(year, month, ddate){
    	var self = this;
    	var result = false; 
    	if(this.activeDays){
    		for(var i=0;i<this.activeDays.length;i++){
    			var d = this.activeDays[i];
    			if(d.y == year && d.m == month && d.d == ddate){
    				result = true;
    				break;
    			}
    		}
    	}
    	return result;
    };
    
    proto.isDayActive = function(date){
    	var self = this;
    	var result = false;
    	var year = date.getFullYear();
    	var month = date.getMonth() + 1;
    	var ddate = date.getDate();
    	if(this.activeDays){
    		for(var i=0;i<this.activeDays.length;i++){
    			var d = this.activeDays[i];
    			if(d.y == year && d.m == month && d.d == ddate){
    				result = true;
    				break;
    			}
    		}
    	}
    	return result;
    };
    proto.getCenterDate=function(){return this.centerDate ||this.date|| new Date()}
    proto.initBackground = function(){
    	var month = this.getCenterDate().getMonth() + 1;
    	this.backNode.innerHTML = month;
    };
    

    var activeClass="dateItem-active",
    	todayClass="dateItem-today",
    	selectedClass="dateItem-current";
    
    proto.selectDate= proto.setDate = function(date,silence){
    	var self = this;
    	self.date = date;
    	if(!self.centerDate || (date && date.getMonth() != self.getCenterDate().getMonth())){
	    	this.initContent();
    	}
    	//
    	self.todayDom = $(this.contentNode).find("[date="+self.today.format("yyyy-M-d")+"]").addClass(todayClass);
    	$(self.contentNode).find("."+selectedClass).removeClass(selectedClass);
    	if(self.date)
    		$(this.contentNode).find("[date="+self.date.format("yyyy-M-d")+"]").addClass(selectedClass);
    	if(self.activeDays)
    		self.setActiveDays(self.activeDays);
    	
    	if(!silence)self.trigger("change", self.date);
    };
    proto.getSelectedDate=function(){return this.date};
    proto.getYear=function(){return this.getCenterDate().getFullYear()};
    proto.getMonth=function(){return this.getCenterDate().getMonth()+1};
    proto.setActiveDays=function(days){
    	self.activeDays=days;
    	var jd=$(self.contentNode);
    	jd.find("."+activeClass).removeClass(activeClass);
    	$.each(days,function(i,d){
    		jd.find(tplReplace("[date={year}-{month}-{date}]",d)).addClass(activeClass);
    	});
    }
    
    proto.showMonth=function(y,m,silence){
    	if(this.getCenterDate().getMonth()+1==m)return;
    	this.centerDate = new Date(y , m-1, 15);
    	this.initContent();
    	this.setDate(null);
    	if(!silence)this.trigger("monthChanged",m);
    }
    proto.showLastMonth = function(silence){
    	var date = this.getCenterDate();
    	this.showMonth(date.getFullYear(), date.getMonth(), silence)
    };
    
    proto.showNextMonths = function(silence){
    	var date = this.getCenterDate();
    	this.showMonth(date.getFullYear(), date.getMonth()+2, silence);
    };
    
    proto.getMonthDaysByDate = function(date){
    	var self = this;
    	var days = [];
    	if(typeof date == "string"){
    		date = new Date(date);
    	}
    	var year = date.getFullYear();
    	var mounth = date.getMonth() + 1;
    	var d = date.getDate();
    	var day = date.getDay();
    	var weekDay = this.weekday[day];
    	var startDate = new Date(year , mounth - 1, 1);
    	var weekDayOfStartDate = startDate.getDay();
    	startDate.setDate(1 - weekDayOfStartDate);
    	var endDate = new Date(year , mounth, 0);
    	var weekDayOfEndDate = endDate.getDay();
    	endDate = new Date(year , mounth, 6 - weekDayOfEndDate);
    	//console.log(startDate,endDate);
    	while(startDate <= endDate){
    		var cDate = startDate;
    		var date = cDate.getDate();
    		days.push({year:cDate.getFullYear(),date:date, month: cDate.getMonth() + 1, day: cDate.getDay()});
    		cDate.setDate(date + 1);
    	}
    	return days;
    };
    
    proto.getDate = function(){
    	return this.date;
    }
    proto.selectYearAndMonth = function(){
    	$(this.inputNode).trigger("click")
    }
    
	/**
     * 初始化完成后
     */
	proto._oncreate = function () {
    	this.addToParent?this.addToParent():"";
    	this.initSwipe();
    	this.trigger("create", this); 
    };
    proto.expand = function () {
    	var self = this;
    	var currentElement = $(".dateItem-current", this.domNode);
    	var height =  $(".sunzmui-list-panel:last",this.domNode).height();
    	$(this.contentNode).height(height);
		$(this.contentNode).css("0px"); 
		$(this.daysList.domNode).css("margin-top","0px");
		this.isShrink = false;
    };
    proto.shrink = function () {
    	var self = this;
    	 
    	var currentElement = $(".dateItem-current", this.domNode).parent()[0];
    	if(currentElement){
    		var top = currentElement.offsetTop;
    		var left = currentElement.offsetLeft;
    		$(this.contentNode).height("45px");
    		$(this.daysList.domNode).css("margin-top","-"+top+"px");
    		//$(this.contentNode).css("top",""+top+"px");
    		this.isShrink = true;
    	}
    	
    	
    };
    /**
     * 触摸事件
     */
    proto.initSwipe = function(){
    	var self = this; 
        var swipe = sunz.Swipe.create(this.contentNode,{
 			moveWithTouch:true,
 			moveDirection:"leftorright",
 			maxMove:self.parentWidth,
 			onMoveStart: function(){ 
 				//console.log(this)
 				self.startSwip = true;
 				//console.log("onStart")
 			},
 			eleMoveEnd:function(a,b,c){
 				self.parentWidth = $(self.parent).width();
 				self.contentNode.style.transition = "margin-left 0s  ,left 0s";
 				if(Math.abs(c)>self.parentWidth/3){
 					
 					self.contentNode.style.cssText += "left:0px;margin-left:-"+ (self.parentWidth*self.currentIndex-c) + "px;"
 	 				if(c<0 ){
 	 					self.showNextMonths();
 	 				}else{
 	 					self.showLastMonth();
 	 				}
 				} else{
 					self.contentNode.style.transition = "margin-left 0.5s  ,left 0.5s ";
 					self.contentNode.style.cssText += "left:0px; " 
 				}
 				//self.contentNode.style.transition = "margin-left 0.5s  ,left 0.5s ";
 				self.startSwip= false;
 				//console.log("end")
 			},
 			eleMove:function(a,b,c){
 				if(self.startSwip){ 
 				}  
 			}
 		});
        
    };
    
	
	return Calendar;
}();


