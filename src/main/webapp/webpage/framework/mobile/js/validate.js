window.sunz=window.sunz||{};
sunz.validRules={
	required:{message:"该项为必填项",validator:function(v){return v||v===0;}},
	email : {
		validator : function(v) {
			return /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test(v);
		},
		message : "请输入正确的邮件地址"
	},
	url : {
		validator : function(v) {
			return /^(https?|ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(\#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(v);
		},
		message : "请输入正确的网址"
	},
	length : {
		validator : function(v, params) {
			var len = $.trim(v).length;
			return params[1]?(len >= params[0] && len <= params[1]):len==params[0];
		},
		message:function(v,params){params[1]||params[1]==params[0]?"输入内容长度必须为"+params[1]:"输入内容长度必须介于"+params[0]+"和"+params[1]+"之间";}
	},
	remote:{
		validator : function(v, params) {
			sunz.validRules.remote.message="验证服务访问失败";
			var data = {};
			data[params[1]] = v;
			var jr = $.ajax({
				url : params[0],
				dataType : "json",
				data : data,
				async : false,
				cache : false,
				type : "post"
			}).responseJSON;
			sunz.validRules.remote.message=jr.msg;
			return jr.success && jr.data;
		}
	},
	json:{
		message:"语法分析发现错误",
		validator:function(v,param){
			try{
				var tmp=eval("("+v+")");
				if(param){
					return param===typeof tmp;
				}
				return true;
			}catch(e){
				return false;
			}
		}
	}
};
sunz.validate=(
	validRule=function(validType, param) {
			var value = this.getValue();
			var m = /([a-zA-Z_]+)(.*)/.exec(validType);
			var rule = sunz.validRules[m[1]];
			if (rule) {
				var ruleParam = param || this.options("validParams") || eval(m[2]);
				if (!rule["validator"].call(this, value, ruleParam)) {
					var msg = rule["message"];
					if(typeof msg ==="function") msg=msg(v,ruleParam);
					else
						if (ruleParam) {
							for (var i = 0; i < ruleParam.length; i++) {
								msg = msg.replace(new RegExp("\\{" + i + "\\}", "g"), ruleParam[i]);
							}
						}
					this.markInvalid(this.options("invalidMessage")||msg);
					return false;
				}
			}
			return true;
	},validate=function(){
		var opt=this.options(),validType=opt.validType;
		if(opt.required){
			if(!validRule.call(this,"required"))
				return false;
		}
		if ($.isArray(validType)) {
			for (var i = 0; i < validType.length; i++) {
				if (!validRule.call(this,validType[i])) {
					return false;
				}
			}
		} else {
			if (typeof validType == "string") {
				if (!validRule.call(this,validType)) {
					return false;
				}
			} else {
				for (var n in validType) {
					var params = validType[_4c9];
					if (!validRule.call(this,n, params)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	,function(){		
		if(this.onBeforeValidate)this.onBeforeValidate.call(this);
		var valid=validate.call(this);
		if(valid) this.clearInvalid();
		if(this.onValidate)this.onValidate.call(this,valid);
		return valid;
	}
);
sunz.markInvalid = function (ele, tip) {
	$(ele).addClass("has-error");
};
sunz.clearInvalid = function (ele) {
	$(ele).removeClass("has-error");
};
$.fn.validatebox=$.fn.validatebox||function(opt){
	this.config=$.extend(this.config||{},opt);
	this.element=this[0];
	this.options=function(n){return n?this.config[n]:this.config;};
	this.getValue=function(){this.val()};
	this.setValue=function(v){this.val(v)};
	this.markInvalid=function(msg){sunz.markInvalid(this.element,msg);};
	this.clearInvalid=function(){sunz.clearInvalid(this.element);};
	this.validate=function(){sunz.validate.call(this);};
	this.destroy=function(){this.remove();};
	return this;
};