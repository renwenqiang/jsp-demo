Ext.ns('Ext.ux.gz');

Ext.ux.gz.SynTree = Ext.extend(Ext.tree.TreePanel, {
	config:null,
    constructor : function(config){
    	config = config==null?new Object():config;
    	this.loader = new Ext.tree.TreeLoader({
			url:config.url
		});
		this.root = new Ext.tree.AsyncTreeNode({id:'root',value:'root',text:config.roottitle, expanded:true});
		this.config = config;	
		this.useArrows = true;
		this.autoScroll = true; 
		this.animate = true;
		this.containerScroll = true;
		this.border = true; // 边框
		this.lines = true;//节点之间连接的横竖线
	    Ext.ux.gz.SynTree.superclass.constructor.call(this, config);  	  
    },
    onRender: function() {
		this.on('beforeload', function(n){
			if(typeof(this.config.onbeforeload)!='undefined'){
				this.config.onbeforeload(this.loader, n);
			}
		}, this);
    	this.on('checkchange', function(node, checked){
       		node.attributes.checked = checked;  
    	}, this);
        Ext.ux.gz.SynTree.superclass.onRender.apply(this, arguments);
    }    
});

Ext.ux.gz.SynTree.orgTree = Ext.extend(Ext.ux.gz.SynTree, {
	constructor : function(config){
		config = config==null?new Object():config;
		config.url = ROOTPATH+'/orgAction.do?method=orgtree&ajax=2';
		for(var i in config.params){
			config.url += "&" + i + "=" + config.params[i];
		}
		config.url += "&issync=true";
		config.roottitle = '组织机构';
		config.onbeforeload = function(loader, node){
			var params = "";
			var data = node.attributes.data;
			if(data==null){
				params = "&ntype=root"; 
			}else{
				params = "&id="+node.id.split("_")[1]+"&parentid="+(data.parentid==null?0:data.parentid)+"&ntype="+data.ntype;
			}
			loader.url = this.url + params;
		};	
		Ext.ux.gz.SynTree.orgTree.superclass.constructor.call(this, config);  
	}
});

Ext.ux.gz.SynTree.sysTree = Ext.extend(Ext.ux.gz.SynTree, {
	constructor : function(config){
		config = config==null?new Object():config;
		config.url = ROOTPATH+'/systemAction.do?method=systree&ajax=2';
		for(var i in config.params){
			config.url += "&" + i + "=" + config.params[i];
		}
		config.url += "&issync=true&leaf=true";
		config.roottitle = '系统';
		config.onbeforeload = function(loader, node){
			loader.url = this.url;
		};	
		Ext.ux.gz.SynTree.sysTree.superclass.constructor.call(this, config);  
	}
});

Ext.ux.gz.SynTree.chargeTree = Ext.extend(Ext.ux.gz.SynTree, {
	constructor : function(config){
		config = config==null?new Object():config;
		config.url = ROOTPATH+'/systemAction.do?method=chargetree&ajax=2';
		for(var i in config.params){
			config.url += "&" + i + "=" + config.params[i];
		}
		config.url += "&issync=true";
		config.roottitle = '收费列表';
		config.onbeforeload = function(loader, node){
			var params = "";
			var data = node.attributes.data;
			if(data==null){
				params = "&ntype=root"; 
			}else{
				params = "&id="+node.id.split("_")[1]+"&ntype="+data.ntype;
			}
			loader.url = this.url + params;
		};	
		Ext.ux.gz.SynTree.chargeTree.superclass.constructor.call(this, config);  
	}
});

Ext.ux.gz.SynTree.jobTree = Ext.extend(Ext.ux.gz.SynTree, {
	constructor : function(config){
		config = config==null?new Object():config;
		config.url = ROOTPATH+'/systemAction.do?method=jobtree&ajax=2';
		for(var i in config.params){
			config.url += "&" + i + "=" + config.params[i];
		}
		config.url += "&issync=true";
		config.roottitle = '业务列表';
		config.onbeforeload = function(loader, node){
			var params = "";
			var data = node.attributes.data;
			if(data==null){
				params = "&ntype=root"; 
			}else{
				params = "&id="+node.id.split("_")[1]+"&parentid="+(data.parentid==null?0:data.parentid)+"&ntype="+data.ntype;
			}
			loader.url = this.url + params;
		};	
		Ext.ux.gz.SynTree.jobTree.superclass.constructor.call(this, config);  
	}
});

Ext.ux.gz.SynTree.menuTree = Ext.extend(Ext.ux.gz.SynTree, {
	constructor : function(config){
		config = config==null?new Object():config;
		config.url = ROOTPATH+'/orgAction.do?method=menutree&ajax=2';
		for(var i in config.params){
			config.url += "&" + i + "=" + config.params[i];
		}
		config.url += "&issync=true";
		config.roottitle = '菜单权限';
		config.onbeforeload = function(loader, node){
			var params = "";
			var data = node.attributes.data;
			if(data==null){
				params = "&ntype=root"; 
			}else{
				params = "&id="+node.id.split("_")[1]+"&parentid="+(data.parentid==null?0:data.parentid)+"&ntype="+data.ntype;
			}
			loader.url = this.url + params;
		};	
		Ext.ux.gz.SynTree.menuTree.superclass.constructor.call(this, config);  
	}
});

Ext.ux.gz.SynTree.sysrightTree = Ext.extend(Ext.ux.gz.SynTree, {
	constructor : function(config){
		config = config==null?new Object():config;
		config.url = ROOTPATH+'/orgAction.do?method=sysrighttree&ajax=2';
		for(var i in config.params){
			config.url += "&" + i + "=" + config.params[i];
		}
		config.url += "&issync=false";
		config.roottitle = '系统权限';
		config.onbeforeload = function(loader, node){
			var params = "";
			var data = node.attributes.data;
			if(data==null){
				params = "&ntype=root"; 
			}else{
				params = "&id="+node.id.split("_")[1]+"&parentid="+(data.parentid==null?0:data.parentid)+"&ntype="+data.ntype;
			}
			loader.url = this.url + params;
		};	
		Ext.ux.gz.SynTree.sysrightTree.superclass.constructor.call(this, config);  
	}
});

Ext.ux.gz.SynTree.areasysTree = Ext.extend(Ext.ux.gz.SynTree, {
	constructor : function(config){
		config = config==null?new Object():config;
		config.url = ROOTPATH+'/systemAction.do?method=areasystree&ajax=2';
		for(var i in config.params){
			config.url += "&" + i + "=" + config.params[i];
		}
		config.onbeforeload = function(loader, node){
			var params = "&id="+node.id.split("_")[1];
			loader.url = this.url + params;
		};	
		Ext.ux.gz.SynTree.areasysTree.superclass.constructor.call(this, config);  
	}
});

Ext.ux.gz.SynTree.viewerTree = Ext.extend(Ext.ux.gz.SynTree, {
	constructor : function(config){
		config = config==null?new Object():config;
		config.url = ROOTPATH+'/systemAction.do?method=viewertree&ajax=2';
		for(var i in config.params){
			config.url += "&" + i + "=" + config.params[i];
		}
		config.url += "&issync=true";
		config.roottitle = '视图列表';
		config.onbeforeload = function(loader, node){
			var params = "";
			var data = node.attributes.data;
			if(data==null){
				params = "&ntype=root"; 
			}else{
				params = "&id="+node.id.split("_")[1]+"&parentid="+(data.parentid==null?0:data.parentid)+"&ntype="+data.ntype;
			}
			loader.url = this.url + params;
		};	
		Ext.ux.gz.SynTree.viewerTree.superclass.constructor.call(this, config);  
	}
});

Ext.ux.gz.SynTree.loadTree = Ext.extend(Ext.ux.gz.SynTree, {
	constructor : function(config){
		config = config==null?new Object():config;
		config.url = ROOTPATH+'/systemAction.do?method=loadtree&ajax=2';
		for(var i in config.params){
			config.url += "&" + i + "=" + config.params[i];
		}
		config.url += "&issync=true";
		config.roottitle = '调档列表';
		config.onbeforeload = function(loader, node){
			var params = "";
			var data = node.attributes.data;
			if(data==null){
				params = "&ntype=root"; 
			}else{
				params = "&id="+node.id.split("_")[1]+"&parentid="+(data.parentid==null?0:data.parentid)+"&ntype="+data.ntype;
			}
			loader.url = this.url + params;
		};	
		Ext.ux.gz.SynTree.loadTree.superclass.constructor.call(this, config);  
	}
});

Ext.ux.gz.SynTree.queryTree = Ext.extend(Ext.ux.gz.SynTree, {
	constructor : function(config){
		config = config==null?new Object():config;
		config.url = ROOTPATH+'/systemAction.do?method=querytree&ajax=2';
		for(var i in config.params){
			config.url += "&" + i + "=" + config.params[i];
		}
		config.url += "&issync=true";
		config.roottitle = '查档列表';
		config.onbeforeload = function(loader, node){
			var params = "";
			var data = node.attributes.data;
			if(data==null){
				params = "&ntype=root"; 
			}else{
				params = "&id="+node.id.split("_")[1]+"&parentid="+(data.parentid==null?0:data.parentid)+"&ntype="+data.ntype;
			}
			loader.url = this.url + params;
		};	
		Ext.ux.gz.SynTree.queryTree.superclass.constructor.call(this, config);  
	}
});

Ext.ux.gz.SynTree.regionTree = Ext.extend(Ext.ux.gz.SynTree, {
	constructor : function(config){
		config = config==null?new Object():config;
		config.url = ROOTPATH+'/systemAction.do?method=regionTree&ajax=2';
		for(var i in config.params){
			config.url += "&" + i + "=" + config.params[i];
		}
		config.url += "&issync=true";
		config.roottitle = '行政区域列表';
		config.onbeforeload = function(loader, node){
			var params = "";
			var data = node.attributes.data;
			if(data==null){
				params = "&ntype=root"; 
			}else{
				params = "&id="+node.id.split("_")[1]+"&ntype="+data.ntype;
			}
			loader.url = this.url + params;
		};	
		Ext.ux.gz.SynTree.regionTree.superclass.constructor.call(this, config);  
	}
});

Ext.ux.gz.SynTree.nationTree = Ext.extend(Ext.ux.gz.SynTree, {
	constructor : function(config){
		config = config==null?new Object():config;
		config.url = ROOTPATH+'/systemAction.do?method=nationTree&ajax=2';
		for(var i in config.params){
			config.url += "&" + i + "=" + config.params[i];
		}
		config.url += "&issync=true";
		config.roottitle = '国家列表';
		config.onbeforeload = function(loader, node){
			var params = "";
			var data = node.attributes.data;
			if(data==null){
				params = "&ntype=root"; 
			}else{
				params = "&id="+node.id.split("_")[1]+"&ntype="+data.ntype;
			}
			loader.url = this.url + params;
		};	
		Ext.ux.gz.SynTree.nationTree.superclass.constructor.call(this, config);  
	}
});


















Ext.ux.gz.SynTree.bankTree = Ext.extend(Ext.ux.gz.SynTree, {
	constructor : function(config){
		config = config==null?new Object():config;
		config.url = ROOTPATH+'/systemAction.do?method=getBankTree&ajax=2';
		for(var i in config.params){
			config.url += "&" + i + "=" + config.params[i];
		}
		config.url += "&issync=true";
		config.roottitle = '银行信息';
		config.onbeforeload = function(loader, node){
			var params = "";
			var data = node.attributes.data;
			if(data==null){
				params = "&ntype=root"; 
			}else{
				params = "&id="+node.id.split("_")[1]+"&ntype="+data.ntype;
			}
			loader.url = this.url + params;
		};	
		Ext.ux.gz.SynTree.bankTree.superclass.constructor.call(this, config);  
	}
});

Ext.ux.gz.SynTree.mapprvlTree = Ext.extend(Ext.ux.gz.SynTree, {
	constructor : function(config){
		config = config==null?new Object():config;
		config.url = ROOTPATH+'/mapprvlAction.do?method=getmapprvlTree&ajax=2';
		for(var i in config.params){
			config.url += "&" + i + "=" + config.params[i];
		}
		config.url += "&issync=true";
		config.roottitle = '幢列表';
		config.onbeforeload = function(loader, node){
			var params = "";
			var data = node.attributes.data;
			if(data==null){
				params = "&ntype=root"; 
			}else{
				params = "&id="+node.id.split("_")[1]+"&ntype="+data.ntype;
			}
			loader.url = this.url + params;
		};	
		Ext.ux.gz.SynTree.mapprvlTree.superclass.constructor.call(this, config);  
	}

});


Ext.ux.gz.SynTree.AvrchiverterTree = Ext.extend(Ext.ux.gz.SynTree, {
	constructor : function(config){
		config = config==null?new Object():config;
		config.url = ROOTPATH+'/avrchiventerAction.do?method=avrchiventertree&ajax=2';
		for(var i in config.params){
			config.url += "&" + i + "=" + config.params[i];
		}
		config.url += "&issync=true";
		config.roottitle = '业务列表';
		config.onbeforeload = function(loader, node){
			var params = "";
			var data = node.attributes.data;
			if(data==null){
				params = "&ntype=root"; 
			}else{
				params = "&id="+node.id.split("_")[1]+"&parentid="+(data.parentid==null?0:data.parentid)+"&ntype="+data.ntype;
			}
			loader.url = this.url + params;
		};	
		Ext.ux.gz.SynTree.jobTree.superclass.constructor.call(this, config);  
	}
});