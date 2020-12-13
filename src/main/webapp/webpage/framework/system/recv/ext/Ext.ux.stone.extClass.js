Ext.ns('Ext.ux.stone');

Ext.ux.stone.ComboBox = Ext.extend(Ext.form.ComboBox, {
	config:null,
    constructor : function(config){
    	config = config==null?new Object():config;			
		this.mode='local';
		this.triggerAction='all';
		this.valueField=config.valueField==null?'pname':config.valueField;
		this.displayField=config.displayField==null?'pvalue':config.displayField;
		this.forceSelection=false;
		this.editable=false;		
		if(config.filter){
			this.store = P.getEx(config.ppname, config.showall, config.filter);
		}else{
			this.store = P.get(config.ppname, config.showall);
		}		
		this.config = config;					
	    Ext.ux.stone.ComboBox.superclass.constructor.call(this, config);  	  
    },
    onRender: function() {
    	// 显示时必须判断是否已经有值，否则会冲掉设置好的值（出现这种情况是在显示前已经对控件赋值）
		if(this.value==null &&this.config &&(this.config.value==null || this.config.value=='')){
			if(this.config.itemIndex>=0){
				this.value = P.getIndex(this.store, this.config.itemIndex, this.valueField);
			}else{
				this.value = "";
			}			
		}
        Ext.ux.stone.ComboBox.superclass.onRender.apply(this, arguments);
    }    
});
Ext.reg('stonecombo', Ext.ux.stone.ComboBox);

//数据视图窗体
Ext.ux.stone.DataView = Ext.extend(Ext.DataView,{
	initComponent:function(param) {
        Ext.ux.stone.DataView.superclass.initComponent.call(this);
        this.id = Ext.id();
        this.store = this.getDataStore();   
    },
    multiSelect:true,
    overClass:'x-view-over',
    itemSelector:'div.thumb-wrap',
    emptyText: '没有信息',
    plugins: [
        new Ext.DataView.DragSelector()
    ], 
	listeners:{
   	    selectionchange: {
   		    fn: function(dv,nodes){
   			    var l = nodes.length;
   			    if(this.onsetTitle){
   			    	this.onsetTitle('列表('+l+'项被选中)');
   			    }
   		    }
   	    },
   	    dblclick:function(){
 	        var node = this.getSelectedNodes()[0];
 	        this.ondblclick(node);    	    
   	    },
   	    contextmenu:function(dataView,index,node,e){
   	    	this.oncontextmenu(dataView,index,node,e);
   	    },
   	    render:function(){
   	    	this.onrender();
   	    }
	},   
	getDataStore : Ext.emptyFn,
	ondblclick : Ext.emptyFn,
	onsetTitle : Ext.emptyFn,
	onrender : Ext.emptyFn,
	oncontextmenu: Ext.emptyFn
});
Ext.reg('stonedataview', Ext.ux.stone.DataView);