sunz.openPostWindow = function(url, data, name) {
	var tempForm = document.createElement("form");
	tempForm.id = "tempForm1";
	tempForm.method = "post";
	tempForm.action = url;
	tempForm.target = name;

	var alldata = $.getAllParam(data);
	for ( var id in alldata) {
		var hideInput = document.createElement("input");
		hideInput.type = "hidden";
		hideInput.name = id;
		hideInput.value = alldata[id];
		tempForm.appendChild(hideInput);
	}
	tempForm.attachEvent("onsubmit", function() {
		openWindow(name);
	});
	document.body.appendChild(tempForm);
	tempForm.fireEvent("onsubmit");
	tempForm.submit();
	document.body.removeChild(tempForm);
};
function openWindow(name) {
	window.open('about:blank', name);
}
(function($) {
	$.getUrlParam = function(name, str) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		var r = (str || window.location.search.substr(1)).match(reg);
		if (r != null)
			return unescape(r[2]);
		return null;
	}
	$.getAllParam = function(href) {
		var params = href || (window.location.search);
		var paramArr = params.split('&');
		var res = {};
		for (var i = 0; i < paramArr.length; i++) {
			var str = paramArr[i].split('=');
			res[str[0]] = str[1];
		}
		return res;
	}
})(jQuery);

sunz.MPay = function() {
	/**
	 * 支付组件
	 */

	var defaults = {
			_aliurl:"https://openapi.alipay.com/gateway.do?charset=UTF-8",
			_neworderurl:"pay/pay.do?newOrder",
			title : "支付组件",
			_payurl : "pay/pay.do?payOrder",
			payType: 1,
			orderName:"支付",
			orderContent:"支付内容",
			orderPrice:"0.01",
			returnPageUrl:"",
		_description : {
			
			remark : ""
		}
	};

	var MPay = function(_option) {
		/**
		 * 整体dom
		 */
		this.domNode = undefined;
		/**
		 * 验证时标记变红的dom，应当为 this.element的上一级
		 */
		this.contentNode = undefined;
		/**
		 * form组件dom
		 */
		this.element = undefined;
		/**
		 * 监听事件dom,可以随意定义，但是本类中的所有默认监听会绑定到该dom上，如果想要触发，需要调用trigger方法
		 * 如果想监听别的组件事件，需要自己写代码监听，在监听回调中执行本类中的trigger方法，这样就相当于把事件绑定在本类上
		 */
		this.listenerNode = undefined;

		this.initOption(defaults, _option);

		/**
		 * 必须有DOMNode
		 */
		var div = document.createElement("div");
		div.className += " sunzmui-form form-horizontal";
		div.style.cssText += "  width:100%; ";
		this.domNode = div; 
		this.initDom();
	},proto=MPay.prototype;
	
	proto.initDom = function() {
		this.addToParent?this.addToParent():"";
		this._oncreate();
	};
	proto.newOrder = function(callback, errorback) {
		var self = this;
		var param = {price:self.orderPrice,name:self.orderName,content:self.orderContent,type:self.payType};
		return $.ajax({
			url: self._neworderurl,
			type: "POST",
			data: param,
			success:function(r){
				var data = r.data;
				self.PayOrder = data;
				callback&&callback(data);
			},
			error: function(e){
				errorback&&errorback(e);
			}
		}) 
	};
	proto.startPay =  function(callback, error){
		var self = this;
		if(!self.PayOrder&&!self.orderNo){
			self.newOrder(function(){
	    		self.pay(callback, error);
	    	},error)
		} 
	}
	proto.pay = function(callback, error){
		var self = this;
		if(!self.PayOrder&&!self.orderNo){
			throw("没有订单号orderNo");
		}
		if(!self.PayOrder){
			self.getPayOrderByOrderNo(self.orderNo, function(data){
				self.PayOrder = data;
				if(self.PayOrder&&self.PayOrder.payType == "1"){
					self.aliPay();
				}
				else if(self.PayOrder&&self.PayOrder.payType == "2"){
					self.WXAPPPay();
					if(window.SunzWX&&SunzWX.ready){
						
						
					}
				}
			});
			return;
		}
		if(self.PayOrder&&self.PayOrder.payType == "1"){
			self.aliPay();
		}else if(self.PayOrder&&self.PayOrder.payType == "2"){
			self.WXAPPPay();
			if(window.SunzWX&&SunzWX.ready){
				
				
			}
		}
	}
	/**
	 * 获取订单信息
	 * @param orderNo
	 * @param callback
	 * @param error
	 */
	proto.getPayOrderByOrderNo = function(orderNo, callback, error){
		var self = this;
		var url = "pay/pay.do?queryPayOrder"
		$.ajax({
			url: url,
			type: "POST",
			data: {orderNo: orderNo},
			success: function(r){
				var data = r.data;
				if(data){
					callback&&callback(data);
				}else{
					error&&error(r);
				}
			},
			error: function(e){
				error&&error(e);
			}
		})
	}
	proto.aliPay = function(){
		var self = this;
		if(!self.PayOrder&&!self.orderNo){
			throw("没有订单号orderNo")
			return;
		}
		var orderNo = self.orderNo||self.PayOrder.orderNO; 
		var url = self._payurl +"&orderNo=" + orderNo + "&returnPageUrl=" + self.returnPageUrl + "&returnType=redirect";
		alert("正在跳转到支付页面");
		if(!window.open){
			window.location.href =  url;
		} 
		window.location.href =  url;
	};
	proto.WXAPPPay = function(){
		var self = this;
		if(!self.PayOrder&&!self.orderNo){
			throw("没有订单号orderNo")
			return;
		}
		var orderNo = self.orderNo||self.PayOrder.orderNO; 
		var url = self._payurl +"&orderNo=" + orderNo + "&returnType=qrcode";
		$.ajax({
			url:url,
			type:"POST",
			success:function(r){
				var data = r.data;
				var qrcodeurl = data.payForm;
				self.alertQXQRCodeWindow(qrcodeurl);
			}
		})
	};
	proto.alertQXQRCodeWindow = function(text){
		var self = this;
		var win = new sunz.MWindow({
			parent:"body",
			title:"长按识别二维码付款",
			content: '<div class="wxpayqrcodeimg" style="width:150px;overflow:hidden;margin: auto;"><div>',
			showBottom: true,
	        showClose: false,
	        destroyOnHide: true,
	        class:"mobileAlert",
	        height: 200
		});
		var dom = $(".wxpayqrcodeimg", win.contentNode)[0];
		var qrcode = new QRCode(dom, {
			  text: text,
			  width: 150,
			  height: 150,
			  colorDark: '#000000',
			  colorLight: '#ffffff',
			  correctLevel: QRCode.CorrectLevel.H
			});
	}
	proto.jumpToAli = function(formstr){
		var self = this;
		var windname =  "payfrom"+(Math.random());
		//openPostWindow(self._aliurl, formstr, windname);
	};
	/**
	 * 初始化完成后
	 */
	proto._oncreate = function() {
		this.addToParent ? this.addToParent() : "";
		this.trigger("create", this);
	};

	/**
	 * 获取当前值
	 */
	proto.getValue = function() {
		
	};
	/**
	 * 设置值
	 */
	proto.setValue = function() {

	};
	proto.update = function(_option) {
		this.initOption(defaults, _option);
		sunz.applyToElement(this.element, this.option);
		sunz.registListener(this.listenerNode, this.listener);
	};
	
	return MPay;
}();
