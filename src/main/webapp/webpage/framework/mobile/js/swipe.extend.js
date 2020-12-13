sunz.MSwipe = sunz.Swipe = function() {
	/**
	 * 滑动插件,主参数（本组件需两个参数：ele，option)为：onSwipeRight，onSwipeLeft，onSwipeUp，onSwipeDow-即相关事件监听函数
	 */
	var defaults = {
		moveWithTouch : false,
		moveDirection : "down",
		maxMove : 0,
		moveTime : 0,
		tolerance : 25,
		moveBack : true,
		isStopPropagation : false,
		_description : {
			title : "滑动插件",
			remark : "跟随触摸滑动插件"
		}
	};
	var MSwipe = function(ele, option) {
			$.extend(this, defaults, option);
			this.lis = [];
			this.ele = ele;
			this.moved = false;
			this.startTouch = null;
			this.registLiestener();
		},
		proto = MSwipe.prototype;

	proto.registLiestener = function() {
		var self = this;
		this.remove();
		var ele = this.ele;
		var move = $(ele).bind("touchmove", function(e) {

			self.moveTime++;
			if (self.moveTime == 1) {
				self._touchStart(e);
			}

			self._touchMove(e);
		});
		var start = $(ele).bind("touchstart", function(e) {

			self.moveTime = 0;
		});
		var end = $(ele).bind("touchend", function(e) {

			if (self.moveTime == 0) {
				return;
			}

			self.moveTime = 0;
			self._touchEnd(e);
		});
		this.lis.push(move);
		this.lis.push(start);
		this.lis.push(end);
		this.listenerNode = ele;
		this._moveElementLis();
	};
	proto._touchStart = function(e) {
		//console.log(e)
		this.startTouch = e.touches[0];
		this.startTouch.ele = this.ele;
		this.trigger("onStart");
		this.triggerFun("onStart", this.eleMoved);
		this.eletop = this.__getParentScrollTop();
	};
	proto._touchMove = function(e) {
		this.endTouch = e.touches[0];
		this.endTouch.ele = this.ele;
		this.moved = true;
		this.trigger("onMove");
		this.triggerFun("onMove", this.eleMoved);
		//this._moveElement();
		this._triggerMoving(e);
	};
	proto._triggerMoving = function(e) {
		var $x = this.endTouch.screenX - this.startTouch.screenX;
		var $y = this.endTouch.screenY - this.startTouch.screenY;
		if (!(Math.abs($x) > this.tolerance || Math.abs($y) > this.tolerance)) {
			return;
		}
		var scrolltype = null;
		if (Math.abs($x) > Math.abs($y)) {
			if ($x > 0) {
				scrolltype = "onSwipeingRight";
			} else {
				scrolltype = "onSwipeingLeft";
			}
			this.trigger(scrolltype, [ $x, e ]);
		} else {

			if ($y > 0) {
				var top = this.eletop; //document.body.scrollTop
				if (top < 5)
					scrolltype = "onSwipeingDown";
			} else {
				scrolltype = "onSwipeingUp";
			}
			if (scrolltype) {
				this.trigger(scrolltype, [ $y, e ]);
			}

		}
	};
	proto.__getParentScrollTop = function(ele) {
		if (!ele) {
			ele = this.ele;
		}
		var top = $(ele).scrollTop();
		var ptop = 0;
		if ($(ele).parent().length > 0)
			ptop = this.__getParentScrollTop($(ele).parent());
		return top + ptop;
	};
	proto._moveElementLis = function() {
		var self = this;
		/**
         * 触摸的时候要素随之移动
         */
		if (this.moveWithTouch) {
			if (this.moveDirection == "down") {
				this.bind("onSwipeingDown", function(a, name, es) {
					var b = es[0];
					var e = es[1];
					if (this.isStopPropagation) {
						e.stopPropagation();
					}
					//e.preventDefault();
					if (this.ele.style.position != "absolute")
						this.ele.style.position = 'relative';
					this.ele.style.transition = "top 0s";
					if (!this.startMove) {
						this.triggerFun("onMoveStart", this.eleMoved);
					}
					this.startMove = true;
					if (this.maxMove == 0 || (this.maxMove > 0 && b <= this.maxMove)) {
						this.triggerFun("eleMove", b);
						this.trigger("eleMove", b);
						this.ele.style.top = b + "px";
						this.eleMoved = b;

					}
				});
			}
			if (this.moveDirection == "left") {
				this.bind("onSwipeingLeft", function(a, name, es) {
					var b = es[0];
					var e = es[1];
					if (this.isStopPropagation) {
						e.stopPropagation();
					}
					//e.preventDefault();
					if (this.ele.style.position != "absolute")
						this.ele.style.position = 'relative';
					this.ele.style.transition = "left 0s";
					if (!this.startMove) {
						this.triggerFun("onMoveStart", this.eleMoved);
					}
					this.startMove = true;
					if (this.maxMove == 0 || (this.maxMove > 0 && b <= this.maxMove)) {
						this.triggerFun("eleMove", b);
						this.trigger("eleMove", b);
						this.ele.style.left = b + "px";
						this.eleMoved = b;
					}
				});
			}
			if (this.moveDirection == "right") {
				this.bind("onSwipeingRight", function(a, name, es) {
					var b = es[0];
					var e = es[1];
					if (this.isStopPropagation) {
						e.stopPropagation();
					}
					//e.preventDefault();
					if (this.ele.style.position != "absolute")
						this.ele.style.position = 'relative';
					this.ele.style.transition = "left 0s";
					if (!this.startMove) {
						this.triggerFun("onMoveStart", this.eleMoved);
						this.startLeft = parseInt(this.ele.style.left) || 0;
					}
					this.startMove = true;
					if (this.maxMove == 0 || (this.maxMove > 0 && b <= this.maxMove)) {
						this.triggerFun("eleMove", b);
						this.trigger("eleMove", b);
						this.ele.style.left = this.startLeft + b + "px";
						this.eleMoved = b;
					}
				});
			}
			if (this.moveDirection == "leftorright") {
				this.bind("onSwipeingRight", function(a, name, es) {
					var b = es[0];
					var e = es[1];
					if (this.isStopPropagation) {
						e.stopPropagation();
					}
					//e.preventDefault();
					if (this.ele.style.position != "absolute")
						this.ele.style.position = 'relative';
					this.ele.style.transition = "left 0s";
					if (!this.startMove) {
						this.triggerFun("onMoveStart", this.eleMoved);
						this.startLeft = parseInt(this.ele.style.left) || 0;
					}
					this.startMove = true;
					if (this.maxMove == 0 || (this.maxMove > 0 && b <= this.maxMove)) {
						this.triggerFun("eleMove", b);
						this.trigger("eleMove", b);
						this.ele.style.left = this.startLeft + b + "px";
						this.eleMoved = b;
					}
				});
				this.bind("onSwipeingLeft", function(a, b, es) {
					var b = es[0];
					var e = es[1];
					//e.preventDefault();
					if (this.isStopPropagation) {
						e.stopPropagation();
					}
					if (this.ele.style.position != "absolute")
						this.ele.style.position = 'relative';
					this.ele.style.transition = "left 0s";
					if (!this.startMove) {
						this.triggerFun("onMoveStart", this.eleMoved);
						this.startLeft = parseInt(this.ele.style.left) || 0;
					}
					this.startMove = true;
					if (this.maxMove == 0 || (this.maxMove > 0 && b <= this.maxMove)) {
						this.triggerFun("eleMove", b);
						this.trigger("eleMove", b);
						this.ele.style.left = this.startLeft + b + "px";
						this.eleMoved = b;
					}
				});
			}
		}



		return;
	};
	proto._clearMove = function() {
		/**
		 * 触摸的时候要素随之移动
		 */
		if (this.moveWithTouch) {

			//console.log(this.startTouch,this.endTouch );
			/**
			 * 向下移动
			 */
			if (this.moveDirection == "down" && this.startMove) {
				if (this.moveBack) {
					this.ele.style.transition = "top 0.5s";
					this.ele.style.top = 0;
					document.body.scrollTop = 0;
				}
				this.startMove = false;
				this.trigger("eleMoveEnd", this.eleMoved);
				this.triggerFun("eleMoveEnd", this.eleMoved);
			}
			/**
			 * 向左
			 */
			if (this.moveDirection == "left" && this.startMove) {
				if (this.moveBack) {
					//this.ele.style.transition = "left 0.5s";
					//this.ele.style.left = 0;
					//document.body.scrollTop = 0;
				}
				this.startMove = false;
				this.trigger("eleMoveEnd", this.eleMoved);
				this.triggerFun("eleMoveEnd", this.eleMoved);
			}

			/**
			 * 向右
			 */
			if (this.moveDirection == "right" && this.startMove) {
				if (this.moveBack) {
					//this.ele.style.transition = "left 0.5s";
					//this.ele.style.left = 0;
					//document.body.scrollTop = 0;
				}
				this.startMove = false;
				this.trigger("eleMoveEnd", this.eleMoved);
				this.triggerFun("eleMoveEnd", this.eleMoved);
			}
			/**
			 * 向右
			 */
			if (this.moveDirection == "leftorright" && this.startMove) {
				if (this.moveBack) {
					//this.ele.style.transition = "left 0.5s";
					//this.ele.style.left = 0;
					//document.body.scrollTop = 0;
				}
				this.startMove = false;
				this.trigger("eleMoveEnd", this.eleMoved);
				this.triggerFun("eleMoveEnd", this.eleMoved);
			}
		}
	};
	proto._touchEnd = function(e) {
		//console.log(e) 
		//this.endTouch = e.touches[0];
		this.trigger("onEnd");
		this.triggerFun("onEnd", this.eleMoved);
		if (!this.moved) {
			return;
		}
		;
		var $x = this.endTouch.screenX - this.startTouch.screenX;
		var $y = this.endTouch.screenY - this.startTouch.screenY;

		if (Math.abs($x) > this.tolerance || Math.abs($y) > this.tolerance) {
			if (Math.abs($x) > Math.abs($y)) {
				if ($x > 0) {
					this.trigger("onSwipeRight");this.triggerFun("onSwipeRight");
				} else {
					this.trigger("onSwipeLeft");this.triggerFun("onSwipeLeft");
				}
			} else {
				if ($y > 0) {
					this.trigger("onSwipeDown");this.triggerFun("onSwipeDown");
				} else {
					this.trigger("onSwipeUp");this.triggerFun("onSwipeUp");
				}
			}
		}
		// if(!this.notClearMoveAfterEnd){
		this._clearMove();
		// }

	};

	proto.triggerFun = function(funName, params) {
		//console.log(funName)
		if (this[funName]) {
			this[funName](this.startTouch, this.endTouch, params);
		}
	};
	/**
	 * 清除绑定事件
	 */
	proto.remove = function() {
		var ele = this.ele;
		var move = $(ele).unbind("touchmove");
		var start = $(ele).unbind("touchstart");
		var end = $(ele).unbind("touchend");
	};

	MSwipe.create = function(ele, opt) {
		return new MSwipe(ele, opt);
	}
	return MSwipe;
}();