/**
 * 
 */
window.sunz=window.sunz||{};
sunz.Window=sunz.Window||function(opt){
	if(typeof opt.showBottom =="undefined"){
		opt.showBottom = false;
	}
	opt.width = "auto";
	opt.parent=document.body;
	let win=new sunz.MWindow(opt),jproxy=$(win.domNode);
	win.contentNode.open=function(){win.open();};
	win.contentNode.close=function(){win.close();};
	return win.contentNode;
};
let toWgs=(function(){
	let PI = 3.1415926535897932384626;
	let a = 6378245.0;
	let ee = 0.00669342162296594323;
	let transformlat = function(lng, lat) {
		lat = +lat;
		lng = +lng;
		let ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
		ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
		return ret
   	};
	let transformlng = function(lng, lat) {
		lat = +lat;
		lng = +lng;
		let ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
		ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
		return ret
	};
	
	return function(lat,lng) {
	    lat = +lat;
	    lng = +lng;	    
		let dlat = transformlat(lng - 105.0, lat - 35.0);
		let dlng = transformlng(lng - 105.0, lat - 35.0);
		let radlat = lat / 180.0 * PI;
		let magic = Math.sin(radlat);
		magic = 1 - ee * magic * magic;
		let sqrtmagic = Math.sqrt(magic);
		dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
		dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
		let mglat = lat + dlat;
		let mglng = lng + dlng;
		return {longitude:lng * 2 - mglng, latitude:lat * 2 - mglat};
	};
})(); 
sunz.getPosition=(window.app&&window.app.getPosition)?function(fn,fnErr){
	let fnName="fnTemp_"+new Date().getTime();
	window[fnName]=function(r){
		delete window[fnName];
		if(!r&&fnErr){
			fnErr("定位失败");
		}
		try{
			let jr=$.decode(r);
			fn({coords:toWgs(jr.c,jr.d)});
		}catch(e){
			if(fnErr)fnErr("定位失败");
		}
	};
	app.getPosition(fnName);
}:function(fn,fnErr){
	navigator.geolocation.getCurrentPosition(fn,fnErr);
};
window.onFileDownloaded=function(fileName,size){
	$.tip("您下载了一个文件，路径为："+fileName,5000);
};
sunz.share=window.app&&window.app.share?function(url,title,desc,img,func){
	let fnName="fnTemp_"+new Date().getTime();
	window[fnName]=function(r){
		delete window[fnName];
		if(!func)return;
		
		let jr;
		try{jr=$.decode(r);}catch(e){}
		func(jr||{success:false,msg:"分享出现未知异常"});
	}
	app.share(url,title,desc,img,fnName);
}:function(url){
	sunz.alert("温馨提示","当前环境不支持分享，您可以复制如下地址手动分享：</br>"+url);
};
window.baiduak="x86bY9kw15NOyyVGP0iSEXPGuLD2YCpR";
sunz.getAddress=function(lat,lon,fn,fnErr){$.ajax("https://api.map.baidu.com/geocoder/v2/?location="+lat+","+lon+"&output=json&coordtype=wgs84ll&ak="+window.baiduak,{dataType:"jsonp",success:function(jr){if(jr.status==0)fn&&fn(jr.result);else fnErr && fnErr(jr)}});}