sunz.MNav =function () {
    /**
     * 导航，框架规范下基本没有应用场景
     *  
     */
    var MNav = function () {
        this.views = []; 
        this.currentView = null;
        this.currentIndex = 0;
        this.initHeadNode();
        this.initContentNode();
        this.addBackListener();

    },proto=MNav.prototype; 
    
    proto.addBackListener = function () {
        var self = this;
        window.addEventListener("popstate", function (e) {
            //alert("我监听到了浏览器的返回按钮事件啦");//根据自己的需求实现自己的功能 
            self.back();
        }, false);
    };
    proto.initHeadNode = function () {

        this.head = new sunz.MNavHead();
        this.head.NAV = this;
    };
    proto.initContentNode = function () {
        var content = document.createElement("div");
        document.body.appendChild(content);
        content.className += " sunzmui-pagecontainer ";
        this.content = content;
    };
    proto.setTitle = function (title) {

        this.head.setTitle(title);
        this.toggleBack();
    };
    proto.toggleBack = function () {
        this._refreshCurrentIndex();
        if (this.currentIndex > 0) {
            this.head.showBack();
        } else {
            this.head.hideBack();
        }
    };
    /**
     * 根据URL打开新的页面
     */
    proto.openUrl = function (url, option) {
        var self = this;
        if (option) {

            option.NAV = this;
        } else {
            option = { NAV: this }
        }
        require([url], function (clazz) {
            var view = new clazz(option);
            view.option = option;
            //把当前对象传到子页面中
            if (view.domNode) {
                view.domNode.className += " sunzmui-page  ";
            }
            self.pushView(view);
        });
    };
    /**
     * 添加页面到组
     */
    proto.pushView = function (view) {
        if (view && view.domNode) {
            this.views.push(view);
            this.content.appendChild(view.domNode);
            this.showView(view);
        } else if (view && view.children) {
            var div = document.createElement("div");
            div.className += " sunzmui-page ";
            view.domNode = div;
            this._createElementsByConfig(view, div, view.children);
            this.pushView(view);
        }
    };
    /**
     * 根据配置生成html组件
     */
    proto._createElementsByConfig = function (view, container, children) {
        if (children instanceof Array) {
            for (var i = 0; i < children.length; i++) {
                var itemconfig = children[i];
                var item = this._createElementByConfig(view, container, itemconfig);
                if (!item) {
                    continue;
                }
                //放置子元素
                var ele = item.contentNode;
                if (itemconfig.children) {
                    this._createElementsByConfig(view, ele, itemconfig.children);
                }
            }
        } else if (typeof children == "object" && children.xtype) {
            var item2 = this._createElementByConfig(view, container, children);
            if (!item2) {
                return;
            }
            //放置子元素
            var ele2 = item2.contentNode;
            if (children.children) {
                this._createElementsByConfig(view, ele2, children.children);
            }
        }

    };
    /**
     * 根据配置生成html组件
     */
    proto._createElementByConfig = function (view, container, itemconfig) {
        var ele = null; //html对象
        var item = null; //逻辑对象
        /**
         * 水平容器
         */
        if (itemconfig.xtype == "hpanel") {
            item = ElementHelper.createHPanel();
        }
        /**
         * 垂直容器
         */
        else if (itemconfig.xtype == "list") {
            /**
             * 列表
             */
            item = ElementHelper.createList(container, itemconfig.option);
        } else if (itemconfig.xtype == "fieldset") {
            /**
             * 表单项容器
             */
            item = ElementHelper.createFieldset(itemconfig);
        } else if (itemconfig.xtype == "form") {
            /**
             * 表单容器
             */
            item = ElementHelper.createForm();
        } else if (itemconfig.xtype == "textfield") {
            /**
             * 表单容器
             */
            item = ElementHelper.createTextField(itemconfig);
        } else if (itemconfig.xtype == "numberfield") {
            /**
             * 表单容器
             */
            item = ElementHelper.createNumberField(itemconfig);
        } else if (itemconfig.xtype == "datefield") {
            /**
             * 表单容器
             */
            item = ElementHelper.createDateField(itemconfig);
        } else if (itemconfig.xtype == "combofield") {
            /**
             * 表单容器
             */
            item = ElementHelper.createComboField(itemconfig);
        }
        if (!item) {
            return;
        }
        ele = item.domNode;

        if (itemconfig.cssText) {
            ele.style.cssText += itemconfig.cssText;
        }
        if (itemconfig.className) {
            ele.className += (" " + itemconfig.className);
        }
        if (itemconfig.style) {
            if (typeof itemconfig.style == "object") {
                for (var id in itemconfig.style) {
                    ele.style[id] = itemconfig.style[id];
                }
            }
        }
        if (itemconfig.id) {
            if (view[itemconfig.id]) {
                throw ("指定的ID已存在，或与关键字冲突");
            } else {
                view[itemconfig.id] = item;
            }
        }
        try {
            if (ele instanceof HTMLElement) {
                container.appendChild(ele);
            }
        } catch (e) {
            console.error("解析页面配置出错", view, container, itemconfig);
        }

        return item;

    };
    /**
     * 显示页面
     */
    proto.showView = function (view) {
        var views = this.views;
        var vindex = views.indexOf(view);
        this.currentView = view;
        this.currentIndex = vindex;
        if (vindex >= 0) {
            //从
        }
        for (var i = 0; i < views.length; i++) {
            var viewi = views[i];
            viewi.domNode.style.display = "none";
            if (view != viewi) {
                viewi.domNode.style.display = "none";
                //$(viewi.domNode).slideUp(); 
                //$(viewi.domNode).animate({ left: '250px' }).fadeOut("fast");
                //$(viewi.domNode).hide(); 
            }

        }
        //view.domNode.style.display = "";
        $(view.domNode).animate({  }).fadeIn("fast");
        this.setTitle(view.title);
        var baseurl = window.location.href.split("#")[0];
        window.history.pushState('forward', null, baseurl + '#' + view.title);
    };
    /**
     * 返回上一个页面
     */
    proto.back = function () {
        this._refreshCurrentIndex();
        try {
            if (this.currentIndex > 0) {
                var view = this.views[this.currentIndex];
                var isd = this.delView(view);
                if (isd) {
                    this.showView(this.views[this.currentIndex - 1]);
                }
            }
        } catch (e) {
            console.error(e);
        }
    };
    /**
     * 删除页面
     */
    proto.delView = function (view) {
        try {
            var vindex = this.views.indexOf(view);
            this.views.splice(vindex, 1);
            if (this.views.indexOf(view) < 0) {
                this.content.removeChild(view.domNode);
            }
            return true;
        } catch (e) {
            console.error(e);
        }
    };
    proto._refreshCurrentIndex = function () {
        this.currentIndex = this.views.indexOf(this.currentView);
    };
    return MNav;
}();