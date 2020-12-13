/**
 * 动态解析smart表单信息
 */
(function(){
	var hOffset=C.easyuiWindowHeightOffset||82,wOffset=(C.easyuiWindowWidthOffset||14)+(C.htmlScrollWidth||14);
	var h=window.smartformHelper={
		offsetWidth:wOffset,
		offsetHeight:hOffset,
		parseSize:(s,offset)=>{var num=/^\s*(\d+)(px)?\s*$/.exec(s); return num?(Number(num[1])+offset):num},
		getConfig:(id,fn)=>{
			var info=h[id];
			if(info)return fn(info);
			$.post("framework/query.do?search&k=queryForSmartform&exinfo=true",{id:id},(jr)=>{
				if(jr.success){
					if(jr.data.length){
						var o=h[id]=jr.data[0];
						o.formWidth=h.parseSize(o.formWidth,wOffset)||C.defaultWindowWidth||550;
						o.formHeight=h.parseSize(o.formHeight,hOffset)||C.defaultWindowHeight||400;
						fn && fn(o);
					}else{
						$.messager.alert("对不起","指定的表单配置信息不存在");
					}					
				}else{
					$.messager.alert("对不起","获取表单配置信息出错");
				}
				/*jr.success && jr.data.length && (info=h[id]=jr.data[0],
					info.formWidth=h.parseSize(info.formWidth,wOffset)||C.defaultWindowWidth||550,
					info.formHeight=h.parseSize(info.formHeight,hOffset)||C.defaultWindowHeight||400,
				fn && fn(info));
				jr.success || $.messager.alert("对不起","获取表单配置信息出错");
				jr.success && jr.data.length || $.messager.alert("对不起","指定的表单配置信息不存在");*/
			});
		}
	};
})()
function paramRelation(gfield,sel,sfield){
	var gs= gfield.split(","),ss=sfield.split(",");
	var param="",m="&relationField=";
	for(var i=0,len=Math.max(gs.length,ss.length);i<len;i++){
		var sf=ss[i]||gs[i],gf=gs[i]||ss[i],gv=encodeURIComponent(sel[gf]);
		param +="&"+sf+"="+gv;	// gf=gv => m =sf:gf
		m	+=sf+",";			// 
	}
	return param+m.substr(0,m.length-1);
	/* :, 有风险
	var param="&params=";
	for(var i=0,len=Math.max(gs.length,ss.length);i<len;i++){
		param +=(ss[i]||gs[i])+":"+encodeURIComponent(sel[gs[i]||ss[i]])+",";
	}
	return param.substr(0,param.length-1);
	*/
}