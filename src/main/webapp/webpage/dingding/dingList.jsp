<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools"></t:base>
<%-- <%@include file="/sunzbase.jsp"%>
<%@ taglib prefix="t" uri="/easyui-tags"%>
<z:resource items="jquery,easyui,sunzui"></z:resource> --%>
<script src = "webpage/dingding/dingDing.js"></script>		
<div class="easyui-layout" fit="true">
<div region="center" style="padding:0px;border:0px">
   
	<t:datagrid checkbox="true" name="use" fitColumns="false" title="用户管理" actionUrl="framework/dingUser.do?getDingUserList" idField="id" fit="true" treegrid="true" pagination="false">
		<t:dgToolBar title="批量同步"  icon="icon-add" url="framework/dingUser.do?doPLTB" funname="tongbu"></t:dgToolBar> 
		<t:dgCol title="编号" field="id" treefield="id"  width="200" queryMode="single"></t:dgCol>
		<t:dgCol title="用户名" sortable="false" field="name" width="300" treefield="text" ></t:dgCol>
		
<%-- <t:dgCol title="状态" sortable="true" field="status" replace="正常_1,禁用_0,超级管理员_-1"></t:dgCol> --%>
		<%-- <t:dgCol title="操作" field="opt" width="300" ></t:dgCol>
		<t:dgFunOpt funname="SYNC(id)" title="同步用户" />
		<t:dgFunOpt funname="openRoleSelect(id)" title="选择关联人员" />
		<t:dgFunOpt funname="cancel(id)" title="取消关联" /> --%>
		
<%-- <t:dgDelOpt title="删除" url="userController.do?del&id={id}&userName={userName}" /> --%>
	</t:datagrid>
</div>
</div>
 
   <script type="text/javascript">
   function cancel(id) {
	
		confirm('framework/dingUser.do?doCancel&userid=' + id,'<t:mutiLang langKey='确定取消'></t:mutiLang>','dingList');

		/*setTimeout(function(){
			location.reload();
		},5000);*/
	}
   
   function SYNC(id) {
		
		
		confirm('framework/dingUser.do?doTongbu&userid=' + id,'<t:mutiLang langKey='确定同步'></t:mutiLang>','dingList');

		setTimeout(function(){
			location.reload();
		},5000);
	}
   
   function tongbu(title,url,gname) {
		gridname=gname;
	    var ids = [];
	    var rows = $("#"+gname).datagrid('getSelections');
	    if (rows.length > 0) {
	    	$.dialog.setting.zIndex = getzIndex(true);
	    	$.dialog.confirm('你确定同步吗?', function(r) {
			   if (r) {
					for ( var i = 0; i < rows.length; i++) {
						ids.push(rows[i].id);
					}
					$.ajax({
						url : url,
						type : 'post',
						data : {
							ids : ids.join(',')
						},
						cache : false,
						success : function(data) {
							var d = $.parseJSON(data);
							if (d.success) {
								var msg = d.msg;
								tip(msg);
								reloadTable();
								$("#"+gname).datagrid('unselectAll');
								ids='';
							}
						}
					});
				}
			});
		} else {
			alert("请选择需要同步的数据");
		}
	}
</script>


