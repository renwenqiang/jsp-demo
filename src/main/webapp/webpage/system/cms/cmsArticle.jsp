<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>信息管理</title>
	<t:base type="jquery,easyui,tools,DatePicker"></t:base>
	
		<link rel="stylesheet" href="plug-in/uploadify/css/uploadify.css" type="text/css" />
		<script type="text/javascript" src="plug-in/uploadify/jquery.uploadify-3.1.js"></script>
	<link type="text/css" rel="stylesheet" href="plug-in/cms/css/appmsg_edit.css" />
	<link type="text/css" rel="stylesheet" href="plug-in/cms/css/jquery.fileupload.css" />
	<link type="text/css" rel="stylesheet" href="plug-in/bootstrap/css/bootstrap.min.css" />
	<script type="text/javascript" src="plug-in/ckfinder/ckfinder.js"></script>
	
	<!--fileupload-->
	<script type="text/javascript" src="plug-in/cms/js/vendor/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="plug-in/cms/js/load-image.min.js"></script>
	<script type="text/javascript" src="plug-in/cms/js/jquery.fileupload.js"></script>
	<script type="text/javascript" src="plug-in/cms/js/jquery.fileupload-process.js"></script>
	<script type="text/javascript" src="plug-in/cms/js/jquery.fileupload-image.js"></script>
	<script type="text/javascript" src="plug-in/cms/js/jquery.iframe-transport.js"></script>
	<!--UEditor-->
	<script type="text/javascript"  charset="utf-8" src="plug-in/ueditor/ueditor.config.js"></script>
	<script type="text/javascript"  charset="utf-8" src="plug-in/ueditor/ueditor.all.min.js"></script>
	  
	<!--建议手动加在语言，避免在ie下有时因为加载语言失败导致编辑器加载失败-->
	<!--这里加载的语言文件会覆盖你在配置项目里添加的语言类型，比如你在配置项目里配置的是英文，这里加载的中文，那最后就是中文-->
	<script type="text/javascript" charset="utf-8" src="plug-in/ueditor/lang/zh-cn/zh-cn.js"></script>
	<script type="text/javascript">
		//编写自定义JS代码
		/*jslint unparam: true, regexp: true */
		/*global window, $ */
		$(function () {
		    'use strict';
		    // Change this to the location of your server-side upload handler:
		    var url = 'cmsFileUploadController.do?upload',
		        uploadButton = $('<button/>')
		            .addClass('btn btn-primary')
		            .prop('disabled', true)
		            .text('上传中...')
		            .on('click', function () {
		                var $this = $(this), data = $this.data();
		                $this.off('click').text('正在上传...').on('click', function () {
		                        $this.remove();
		                        data.abort();
		                });
		                data.submit().always(function () {
		                    $this.remove();
		                });
		            });
		    $('#fileupload').fileupload({
		        url: url,
		        dataType: 'json',
		        autoUpload: false,
		        acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
		        maxFileSize: 2000000, // 2 MB
		        disableImageResize: /Android(?!.*Chrome)|Opera/
		            .test(window.navigator.userAgent),
		        previewMaxWidth: 290,
		        previewMaxHeight: 160,
		        previewCrop: true
		    }).on('fileuploadadd', function (e, data) {
		        $("#files").text("");
		        data.context = $('<div/>').appendTo('#files');
		        $.each(data.files, function (index, file) {
		            //var node = $('<p/>').append($('<span/>').text(file.name));
		            //fileupload
		            var node = $('<p/>');
		            if (!index) {
		                node.append('<br>').append(uploadButton.clone(true).data(data));
		            }
		            node.appendTo(data.context);
		        });
		    }).on('fileuploadprocessalways', function (e, data) {
		        var index = data.index,
		            file = data.files[index],
		            node = $(data.context.children()[index]);
		        if (file.preview) {
		            node.prepend('<br>').prepend(file.preview);
		        }
		        if (file.error) {
		            node
		                .append('<br>')
		                .append($('<span class="text-danger"/>').text(file.error));
		        }
		        if (index + 1 === data.files.length) {
		            data.context.find('button')
		                .text('上传')
		                .prop('disabled', !!data.files.error);
		        }
		    }).on('fileuploadprogressall', function (e, data) {
		        var progress = parseInt(data.loaded / data.total * 100, 10);
		        $('#progress .progress-bar').css(
		            'width',
		            progress + '%'
		        );
		    }).on('fileuploaddone', function (e, data) {
		        console.info(data);
		        var  file = data.files[0];
		        //var delUrl = "<a class=\"js_removeCover\" onclick=\"return false;\" href=\"javascript:void(0);\">删除</a>";
		        $("#imgName").text("").append(file.name);
		        $("#imageName").val(file.name);
		        $("#progress").hide();
				var d =data.result;
				if (d.success) {
					var link = $('<a>').attr('target', '_blank').prop('href', d.attributes.viewhref);
		        	$(data.context.children()[0]).wrap(link);
		        	console.info(d.attributes.viewhref);
		        	$("#imageHref").val(d.attributes.url);
				}else{
					var error = $('<span class="text-danger"/>').text(d.msg);
		            $(data.context.children()[0]).append('<br>').append(error);
				}
		    }).on('fileuploadfail', function (e, data) {
		        $.each(data.files, function (index, file) {
		            var error = $('<span class="text-danger"/>').text('File upload failed.');
		            $(data.context.children()[index])
		                .append('<br>')
		                .append(error);
		        });
		    }).prop('disabled', !$.support.fileInput)
		        .parent().addClass($.support.fileInput ? undefined : 'disabled');
		    
		    //编辑时初始化图片预览
		    var name = "${cmsArticlePage.title}", imageHref = "${cmsArticlePage.imageHref}";
		    if(name != ""){
		        $("#imageTitle").html(name);
		    }
		    if(imageHref != ""){
		        $("#imageShow").html('<img src="${cmsArticlePage.imageHref}" width="100%" height="160" />');
		    }
		});
		function setimageTitle(obj){
		 $("#imageTitle").html($(obj).val());
		}
		
		$(function(){
			var publishFlag = "${cmsArticlePage.publish}";
			if(publishFlag == ""){
				$("#publish").val("N");
			}else{
				$("#publish").val("${cmsArticlePage.publish}");
			}
			var publishFirst = $("#publish option:first");
			if(typeof(publishFirst) != "undefined")
				publishFirst.remove();
		})
		$(function() {
			$('#cmsMenuTree').combotree({
				url : 'cmsMenuController.do?tree',
				panelHeight : 200,
				width : 157,
				onClick : function(node) {
					$("#columnId").val(node.id);
				}
			});
			//清除combotree选择
			$("#cleanCmsMenuTree").click(function(){
				$('#cmsMenuTree').combotree("clear");
				$('#columnId').val("");
			});
			
			//初始化showImage的值
			if("${cmsArticlePage.showImage }" == ""){
				$("#showImage").val("N");
			}
			
			if($("#showImage").val() == "Y"){
				$("#showImageCheckbox").attr("checked","checked");
			}
			$("#showImageCheckbox").change(function(){
				if($(this).is(':checked')){
					$("#showImage").val("Y");
				}else{
					$("#showImage").val("N");
				}
			});
		});
	</script>
</head>
<body>
 	<div class="main_bd">
		<div class="media_preview_area">
			<div class="appmsg editing">
				<div class="appmsg_content" id="js_appmsg_preview">
					<h4 class="appmsg_title">
						<a target="_blank" href="javascript:void(0);" onclick="return false;" id="imageTitle">标题</a>
					</h4>
					<div class="appmsg_info">
						<em class="appmsg_date"></em>
					</div>
					<div id="files" class="files">
						<i class="appmsg_thumb default" id="imageShow">栏目图片</i>
					</div>
					 <div id="progress" class="progress">
				        <div class="progress-bar progress-bar-success"></div>
				    </div>
					<p class="appmsg_desc"></p>
				</div>
			</div>
		</div>
 		<div class="media_edit_area" id="js_appmsg_editor">	
			<div class="appmsg_editor" style="margin-top: 0px;">
		 		<div class="inner" style="width: 750px;">
					<t:formvalid formid="formobj" dialog="true" usePlugin="password" layout="table" action="cmsArticleController.do?save" callback="jeecgFormFileCallBack@Override">
					<input id="id" name="id" type="hidden" value="${cmsArticlePage.id }">
					<input type="hidden" name="imageName" id="imageName" value="${cmsArticlePage.imageName}">
					<table cellpadding="0" cellspacing="1" class="formtable">
						<tr>
							<td align="right">
								<label class="Validform_label">标题:</label>
							</td>
							<td class="value">
								<input class="inputxt" id="title" name="title" style="width: 200px" value="${cmsArticlePage.title}" datatype="*" onblur="setimageTitle(this)">
								<span class="Validform_checktip"></span>
								<label class="Validform_label" style="display: none;">标题</label>
							</td>
						</tr>
						<tr>
							<td align="right">
								<label class="Validform_label">所属栏目:</label>
							</td>
							<td class="value">
								<input id="columnId" name="columnId" style="display: none;" value="${cmsArticlePage.columnId}">
								<input id="cmsMenuTree" value="${cmsArticlePage.columnId}">
								<a href="javascript:void(0);" id="cleanCmsMenuTree" >[清除]</a>
								
								<span class="Validform_checktip"></span>
								<label class="Validform_label" style="display: none;">所属栏目</label>
							</td>
						</tr>
						<tr>
							<td align="right">
								<label class="Validform_label">图片链接:</label>
							</td>
							<td class="value">
								<span class="btn btn-success fileinput-button">
							        <i class="glyphicon glyphicon-plus"></i>
							        <span>浏览</span>
							        <!-- The file input field used as target for the file upload widget -->
							        <input id="fileupload" type="file" name="files[]" multiple>
							    </span>
							    <input id="imageHref" name="imageHref" type="hidden" nullmsg="请添加图片" value="${cmsArticlePage.imageHref}">
							    <span id="imgName"></span>
							    <span><input type="checkbox" id="showImageCheckbox" style="margin-top: 0px;"/></span>
							    <input type="hidden" name="showImage" id="showImage" value="${cmsArticlePage.showImage }"/>
							    <span style="padding-left: 1px;">封面图片显示在正文中</span>
								<span class="Validform_checktip"></span>
								<label class="Validform_label" style="display: none;">图片链接</label>
							</td>
						</tr>
						<tr>
							<td align="right">
								<label class="Validform_label">摘要:</label>
							</td>
							<td class="value">
								<textarea class="inputxt" id="summary" name="summary" style="width: 250px" datatype="*">${cmsArticlePage.summary}</textarea>
								<span class="Validform_checktip"></span>
								<label class="Validform_label" style="display: none;">摘要</label>
							</td>
						</tr>
						<!-- 是否发布 -->
						<tr>
							<td align="right">
								<label class="Validform_label">是否发布:</label>
							</td>
							<td class="value">
								<t:dictSelect field="publish" typeGroupCode="yesorno" hasLabel="false" defaultVal="${cmsArticlePage.publish}" id="publish"></t:dictSelect>
								<span class="Validform_checktip"></span>
								<label class="Validform_label" style="display: none;">是否发布</label>
							</td>
						</tr>
						<!-- 作者 -->
						<tr>
							<td align="right">
								<label class="Validform_label">作者:</label>
							</td>
							<td class="value">
								<input class="inputxt" id="author" name="author" style="width: 150px" value="${cmsArticlePage.author}" />
								<span class="Validform_checktip"></span>
								<label class="Validform_label" style="display: none;">作者</label>
							</td>
						</tr>
						<!-- 标签 -->
						<tr>
							<td align="right">
								<label class="Validform_label">标签(逗号分隔):</label>
							</td>
							<td class="value">
								<textarea class="inputxt" id="label" name="label" style="width: 150px">${cmsArticlePage.label}</textarea>
								<span class="Validform_checktip"></span>
								<label class="Validform_label" style="display: none;">标签</label>
							</td>
						</tr>
						<tr>
							<td align="right">
								<label class="Validform_label">正文:</label>
							</td>
							<td class="value">
								 <textarea name="content" id="content" style="width: 650px;height:300px">${cmsArticlePage.content}</textarea>
							    <script type="text/javascript">
							        var editor = UE.getEditor('content');
							    </script>
							</td>
						</tr>
						<tr>
							<td align="right">
								<label class="Validform_label">附件:</label>
							</td>
							<td class="value">
								<table id="fileTable"></table>
								<table></table>
								<script type="text/javascript">
									var serverMsg="";
									$(function(){
										$("#file").uploadify({
											buttonText:'添加文件',
											auto:false,
											progressData:'speed',
											multi:true,
											height:25,
											overrideEvents:['onDialogClose'],
											fileTypeDesc:'文件格式:',
											queueID:'filediv_file',
											fileTypeExts:'*.rar;*.zip;*.doc;*.docx;*.txt;*.ppt;*.xls;*.xlsx;*.html;*.htm;*.pdf;*.jpg;*.gif;*.png',
											fileSizeLimit:'15MB',
											swf:'plug-in/uploadify/uploadify.swf',	
											uploader:'cgUploadController.do?saveFiles&jsessionid='+$("#sessionUID").val()+'',
											onUploadStart : function(file) { 
												var cgFormId=$("input[name='id']").val();
												$("#file").uploadify("settings", "formData", {
													'cgFormId':cgFormId,
													'cgFormName':'cms_article',
													'cgFormField':'file'
												});
											} ,
											onQueueComplete : function(queueData) {
												 var win = frameElement.api.opener;
												 win.reloadTable();
												 win.tip(serverMsg);
												 frameElement.api.close();
											},
											onUploadSuccess : function(file, data, response) {
												var d=$.parseJSON(data);
												if(d.success){
													var win = frameElement.api.opener;
													serverMsg = d.msg;
												}
											},
											onFallback: function() {
							                    tip("您未安装FLASH控件，无法上传图片！请安装FLASH控件后再试")
							                },
							                onSelectError: function(file, errorCode, errorMsg) {
							                    switch (errorCode) {
							                    case - 100 : tip("上传的文件数量已经超出系统限制的" + $("#file").uploadify('settings', 'queueSizeLimit') + "个文件！");
							                        break;
							                    case - 110 : tip("文件 [" + file.name + "] 大小超出系统限制的" + $("#file").uploadify('settings', 'fileSizeLimit') + "大小！");
							                        break;
							                    case - 120 : tip("文件 [" + file.name + "] 大小异常！");
							                        break;
							                    case - 130 : tip("文件 [" + file.name + "] 类型不正确！");
							                        break;
							                    }
							                },
							                onUploadProgress: function(file, bytesUploaded, bytesTotal, totalBytesUploaded, totalBytesTotal) {}
										});
									});
								</script>
								<span id="file_uploadspan"><input type="file" name="file" id="file" /></span> 
								<div class="form" id="filediv_file"></div>
							</td>
						</tr>
					</table>
					</t:formvalid>
				</div>
				<i class="arrow arrow_out" style="margin-top: 0px;"></i>
				<i class="arrow arrow_in" style="margin-top: 0px;"></i>
			</div>
		</div>
	</div>
 </body>
 <script type="text/javascript">
		  	//加载 已存在的 文件
		  	$(function(){
		  		var table = $("#fileTable");
		  		var cgFormId=$("input[name='id']").val();
		  		$.ajax({
		  		   type: "post",
		  		   url: "cmsArticleController.do?getFiles&id=" +  cgFormId,
		  		   success: function(data){
		  			 var arrayFileObj = jQuery.parseJSON(data).obj;
		  			 
		  			$.each(arrayFileObj,function(n,file){
		  				var tr = $("<tr style=\"height:34px;\"></tr>");
		  				var td_title = $("<td>" + file.title + "</td>")
		  		  		var td_download = $("<td><a href=\"commonController.do?viewFile&fileid=" + file.fileKey + "&subclassname=org.jeecgframework.web.cgform.entity.upload.CgUploadEntity\" title=\"下载\">下载</a></td>")
		  		  		var td_view = $("<td><a href=\"javascript:void(0);\" onclick=\"openwindow('预览','commonController.do?openViewFile&fileid=" + file.fileKey + "&subclassname=org.jeecgframework.web.cgform.entity.upload.CgUploadEntity','fList',700,500)\">预览</a></td>");
		  		  		var td_del = $("<td><a href=\"javascript:void(0)\" class=\"jeecgDetail\" onclick=\"del('cgUploadController.do?delFile&id=" + file.fileKey + "',this)\">删除</a></td>");
		  		  		
		  		  		tr.appendTo(table);
		  		  		td_title.appendTo(tr);
		  		  		td_download.appendTo(tr);
		  		  		td_view.appendTo(tr);
		  		  		td_del.appendTo(tr);
		  			 });
		  		   }
		  		});
		  	})
	  		function jeecgFormFileCallBack(data){
	  			if (data.success == true) {
					uploadFile(data);
				} else {
					if (data.responseText == '' || data.responseText == undefined) {
						$.messager.alert('错误', data.msg);
						$.Hidemsg();
					} else {
						try {
							var emsg = data.responseText.substring(data.responseText.indexOf('错误描述'), data.responseText.indexOf('错误信息'));
							$.messager.alert('错误', emsg);
							$.Hidemsg();
						} catch(ex) {
							$.messager.alert('错误', data.responseText + '');
						}
					}
					return false;
				}
				if (!neibuClickFlag) {
					var win = frameElement.api.opener;
					win.reloadTable();
				}
	  		}
	  		function upload() {
				$("#file").uploadify('upload', '*');		
			}
			
			var neibuClickFlag = false;
			function neibuClick() {
				neibuClickFlag = true; 
				$('#btn_sub').trigger('click');
			}
			function cancel() {
				$("#file").uploadify('cancel', '*');
			}
			function uploadFile(data){
				if(!$("input[name='id']").val()){
					if(data.obj!=null && data.obj!='undefined'){
						$("input[name='id']").val(data.obj.id);
					}
				}
				if($(".uploadify-queue-item").length>0){
					upload();
				}else{
					if (neibuClickFlag){
						alert(data.msg);
						neibuClickFlag = false;
					}else {
						var win = frameElement.api.opener;
						win.reloadTable();
						win.tip(data.msg);
						frameElement.api.close();
					}
				}
			}
	  	</script>