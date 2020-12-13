/**
 * 
 */
smart.define("Map",{
	name:"地图",
	defaults:{width:"100%",height:"100%"},
	setFormValue:function(v){
		this.src=v;
	},
	getFormValue:function(){
		return this.src;
	},
	customAdd:function(pele,params){
		if(typeof(dojo)=="undefined"){
			var oHead = document.getElementsByTagName('HEAD').item(0); 
			var oScript= document.createElement("script"); 
			oScript.type = "text/javascript"; 
			oScript.src="Api"; 
			oHead.appendChild(oScript); 
		}
		
		var ele=document.createElement("div");
		pele.insertPoint?pele.insertBefore(ele,pele.insertPoint):pele.appendChild(ele);
		$(ele).addClass(smart.selectProxyClass);
		ele.setting=params;
		ele.master ={};
		this.customUpdate(ele,params);
		return ele;
	},
	customUpdate:function(ele,setting){
		smart.applyToElement(ele,setting||ele.setting,["src"]);
		//再次应用后销毁上一次创建的地图
		if(ele.master && ele.master.map){
			ele.master.map.destroy();
			delete ele.master.map;
		}
		//根据地图代码创建地图
		if(setting && setting.mapcode){
			require(["szmap/map"],function(Map2d){
				//创建地图,地图容器,地图代码
				Map2d.create(ele,setting.mapcode,{
					/**
					*加载成功事件
					**/
					success:function(map){
						
						ele.master.map = map;
					}
				});
			});
		}
	}
});