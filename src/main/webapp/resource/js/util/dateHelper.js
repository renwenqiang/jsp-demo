/**
 * 时间处理（apple产品中不识别普通的日期格式）
 */
if(!Date.prototype.format){
	Date.prototype.format = function(fmt) {
		var o = {
			"M+" : this.getMonth() + 1, //月份 
			"d+" : this.getDate(), //日 
			"h+" : this.getHours(), //小时 
			"m+" : this.getMinutes(), //分 
			"s+" : this.getSeconds(), //秒 
			"q+" : Math.floor((this.getMonth() + 3) / 3), //季度 
			"S" : this.getMilliseconds() //毫秒 
		};
		if (/(y+)/.test(fmt))
			fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
		for (var k in o)
			if (new RegExp("(" + k + ")").test(fmt))
				fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
		return fmt;
	};
	Date.prototype.toString = function() {
		return this.format("yyyy-MM-dd hh:mm:ss");
	};
}

Date.parseDate = function(d) {
	//d=d.replace(" ","T");
	if (typeof d != "string")
		return d;
	d = d.replace(/[-年月日]/g, "/");
	d = d.replace(/[时分]/g, ":");
	d = d.replace("秒", "");
	return new Date(d);
};
window.dateHelper = {
	parseDate : Date.parseDate,
	miniDisplay : function(date, f, spY, spM, spD, y, m, d) {
		/*日期最简/短格式*/
		date = date || new Date();
		date = this.parseDate(date);
		spM = spM || "/";
		var fpre = (m || "MM") + spM + (d || "dd");
		if (spD)
			fpre = fpre + spD;
		if (new Date().getYear() != date.getYear()) {
			spY = spY || spM;
			fpre = (y || "yyyy") + spY + fpre;
		}
		return date.format(f ? (fpre + " " + f) : fpre);
	}
};
window.define && define("resource/js/util/dateHelper",[],function(){return window.dateHelper})