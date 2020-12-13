sunz.MNavHead =  function () {
    /**
     * 导航，框架规范下基本没有应用场景
     * 
     */
    var MNavHead = function () {
        this.initHeadNode();
    },proto=MNavHead.prototype;
    
    proto.initHeadNode = function () {
        var self = this;
        var headEle = document.createElement("nav");
        headEle.className += " navbar navbar-default ";
        headEle.role = "navigation";
        this.headEle = headEle;
        document.body.appendChild(headEle);

        //标题
        var title = document.createElement("div");
        title.className += " nav-head-title ";

        this.titleEle = title;
        headEle.appendChild(title);

        //返回
        var back = this.backEle = document.createElement("a");
        back.style.display = "none";
        back.className += " btn btn-default nav-head-back";
        back.innerHTML = '<span class="glyphicon glyphicon-arrow-left"></span>';
        headEle.appendChild(back);
        $(back).bind("click", function () {
            self.back();
        });
    };
    proto.setTitle = function (title) {
        this.title = title ? title : "";
        this.titleEle.innerHTML = '<span class="  center-block">' + title + '</span>';
        document.title = title;
    };
    proto.showBack = function () {
        $(this.backEle).show();
    };
    proto.hideBack = function () {
        $(this.backEle).hide();
    };
    proto.back = function () {
        if (this.NAV) {
            this.NAV.back();
        }
    };
    return MNavHead;
}();