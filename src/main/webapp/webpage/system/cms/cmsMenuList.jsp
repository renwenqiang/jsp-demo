<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">
	<div region="center" style="padding:1px;">
		<t:datagrid name="cmsMenuList" title="栏目管理"  treegrid="true" pagination="false" actionUrl="cmsMenuController.do?datagrid" idField="id">
			<t:dgCol title="栏目名称" field="name" treefield="text" width="300"></t:dgCol>
			<t:dgCol title="栏目类型" field="type" treefield="fieldMap.type" width="300" dictionary="cms_menu"></t:dgCol>
			<t:dgCol title="连接地址" field="href" treefield="fieldMap.href" width="300" ></t:dgCol>
			<t:dgCol title="操作" field="opt"></t:dgCol>
			<t:dgDelOpt title="删除" url="cmsMenuController.do?del&id={id}" />
			<t:dgToolBar title="录入" icon="icon-add" url="cmsMenuController.do?add" funname="onlineDocSort"></t:dgToolBar>
			<t:dgToolBar title="编辑" icon="icon-edit" url="cmsMenuController.do?addorupdate" funname="onlineDocSort"></t:dgToolBar>
		</t:datagrid>
		
	</div>
</div>

<script type="text/javascript">
	
 	function getSelectNodeId(){
 		var rowData = $('#cmsMenuList').datagrid('getSelected');
		if (rowData) {
			return rowData.id;
		}else{
			return "";
		}
 	}
	 		
	function onlineDocSort(title, url, id) {
		var rowData = $('#' + id).datagrid('getSelected');
		if (rowData) {
			url += '&id=' + rowData.id;
		}
		add(title, url, 'cmsMenuList', 1000, 500);
	}
</script>