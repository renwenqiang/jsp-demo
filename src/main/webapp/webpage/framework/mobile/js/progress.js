sunz.MProgress = function() {
	/**
	 * 进度条,主参数为value，info|msg
	 */
	var defaults = {
		contentFit : true,
		showClose : true,
		showBottom : false,
		title : false,
		loop : true,
		info : "请等待……",
		value : 0.001,
		_description : {
			title : "进度条",
			remark : ""
		}
	}
	var MProgress = function(opt) {
		opt && (opt.info=opt.info||opt.msg);
		this.type = "v"; //v垂直 h 方块水平浮动 
		this.initOption(defaults, opt);
		this.initDomNode();
		this.initDom();
	},proto = MProgress.prototype;

	proto.initDomNode = function() {
		var win = this.win = sunz.MWindow.create(this.option);
		this.domNode = win.domNode;
		this.contentNode = win.contentNode;
	};
	proto.initDom = function() {
		var self = this;
		this.contentNode.innerHTML = "";
		var barout = this.barOut = $('<div class="progress-bar-out"><div class="progress-bar-in"></div></div>')[0];
		var barin = this.barIn = $(".progress-bar-in", barout)[0];

		var infoNode = this.infoNode = $('<div class="progress-info"></div>')[0];

		$(this.contentNode).append(barout);
		$(this.contentNode).append(infoNode);
		this.initListener();
		this._oncreate();

		if (this.loop) {
			this.loopFun(true);
		} else {
			this.value!=null && this.setValue(this.value);
		}
		this.setInfo(this.info);
	};
	proto.loopFun = function(isFirst) {
		var self = this;
		if (isFirst) {
			this.value = 5;
		} else {
			this.value += 5;
		}
		if (this.value > 100) {
			this.value = 5;
		}
		self._setValue(this.value);
		if (this.loop) {
			setTimeout(function() {
				self.loopFun();
			}, 200);
		}
	};
	proto._setValue = function(value) {
		this.value = value;
		if (parseFloat(value) > 1) {
			this.barIn.style.cssText += "width:calc(100% * " + parseFloat(value / 100) + ")";
		} else {
			this.barIn.style.cssText += "width:calc(100% * " + parseFloat(value) + ")";
		}
	};
	proto.setValue = function(value) {
		this._setValue(value);
		this.loop = false;
	};
	proto.setInfo = function(info) {
		this.info = info;
		this.infoNode.innerHTML = info;
	};
	proto.onOk = function(fn) {
		this.bind("ok", fn);
		return this;
	};
	proto.onCancel = function(fn) {
		this.bind("cancel", fn);
		return this;
	};
	proto._onOk = function(e) {
		this.trigger("ok", e, true);
		this.hide();
	};
	proto._onCancel = function(e) {
		this.trigger("cancel", e, false);
		this.hide();
	};
	/**
	 * 初始化完成后
	 */
	proto._oncreate = function() {
		if (!this.loaded) {
			this.addToParent ? this.addToParent() : "";
		}
		this.trigger("create", this);
		this.loaded = true;
	};
	proto.open = function() {
		$(this.domNode).show();
	};
	proto.close = function() {
		this.hide();
	};
	proto.hide = function() {
		$(this.domNode).hide();
		if (this.destroyOnHide) {
			this.destroy();
		}
	};
	proto.show = function() {
		$(this.domNode).show();
	};

	return MProgress;
}();