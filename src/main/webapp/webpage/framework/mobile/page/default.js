define([], function ( ) {

    /**
     * 导航
     *  
     */
    var Index = function (option) {
        this.title = "首页";
        if (option.url) {
            this.url = option.url;
        }
        if (option.listitemsrc) {
            this.listitemsrc = option.listitemsrc;
        } 
        var div = this.domNode = document.createElement("div"); 
        var listContainer = this.listContainer = document.createElement("div"); 
        div.appendChild(listContainer);
        var list = this.list = new sunz.MList({ type: "h", itemsrc: this.listitemsrc, url: this.url,parent:this.listContainer });
        var tmplate = "<img src='{img}'/><li>{title}</li>";
        this.list.setTemplate(tmplate);
        list.loadFilter = function (result) {
            return result.data;
        }; 
    };
 
    return Index;
});