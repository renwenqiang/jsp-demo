/**
 * 
 */
var roleid=null;
var queryUrl=null,
	saveUrl="framework/datatable.do?save&t=T_S_UIRESOURCE_ROLE",
	deleteUrl="framework/datatable.do?delete&t=T_S_UIRESOURCE_ROLE",
	fnAdd=function(uid,type,fn,orig){
		if(roleid==null){
			$.messager.alert("操作提示","请先选择一种角色");
			return;
		}
		$.post("framework/datatable.do?add",{
			t:"T_S_UIRESOURCE_ROLE",
			roleid:roleid,
			resourceid:uid,
			controlType:type
		},fn);
	};

$(function(){
	//var dp=$('<div style="position:absolute;width:320px;height:64px;top:50%;left:50%;background-color:#fff;border:#f00 1px solid;border-radius:5px;"><div style="margin:0 0 5px;height:24px;background-color:#f00;color:#fff;line-height:24px;padding-left:25px;">请选择一种角色以开始配置</div><div class="c" style="padding-left:25px;"></div></div>').appendTo(document.body);
	//dp.draggable();
	//var pele=dp.find(".c")[0];
	var pele=$("tr","#fSearch")[0].insertCell(6);
	
	new sunz.Dictcombo({parent:pele,label:"目标角色",dict:"UserRole",width:240,style:"margin-left:25px",onChange:function(nv,ov){
		roleid=nv;
		tree.treegrid({url:"framework/query.do?search&k=queryRoleUiresource&roleid="+roleid});
	}});	
});