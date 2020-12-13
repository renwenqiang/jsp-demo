/**
 * 
 */
smart.define("Textarea",{
	name:"多文本框",isInput:true,
	defaults:{width:null,height:null},
	customAdd:function(pele,params){
		var ele=document.createElement("textarea");
		pele.insertPoint?pele.insertBefore(ele,pele.insertPoint):pele.appendChild(ele);//pele.appendChild(ele);
		$(ele).addClass(smart.selectProxyClass);
		ele.master=ele;
		ele.setting=params;
		this.customUpdate(ele);
		if(params.required || params.validType)
			$(ele).validatebox(params);
		
		ele.setValue=function(v){ele.value=(v===undefined?null:v);};
		ele.getValue=function(){return ele.value;};
		
		return ele;
	},
	customUpdate:function(ele,setting){
		smart.applyToElement(ele.master,setting||ele.setting,["hidden"]);
	}
});