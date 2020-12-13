<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">
	<div region="center" style="padding:1px;">
		<t:datagrid name="cmsSiteList" checkbox="true" fitColumns="false" title="站点信息" actionUrl="cmsSiteController.do?datagrid" idField="id" fit="true" queryMode="group">
			<t:dgCol title="主键"  field="id"  hidden="true"  queryMode="single"  width="120"></t:dgCol>
			<t:dgCol title="创建人名称"  field="createName"  hidden="true"  queryMode="single"  width="120"></t:dgCol>
			<t:dgCol title="创建日期"  field="createDate"  hidden="true"  queryMode="single"  width="120"></t:dgCol>
			<t:dgCol title="修改人名称"  field="updateName"  hidden="true"  queryMode="single"  width="120"></t:dgCol>
			<t:dgCol title="修改日期"  field="updateDate"  hidden="true"  queryMode="single"  width="120"></t:dgCol>
			<t:dgCol title="站点名称"  field="siteName"    queryMode="single"  width="120"></t:dgCol>
			<t:dgCol title="公司电话"  field="companyTel"   queryMode="single"  width="120"></t:dgCol>
			<t:dgCol title="站点logo"  field="siteLogo"    queryMode="single"  image="true" imageSize="100,50" ></t:dgCol>
			<t:dgCol title="操作" field="opt" width="100"></t:dgCol>
			<t:dgDelOpt title="删除" url="cmsSiteController.do?doDel&id={id}" />
			<t:dgToolBar title="录入" icon="icon-add" url="cmsSiteController.do?goAdd" width="1100" height="500" funname="add"></t:dgToolBar>
			<t:dgToolBar title="编辑" icon="icon-edit" url="cmsSiteController.do?goUpdate" funname="update" width="1100" height="500"></t:dgToolBar>
			<t:dgToolBar title="批量删除"  icon="icon-remove" url="cmsSiteController.do?doBatchDel" funname="deleteALLSelect"></t:dgToolBar>
			<t:dgToolBar title="查看" icon="icon-search" url="cmsSiteController.do?goUpdate" funname="detail"  width="1100" height="500"></t:dgToolBar>
		</t:datagrid>
	</div>
</div>
<script src = "webpage/cms/site/cmsSiteList.js"></script>		
