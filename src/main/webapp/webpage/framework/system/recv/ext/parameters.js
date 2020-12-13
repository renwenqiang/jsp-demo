function Parameters(){
	this.params = new Array(),
	this.parameter = new Ext.data.Record.create([
		{name:'ppname',type:'string'},
		{name:'pname',type:'string'},
		{name:'pvalue',type:'string'},
		{name:'ptype',type:'int'},
		{name:'sequence',type:'int'},
		{name:'data'}
	]),
	this.get = function(ppname, allowblank, fn){
		var scopt = this;
		var arr = new Array();
		if(allowblank==true){
			var o = new scopt.parameter({
	   			ppname:ppname,
	   			pname:'0',
	   			ptype:1,
	   			data:null,
	   			pvalue:'全部',
	   			sequence:0
	   		});
			arr.push(o);
		}
		Ext.each(this.params, function(rec){
			if(rec.ppname==ppname){
				var o = new scopt.parameter(rec);
				arr.push(o);
			}
		})
		if(typeof(fn)=='function'){
			if(arr.length>0){
				fn(arr[0].data.pname);
			}else{
				fn('');
			}
		}
		var store = new Ext.data.JsonStore();
		store.add(arr);
		return store;
	},
	this.getEx = function(ppname, allowblank, filter, fn){
		var scopt = this;
		var arr = new Array();
		if(allowblank==true){
			var o = new scopt.parameter({
	   			ppname:ppname,
	   			pname:'0',
	   			ptype:1,
	   			data:null,
	   			pvalue:'全部',
	   			sequence:0
	   		});
			arr.push(o);
		}
		var fs = filter.split(",");
		Ext.each(this.params, function(rec){
			if(fs.length>0){
				if(rec.ppname==ppname){
					var exist = false;
					for(var i=0; i<fs.length; i++){
						if(fs[i]==rec.pname){
							exist = true;
							break;
						}
					}	
					if(!exist){
						var o = new scopt.parameter(rec);
						arr.push(o);
					}
				}			
			}else{
				if(rec.ppname==ppname){
					var o = new scopt.parameter(rec);
					arr.push(o);
				}			
			}
		})
		if(typeof(fn)=='function'){
			if(arr.length>0){
				fn(arr[0].data.pname);
			}else{
				fn('');
			}
		}
		var store = new Ext.data.JsonStore();
		store.add(arr);
		return store;
	},	
	this.getValue = function(pname, ppname){
		var rs = "";
		Ext.each(this.params, function(rec){
			if(ppname){
				if(rec.ppname==ppname && rec.pname==pname){
					rs = rec.pvalue;
				}				
			}else{
				if(rec.pname==pname){
					rs = rec.pvalue;
				}			
			}
		});
		return rs;
	},
	this.getCode = function(ppname, pvalue){
		var rs = "";
		Ext.each(this.params, function(rec){
			if(rec.ppname==ppname && rec.pvalue==pvalue){
				rs = rec.pname;
			}
		});
		if(rs==""){
			rs = pvalue;
		}
		return rs;
	},
	this.getData = function(ppname, pname){
		var rs = null;
		Ext.each(this.params, function(rec){
			if(rec.ppname==ppname && rec.pname==pname){
				rs = rec.data;
			}
		});
		return rs;		
	},	
	this.getFirst = function(store, valueField){
		if(valueField){
			return this.getFirstItem(store)[valueField];
		}else{
			return this.getFirstItem(store).pname;
		}
	},
	this.getFirstItem = function(store){
		if(store.getRange().length>0){
			return store.getRange()[0].data;
		}else{
			return new Object();
		}		
	},
	this.getIndex = function(store, index, valueField){
		if(valueField){
			return this.getIndexItem(store, index)[valueField];
		}else{
			return this.getIndexItem(store, index).pname;
		}
	},	
	this.getIndexItem = function(store, index){
		if(store.getRange().length>=(index+1) && index>=0){
			return store.getRange()[index].data;
		}else{
			return new Object();
		}
	},
	this.add = function(param){
		this.params.push(param);
	},
	this.remove = function(ppname){
		for(var i=this.params.length-1; i>=0; i--){
			if(this.params[i].ppname==ppname){
				this.params.splice(i,1);
			}
		}
	},	
	this.item=function(v){
		var r = null;
		Ext.each(this.params, function(rec){
			if(rec.pname==v){
				r = rec;
				return false;
			}
		});
		return r; 
	}
}