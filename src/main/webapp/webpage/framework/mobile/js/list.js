sunz.MList = function() {
	/**
	 * 列表,主参数data:[]|url,template:string|function|dom，pagesize,queryParams|searchParams,
	 * 
	 */
	var defaults = {
		firstRow : null,
		template : "<a>Title . {title}</a>",
		noresultContent : "<div class='listnoresult'>没有查到结果……</div>",
		type : "v",
		showbypage : true,
		pagesize : 10,
		currentpage : 1,
		totalSize : -1,
		searchParams : {},
		pageParams : {},
		refresh : true,
		nextPageText : "下一页",
		_description : {
			title : "列表",
			remark : ""
		},
		pullMax : 80,
		pullTolerence : 64,
		pulldownTip : "下拉刷新",
		pulldownText : "松开刷新",
		pullupTip : "上拉加载更多",
		pullupText : "松开加载",
		refreshing : "加载中...",
		loadding : "加载中...",
		nomoreTip : "没有更多啦",
		nextMode : 'pullup'
	}
	var MList = function(option) {
			//this.type = "v";//v垂直 h 方块水平浮动
			option = option || {};
			if (option.pagination != undefined) {
				option.showbypage = option.pagination ;
			}
			option.searchParams = option.queryParams || option.searchParams;

			this.data = [];
			this.items = [];

			this.initOption(defaults, option);

			this.initDomNode();
			this.initDom();
		},
		proto = MList.prototype;

	proto.initDomNode = function() {
		var self = this;
		var domNode = document.createElement("div");
		if (this.type == "h") {
			domNode.className += " sunzmui-list-h ";
		} else if (this.type == "v") {
			domNode.className += " sunzmui-list-v  ul list-group ";
		} else if (this.type == "panel") {
			domNode.className += " sunzmui-list-panel ul list-group  ";
		}
		this.domNode = domNode;
		this.element = domNode;
		var listItemNode = this.listItemNode = document.createElement("div");
		if (this.type == "h") {
			listItemNode.className += " sunzmui-list-itemcontainer-h ";
		} else if (this.type == "v") {
			listItemNode.className += " sunzmui-list-itemcontainer ";
		} else {
			listItemNode.className += " sunzmui-list-itemcontainer-" + this.type;
		}
		this.domNode.appendChild(listItemNode);
	};
	proto.initSwipeDom = function() {
		try {
			$(this.tipDom).remove();
		} catch (e) {}

		var tipDom = this.tipDom = document.createElement("div");
		tipDom.className += " sunzmui-searchlist-toptip ";
		tipDom.innerHTML = this.pulldownTip;
		this.domNode.appendChild(tipDom);
	};
	/**
	 * 初始化触摸事件
	 */
	proto.initSwipe = function() {
		var self = this;
		//self.initSwipeDom();
		var swipe = sunz.Swipe.create(this.listItemNode, {
			moveWithTouch : true,
			moveDirection : "down",
			maxMove : self.pullMax,
			onMoveStart : function() {
				self.initSwipeDom();
				self.startSwip = true;
			},
			eleMoveEnd : function(a, b, c) {
				$(self.tipDom).remove();
				if (c > self.pullTolerence) {
					if (self.startSwip)
						self.research();
				}
				self.startSwip = false;
			},
			eleMove : function(a, b, c) {
				if (self.startSwip) {
					if (c > self.pullTolerence) {
						self.changeTipText(self.pulldownText);
					} else {
						self.changeTipText(self.pulldownTip);
					}
					if (c < self.pullMax) {
						$(self.tipDom).height(c);
					}
				}

			}
		});
	};
	/**
	 * 改变下拉滑动显示内容
	 * @param html
	 */
	proto.changeTipText = function(html) {
		this.tipDom.innerHTML = html;
	};

	proto.beforeUpdate = function(option) {
		this.totalSize = -1;
		this.option.totalSize = -1;
		if (this.ajaxObject) {
			//console.log("请求终止");
			this.ajaxObject.abort()
		}
	};

	proto.initDom = function() {
		var self = this;
		if (this.refresh && this.url) {
			this.initSwipe();
		}
		if (this.autoload != false) { // 允许创建时不加载数据
			if (this.option && this.option.data) {
				this.setData(this.option.data);
			} else if (this.url) {
				this.loadData(this.url);
			}
		}
		this._oncreate();
	};
	proto.setUrl = function(url) {
		if (url) {
			this.url = url;
			this.loadData(url);
		}
	};
	/**
	 * 初始化完成后
	 */
	proto._oncreate = function() {
		this.listenerNode = this.element;
		$(this.listenerNode).unbind();
		this.initListener();

		sunz.applyToElement(this.element, this.option);
		if (this.height) {
			this.domNode.style.cssText += "overflow-y:auto;"
		}
		if (!this.loaded) {
			this.addToParent ? this.addToParent() : "";
		}
		this.trigger("create", this);
		this.loaded = true;
	};


	/**
	 * 设置模板
	 */
	proto.setTemplate = function(template) {
		this.template = template;
	};

	proto.research = proto.reload = function() {
		this.currentpage = 1;
		this.total = this.totalSize = -1;
		if (this.option && this.option.data) {
			this.setData(this.option.data);
		} else if (this.url) {
			this.loadData(this.url);
		}
	};
	proto.search = proto.load = function(queryParam) {
		this.searchParams = this.option.searchParams = $.extend(this.searchParams || {}, queryParam);
		this.research();
	}


	proto.loadData = function(url, params) {
		var self = this;
		if (!url) {
			return;
		}
		this.url = url;
		this.currentpage = 1;
		var data = $.extend({}, this.searchParams, params);
		if (this.showbypage) {
			var sp = this.getPageParams(this.pagesize, this.currentpage, this.total || this.totalSize);
			data = $.extend({
				total : sp.total
			}, this.searchParams, params, (
				delete sp.total
				, sp));
		}
		self.trigger("startload", data);
		var ajax = this.ajaxObject = $.ajax({
			url : this.url,
			type : "POST",
			data : data,
			dataType : 'json'
		}).done(function(result) {
			if (typeof result.total != "undefined") {
				self.totalSize = result.total;
			}
			if (self.loadFilter) {
				var data = self.loadFilter(result);
				self.setData(data);
			} else {
				self.setData(result.data ? result.data : result);
			}
			self.trigger("load", result);
			self.ajaxObject = null;
		}).fail(function(e) {
			console.error(e);
			self.trigger("loaderror", e);
		});
	};
	proto.loadNextPage = function() {
		this.loadPage(this.currentpage + 1);
	};
	proto.loadPage = function(pagenum) {
		var self = this;
		this.currentpage = pagenum;
		if (!this.url) {
			return;
		}
		var data = $.extend({}, this.searchParams, this.getPageParams(this.pagesize, this.currentpage, this.total));
		self.trigger("startload", data);
		if (self.totalSize > 0) {
			data.total = self.totalSize;
		}
		var ajax = $.ajax({
			url : this.url,
			type : "POST",
			data : data,
			dataType : 'json'
		}).done(function(result) {
			if (typeof result.total != "undefined") {
				self.totalSize = result.total;
			}
			if (self.loadFilter) {
				var data = self.loadFilter(result);
				self.appendData(data);
			} else {
				self.appendData(result.data ? result.data : result);
			}
			self.trigger("load", result);

		}).fail(function(e) {
			console.error(e);
			self.trigger("loaderror", e);
		});
	};
	proto.getPageParams = function(pagesize, currentPage, total) {
		return {
			total : total,
			limit : pagesize,
			start : pagesize * (currentPage - 1)
		};
	};

	/**
	 * 装载数据
	 */
	proto.setData = function(_data) {
		this.trigger("setData", _data);
		if (_data && _data.length != null) {
			this.data = _data;
			this.clear();
			this.render();
			if (_data.length == 0 && this.url) {
				this.shownoresult();
			}
		} else {
			//throw ("data must be a array"); 
		}
	};
	proto.shownoresult = function() {
		this.clear();
		$(this.listItemNode).append(this.noresultContent);
	};

	proto.appendData = function(_data) {
		this.trigger("appendData", _data);
		if (_data && _data.length != null) {
			for (var i = 0; i < _data.length; i++) {
				this.data.push(_data[i]);
				this.showItem(_data[i]);
			}
		} else {
			//throw ("data must be a array");
		}
		this.trigger("rendered", this.data);
		this.showNextPageButton();
	};
	proto.showItem = function(data) {
		var _No = this.data.indexOf(data);
		data._No = _No + 1;
		data.__rowIndex__ = _No;
		if (this.itemFilter) {
			var _data = this.itemFilter(data);
			if (_data) {
				this.data[_No] = _data;
			}
		}
		var item = new sunz.MListItem({
			_parent : this,
			data : data,
			template : this.template,
			type : this.type,
			itemurl : this.itemurl,
			parent : this.listItemNode,
			listener : {
				click : this.itemclick
			},
			_No : _No,
			_rowNumber : _No
		});

		this.items.push(item);
	}
	/**
	 * 
	 */
	proto.render = function() {
		var self = this;
		for (var i = 0; i < this.data.length; i++) {
			this.showItem(this.data[i]);
		}
		this.trigger("rendered", this.data);
		this.showNextPageButton();
	};
	proto.sort = function(func) {
		var self = this;
		this.clear();
		if (this.oldData) {
			this.data = this.oldData;
		}
		this.oldData = this.data;
		this.data = [];
		this.data = this.oldData.sort(func)
		this.render();
	};
	proto.cancelSort = function(func) {
		var self = this;
		this.clear();
		this.data = this.oldData;
		this.render();
	};
	proto.filter = function(func) {
		var self = this;
		this.clear();
		if (this.oldData) {
			this.data = this.oldData;
		}
		this.oldData = this.data;
		this.data = this.oldData.filter(func);
		this.render();
	};
	proto.cancelFilter = function(func) {
		var self = this;
		this.clear();
		if (this.oldData) {
			this.data = this.oldData;
		}
		this.render();
	};
	proto.nextPage = function() {
		var self = this;
		self.trigger("nextpageclick", {
			pagesize : self.pagesize,
			currentpage : self.currentpage,
			total : self.totalSize
		});
		self.loadNextPage();
	};
	proto.showNextPageButton = function() {
		var self = this;
		$(self.nextPageDom).remove();
		if (self.showbypage) {
			self.hasMore = self.totalSize > self.pagesize * self.currentpage;
			if (self.hasMore) {
				if (self.nextMode == 'pullup') { // 上拉式
					if (self.pullupDom) return;
					$(self.listItemNode).addClass("sunzmui-list-container");
					var _ele = self.listItemNode,
						jproxy = this.pullupDom = $('<div class="sunzmui-list-pullup"></div>').appendTo(this.domNode),
						_started = false,
						_startY = 0,
						_offset = 0,
						_loading = false,
						noScroll = function() {
							var b = true,
								el = _ele;
							while (el != document.body && b) {
								el = el.parentNode;
								var scroll = el.scrollTop;
								el.scrollTop = scroll + 1;
								if (scroll < el.scrollTop && el.scrollHeight - el.clientHeight - el.scrollTop > 6) {
									b = false;
								}
							}
							return b;
						};

					_ele.addEventListener('touchstart', function(e) {
						_offset = 0;
						_started = false;
						_startY = e.touches[0].pageY;
						_ele.style.transition = 'transform 0s';
					}, false);
					_ele.addEventListener('touchmove', function(e) {
						var touch = e.touches[0];
						_offset = _startY - touch.pageY;
						if (_offset <= 0) return;
						if (!_started) {
							jproxy.height();
							_started = noScroll(); // 所有父div都没有scroll    	    			
							if (_started)
								_startY = touch.pageY; // 

							return;
						}

						if (_offset < self.pullMax) {
							_ele.style.transform = 'translateY(-' + _offset + 'px)';
							jproxy.height(_offset);
							if (self.hasMore) {
								if (_offset >= self.pullTolerence) {
									jproxy.text(self.pullupText);
								} else {
									jproxy.text(self.pullupTip);
								}
							} else {
								jproxy.text(self.nomoreTip);
							}
						} else { // 滑哪从哪开始算，可取消
							_startY = touch.pageY + self.pullMax;
						}
					}, false);
					_ele.addEventListener('touchend', function(e) {
						if (!_started) return;

						_started = false;
						_ele.style.transition = 'transform 0.5s ease 0.3s';
						_ele.style.transform = 'translateY(0px)';
						_offset = _startY - e.changedTouches[0].pageY; // move事件有精度，需重新计算
						if (_offset < self.pullTolerence || _loading) {
							return;
						}
						if (self.hasMore) {
							_loading = true;
							jproxy.text(self.loadding);
							self.nextPage();
						}
					}, false);
					$(self.listenerNode).on("load", function() {
						_loading = false;jproxy.text("")
					});
				} else { // 按钮式
					var nextpageDom = self.nextPageDom = document.createElement("div");
					nextpageDom.style.cssText += " width:100%;";
					var button = sunz.MButton.create({
						text : self.nextPageText,
						class : "nextpage",
						listener : {
							click : function() {
								self.nextPage()
							}
						}
					}).domNode;
					$(self.nextPageDom).append(button);
					$(self.listItemNode).append(nextpageDom);
					self.nextPageButton = button;
					return;
				}
			}
		}
		self.nextPageButton = null;
		self.nextPageDom = null;
	};

	proto.clear = function() {
		this.listItemNode.innerHTML = "";
		if (this.firstRow) {
			$(this.listItemNode).append(this.firstRow);
		}
		this.items = [];
	};
	proto.toString = function() {
		return "HI this is a list";
	};

	return MList;
}();