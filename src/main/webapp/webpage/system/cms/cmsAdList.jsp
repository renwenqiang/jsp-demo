<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">
	<div region="center" style="padding:1px;">
		<t:datagrid name="adList" title="首页广告" actionUrl="cmsAdController.do?datagrid" idField="id" fit="true" >
			<t:dgCol title="编号" field="id" hidden="true"></t:dgCol>
			<t:dgCol title="标题" field="title" width="20"></t:dgCol>
			<t:dgCol title="图片名称" field="imageName" hidden="true"></t:dgCol>
			<t:dgCol title="图片" field="imageHref" hidden="false" image="true" imageSize="50,30"></t:dgCol>
			<t:dgCol title="操作" field="opt"  width="100"></t:dgCol>
			<t:dgDelOpt title="删除" url="cmsAdController.do?del&id={id}" />
			<t:dgToolBar title="录入" icon="icon-add" url="cmsAdController.do?addorupdate" funname="add" width="1000" height="500"></t:dgToolBar>
			<t:dgToolBar title="编辑" icon="icon-edit" url="cmsAdController.do?addorupdate" funname="update" width="1000" height="500"></t:dgToolBar>
			<t:dgToolBar title="查看" icon="icon-search" url="cmsAdController.do?addorupdate" funname="detail" width="1000" height="500"></t:dgToolBar>
		</t:datagrid>
	</div>
</div>