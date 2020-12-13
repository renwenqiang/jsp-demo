/**
 * 
 */
define([],function(){	
	var Test=function(){
		var jele=$('<div class="mcontainer" style="text-align:center;"></div>');
		this.title="Demo";
		this.buttons=[{text:"后退",iconCls:"tbar",handler:viewport.back}];
		this.panel={domNode:jele[0]};
		this.setParam=function(param){
			jele.append(param);
		}
	};
	Test.prototype.setData=function(data){
		jele.append($.encode(data));
	}
	
	return Test;
});