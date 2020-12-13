	Ext.isIE8=Ext.isIE8||Ext.isIE7||Ext.isIE6; // 暂不支持IE6、IE7--也就是说在IE6、IE7下可能显示不正常或出错

	
	
	Ext.override(Date,{toString:function(){return this.getFullYear()+"-"+(this.getMonth()+1)+"-"+this.getDate();}});
	Ext.override(Ext.form.DateField,{format:"Y-m-d"});
	Ext.override(Ext.form.DateField,{altFormats:"Y-m-dTh:i:s|Y-m-d|Y-m-d h:i:s|Y/m/d|Y/m/d h:i:s"});

	Ext.override(Ext.form.DateField, {
	    setValue : function(date) {
	        if (Ext.isEmpty(date)) {
	        }
	        else if (Ext.isEmpty(date.time)) {
	        	// 2014-06-12 张航宇
	        	// 正常的Date再new一次其值也不会改变，但因为Date方法本身的原因，会导致某些情况下失败
	            //date = new Date(date);
	        }
	        else {
	            date = new Date(date.time);
	        }
	        Ext.form.DateField.superclass.setValue.call(this, this.formatDate(this.parseDate(date)));
	    }
	});
	
	Ext.override(Ext.form.DisplayField, {  
	    getValue: function () {  
	        return this.value;  
	    },  
	    setValue: function (v) {  
	        this.value = v;  
	        this.setRawValue(this.formatValue(v));  
	        return this;  
	    },  
	    formatValue: function (v) {  
	        if (this.dateFormat) {  
		        if(!Ext.isDate(v)){
		    		v = new Date(v);
		    	}
		    	if(Ext.isDate(v)){
	            	return v.dateFormat(this.dateFormat); 
	            }else{
	                return v; 
	            }
	        }  
	        if (this.numberFormat && typeof v == 'number') {  
	            return Ext.util.Format.number(v, this.numberFormat);  
	        }  
	        return v;  
	    }  
	});	
	
	Ext.override(Ext.form.CheckboxGroup,{　　　 
	　　　 //在inputValue中找到定义的内容后，设置到items里的各个checkbox中　　　 
	　　　 setValue : function(value){　　
			if(typeof(this.items.each)!='undefined'){
		　　　　　　　 this.items.each(function(f){　　
		　　　　　　　　　　　 if(value==f.inputValue){　　 
		　　　　　　　　　　　　　　　 f.setValue(true);　　 
		　　　　　　　　　　　 }　 
		　　　　　　　 });　
			}　 
	　　　 },　　 
	　　　 //以value1,value2的形式拼接group内的值　　 
	　　　 getValue : function(){　　 
	　　　　　　　 var rs = "";　
			if(typeof(this.items.each)!='undefined'){　 
		　　　　　　　 this.items.each(function(f){　　 
		　　　　　　　　　　　 if(f.getValue() == true){　　 
		　　　　　　　　　　　　　　　 rs += f.inputValue + ",";　　 
		　　　　　　　　　　　 }　　 
		　　　　　　　 });　
				if(rs.length>0){
					rs = rs.substr(0,rs.length - 1);
				}　 
			}
	　　　　　　　 return rs　　 
	　　　 },　　
	    getUnCheckValue:function(){
	　　　　　　  var rs = "";　
			if(typeof(this.items.each)!='undefined'){　 
		　　　　　　　 this.items.each(function(f){　　 
		　　　　　　　　　　　 if(f.getValue() == false){　　 
		　　　　　　　　　　　　　　　 rs += f.inputValue + ",";　　 
		　　　　　　　　　　　 }　　 
		　　　　　　　});　
				if(rs.length>0){
					rs = rs.substr(0,rs.length - 1);
				}　 
			}
	　　　　　　　 return rs	  
	    },	 
	　　　 //在Field类中定义的getName方法不符合CheckBoxGroup中默认的定义，因此需要重写该方法使其可以被BasicForm找到　　 
	　　　 getName : function(){　　 
	　　　　　　　 return this.name;　　 
	　　　 },
	  	clear : function(){
	  		if(typeof(this.items.each)!='undefined'){　 
		　　　　　　　 this.items.each(function(f){　　
		　　　　　　　　　　　　f.setValue(false);　　 
		　　　　　　　 });		
			}  
	  	}　　 
	});	
	
	Ext.override(Ext.form.RadioGroup, {   
		getValue: function(){   
	        var v;   
	        if (this.rendered) {   
	            this.items.each(function(item){   
	                if (!item.getValue())    
	                    return true;   
	                v = item.getRawValue();   
	                return false;   
	            });   
	        }   
	        else {   
	            for (var k in this.items) {   
	                if (this.items[k].checked) {   
	                    v = this.items[k].inputValue;   
	                    break;   
	                }   
	            }   
	        }   
	        return v;   
	    },   
	    setValue: function(v){   
	        if (this.rendered)    
	            this.items.each(function(item){   
	                item.setValue(item.getRawValue() == v);   
	            });   
	        else {   
	            for (var k in this.items) {   
	                this.items[k].checked = this.items[k].inputValue == v;   
	            }   
	        }   
	    }   
	});	
	
	Ext.override(Ext.tree.TreePanel, {
	    setChecked : function(ids,startNode){
	    	var node = startNode || this.root;
	    	this.setAllChecked(false,node);
			this.setChecks(ids,node);
	    },
		setAllChecked: function(checked, node){
			var tree = this;
			//node.expand();
			if(typeof(node.attributes.checked)!='undefined'){
				node.attributes.checked = checked;
				node.ui.toggleCheck(checked);
			}
			node.eachChild(function(child){
				if(typeof(child.attributes.checked)!='undefined'){
					child.attributes.checked = checked;
		    		child.ui.toggleCheck(checked);	
		    	}
		    	tree.setAllChecked(checked, child);	
			});
		},	    
		setChecks: function(ids, node){
			var tree = this;
	    	//node.expand();    
	    	for(var i=0; i<ids.length; i++){
	    		if(node.id==ids[i]){
	    			if(typeof(node.attributes.checked)!='undefined'){
		    			node.attributes.checked = true;
		    			node.ui.toggleCheck(true);
		    			break;
	    			}
	    		}
	    	}
	    	node.eachChild(function(child){
	    		for(var j=0; j<ids.length; j++){
		    		if(child.id==ids[j]){
		    			if(typeof(node.attributes.checked)!='undefined'){
			    			child.attributes.checked = true;
			    			child.ui.toggleCheck(true);
			    			break;
		    			}
		    		}	    		
	    		}
	    		tree.setChecks(ids,child);
	    	});	
		},	    
	    getUnChecked : function(a, startNode){
	        startNode = startNode || this.root;
	        var r = [];
	        var f = function(){
	            if(this.ui.checkbox!=null && !this.attributes.checked){
	                r.push(!a ? this : (a == 'id' ? this.id : this.attributes[a]));
	            }
	        };
	        startNode.cascade(f);
	        return r;
	    }
	}); 
	
	Ext.override(Ext.grid.ColumnModel,{ 
	　　 	addColumn: function(column, colIndex){ 
		　　 	if(typeof(column) == 'string'){ 
		　　 		column = {header: column, dataIndex: column}; 
		　　 	} 
		　　 	var config = this.config; 
		　　 	this.config = []; 
		　　 	if(typeof(colIndex) == 'number'){ 
		　　 		config.splice(colIndex, 0, column); 
		　　 	}else{ 
		　　 		colIndex = config.push(column); 
		　　 	} 
		　　 	this.setConfig(config); 
		　　	 	return colIndex; 
	　　 	}, 
	　　 	removeColumn: function(colIndex){ 
	　　 		var config = this.config; 
	　　 		this.config = [config[colIndex]]; 
	　　 		config.splice(colIndex,1); 
	　　 		this.setConfig(config);
	　　 	},
		removeAll: function(begin,end){
			for(var i=end; i>=begin; i--){
				this.removeColumn(i);
			}
		} 
　　});
	
	/*
	TODO:增加表单必填项前端加入红色星号标记     张涛
	*/
	Ext.override(Ext.form.Field, {  
    initComponent: Ext.form.Field.prototype.initComponent.createSequence(function(){  
        if (this.allowBlank != undefined && !this.allowBlank) {  
            var composite_field = this.findParentByType('compositefield');  
            if (composite_field) {  
                composite_field.setFieldLabel(composite_field.fieldLabel + ' <font style="color:red">*</font>');  
            } else {  
                this.setFieldLabel(this.fieldLabel + ' <font style="color:red">*</font>');  
            }  
        }  
    }),  
    setFieldLabel : function(text) {  
      if (this.rendered) {  
        this.el.up('.x-form-item', 10, true).child('.x-form-item-label').update(text);  
      }  
      this.fieldLabel = text;  
    }  
  }); 
  /*
  hyp excel导出
   */
   function ExportExcel(grid){
 if (grid.getStore().getCount() == 0) {  
    alert("目前没有数据需要导出!");  
                return;  
            }  
   var vExportContent = grid.getExcelXml(); //获取数据
if (Ext.isIE6 || Ext.isIE7 ||Ext.isIE8|| Ext.isSafari || Ext.isSafari2 || Ext.isSafari3) { //判断浏览器
var fd=Ext.get('frmDummy');
if (!fd) {
fd=Ext.DomHelper.append(Ext.getBody(),{tag:'form',method:'post',id:'frmDummy',
action:'<%=request.getContextPath()%>/net/ocx/exportexcel.jsp',
 target:'_blank',name:'frmDummy',cls:'x-hidden',cn:[
{tag:'input',name:'exportContent',id:'exportContent',type:'hidden'},
{tag:'input',name:'fileName',id:'fileName',type:'hidden'}
]},true);
}
fd.child('#exportContent').set({value:vExportContent});
fd.child('#fileName').set({value:grid.title});
fd.dom.submit();  
            } else {  
  document.location = 'data:application/vnd.ms-excel;base64,' + Base64.encode(vExportContent);  
            }
	

}
   Ext.grid.GridPanel.prototype.exportToExcel=function(){
	   document.location = 'data:application/vnd.ms-excel;base64,' + Base64.encode(this.getExcelXml());
	};
	

	// Ext的Window在拖动的时候可拖也界面
	var extOnPosition= Ext.Window.prototype.onPosition;
	var windowOnMove=function(){
		var _win=this;
		var offset=_win.header?_win.header.getHeight():25;
		var x=_win.x,y=_win.y,w=_win.width,h=_win.height;
		var pw=window.innerWidth,ph=window.innerHeight;
		var flag=false;
		if(x>pw-offset){x=pw-offset;flag=true;}
		if(x+w<offset){x=offset-w;flag=true;}
		if(y<0){y=0;flag=true;}
		if(y>ph-offset){y=ph-offset;flag=true;}
		
		if(flag)_win.setPosition(x,y);
	};
	Ext.Window.prototype.onPosition=function(){
		extOnPosition.call(this);
		windowOnMove.call(this);
	}
	var curRecord={};
	// 方便form直接从url（从target读取数据）加载数据
	Ext.form.FormPanel.prototype.loadData=function(url){
		var _form=this;
		Ext.Ajax.request({
			url:url,
			method:'post',
			success:function(r,o){
				var json=Ext.decode(r.responseText);
				if(json.success){
					saveFlag=true;
					curRecord=curRecord||{};
					curRecord.id=json.targetList[0].id;
					_form.getForm().loadRecord({data:json.targetList[0]});
				}else{
					alert("数据加载错误:"+json.msg);
				}
			}
		});
	};

	// *** 为修改日志记录发送消息以便进行信息抓取  ****
	Ext.onReady(function(){document.body.appendChild(document.createElement("script")).text='if(parent!=this&&parent.onFrameReady)setTimeout("parent.onFrameReady(this);",800);';});
	// 解决IE下不能选择表格单元文字（Grid Cell)
	Ext.grid.GridView.prototype.templates={cell:new Ext.Template(
            '<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} {css}" style="{style}" tabIndex="0" {cellAttr}>',
            '<div class="x-grid3-cell-inner x-grid3-col-{id}" {attr}>{value}</div>',
            '</td>'
            )
	};