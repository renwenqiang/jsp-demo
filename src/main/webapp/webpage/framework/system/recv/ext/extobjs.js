// ext执行ajax方法
function doExtAjax(url, params, sfn, ffn, showprogressbar, progress, parent){
	if(showprogressbar==null || showprogressbar==true){
		openExtProgress(progress, parent);
	}
	Ext.Ajax.request({ 
        url: url,
        timeout: 30000, 
        method:'post',	
        waitTitle : '操作中',
		waitMsg : '正在执行后台操作，请稍候...',
        params:params,	        
        success: function(response){ 
        	if(showprogressbar==null || showprogressbar==true){
        		closeExtProgress(parent);
        	}
            var respText = Ext.util.JSON.decode(response.responseText); 
            if(respText.success){
            	if(typeof(sfn) == "function"){
            		sfn(respText);
            	}
            }else{
            	if(typeof(ffn) == "function"){
            		ffn(respText);
            	}else{
            		if(!parent){
		            	Ext.MessageBox.show({
		                    title: '失败',
		                    msg: respText.msg,
		                    icon: Ext.MessageBox.ERROR,
		                    buttons: {cancel:'确定'}
		                });
	                }else{
		            	parent.Ext.MessageBox.show({
		                    title: '失败',
		                    msg: respText.msg,
		                    icon: Ext.MessageBox.ERROR,
		                    buttons: {cancel:'确定'}
		                });		                
	                }            	
            	}			   	    
   	    	}      
        }, 
        failure : function(){ 
        	if(showprogressbar==null || showprogressbar==true){
        		closeExtProgress(parent);
        	}
        	if(!parent){
				Ext.MessageBox.show({
	                title: '失败',
	                msg: "提交服务器失败，请重新操作",
	                icon: Ext.MessageBox.ERROR,
	                buttons: {cancel:'确定'}
	            });	
            }else{
				parent.Ext.MessageBox.show({
	                title: '失败',
	                msg: "提交服务器失败，请重新操作",
	                icon: Ext.MessageBox.ERROR,
	                buttons: {cancel:'确定'}
	            });	            
            }	
        }
	});
}
Ext.Msg.minWidth=260;
/**
 * @description N秒后，自动隐藏消息框
 * @param {} time 毫秒数
 * @author xjr
 */ 
function msgbox(title,msg,time,fn,parent) {
	var icon = Ext.MessageBox.INFO
	if(title=='错误'){
		icon = Ext.MessageBox.ERROR
	}
	if(!parent){
		Ext.MessageBox.show({
	        title: title,
	        msg: msg,	
	        icon: icon,
	        buttons: {cancel:'确定'} ,
	        fn:fn
	    });	
		if(time!=null && typeof(time)!='undefined' && time>0 && typeof(fn)!="function"){
			setTimeout("Ext.Msg.hide()",time);
		}
	}else{
		parent.Ext.MessageBox.show({
	        title: title,
	        msg: msg,	
	        icon: icon,
	        buttons: {cancel:'确定'} ,
	        fn:fn
	    });	
		if(time!=null && typeof(time)!='undefined' && time>0 && typeof(fn)!="function"){
			setTimeout("parent.Ext.Msg.hide()",time);
		}	
	}
}

// ext 确认提示框
function doExtConfirm(title,content,fn,parent){
	if(title==""){
		title = "请确认";
	}
	if(content==""){
		content = "您确定要操作吗？";
	}
	if(parent==null){
		Ext.Msg.confirm(title,content,
			function(btn){
				if(btn=='yes'){
					if(typeof(fn) == "function"){
						fn();
					}
				}
			}
		)
	}else{
		parent.Ext.Msg.confirm(title,content,
			function(btn){
				if(btn=='yes'){
					if(typeof(fn) == "function"){
						fn();
					}
				}
			}
		);
	}
}

// ext树结点
function extTreeNode(title, childnodes){
	var t = "";
	if(typeof(title)=='undefined'){
		t = "未定义";
	}else{
		t = title;
	}
	
	var c = null;
	if(typeof(childnodes)!='undefined'){
		c = childnodes;
	}
	if(c!=null){
		return {
	        draggable: false,
	        id: 'root',
	        text:t,
			expanded:true,//展开
	        leaf:false,
            children:Ext.util.JSON.decode(childnodes)  
		};		
	}else{
		return {
	        draggable: false,
	        id: 'root',
	        text:t,
			expanded:true,//展开
	        leaf:false
		};		
	}
}

// 添加树结点
function addExtTreeNode(tree, n, autoselect) {	
	var pnode = tree.getNodeById(n.pid);
	if(pnode==null){
		pnode = tree.getRootNode();
	}
	pnode.appendChild(n);
	pnode.expand();
	if(autoselect==null || autoselect==true){
		var node = tree.getNodeById(n.id);
		if(node){
			node.select();  
			node.fireEvent('click', node);
		}
	} 	
}

// 添加树结点(中间插入)
function insertExtTreeNode(tree, n, refnode, autoselect) {
	var pnode = tree.getNodeById(n.pid);
	if(pnode==null){
		pnode = tree.getRootNode();
	}
	pnode.insertBefore(n, refnode.nextSibling);
	if(autoselect==null || autoselect==true){
		var node = tree.getNodeById(n.id);
		if(node){
			node.select();  
			node.fireEvent('click', node);
		}
	} 	
	pnode.expand();  	
}

// 打开进度条执行窗口
function openExtProgress(progress, parent){
	var content = "操作进行中...";
	if(progress){
		content = "操作进行中，"+progress+"...";
	}
	if(!parent){
	    Ext.Msg.wait(content,'请等待', { 
	        interval:300,     //进度条的时间间隔 
	        increment:10      //进度条的分段数量 
	    }); 
    }else{
	    parent.Ext.Msg.wait(content,'请等待', { 
	        interval:300,     //进度条的时间间隔 
	        increment:10      //进度条的分段数量 
	    });     
    }
}

//关闭进度条执行窗口
function closeExtProgress(parent){
	if(!parent){
		Ext.Msg.hide();
	}else{
		parent.Ext.Msg.hide();
	}
}

// N秒后，自动隐藏消息框
function msgAutoHide() {
    setTimeout("Ext.Msg.hide()",1000);
}

// 返回“文字方向”的格式
function getFontDirectstore(){
    return new Ext.data.SimpleStore({
         fields:["pname","pvalue"],
         data: [["fontdirect:0;","横向"],
         		["fontdirect:1; table-layout:fixed; word-break: break-all; width:1px; text-align:center;","纵向"]]
    });		
}

// 返回业务有效状态的格式
function getStatusstore(){
    return new Ext.data.SimpleStore({
         fields:["pname","pvalue"],
         data: [["-1","全部"],["0","正办理"],
         		["1","有效"],["2","历史"]]
    });	
}

// 返回组织机构下拉表项
function getOrgCombobox(fn){
	return new Ext.data.JsonStore({
		url:ROOTPATH+"/orgAction.do?method=orgcmb&ajax=3",
		root: 'map.obj',
	    fields : ['id','name'],
	    listeners:{
	     	load:function(){
	     		if(this.getRange().length>0){
	     			if(typeof(fn)!='undefined'){
	     				fn(this.getRange()[0].data);
	     			}
	     		}
	     	}	
	    }
	});
}

// 返回行政区域下拉表项
function getDistrictCombobox(fn){
	return new Ext.data.JsonStore({
		url:ROOTPATH+"/orgAction.do?method=districtcmb&ajax=3",
		root: 'map.obj',
	    fields : ['id','name'],
	    listeners:{
	     	load:function(){
	     		if(this.getRange().length>0){
	     			if(typeof(fn)!='undefined'){
	     				fn(this.getRange()[0].data);
	     			}
	     		}
	     	}	
	    }
	});
}
	
// 返回所有元素值是否都为空
function getIsExtEmpty(objs){
	var rs = true;
	Ext.each(objs, function(obj){
		if(Ext.getCmp(obj.id).getValue()!="" && Ext.getCmp(obj.id).getValue()!="0"){
			rs = false;
			return;
		}
	});
	return rs;
}

// 返回元素值
function getExtElementValue(objs, obj){
	Ext.each(objs, function(o){
		if(Ext.getCmp(o.id).getXType()=="combo" || Ext.getCmp(o.id).getXType()=="stonecombo"){
			if(Ext.getCmp(o.id).showall==true){
				obj[Ext.getCmp(o.id).field]=Ext.getCmp(o.id).getValue()=="0"?"":Ext.getCmp(o.id).getValue();
			}else{
				obj[Ext.getCmp(o.id).field]=Ext.getCmp(o.id).getValue();
			}
		}else if(Ext.getCmp(o.id).getXType()=="checkbox"){
			obj[Ext.getCmp(o.id).field]=Ext.getCmp(o.id).getValue()==true?1:0;
		}else if (Ext.getCmp(o.id).getXType()=="datefield"){
			obj[Ext.getCmp(o.id).field]=Ext.getCmp(o.id).getRawValue();
		}else{
			obj[Ext.getCmp(o.id).field]=Ext.getCmp(o.id).getValue();
		}
	});
}

//设置元素值
function setExtElementValue(objs, obj){
	Ext.each(objs, function(o){
		for(var i in obj){
			if(Ext.getCmp(o.id).field==i){
				Ext.getCmp(o.id).setValue(obj[Ext.getCmp(o.id).field]);
				break;
			}
		}
	});
}

//打印表单
function execPrintBill(title, url, params){
	openTab(getToptabs(),title,ROOTPATH+"/businessAction.do?method=viewer&ajax=0&url="+url+"&reporttype=pdf&params="+encodeURIComponent(params),'printbill_'+url, false);
}

// 返回顶部页签控件
function getToptabs(){
	return top.tabs; 	
}

// 返回Ext窗体Y坐标 
function getExtWinTop(){
	return 20;
}

// 返回Ext窗体宽度 
function getExtWinWidth(){
	return 600;
}

// 返回Ext窗体高度 
function getExtWinHeight(){
	return 400;
}