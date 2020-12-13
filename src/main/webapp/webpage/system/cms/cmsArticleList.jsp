<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">
	<div region="center" style="padding:1px;">
		<t:datagrid name="cmsArticleList" title="信息管理" actionUrl="cmsArticleController.do?datagrid" idField="id" fit="true"  queryMode="group">
			<t:dgCol title="编号" field="id" hidden="true"></t:dgCol>
			<t:dgCol title="标题" field="title" query="true"></t:dgCol>
			<t:dgCol title="所属栏目" field="columnId" dictionary="cms_menu,id,name" query="true"></t:dgCol>
			<t:dgCol title="摘要" field="summary"></t:dgCol>
			<t:dgCol field="publish" title="是否发布" dictionary="yesorno"></t:dgCol>
			<t:dgCol field="publishDate" title="发布时间" formatter="yyyy-MM-dd hh:mm:ss"></t:dgCol>
			<t:dgCol field="author" title="作者" ></t:dgCol>
			<t:dgCol title="创建时间" field="createDate" formatter="yyyy-MM-dd hh:mm:ss"></t:dgCol>
			<t:dgCol title="图片名称" field="imageName" hidden="true"></t:dgCol>
			<t:dgCol title="图片地址" field="imageHref" hidden="true"></t:dgCol>
			<t:dgCol title="显示封面" field="showImage" replace="是_Y,否_N"></t:dgCol>
			<t:dgCol title="操作" field="opt"></t:dgCol>
			<t:dgConfOpt title="发布" url="cmsArticleController.do?publish&id={id}" message="您需要发布该文章吗?"/>
			<t:dgFunOpt funname="popMenuLink(id)" title="地址"></t:dgFunOpt>
			<t:dgDelOpt title="删除" url="cmsArticleController.do?del&id={id}" />
			<t:dgToolBar title="录入" icon="icon-add" url="cmsArticleController.do?addorupdate" funname="add" width="1500" height="500"></t:dgToolBar>
			<t:dgToolBar title="编辑" icon="icon-edit" url="cmsArticleController.do?addorupdate" funname="update" width="1500" height="500"></t:dgToolBar>
			<t:dgToolBar title="查看" icon="icon-search" url="cmsArticleController.do?addorupdate" funname="detail" width="1500" height="500"></t:dgToolBar>
		</t:datagrid>
	</div>
</div>
<script src="plug-in/clipboard/ZeroClipboard.js"></script>
<script>
/**
*	弹出
*/
function popMenuLink(id){
	$.dialog({
		content: "url:cmsArticleController.do?popmenulink&id="+id,
        drag :false,
        lock : true,
        title:'链接',
        opacity : 0.3,
        width:600,
        height:80,drag:false,min:false,max:false
	}).zindex();
}
</script>