<%@ page contentType="text/html; charset=UTF-8"%>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=10" />
	<%@include file="/sunzbase.jsp"%>
	<link rel="stylesheet" type="text/css" href="webpage/framework/system/recv/extjs3.0/resources/css/ext-all.css" />	
	<script type="text/javascript" src="webpage/framework/system/recv/extjs3.0/adapter/ext/ext-base-debug.js"></script> 
	<script type="text/javascript" src="webpage/framework/system/recv/extjs3.0/ext-all.js"></script>
	<script type="text/javascript" src="webpage/framework/system/recv/extjs3.0/ext-lang-zh_CN.js"></script> 
	<script type="text/javascript" src="webpage/framework/system/recv/ext/parameters.js"></script>
	<script type="text/javascript" src="webpage/framework/system/recv/ext/upload/data-view-plugins.js"></script>
	<script type="text/javascript" src="webpage/framework/system/recv/ext/Ext.ux.stone.extClass.js"></script>	
	<script type="text/javascript" src="webpage/framework/system/recv/ext/extobjs.js"></script>
	<script type="text/javascript">
	  	$={
	  		each:function(d,fn){return Ext.each(d,function(n,i){return fn(i,n);});},
	  		extend:Ext.apply
	  	};
	</script>
</head>
<z:dict items="SYS005"></z:dict>
<body onunload="filenotify.Close();saveLocale();">
  <object id="filenotify"
	classid="clsid:3D2FD361-225B-4F6B-AB70-2D1FF8C02FFC"
	style="width: 0px; height: 0px"
	codebase="cab/certprint.cab#Version=1,0,0,0">
  </object>
  <script type="text/javascript">
	var jobid='${JOBID}';//${oinsid};
	var isReadOnly =false;
		//!(parent.hasOperation('OP004')||parent.hasOperation('OP012'));//false;//
	var cookieEnabled = navigator.cookieEnabled;
	var rectems= ${receiveData};
	var restems= ${resourceData};
	var cstTreeNodes=[];
	//构建树节点
	$.each(rectems,function(i,t){
		var treeNode={},zchild=[];
		treeNode.ID=t.ID;
		treeNode.text=t.NAME;
		treeNode.leaf=false;
		treeNode.expanded=true;
		treeNode.checked=false;
		treeNode.iconCls=null;
		treeNode.draggable=false;
		treeNode.data={};
		treeNode.data.map=t;
		//构建子类节点
		$.each(restems,function(i,tt){
			if(tt.RECVID==t.ID){
				console.log(tt.ID);
				var childNode={},node={};
				childNode.id=tt.ID;
				node.ID=tt.ID;
				node.shortname=tt.NAME;
				node.type=tt.TYPE;
				node.filename='${downloadRootPath}' + tt.PATH.replace(/\\/g,"/");
				//node.fullpath=tt.PATH;
				node.time=tt.TIME;
				childNode.data=node;
				childNode.pid=t.ID;
				childNode.leaf=true;
				zchild.push(childNode);
			}
		});
		treeNode.children=zchild;
		cstTreeNodes.push(treeNode);
	});	
	//var cstTreeNodesJson = '[{"id":"R_101704806","text":"新收件2","data":{"ntype":"WRECVNODEKEY","map":{"scans":0,"oinsid":1748784,"recvid":101704806,"sequence":0,"pages":1,"copys":1,"name":"新收件2","rtype":"A007001"}}},{"id":"R_101704807","text":"新收件","data":{"ntype":"WRECVNODEKEY","map":{"scans":0,"oinsid":1748784,"recvid":101704807,"sequence":0,"pages":1,"copys":1,"name":"新收件","rtype":"A007001"}},"children":[{"id":"I_16116640","text":"第1页","data":{"sequence":1,"ntype":"WRECVIMGNODEKEY","filename":"http://113.9.163.24:5399/digitalestate/image_load.action?id=16116640","shortname":"8597509F127445EAB6BFB3C31B0AE11C.jpg","fullpath":"Photo/2015/10/13/101704807"},"children":[],"pid":"R_101704807","leaf":true,"expanded":true,"iconCls":null,"draggable":true}],"pid":"0","leaf":false,"expanded":true,"checked":false,"iconCls":null,"draggable":false}]'; //'${receives}';
	 //'[{"id":"R_101704806","text":"新收件2","data":{"ntype":"WRECVNODEKEY","map":{"scans":0,"oinsid":1748784,"recvid":101704806,"sequence":0,"pages":1,"copys":1,"name":"新收件2","rtype":"A007001"}}},{"id":"R_101704807","text":"新收件","data":{"ntype":"WRECVNODEKEY","map":{"scans":0,"oinsid":1748784,"recvid":101704807,"sequence":0,"pages":1,"copys":1,"name":"新收件","rtype":"A007001"}},"children":[{"id":"I_16116640","text":"第1页","data":{"sequence":1,"ntype":"WRECVIMGNODEKEY","filename":"http://113.9.163.24:5399/digitalestate/image_load.action?id=16116640","shortname":"8597509F127445EAB6BFB3C31B0AE11C.jpg","fullpath":"Photo/2015/10/13/101704807"},"children":[],"pid":"R_101704807","leaf":true,"expanded":true,"iconCls":null,"draggable":true}],"pid":"0","leaf":false,"expanded":true,"checked":false,"iconCls":null,"draggable":false}]';
	var maxSize= 20;
	//
	var editWidth=768,editHeight=500;
	var pmOpenInTop={id:'openInTop',text:'在最上层查看',handler:function(){
		var curHref=window.location.href;
		if(top.ImageViewer!=null)top.ImageViewer.close();
		var w=new Ext.Window({shadow:false,width:editWidth,height:editHeight,html:'<iframe src="'+curHref.replace("recvAction","recveShow")+'" width="100%" height="100%" frameborder="0" scrolling="auto"></iframe>'});
		top.ImageViewer=w;
		w.show(top);
	}};
	
	function getData(_d){
		var datas=[];
		$.each(_d,function(i,t){
			var data=[];
			data.push(t.value);
			data.push(t.text);
			datas.push(data);
		});
		return datas;
	}
	var type={};
	$.each(D.get("SYS005"),function(i,t){
		type[t.value]=t.text;
	});
	var storeProvince = new Ext.data.ArrayStore({
	       fields : ['value','text'],  
	       data : getData(D.get("SYS005"))//[['yj','原件'], ['fyj','复印件']]
	   });
	function showRecv(recv){
		var form = new Ext.form.FormPanel({
			labelAlign: 'right',
		    frame:true,
		    labelWidth:80,
		    items: [{
				layout:'column', 
		        border:false,
		        labelSeparator:':',
		        defaults:{layout:'form',border:false,columnWidth:1},
		        items:[	                    
		            {items: [{xtype:'textfield', fieldLabel:'收件名称', name:'formfield', field:'name', value:recv.NAME, allowBlank:false, anchor:'98%'}]},
		            {items: [{xtype:'combo', valueField:'value',displayField:'text',emptyText:'请选择',store:storeProvince,fieldLabel:'收件类型', name:'formfield', field:'type', triggerAction:'all',value:recv.TYPE,allowBlank:false, anchor:'98%', mode:'local',
		            	listeners: {  
		                    'select': function(combo, record, index)  
		                    {  
		                      return record.data.value;
		                    }}
		            	}]},
		            {items: [{xtype:'numberfield', fieldLabel:'收件份数', name:'formfield', field:'copynum', value:recv.COPYNUM, anchor:'98%'}]},
		            {items: [{xtype:'numberfield', fieldLabel:'收件页数', name:'formfield', field:'pagenum', value:recv.PAGENUM, anchor:'98%'}]},
		            {items: [{xtype:'numberfield', fieldLabel:'扫描数', name:'formfield', field:'scannum', value:recv.SCANNUM, readOnly:false, anchor:'98%'}]},
		            {items: [{xtype:'numberfield', fieldLabel:'收件顺序', name:'formfield', field:'orderindex', value:recv.ORDERINDEX, readOnly:false, anchor:'98%'}]},
		            {items: [{xtype:'textfield', fieldLabel:'收件标识', name:'formfield', field:'id', value:recv.ID, hidden:true, hideLabel:true, anchor:'98%'}]},
		            {items: [{xtype:'textfield', fieldLabel:'流程标识', name:'formfield', field:'jobid', value:recv.JOBID, hidden:true, hideLabel:true, anchor:'98%'}]}
		        ]
		    }]
		});
		var winEdit=new Ext.Window({
			title:'收件信息',
			layout:'fit',
			width:400,height:245,
			modal: true,shadow: true,
			items:[form],
			buttonAlign:'center',
			buttons:[{
				text : "确定",
				width:70,
				iconCls:'save',
				handler : function() {
					var obj = new Object();
					getExtElementValue(Ext.query("*[name=formfield]"), obj);
					if(recv.ID==null) delete obj.id;
					doExtAjax(
						'framework/datatable.do?'+(recv.ID==null?'add':'save')+'&t=T_S_RECEIVE_INSTANCE',obj,    
						function(resp){
							if(recv.ID==null){
								addReceive(resp.data);	
							}else{
								selectedNode.setText(resp.data.NAME);
								selectedNode.receiveInfo=resp.data;
							}						
							winEdit.close();
						}
					);
				}
			},{
				text : "退出",
				width:70,
				iconCls:'return',
				handler : function() {
					winEdit.close();
				}
			}]			
		}).show();
	}
				
	function editRecvs(){
		var allRecv=[];
		Ext.each(nodeRoot.childNodes,function(nRecv){
			var info=nRecv.receiveInfo;
			info.scans=nRecv.childNodes.length;
			allRecv.push(info);
		});
		var recvStore = new Ext.data.JsonStore({
			fields:["SCANNUM","JOBID","ID","PAGENUM","COPYNUM","NAME","TYPE","ORDERINDEX"],
			data:allRecv,
			sortInfo:{field:'ORDERINDEX',direction:'ASC'}
		});	
		//排序
		var orderEditor=new Ext.form.NumberField();
		/* orderEditor.addListener("change",function(neditor, nv, ov ){
			var len=recvStore.getCount();
			if(nv>len)nv=len;
			if(nv<1)nv=1;
			neditor.setValue(nv);
			
			var row=neditor.gridEditor.row;
			recvStore.getAt(row).data.ORDERINDEX=nv;
			var nrow=nv>ov?nv:nv-1;
			for(var i=0,oi=1;i<len;i++){
				if(i==nrow)oi++;
				if(i==row)continue;
				recvStore.getAt(i).data.NAME=oi++;				
			};
			recvStore.sort("ORDERINDEX","ASC");
		}); */
		var recvColm = new Ext.grid.ColumnModel( [
		    {header : "收件名称",dataIndex : "NAME",editor:{xtype:'textfield'}},
		    {header : "扫描数",dataIndex : "SCANNUM",width:35,editor:{xtype:'numberfield'}}, 
		    {header : "页数",dataIndex : "PAGENUM",width:30,editor:{xtype:'numberfield'}},
		    {header : "份数",dataIndex : "COPYNUM",width:30,editor:{xtype:'numberfield'}}, 
		    {header : "收件类型",dataIndex : "TYPE",width:50,renderer:function(v){return type[v];},
		    		editor:{xtype:'combo',editable:false,triggerAction:'all',
		    		store:getData(D.get("SYS005"))}},
		    {header : "收件顺序",dataIndex : "ORDERINDEX",width:30,editor:{xtype:'numberfield'}}, 
		    {header : "JOBID",dataIndex : "JOBID",hidden:true},
		    {header : "ID",dataIndex : "ID",hidden:true}
		]);
		
		var recvGrid=new Ext.grid.EditorGridPanel({   		      		  	
	   		autoWidth:true,id:'recvGrid',
	   		frame : true,clicksToEdit:1,	
	   		autoScroll:true,closable:true,
	   		viewConfig:{forceFit:true},
			store : recvStore,
			columnLines:true,
			cm : recvColm	
		});
		
		var gridWindow = new Ext.Window( {
			width:580,height:400,
            shadow:false,
			id:'gridWindow',
			resizable : true,
			autoDestroy : false,
			layout:'fit',
			items : [recvGrid],
			buttons:[{
				text : "确定",
				iconCls:'save',
				handler : function() {
					var obj=[],recvs =[];
					for (var i=0;i<recvStore.getCount();i++){
						var newo={};
						for(var v in recvStore.getAt(i).data){
							newo[v]=recvStore.getAt(i).data[v];
						}
						recvs.push(newo);
					}
					$.each(recvs,function(i,t){
						$.each(rectems,function(i,tt){
							if(t.ID==tt.ID){
								if(!(t.NAME==tt.NAME&&t.PAGENUM==tt.PAGENUM&&t.SCANNUM==tt.SCANNUM&&t.TYPE==tt.TYPE&&t.COPYNUM==tt.COPYNUM&&t.ORDERINDEX==tt.ORDERINDEX)){
									obj.push(t);
								}
							}
						});
					});
					$.each(obj,function(i,t){
						doExtAjax('framework/datatable.do?save&t=T_S_RECEIVE_INSTANCE',t,function(resp){
							if(i==obj.length-1){
								gridWindow.close();
								window.location.reload();
							}
						});
					});
				
				}
			},{
				text : "退出",
				width:70,
				iconCls:'return',
				handler : function() {
					gridWindow.close();
				}
			}]		     
		});	
		if(top.ImageViewer!=null)top.ImageViewer.close();
		top.ImageViewer=gridWindow;
		gridWindow.show(top);
	}
	
	var selectedNode=null;
	var nodeRoot=new Ext.tree.TreeNode({text:'收件列表',draggable:false,id:'root',expanded:true});		
	var tree = new Ext.tree.TreePanel({
		id:'ext-tree-panel',
		region: 'west',
		width:160,split:true,
		root: nodeRoot,
		autoScroll:true,
		enableDD: true,//isReadOnly?false:true,//是否支持拖拽效果 
		tbar:[{
			id:'upload',
			text:'上传到服务器',
			hidden:isReadOnly || !cookieEnabled,
			width:70,
			iconCls:'upload',
			disabled:true,
			handler:function(){
				upload();
			}
		}],
		selectDefault:function(nRefer){
			var nDefault=null;
			if(nRefer){
				nDefault=nRefer.previousSibling||nRefer.nextSibling||nRefer.parentNode;
			}else{
				Ext.each(nodeRoot.childNodes,function(n){
					nDefault=n;
					if(n.childNodes.length>0){
						n.expand();
						nDefault=n.childNodes[0];
						return false;
					}
				});
			}
			if(nDefault)nDefault.fireEvent("click",nDefault);
		},
		listeners: {
			click: function(n) {
				selectedNode=n;//tree.getSelectionModel().getSelectedNode();
				if(n.scanInfo){
					showScan(n.scanInfo.filename,n.scanInfo.ID);
				}
			},
			contextmenu:function(node,e){
				node.select();
				selectedNode=node;
				e.preventDefault(); //阻止浏览器默认右键菜单
				this.treeRightMenu.showAt(e.getXY()); //是该节点被选中状态
				var node = this.getSelectionModel().getSelectedNode();
				Ext.getCmp('addRecv').disable();
				Ext.getCmp('delRecv').disable();
				Ext.getCmp('modifyRecv').disable();
				Ext.getCmp('delScan').disable();
				Ext.getCmp('editRecv').disable();
				if(node.scanInfo){
					Ext.getCmp('delScan').enable();
				}else if(node.receiveInfo){
					Ext.getCmp('delRecv').enable();
					Ext.getCmp('modifyRecv').enable();					
				}else{
					Ext.getCmp('addRecv').enable();
					Ext.getCmp('editRecv').enable();
				}           
			},
			nodedragover:function(e){
				return e.dropNode.scanInfo!=null && e.target.receiveInfo!=null && e.point=='append'; // 仅支持从类型1到类型2
            },
			nodedrop:function(e){
				var n=e.dropNode;
				if(n.scanInfo && !n.scanInfo.isNew){
					var nTo=e.target;
					if(n.scanInfo.recvid!=nTo.receiveInfo.recvid){
						n.scanInfo.recvid=nTo.receiveInfo.recvid;
						doExtAjax('recvAction.do?method=adjustscan&ajax=1',
								{data:Ext.util.JSON.encode({rimgid:n.scanInfo.id,recvid:n.scanInfo.recvid})}
							);
					}
				}
	        }
		}
	});
	tree.treeRightMenu=isReadOnly?
			new Ext.menu.Menu({items:[pmOpenInTop]}):
			new Ext.menu.Menu({
					items:[pmOpenInTop,'-',{
						id:'editRecv',
						text:'收件编辑',
						iconCls:'add',
				        handler:function(){
				        	editRecvs();
						}
					},'-',{
						id:'addRecv',
						text:'新增收件',
						iconCls:'add',
				        handler:function(){showRecv({NAME:'新收件',TYPE:'yj',COPYNUM:1,SCANNUM:0,PAGENUM:1,ID:null,JOBID:'${JOBID}'});
						}
					}
					,'-',{
				    	id:'modifyRecv',
				    	text:'修改收件',
				    	iconCls:'modify',
				       	handler:function(){
				        	showRecv(selectedNode.receiveInfo);
						}
					},'-',{
				    	id:'delRecv',
				    	text:'删除收件',
				    	iconCls:'delete',
				       	handler:function(){
							doExtConfirm("访问","您确定要删除收件吗？",function(){
								deleteNode(selectedNode);
							});
						}
					},'-',{
				    	id:'delScan',
				    	text:'删除扫描件',
				    	iconCls:'delete',
				       	handler:function(){
							doExtConfirm("访问","您确定要删除收件吗？",function(){
								deleteNode(selectedNode);
							});
						}
					}]
				});
	var dictNode={};			
	var addReceive=function(o){
		var nReceive=nodeRoot.appendChild(new Ext.tree.TreeNode({
				text:o.NAME
			}));
		nReceive.receiveInfo=o;
		dictNode[o.ID]=nReceive;
		return nReceive;
	};
	var initTree=function(){
		Ext.each(cstTreeNodes,function(n){
			var o=n.data.map;
			var nReceive=addReceive(o);
			var index=1;
			Ext.each(n.children,function(sub){
				var oSub=sub.data;
				oSub.id=sub.id.substr(2);
				var nScan=nReceive.appendChild(new Ext.tree.TreeNode({
					text:'第'+(index++)+'页'
				}));
				nScan.scanInfo=oSub;
				nScan.scanInfo.recvid=o.recvid;
			});
		});
		
	};
	window.useBase64=getCookie("useBase64");
	var getLocalImage=function(file){
		return window.useBase64?('data:image/gif;base64,'+filenotify.ToBase64(file)):file;
	};
	var addScan=function(file){
		if(selectedNode==null||selectedNode==nodeRoot)return;
		var n=(selectedNode.scanInfo?selectedNode.parentNode:selectedNode);
		var nScan= n.appendChild(new Ext.tree.TreeNode({
			text:'第'+(n.childNodes.length+1)+'页',
			iconCls:'add'
		}));
		nScan.scanInfo={filename:getLocalImage(file),file:file,isNew:true,sequece:n.childNodes.length};
		nScan.parentNode.expand();
		nScan.fireEvent("click",nScan);
		updateState(true);
		return nScan;
	};
	var deleteNode=function(n,fn){
		if(!n)return;
		var cb=function(e){
			updateState();
			if(fn)fn(e);
		};
		var doDelete=function(){
			tree.selectDefault(n);
			n.remove();
			cb(n);
		};	
		if(n.receiveInfo){
			var all=n.childNodes.length,succeed=0;
			var delSelf=function(){
				doExtAjax('framework/receive.do?deleteRecv',
						{id:n.receiveInfo.ID},
						function(resp){
							doDelete();
						}
					);
				};
			if(all==0)delSelf();
			// 必须重新构造数组，否则each不能正常工作 
			var allSub=[];
			Ext.each(n.childNodes,function(sub){allSub.push(sub);});
			
			Ext.each(allSub,function(sub){
				deleteNode(sub,function(arg){
					succeed++;
					if(succeed==all){
						delSelf();
					}
				});
			});
		}else{
			var info=n.scanInfo;
			if(!info.isNew){
				doExtAjax(
						'framework/receive.do?deleteScan',{id:n.scanInfo.ID},
					function(resp){
							console.log(resp);
						doDelete();
					});
			}else{
				doDelete();
			}
		}
	};

	var getEditInfos=function(){
		var allNew=[],allEdit=[],allInfo=[];
		Ext.each(nodeRoot.childNodes,function(nReceive){
			var rInfo=nReceive.receiveInfo;
			var index=0;
			Ext.each(nReceive.childNodes,function(nScan){
				var sInfo=nScan.scanInfo;
				if(sInfo.isNew){
					allInfo.push({node:nScan,recvid:rInfo.ID});
					allNew.push({
						recvid:rInfo.ID,
						file:sInfo.filename,
						filename:sInfo.file
					});
				}else{
					if(sInfo.sequence!=index){
						allEdit.push(Ext.apply({node:nScan},sInfo,{sequence:index}));
					}
				}
				index++;
			});
		});
		
		return {News:allNew,Edits:allEdit,Infos:allInfo};
	};
	var setUseBase64=function(useBase64){
		window.useBase64=useBase64;
		setCookie("useBase64",useBase64);
		var news=getEditInfos().News;
		Ext.each(news,function(n){
			n.node.scanInfo.filename=getLocalImage(n.node.scanInfo.file);
		});
		if(selectedNode.scanInfo&&selectedNode.scanInfo.isNew) 
			selectedNode.fireEvent("click",selectedNode);
	};
	var upload=function(){
		var eInfo=getEditInfos(), allNew=eInfo.News, allInfo=eInfo.Infos;
		if(allNew.length==0){return;}
		var count=allNew.length,current=0,errCount=0,bandCount=0,dealCount=0;		
		var pg=Ext.Msg.progress("正在上传","正在准备..."," ");
		var newDeal=function(){
			var d={};
			doExtAjax('framework/receive.do?upload',{data:JSON.stringify(allNew)},
			function(resp){
				if(resp.success){
					for(var i=0;i<allInfo.length;i++){
						var o= allInfo[i];
						try{filenotify.DeleteFile(o.file);}catch(e){}
						if(resp.data[i].recvid==o.recvid){
							o.node.scanInfo=resp.data[i];
							if(selectedNode==o.node){o.node.fireEvent("click",o.node);}
						}
					}
					updateState(false);
				}
			});
		};
		newDeal();
	};
		
var pImage=new Ext.Panel({
	region:'center',
	layout:'fit'
});
var showScan=function(url,id){
	pImage.removeAll();
	var script=id?
			('window.eleImage=this;window.orgSrc=window.orgSrc||this.src;var winEdit=window.open(\'receive.do?imageEdit&id='+id+'\',null,\'resizable=yes,alwaysRaised=yes,location=no,menubar=no,titlebar=no,toolbar=no,z-look=yes\');winEdit.owner=window;')
			:'alert(\'请先上传再浏览\');';
	//url=id?('image_load.action?id='+id):url;
	pImage.add({html:'<img width="90%" src="'+url+'" style="margin: 15px 5% 5px 5%;cursor:hand" title="双击打开新窗口显示" ondblclick="'+script+'" onerror="this.src=\'webpage/framework/system/recv/images/error.jpg\'" />'});
	pImage.doLayout();
};
this.refreshImage=function(){
	window.eleImage.src=window.orgSrc+((window.orgSrc||'').indexOf("?")>-1?"&":"?")+"time="+new Date().getTime();
};

var onFileAdded=function(fInfo){
	if(!selectedNode)return;
	Ext.Msg.show({title:"检测到新扫描件",msg:"正在处理，请等待处理完成后再拍下一张！",closable :false});
	var len=fInfo.Length,file=fInfo.FullName;
	if(len>maxSize*1000000){
		msgbox("","当前文件大小过大，请重新扫描！",2000);
	}else{
		addScan(file);
	}
	Ext.Msg.hide();
};
var watchFile=function(path){
	if(!filenotify.listened){ // 只监听一次
		var band=filenotify.attachEvent||filenotify.addEventListener;
		band.call(filenotify,"FileAdded",onFileAdded);
		band.call(filenotify,"OnFileAdded",onFileAdded);
		filenotify.listened=true;
	}
	var info= filenotify.Watch(path);
	var free=info.TotalFreeSpace,
		strFree=free>1000000000?( Math.round(free/1000000000)+"G"):( Math.round(free/1000000)+"M");
		var t = '扫描监控中...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'
			+'<a href="javascript:void(0);" onclick="var path=filenotify.Browse()||getCookie(\'notifypath\');setCookie(\'notifypath\',path);watchFile(path);">监控目录：</a>'+path
	 		+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;磁盘空间剩余：'+strFree
			+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;文件大小限制：'+maxSize+"M"
			+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" onclick="watchFile(getCookie(\'notifypath\'));">监控失灵?</a>'
			+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" onclick="setUseBase64(!window.useBase64);">本地文件不显示?</a>';
	win.setTitle(t);	
};
var initFilenotify=function(){
	if(isReadOnly)return;
	
	//filenotify.InitFtp('${ip}', '${port}', '${username}', '${password}');
	filenotify.Filter="*.jpg";
	var path = getCookie("notifypath");
	if(!path){
		msgbox('提示','没有指定文件扫描路径，请选择扫描仪、相机、高拍仪等设备的扫描路径', 2000, function(){
			path =filenotify.Browse();
			setCookie("notifypath",path);
			watchFile(path);
		});					
	}else{
		watchFile(path);
	}
};

var saveLocale=function(){
	var all=[];
	Ext.each(getEditInfos().News,function(info){
		all.push({rid:info.recvid,file:info.filename});
	});
	setCookie("Unupload_"+jobid,Ext.encode(all));	
};
var loadLocal=function(){
	var all=Ext.decode(getCookie("Unupload_"+jobid)||"[]");
	Ext.each(all,function(info){
		selectedNode=dictNode[info.rid];
		addScan(info.file);
	});
};
var updateState=function(state){
	Ext.getCmp("upload").setDisabled(state!=null?!state:(getEditInfos().News.length==0));
};
var win;
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget ="side";
	initTree();
	win= new Ext.Window({
		title:'扫描监控未启动',
		closable:false,collapsible:true,resizable:false,maxinizable:false,maximized:true,
		layout:'border',
		items:[
			tree,
			pImage
		]
	});
	win.show();	
	loadLocal();
	tree.selectDefault();
	initFilenotify();
});

function setCookie(name,value,day)
{
    var exp  = new Date();
    exp.setTime(exp.getTime() + (day||30)*24*60*60*1000);
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}
function getCookie(name)
{
    var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
     if(arr != null) return unescape(arr[2]); return null;

}

</script>
	</body>
</html>	