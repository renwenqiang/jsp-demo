/**-----用户有关的方法------**/
/**
 * 查询出来的是大写的字段
 * @param data
 * @returns
 */
function toLowerCaseData(data) {
	var dd = [];
	if(data && data.length >0) {
		for(var i =0;i<data.length;i++) {
			var obj = {};
			for(var m in data[i]) {
				obj[m.toLowerCase()] = data[i][m];
			}
			dd.push(obj);
			
		}
		return dd;
	} else {
		return data;
	}
}

/**
 * 重置表单各项，简单的项
 */
function resetForm(id) {
	document.getElementById("" + id).reset();
}

/**
 * 通过ajax调用时loading效果 
 */
function ajaxLoading(){   
    $("<div class=\"datagrid-mask\"></div>").css({display:"block",width:"100%",height:$(window).height()}).appendTo("body");   
    $("<div class=\"datagrid-mask-msg\"></div>").html("数据加载中......").appendTo("body").css({display:"block",left:($(document.body).outerWidth(true) - 190) / 2,top:($(window).height() - 45) / 2});   
 }   
/**
 * 通过ajax调用时loading取消
 */
function ajaxLoadEnd(){   
     $(".datagrid-mask").remove();   
     $(".datagrid-mask-msg").remove();               
} 

//对象序列化功能
function serializeObject(a){  
    var o,h,i,e;  
    o={};  
    h=o.hasOwnProperty;  
    for(i=0;i<a.length;i++){  
        e=a[i];  
        if(!h.call(o,e.name)){  
            o[e.name]=e.value;  
        }  
    }  
    return o;  
};

var ad=function(id,departname,description,parentdepartid,org_code,org_type,mobile,fax,address,orderindex,pid){
	var item=dict[id]=$.extend(dict[id]||{},{id:id,departname:departname,description:description,parentdepartid:parentdepartid
		,org_code:org_code,org_type:org_type,mobile:mobile,fax:fax,address:address,orderIndex:orderindex,pid:pid,operate:'[<a style="text-decoration:none;cursor:pointer;" onclick="queryUsersByDepart(\''+ id +'\',\'\')">查看成员</a>]'});
	var pItem=dict[pid]=dict[pid]||{}
		,list=pItem.children=pItem.children||[];
	list.push(item);
	all.push(item);
	
};

var fnGrep=function(items,state){
	return $.grep(items,function(item){if(item.children)item.state=state; 
	return item.pid==0;}); //item.children!=null;});
};

/**
 * 加载树节点
 * @param id
 */
function loadDeparttree(id,pid,hasNullNode){
    $.ajax({
        url : 'framework/query.do?search&k=user_org_tree',
        type : 'post',
        data : {pid:pid||''},
        cache : true,
        success : function(data) {
        	//加载树
        	var dd = toLowerCaseData(data.data);
        	if(hasNullNode && hasNullNode==true)
        		dd.splice(0,0,{id:'',code:'',text:'',pid:null,iconcls:'icon-duka'});//加入一个空节点，不然选择了一个项目之后无法取消选择了。
        	$('#' + id).combotree({data:dd, editable:true,
        		onClick: function(node){
                    var branchId = node.id;
                    if(branchId == '') {
                    	return false;//空节点
                    }
                    var childrenArr = $('#' + id).combotree('tree').tree('getChildren',node.target); 
                    if(childrenArr.length>1){  
                        return false;  
                    }  
                    $.ajax({  
                        type:'GET',  
                        data:{"pid":branchId},  
                        url:'framework/query.do?search&k=user_org_tree',  
                        dataType:'json',  
                        success:function(data){  
                            if(data.data.length==0){  
                                 //$('#'+id).combotree('showPanel');  
                                 return;  
                            }  
                            $('#'+id).combotree('tree').tree('append', {  
                                parent: node.target,  
                                data: toLowerCaseData(data.data)
                              });    
                              
                              $('#'+id).combotree('tree').tree("expandAll",node.target);   
                              $('#'+id).combotree('showPanel');  
                                
                        },  
                        error:function(){  
                            alert('机构信息加载失败');  
                        }  
                    });  
                },  
                 onLoadSuccess: function (node, data) {  
                    //回显时，默认选中的值  
//                          $('#'+id).combotree('tree').tree("collapseAll");   
                 } 	
        	
        	} );
        }
	});
}