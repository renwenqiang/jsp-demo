/**
 * 
 */
var qs=function(n){var reg=new RegExp("[?&]"+n+"=([^&]*)");var m=window.location.search.match(reg);return m?decodeURIComponent(m[1]):"";};
var jobkey=qs('jobkey'),stepkey=qs('stepkey');
var queryUrl='framework/query.do?search&k=queryStepUiresource&jobkey='+jobkey+'&stepkey='+stepkey,
	saveUrl="framework/datatable.do?save&t=T_S_UIRESOURCE_STEP",
	deleteUrl="framework/datatable.do?delete&t=T_S_UIRESOURCE_STEP",
	fnAdd=function(uid,type,fn){
		$.post("framework/authtag.do?addToStep",{
			jobkey:jobkey,
			stepkey:stepkey,
			resourceid:uid,
			controlType:type
		},fn);
	};
$(function(){
	$('<span>流程：'+jobkey+',环节：'+stepkey+'</span>').appendTo($("tr","#fSearch")[0].insertCell(6));
});