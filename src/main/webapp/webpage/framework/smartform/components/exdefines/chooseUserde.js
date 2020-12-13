var choose_user_dialog={
    	CLOSE_CLICK:{},
    	DIALOGS:{},
    	open:function(data){
    		if(!data){
    			data={};
    			isCreate=false;
    		}else if(typeof(data)=='string'){
    			data={"id":data};
    			isCreate=false;
    		}
    		var This=this;
    		var id='';
    		if(data.id){
    			id+=data.id;
    		}
    		var zindex=0;
    		if(data.zindex){
    			zindex=data.zindex;
    		}
    		if(!This.DIALOGS[id]){
    			isCreate=true;
    			This.DIALOGS[id]=$("<div id='"+id+"' style='display:none;background-color:rgba(0,0,0,0.3);position: fixed;top: 0px;left: 0px;width: 100%;height: 100%; z-index:"+zindex+"'></div>");
    			var h='<div class="dialog2-context-div" style="margin: 0px auto;margin-top:20px;position: relative;">';
    					h+='';
    						h+='<span class="glyphicon glyphicon-remove"></span>';
    					h+='</span>';
    					h+='<div style="display:none;width: 100%;height: 100%;background-color: #fff;box-shadow: 0 0 10px rgba(0, 0, 0, .8);border-radius:2px;padding: 10px;" id="_div_'+id+'"></div>';
    				h+='</div>';
    			var $h=$(h);
    			
    			This.DIALOGS[id].append($h);
    			$(window.top.document.body).append(This.DIALOGS[id]);
    		}
    		var dialog=This.DIALOGS[id];
        	if(data.dom){
        		//debugger;
        		dialog.find("#_div_"+id).html(data.dom);
        	}
        	if(!data.width){
        		data.width=window.top.document.body.clientWidth/10*8;
        	}
        	if(!data.height){
        		data.height=window.top.document.body.clientHeight/10*8;
        	}
        	if(!data.marginTop){
        		data.marginTop=(window.top.document.body.clientHeight-data.height)/2;
        	}
        	
        	dialog.find(".dialog2-context-div").css({"width":data.width+"px","height":data.height+"px","margin-top":data.marginTop+"px"});

        	This.DIALOGS[id].find('#_div_'+id).unbind("click");
        	This.DIALOGS[id].unbind("click");
        	if(data.backgroundClick){
    			This.DIALOGS[id].on("click",'#_div_'+id,function(){
    				This.CLOSE_CLICK[id]=true;
    			});
    			This.DIALOGS[id].click(function(){
    				if(!This.CLOSE_CLICK[id])data.backgroundClick();
    				This.CLOSE_CLICK[id]=false;
    			});
        	}
        	dialog.show();
    		return dialog;
    	},
    	show:function(id){
    		this.DIALOGS[''+id].show();
    	},
    	/**
    	 * 关闭
    	 */
    	close:function(id){
    		if(id){
    			id=''+id;
    		}else{
    			id='';
    		}
    		this.DIALOGS[id].hide();
    	}
    };
var openwindow = function() {
	return {id:"choose_user_window_dialog",
		/**
		 * 打开选择表的数据框
		 * 参数:
		 * data.key 搜索字段名称 字符型
		 * data.title 窗体标题
		 * data.select 查询sql语句,用?代替搜索条件模糊查询
		 * data.searchTitle 查询字段名称
		 * data.columns easyui中的表格columns
		 * data.width 窗体宽度
		 * data.height 窗体高度
		 * data.success 回调函数
		 * data.param 查询语句参数
		 */
		open:function(data){
			var This=this;
			//if(!This.table){
			var html = '<div title="<span style=\'color:white;padding-top:5px;display:inline-block;\'>人员选择</span>" style="border-radius:5px;padding:15px;height:500px;width:680px;overflow:hidden;" data-options=""  class="easyui-window" id="choose_user_window">'
								+'<div class="" title="" style="height: 427px;width:50%;float:left;border:#e3e3e3 solid 1px;">'
								+'<span onclick="closeChooseUser()" style="position:absolute;top:8px;right:12px;color:white;cursor:pointer;font-weight:bold;">x</span>'
								+'<input class="easyui-combobox" id="choose_user_window_search" style="border:none;width:100%;height:35px;font-size:12px;" placeholder="搜索用户名或者真实姓名"/>'
							+'<div id="choose_user_window_area" style="overflow:scroll;height:350px;">'
							
							+'</div>'
							+'<div style="position:absolute;bottom:30px;text-align:center;margin:auto;width:49%;">'
							+'<a style="width:40px;height:20px;background-color:#4abdfc;border-radius:4px;padding:4px 12px;cursor:pointer;color:white;" id="choose_user_window_ok" >确定</a>&nbsp;&nbsp;'
							+'<a style="width:40px;height:20px;background-color:#2bbdae;border-radius:4px;padding:4px 12px;cursor:pointer;color:white;" id="choose_user_window_cancel" onclick="closeChooseUser()">取消</a>'
							+'</div>'
							+'</div>'
						
							+'<div id="choose_user_window_right" class="" title="" style="height: 427px;border:#e3e3e3 solid 1px;width:49%;float:left;padding-left:2px;overflow:hidden;">'
							+'<div  class="easyui-accordion" style="width:100%;height:auto;overflow:auto;">'
							+'<div id="choose_user_window_class" title="分类检索" data-options="iconCls:\'icon-search\'" style="height:427px;overflow:scroll;padding:10px;font-size:14px;border:none;">'
							+'<ul id="choose_user_window_right_list" style="">'
										
							+'</ul>'
							+'</div>'
							+'</div>'
							+'</div>'
							+'</div>';
				This.$div=$(html);
				var dialogParam={
					id:This.id,
					title:data.title,
					dom:This.$div,
					width:data.width,
					height:data.height
				};
			
				This.$div.on("click",'#choose_user_window_ok',function(){
					var datas = returnUser();
					data.success(datas);
				});	
				//$(window.top.document.body).append(html);
			choose_user_dialog.open(dialogParam);
			
		},close:function(){
			choose_user_dialog.close(this.id);
		},
	}
};


smart.define("ChooseUser",{
	name:"选择用户",
	defaults:{width:"90px",height:"30px",name:"CHOOSE_USER",otherSetting:'{mappingParams:{id:"id",realname:"realname"}}'},
	getFormValue:function(){return this.choose_user;},
	setFormValue:function(id){
		var div=this;
		$div=$(div);
		this.choose_user=id;
		//console.log("id="+id+"  "+$div.html());
		// 根据bid以及fjlx ，加载已有的附件，列出来 
	},
	customAdd:function(pele,params){
		var self=this;
		var defaultValueParams=eval('('+params["defaultValueParams"]+')');
		var div=document.createElement("div");
		pele.insertPoint?pele.insertBefore(div,pele.insertPoint):pele.appendChild(div);
		$(div).addClass(smart.selectProxyClass);
		//***********************
		//params.fjlx/path
		var width=params.width || "";
		var height=params.height || "";
		var sfalldy=params["alldy_"];
		var sendparam={};
		var sendcolumns=null;
		var zzid='';
		
		var $div=$(div);
		$div.css({"width":width,"height":height});
		var h='<div style="padding-top:10px;">';
			h+='<label class="choose_user_anniu" style="cursor:pointer;border: 1px solid #afafaf;border-radius:5px;font-size: 1.0em;padding: 5px;color:#000; background: linear-gradient(to bottom, #e8e8e8 0, #e8e8e8 100%);text-decoration: none;">选择用户</label>';
		h+='</div>';
		var $h=$(h);
		$h.find(".choose_user_anniu").click(function(){
			//alert("d");
			var dyfun=openwindow();
			
			dyfun.open(
					{
						key:'DY_NAME',
						title:'选择党员',
						path:'B_DJ_DY',
						param:sendparam,
						searchTitle:'名称',
						width:700,
						height:520,
						zindex:10000,
						columns:sendcolumns,
						success:function(data){
							div.choose_user = data;
							var formparams={"choose_user":data};
							$.extend(formparams,defaultValueParams);
							div.smartform.setFieldValues(formparams,true);
							closeChooseUser();
						}
					}
			);
			
			
			openChooseUser();
			//alert($("input['XM']").val());
		});
		$div.html($h);
		//this.customUplod();
		
		//***********************
		
		
		div.master=div;
		div.setting=params;
		//this.customUpdate(div);
		
		return div;
	},
	customUpdate:function(div,setting){
		smart.applyToElement(div.master,setting||div.setting,["src"]);
	}
});


var CHOOSE_USER_WINDOW_ID = 'choose_user_window';
var CHOOSE_USER_WINDOW_HEIGHT = 500;
var CHOOSE_USER_ARRAY = [];//以用户为基本的选择单位，返回用户id的集合
var CHOOSE_USER_CALLBACK = null;
var CHOOSE_DEPART_ARRAY = [];//选择的部门
var CHOOSE_DEPART_USER_ARRAY = [];
var CHOOSE_DEPART_LEVEL = 0;
var CHOOSE_DEPART_ORGID = {};
var CHOOSE_USER_ID = '${LOCAL_CLINET_USER.id}';
var CHOOSE_USER_NEXT_IMG = 'plug-in/designer/icons/choose_user_next.png';
var CHOOSE_USER_BACK_IMG = 'plug-in/designer/icons/choose_user_back.png';
var CHOOSE_USER_IMG_STYLE = "width:10px;height:10px;padding:3px 7px 0px 0px;";
function initChooseUser() {
	CHOOSE_USER_ARRAY = [];
	CHOOSE_DEPART_ARRAY = [];
	CHOOSE_DEPART_LEVEL = 0;
	CHOOSE_DEPART_ORGID = {};
	$("#choose_user_window_area").html("");
	formCondition();
}
function openChooseUser(callback) {
	//CHOOSE_USER_CALLBACK = callback;
	$("#choose_user_window").window({
		closed:true,closable:false,maximizable:false,minimizable:false,resizable:false,collapsible:false,draggable:false
	});
	$('#choose_user_window_class').accordion({
	    animate:false,
	});
	/*$("#choose_user_window_ok").linkbutton({
	    iconCls: 'icon-ok'
	});
	$("#choose_user_window_cancel").linkbutton({
	    iconCls: 'icon-cancel'
	});*/
	searchInput(null,true);
	
	initChooseUser();
	$("#"+CHOOSE_USER_WINDOW_ID).window("open");
	
}
function closeChooseUser() {
	$("#"+CHOOSE_USER_WINDOW_ID).window("destroy");
	//$(window.parent.document.getElementById("choose_user_window_dialog")).hide();
	$("#choose_user_window_dialog").hide();
}

function searchListener() {
	$('#choose_user_window_search').combobox('textbox').bind('keyup',function(){  
		var user_name = $(this).val();
		if(user_name && user_name.length > 0) {
			searchInput(user_name,false);
			
		}
    });  
}

//搜索框
function searchInput(user_name,isFirst) {
	
	$.ajax({
		type:'post',
		url:'framework/query.do?search&k=user_list_tb&user_real_name='+user_name,
		async:true,
		success:function(data) {
			var dd = data.data;
			var availableTags = [];
			if(dd) {
				for(var i =0;i<dd.length;i++) {
					var id = dd[i].id || dd[i].ID;
					var username = dd[i].username||dd[i].USERNAME;
					var realname = dd[i].realname||dd[i].REALNAME;
					
					availableTags.push({id,id,username:username,realname:realname});
					
				}
				/*$( "#choose_user_window_search" ).autocomplete({
			      source: availableTags
			    });*/
				if(isFirst) {
					$("#choose_user_window_search").combobox({
			            
			            data: availableTags,
			            valueField: 'id',
			            textField: 'realname',
			            editable:"true",
			            prompt:"输入用户名或者真实姓名",
			            onSelect:function(record){
			            	var data = record;
			    			var tag = userTags(data);
			    			if(tag) {
			    				CHOOSE_USER_ARRAY.push(data);
			    			}
			            },
			            filter: function (q, row) {
			                /*var ret = false;
			                //拼音
			                var spell = row['spell'];
			                if (spell && spell.indexOf(q) >= 0) {
			                    ret = true;
			                }
			                //textField
			                if (row[$(this).combobox('options').textField].indexOf(q) >= 0) {
			                    ret = true;
			                }
			                return ret;*/
			            }
			        });
					
					searchListener();
				} else {
					$("#choose_user_window_search").combobox("loadData",availableTags);
				}
				
			}
		},
		fail:function(e){
			top.$.messager.alert('失败', e, 'warning');
		}
	});
}

//第一级分类
function formCondition() {
	var html ='<li onclick="search(\'role\')" style="height:20px;padding:0px 0px 5px 15px;margin:10px 0px 10px 0px;cursor:pointer;"><img style="float:left;'+ CHOOSE_USER_IMG_STYLE +'" src="'+ CHOOSE_USER_NEXT_IMG +'"><a style="float:left;height:25px;line-height:15px;">按角色选择</a></li>';
	html+='<li onclick="search(\'org\')" style="height:20px;padding:0px 0px 0px 15px;margin:10px 0px 10px 0px;cursor:pointer;"><img style="float:left;' + CHOOSE_USER_IMG_STYLE + '" src="' + CHOOSE_USER_NEXT_IMG + '"><a style="float:left;height:25px;line-height:15px;">按组织机构选择</a></li>';
	$("#choose_user_window_right_list").html(html);
}

function search(type) {
	if(type == "role") {
		loadRole();
	} else if(type=="org") {
		loadOrg();
	} else {
		
	}
}
//第一级分类:角色:第二级
function loadRole() {
	$.ajax({
		type:'post',
		url:'framework/datatable.do?all&t=t_s_role',
		async:true,
		success:function(data) {
			formRole(data.data);
		},
		fail:function(e){
			top.$.messager.alert('失败', e, 'warning');
		}
	});
}

function formRole(data) {
	if(data&&data.length>0){
		var html = '<span style="position:absolute;top:75px;right:38px;display:inline-block;cursor:pointer;"><img style="' + CHOOSE_USER_IMG_STYLE + '" onclick="formCondition()" src="'+ CHOOSE_USER_BACK_IMG +'"></span>';
		for(var i =0;i<data.length;i++) {
			var rolename = (data[i].ROLENAME==null?data[i].rolename:data[i].ROLENAME);
			if(rolename && rolename.length >=15) {
				rolename = rolename.substring(0,15)+"..";
			}
			html += '<li onclick="listRoleUser(\''+ (data[i].ID==null?data[i].id:data[i].ID) +'\')" style="padding:10px 0px 10px 10px;cursor:pointer;"><img style="float:left;' + CHOOSE_USER_IMG_STYLE + '" src="'+ CHOOSE_USER_NEXT_IMG +'">' + rolename + '</li>';
		}
		$("#choose_user_window_right_list").html(html);
	} else {
		$("#choose_user_window_right_list").html("暂无角色");
	}
}
//第一级分类:角色:第二级:第三级
function listRoleUser(roleid) {
	
	var ajaxTimeOut = $.ajax({
		type:'post',
		url:'framework/query.do?search&k=user_list_tb',
		data:{roleid:roleid},
		async:true,
		timeout : 10000,
		beforeSend: ajaxLoading,
		success:function(data) {
			ajaxLoadEnd();
			formRoleUser(data.data);
		},
		complete : function(XMLHttpRequest,status){ //请求完成后最终执行参数
	　　　　if(status=='timeout'){//超时,status还有success,error等值的情况
			   ajaxTimeOut.abort(); //取消请求
	　　　　　   alert("请求超时");
	           ajaxLoadEnd();
	　　　　}
	　　},
		fail:function(e){
			ajaxLoadEnd();
			top.$.messager.alert('失败', e, 'warning');
		}
	});
}
function formRoleUser(data) {
	if(data&&data.length>0){
		var html = '<li onclick="addUser(\'all\',\'' + JSON.stringify(data).replace(/\"/g,"\\'") + '\',\'\',this)" style="padding:10px 0px 10px 10px;cursor:pointer;width:90%;"><span  style="display:inline-block;margin-left:4px;margin-right:3px;width:10px;height:10px;border-radius:50%;border:1px solid #c3c3c3; "></span><span>全选</span></li><span style="position:absolute;top:75px;right:38px;display:inline-block;cursor:pointer;"><img style="'+ CHOOSE_USER_IMG_STYLE +'" onclick="loadRole()" src="'+ CHOOSE_USER_BACK_IMG +'"></span>';
		for(var i =0;i<data.length;i++) {
			var id = (data[i].ID==null?data[i].id:data[i].ID);
			var realname = (data[i].REALNAME==null?data[i].realname:data[i].REALNAME);
			if(realname && realname.length >=15) {
				realname = realname.substring(0,15)+"..";
			}
			var checkHtml = "";
			
			if(checked(id)) {
				checkHtml = "background-color:#87CEFA;";
			}
			html += '<li onclick="addUser(\'single\',\''+ id +'\',\''+ realname +'\',this)" style="padding:10px 0px 10px 10px;cursor:pointer;"><span type="user" uid="'+ id +'" id="choose_user_window_right_radio_'+id+'" style="display:inline-block;margin-left:4px;margin-right:3px;width:10px;height:10px;border-radius:50%;border:1px solid #c3c3c3;'+ checkHtml +' "></span>' + realname + '</li>';
		}
		$("#choose_user_window_right_list").html(html);
	} else {
		$("#choose_user_window_right_list").html("暂无用户");
	}
}

//第一级:组织机构
function loadOrg() {
	//目前取数据是定死的，可以扩展传sqlkey参数，具体哪些查询和权限相关需要后面明确。
	var condition = "";
	var userid = loginUser.id;
	if(userid && userid != null) {
		condition = 'user_id=' + user_id;
	} else {
		condition = 'username=' + loginUser.userName;
	}
	
	$.ajax({
		type:'post',
		url:'framework/query.do?search&k=user_org_map&'+condition,
		data:{},
		async:true,
		timeout : 18000,
		beforeSend: ajaxLoading,
		success:function(data) {
			ajaxLoadEnd();
			var orgid = null;
			if(data&& data.data && data.data.length > 0 ) {
				orgid = data.data[0].DEPARTID||data.data[0].departid; 
			}
			if(orgid== null) {
				top.$.messager.alert('失败', "当前用户没有组织机构，无法查询", 'warning');
				return;
			}
			CHOOSE_DEPART_ORGID[CHOOSE_DEPART_LEVEL] = orgid;
			loadNextOrg(orgid,'next',true);
			
		},
		fail:function(e){
			ajaxLoadEnd();
			top.$.messager.alert('失败', e, 'warning');
		}
	});
}
function loadNextOrg(orgid,type,isFirst) {
	var param = "";
	if(type=="prev") {
		//console.log("prev处理之前的level"+CHOOSE_DEPART_LEVEL);
		CHOOSE_DEPART_LEVEL=CHOOSE_DEPART_LEVEL-1;
		param = CHOOSE_DEPART_ORGID[CHOOSE_DEPART_LEVEL];
		//console.log(CHOOSE_DEPART_ORGID);
		//console.log("prev当前level是:"+CHOOSE_DEPART_LEVEL+"---取到的值是"+param);
	} else {
		param = orgid;
		//console.log("next处理之前的level:"+CHOOSE_DEPART_LEVEL);
		
		
		if(isFirst) {
			
		} else	
			CHOOSE_DEPART_LEVEL=CHOOSE_DEPART_LEVEL+1;
		
		CHOOSE_DEPART_ORGID[CHOOSE_DEPART_LEVEL] = param;
		//console.log(CHOOSE_DEPART_ORGID);
		//console.log("next当前level是:"+CHOOSE_DEPART_LEVEL+"---参数值是"+param);
	}
	$.ajax({
		type:'post',
		url:'framework/query.do?search&k=user_org_tree&pid=' + param,
		data:{},
		async:true,
		timeout : 18000,
		beforeSend: ajaxLoading,
		success:function(data) {
			ajaxLoadEnd();
			formNextOrg(data.data,param);
		},
		fail:function(e){
			ajaxLoadEnd();
			top.$.messager.alert('失败', e, 'warning');
		}
	});
}
function formNextOrg(data,orgid) {
	if(data && data.length >0) {
		var backHtml = "formCondition()";
		if(CHOOSE_DEPART_LEVEL && CHOOSE_DEPART_LEVEL != 0) {
			backHtml = "loadNextOrg(\'" + orgid + "\','prev')";
		}
		
		var html = '<li onclick="addOrg(\'all\',\'' + JSON.stringify(data).replace(/\"/g,"\\'") + '\',\'\',this)" style="padding:10px 0px 10px 10px;cursor:pointer;width:90%;"><span  style="display:inline-block;margin-left:4px;margin-right:3px;width:10px;height:10px;border-radius:50%;border:1px solid #c3c3c3; "></span><span>全选</span></li><span style="position:absolute;top:75px;right:38px;display:inline-block;cursor:pointer;"><img style="'+ CHOOSE_USER_IMG_STYLE +'" onclick="'+ backHtml +'" src="'+ CHOOSE_USER_BACK_IMG +'"></span>';
		for(var i = 0;i<data.length;i++) {
			var id = data[i].ID || data[i].id;
			var departname = data[i].TEXT || data[i].text;
			var temp = departname;
			if(departname && departname.length >=15) {
				temp = departname.substring(0,15)+"...";
			}
			var checkHtml = "";
			var displayHtml = "display:block;";
			if(checked(id)) {
				checkHtml = "background-color:#87CEFA;";
				displayHtml = "display:none;";
			}
			
			html += '<li  style="padding:10px 0px 10px 10px;cursor:pointer;"><span onclick="addOrg(\'single\',\''+ id +'\',\''+ departname +'\',this)"><span name="'+ departname +'"  type="org" uid="'+id+'" id="choose_user_window_right_radio_'+id+'" style="display:inline-block;margin-left:4px;margin-right:3px;width:10px;height:10px;border-radius:50%;border:1px solid #c3c3c3;'+ checkHtml +' "></span>'
			+'<span>' + temp + '</span></span><img onclick="loadNextOrg(\''+ id +'\',\'next\')" style="float:right;'+ CHOOSE_USER_IMG_STYLE + displayHtml +'" src="' + CHOOSE_USER_NEXT_IMG + '"></li>';
		}
		
		$("#choose_user_window_right_list").html(html);
		
		//加载该org下的用户
		loadOrgUser(orgid,true);
	} else {
		//$("#choose_user_window_right_list").html("暂无内容");
		//加载用户
		loadOrgUser(orgid,false);
		
	}
	
}
function loadOrgUser(orgid,isAppended) {
	$.ajax({
		type:'post',
		url:'framework/query.do?search&k=user_list_tb&simple_departid=' + orgid,
		data:{},
		async:true,
		timeout : 18000,
		success:function(data) {
			formOrgUser(data.data,isAppended);
		},
		fail:function(e){
			top.$.messager.alert('失败', e, 'warning');
		}
	});
}
function formOrgUser(data,isAppended) {
	if(data&&data.length>0){
		var html = '';
		if(isAppended) {
			
		} else
			html +='<li onclick="addUser(\'all\',\'' + JSON.stringify(data).replace(/\"/g,"\\'") + '\',\'\',this)" style="padding:10px 0px 10px 10px;cursor:pointer;width:90%;"><span  style="display:inline-block;margin-left:4px;margin-right:3px;width:10px;height:10px;border-radius:50%;border:1px solid #c3c3c3; "></span><span>全选</span></li><span style="position:absolute;top:75px;right:38px;display:inline-block;cursor:pointer;"><img style="' + CHOOSE_USER_IMG_STYLE + '" onclick="loadRole()" src="'+ CHOOSE_USER_BACK_IMG +'"></span>';
		for(var i =0;i<data.length;i++) {
			var id = (data[i].ID==null?data[i].id:data[i].ID);
			var realname = (data[i].REALNAME==null?data[i].realname:data[i].REALNAME);
			var temp = realname;
			if(realname && realname.length >=15) {
				temp = realname.substring(0,15)+"..";
			}
			var checkHtml = "";
			
			if(checked(id)) {
				checkHtml = "background-color:#87CEFA;";
			}
			html += '<li onclick="addUser(\'single\',\''+ id +'\',\''+ realname +'\',this)" style="padding:10px 0px 10px 10px;cursor:pointer;"><span type="user" name="' + realname + '" uid="'+ id +'" id="choose_user_window_right_radio_'+id+'" style="display:inline-block;margin-left:4px;margin-right:3px;width:10px;height:10px;border-radius:50%;border:1px solid #c3c3c3;'+ checkHtml +' "></span>' + temp + '</li>';
		}
		if(isAppended)
			$("#choose_user_window_right_list").append(html);
		else
			$("#choose_user_window_right_list").html(html);
	} else {
		if(isAppended) {
			
		} else {
			var html ='<li style="padding:10px 0px 10px 10px;cursor:pointer;width:90%;"><span>暂无用户</span></li><span style="position:absolute;top:75px;right:38px;display:inline-block;cursor:pointer;"><img style="'+ CHOOSE_USER_IMG_STYLE +'" onclick="loadNextOrg(\'\',\'prev\')" src="'+ CHOOSE_USER_BACK_IMG +'"></span>';
			$("#choose_user_window_right_list").html(html);
		}
	}
}

//添加机构或者取消机构
function addOrg(type,id,realname,obj) {
	var readColor = $($(obj).find("span")[0]).css("background-color");
	if("#000000" == colorRGB2Hex(readColor)) {
		$($(obj).find("span")[0]).css("background-color","#87CEFA");
		if(type == "all") {
			$("span[id^='choose_user_window_right_radio_']").each(function(i,e){
				//判断颜色再点击
				var _readColor = $(e).css("background-color");
				if(colorRGB2Hex(_readColor) == "#000000" ) {
					$(e).parent().click();
					$(e).parent().next().css("display","none");//隐藏掉后面的图片
				}
			});
		} else if(type=="single") {
			var data = {id:id,realname:realname};
			var tag = userTags(data);
			if(tag) {
				CHOOSE_DEPART_ARRAY.push(data);
				//查询用户
				$.ajax({
					type:'post',
					url:'framework/query.do?search&k=user_list_tb&simple_departid=' + id,
					data:{},
					async:true,
					timeout : 18000,
					success:function(data) {
						var dd = data.data;
						if(dd) {
							for(var i=0;i<dd.length;i++) {
								var did = dd[i].ID||dd[i].dd;
								var drealname = dd[i].REALNAME||dd[i].realname;
								if(checkedDepartUser(did)) {
									
								} else {
									var temp = {id:did,realname:drealname};
									CHOOSE_DEPART_USER_ARRAY.push(temp);
								}
							}
						}
					}
				});
				$(obj).next().css("display","none");
			}
		} else {
			
		}
	} else {
		$($(obj).find("span")[0]).css("background-color","rgba(0,0,0,0)");
		if(type == "all") {
			$("span[id^='choose_user_window_right_radio_']").each(function(i,e){
				var id = $(e).attr("uid");
				removeUser(id);
				$(e).parent().next().css("display","block");
				//删除
				removeOrgUser(id);
			});
		} else if(type == "single"){
			removeUser(id);
			$(obj).next().css("display","block");
			removeOrgUser(id);
		} else {
			
		}
	}
	
}

//查询部门下的用户
function removeOrgUser(orgid) {
	$.ajax({
		type:'post',
		url:'framework/query.do?search&k=user_list_tb&simple_departid=' + orgid,
		data:{},
		async:true,
		timeout : 18000,
		success:function(data) {
			var dd = data.data;
			if(dd) {
				for(var i=0;i<dd.length;i++) {
					var did = dd[i].ID||dd[i].dd;
					if(checkedDepartUser(did)) {
						removeDepartUser(did);
					} else {
						
					}
				}
			}
		}
	});
}

//添加用户或者取消用户
function addUser(type,id,realname,obj) {
	var readColor = $($(obj).find("span")[0]).css("background-color");
	//改变该obj下面check的背景色
	if("#000000" == colorRGB2Hex(readColor)) {
		$($(obj).find("span")[0]).css("background-color","#87CEFA");
		if(type == "all") {//添加
			var data = $.parseJSON(id.replace(/\'/g,'"'));
			for(var i=0;i<data.length;i++) {
				var temp = {id:data[i].ID==null?data[i].id:data[i].ID,realname:(data[i].REALNAME==null?data[i].realname:data[i].REALNAME)};
				var tag = userTags(temp);
				if(tag) {
					CHOOSE_USER_ARRAY.push(temp);
					//
					$("#choose_user_window_right_radio_"+temp.id).css("background-color","#87CEFA");
				}
			}
		} else if(type=="single") {
			var data = {id:id,realname:realname};
			var tag = userTags(data);
			if(tag) {
				CHOOSE_USER_ARRAY.push(data);
			}
		} else {
			
		}
	} else {
		$($(obj).find("span")[0]).css("background-color","rgba(0,0,0,0)");
		if(type == "all") {
			var data = $.parseJSON(id.replace(/\'/g,'"'));
			for(var i=0;i<data.length;i++) {
				var temp = {id:data[i].ID==null?data[i].id:data[i].ID,realname:(data[i].REALNAME==null?data[i].realname:data[i].REALNAME)};
				removeUser(temp.id);
				
			}
		} else if(type=="single") {
			removeUser(id);
		} else {
			
		}
	}
	
}


//显示添加的用户标签
function userTags(data) {
	var html = "";
	var tag = true;
	for(var i=0;i<CHOOSE_USER_ARRAY.length;i++) {
		//
		if(data.id == CHOOSE_USER_ARRAY[i].id) {
			tag = false;
			break;
		}
		
	}
	if(tag) {
		var tagname = data.realname;
		if(tagname && tagname.length >=25) {
			tagname = tagname.substring(0,25);
		}
		html+= '<span id="choose_user_window_area_'+ data.id +'" style="display:inline-block;margin-right:4px;margin-top:2px;margin-bottom:2px;background-color:#86ceff;height:20px;line-height:20px;border-radius:3px;padding:2px;">' + tagname + '&nbsp;<span style="color:white;cursor:pointer;" onclick="removeUser(\''+ data.id +'\')">x</span></span>';
		$("#choose_user_window_area").append(html);
	}
	return tag;
}

//辅助方法
function removeUser(userid) {
	var index = -1;
	for(var i=0;i<CHOOSE_USER_ARRAY.length;i++) {
		if(userid == CHOOSE_USER_ARRAY[i].id) {
			index = i;
			break;
		}
	}
	if(index >=0) {
		CHOOSE_USER_ARRAY.splice(index,1);
		$("#choose_user_window_area_"+userid).remove();
		$("#choose_user_window_right_radio_"+userid).css("background-color","rgba(0,0,0,0)");
	} else {
		index = -1;
		var orgid = userid;
		for(var i =0;i<CHOOSE_DEPART_ARRAY.length;i++) {
			if(orgid == CHOOSE_DEPART_ARRAY[i].id) {
				index = i;
				break;
			}
		}
		if(index >= 0) {
			CHOOSE_DEPART_ARRAY.splice(index,1);
			$("#choose_user_window_area_"+orgid).remove();
			$("#choose_user_window_right_radio_"+orgid).css("background-color","rgba(0,0,0,0)");
			$("#choose_user_window_right_radio_"+orgid).parent().next().css("display","block");
		}
	}
}

function removeDepartUser(userid) {
	var index = -1;
	for(var i=0;i<CHOOSE_DEPART_USER_ARRAY.length;i++) {
		if(userid == CHOOSE_DEPART_USER_ARRAY[i].id) {
			index = i;
			break;
		}
	}
	if(index >=0) {
		CHOOSE_DEPART_USER_ARRAY.splice(index,1);
	}
}

function checked(userid) {
	var checked = false;
	for(var i =0;i<CHOOSE_USER_ARRAY.length;i++) {
		if(userid == CHOOSE_USER_ARRAY[i].id) {
			checked = true;
			return checked;
		}
	}
	for(var i=0;i<CHOOSE_DEPART_ARRAY.length;i++) {
		if(userid == CHOOSE_DEPART_ARRAY[i].id) {
			checked = true;
			return checked;
		}
	}
	return checked;
}
function checkedDepartUser(userid) {
	var checked = false;
	for(var i =0;i<CHOOSE_DEPART_USER_ARRAY.length;i++) {
		if(userid == CHOOSE_DEPART_USER_ARRAY[i].id) {
			checked = true;
			return checked;
		}
	}
	return checked;
}
function colorRGB2Hex(color) {
    var rgb = color.split(',');
    var r = parseInt(rgb[0].split('(')[1]);
    var g = parseInt(rgb[1]);
    var b = parseInt(rgb[2].split(')')[0]);
 
    var hex = "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
    return hex;
 }
function ajaxLoading(){   
    $("<div class=\"datagrid-mask\"></div>").css({display:"block",width:"100%",height:$(window).height()}).appendTo($("#choose_user_window_right_list"));   
    $("<div class=\"datagrid-mask-msg\"></div>").html("数据加载中..").appendTo($("#choose_user_window_right_list")).css({display:"block",left:300,top:250});   
 }  
function ajaxLoadEnd(){   
    $(".datagrid-mask").remove();   
    $(".datagrid-mask-msg").remove();               
} 
function returnUser() {
	indexOf();
	
	for(var i =0;i<CHOOSE_DEPART_USER_ARRAY.length;i++) {
		if(CHOOSE_USER_ARRAY.indexOf(CHOOSE_DEPART_USER_ARRAY[i])==-1){
			CHOOSE_USER_ARRAY.push(CHOOSE_DEPART_USER_ARRAY[i]);
		}
	}
	closeChooseUser();
	var userids = "";
	for(var i =0;i<CHOOSE_USER_ARRAY.length;i++) {
		userids += CHOOSE_USER_ARRAY[i].id + ",";
	}
	if(userids.length >0) {
		userids = userids.substring(0,userids.length-1);
	}
	return userids;
}
function indexOf() {
	if (!Array.prototype.indexOf){ 
		// 新增indexOf方法 
		Array.prototype.indexOf = function(item){ 
			var result = -1, a_item = null; 
			if (this.length == 0){ 
				return result; 
			} 
			for(var i = 0, len = this.length; i < len; i++){ 
				a_item = this[i]; 
				if (a_item === item){ 
					result = i; 
					break; 
				} 
			} 
			return result; 
		} 
	} 
}
