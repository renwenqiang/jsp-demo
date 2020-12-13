<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">
	<div region="center" style="padding:1px;">
		<t:datagrid name="cmsStyleList"  fitColumns="false" title="站点模板" actionUrl="cmsStyleController.do?datagrid" idField="id" fit="true" queryMode="group">
			<t:dgCol title="主键"  field="id"  hidden="true"  queryMode="single"  width="120"></t:dgCol>
			<t:dgCol title="创建人名称"  field="createName"  hidden="true"  queryMode="single"  width="120"></t:dgCol>
			<t:dgCol title="创建日期"  field="createDate"  hidden="true"  queryMode="single"  width="120"></t:dgCol>
			<t:dgCol title="修改人名称"  field="updateName"  hidden="true"  queryMode="single"  width="120"></t:dgCol>
			<t:dgCol title="修改日期"  field="updateDate"  hidden="true"  queryMode="single"  width="120"></t:dgCol>
			<t:dgCol title="模板名称"  field="templateName"  hidden="false"  queryMode="single"  width="120"></t:dgCol>
			<t:dgCol title="模板路径"  field="templateUrl"  hidden="false"  queryMode="single"  width="120"></t:dgCol>
			<t:dgCol title="预览图"  field="reviewImgUrl" image="true" imageSize="100,50" hidden="false"  queryMode="single" ></t:dgCol>
			<t:dgCol title="操作" field="opt" width="100"></t:dgCol>
			<t:dgDelOpt title="删除" url="cmsStyleController.do?doDel&id={id}" />
			<t:dgToolBar title="录入" icon="icon-add" url="cmsStyleController.do?goAdd" funname="add" width="1100" height="500"></t:dgToolBar>
			<t:dgToolBar title="修改" icon="icon-search" url="cmsStyleController.do?goUpdate" funname="update" width="1100" height="500"></t:dgToolBar>
			<t:dgToolBar title="查看" icon="icon-search" url="cmsStyleController.do?goUpdate" funname="detail" width="1100" height="500"></t:dgToolBar>
			<t:dgToolBar title="下载选中模板" icon="icon-putout" funname="downtemplate"></t:dgToolBar>
			<t:dgToolBar title="下载默认模板" icon="icon-putout" funname="downdefaulttemplate"></t:dgToolBar>
		</t:datagrid>
	</div>
</div>
<script src = "webpage/cms/cmsStyleList.js"></script>	
<script type="text/javascript">
	//模板下载
	function downtemplate() {
		var row = $('#cmsStyleList').datagrid('getSelected');
		if(!row){
			alert("请选择模板。");
			return ;
		}
		JeecgExcelExport("cmsStyleController.do?downloadTemplate&id="+row.id,"cmsStyleList");
	}
	//模板下载
	function downdefaulttemplate() {
		JeecgExcelExport("cmsStyleController.do?downloadDefaultTemplate&url=default","cmsStyleList");
	}
</script>