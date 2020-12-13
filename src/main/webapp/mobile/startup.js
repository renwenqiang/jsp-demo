/**
 * 
 */
if(top!=window) top.location.href=location.href;
$(()=>{
	let transAbsUrl=(deps)=>$.map(deps,u=>u.substr(0,1)=="/"?"mobile"+u:u);
	let showError=()=>{
		$.tip("对不起，界面加载出错了！");
	};
	let realLoad=()=>{
		let path=getQueryParam("startup");
		if(path){
			$.tip("正在加载界面，请稍候...");
			try{
				require(transAbsUrl([path]),model=>{
					$.hideTip(500);
					let param=getQueryParam();
					viewport.add(model);
					if(model.setParam )
						model.setParam(param);
				},showError);
			}catch(e){
				showError();
				console.error(e);
			}
		}
	},
	loadDefault=()=>window.loginUser?realLoad():window.onUserLogin.add(realLoad);	
	let dependence=getQueryParam("rely");
	if(dependence){
		typeof dependence=="string" && (dependence=[dependence]);		
		require(transAbsUrl(dependence),loadDefault);
	}else{
		loadDefault();
	}
	if(getQueryParam("_onlinedebug_")){require(["_onlinedebug_"]);}
});