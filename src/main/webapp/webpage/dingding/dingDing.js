function addwin(title,addurl,gname,width,height) {
	gridname=gname;
	createwindow(title, addurl,width,height);
}

var userId;
var ids;
function openRoleSelect(id) {
	userId=id;
	
	$.dialog.setting.zIndex = 9999; 
	$.dialog({content: 'url:userController.do?roleSelectL', zIndex: 2100, title: '用户表', lock: true, width: '400px', height: '350px', opacity: 0.4, button: [
	   {name: '确定', callback: callbackRoleSelect},
	   {name: '取消', callback: function (){}}
   ]}).zindex();
}
	
function callbackRoleSelect() {
	
	  var iframe = this.iframe.contentWindow;
	  var treeObj = iframe.$.fn.zTree.getZTreeObj("roleSelect");
	  var nodes = treeObj.getCheckedNodes(true);
	  if(nodes.length>0){
	  var ids='',names='';
	  for(i=0;i<nodes.length;i++){
	     var node = nodes[i];
	     ids += node.id+',';
	    names += node.name+',';
	 }
	
	  
	}var obj={
			userId:userId,
		    ids:ids,
		    names:names
			
	}
	  
	  $.ajax({
			url:"framework/dingUserController.do?getTongbu",
			data:obj,
			type:"post",
			dataType:"json",
			success:function(data,e){
			
				tip(data.msg);				
			},
			 error: function(e) 
			 
			 {window.unmask()}
		})
}







/*function callbackClean(){
	$('#roleid').val('');
	 $('#roleName').val('');	
}*/