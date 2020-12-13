function listfiles(cgformId, tableId){
  		$.ajax({
 		   type: "post",
 		   url: "cmsArticleController.do?getFiles&cgformId=" + cgformId,
 		   success: function(data){
 			 	 var arrayFileObj = $.parseJSON(data).obj;
 				$.each(arrayFileObj,function(n,file){
	 				var tr = $("<tr id="+ file.fileKey + " style=\"border:1px solid white;height:28px;background-color:lightgray;\"></tr>");
	 				var td_title = $("<td width='70%' style='padding:5px;text-align:left;'>" + file.title + "." + file.extend + "</td>");
	 				//var td_extend = $("<td style='padding:5px;'>" + file.extend + "</td>");
	 		  		var td_download = $("<td style='padding:5px;'><a href=\"commonController.do?viewFile&fileid=" + file.fileKey + "&subclassname=org.jeecgframework.web.cgform.entity.upload.CgUploadEntity\" title=\"下载\">下载</a></td>");
	 		  		var td_view = $("<td style='padding:5px;'><a href=\"javascript:void(0);\" onclick=\"openwindow('预览','commonController.do?openViewFile&fileid=" + file.fileKey + "&subclassname=org.jeecgframework.web.cgform.entity.upload.CgUploadEntity','fList',700,500)\">预览</a></td>");
	 		  		var td_del = $("<td style='padding:5px;'><a href=\"javascript:void(0)\" class=\"jeecgDetail\" onclick=\"delFile('cgUploadController.do?delFile&id=" + file.fileKey + "', '"+file.fileKey+"')\">删除</a></td>");
	 		  		
	 		  		tr.appendTo($("#"+tableId));
	 		  		td_title.appendTo(tr);
	 		  		//td_extend.appendTo(tr);
	 		  		td_download.appendTo(tr);
	 		  		td_view.appendTo(tr);
	 		  		td_del.appendTo(tr);
 			 	}); 
 		   }
 		});
  	}
  	function doActionCallbackBeforeUpload(data){
  		if($(".uploadify-queue-item").length<=0){
  			 $.Hidemsg();	
  			 //var win = frameElement.api.opener;
			 //win.reloadTable();
			 //win.tip(data.msg);
			 //frameElement.api.close();
			 return true;
  		}
  		if (data.success == true) {
  			if(data.obj!=null && data.obj!='undefined' && data.obj.id!=null && data.obj.id!='undefined' && $.trim(data.obj.id)!=""){
				$("#cgFormId").val(data.obj.id);
				upload();
				return true;
			}
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
  	}
  	function delFile(url, fileKey){
  		$.ajax({
  		   type: "post",
  		   url: url,
  		   success: function(data){
 			 	var fileObj = $.parseJSON(data);
  				if(fileObj.success){
  					$("tr[id="+fileKey+"]").remove(); // 删掉行
  					$.messager.alert('提示', fileObj.msg);
					$.Hidemsg();
  				}else{
  					$.messager.alert('错误', "附件删除失败!");
					$.Hidemsg();
  				}
  		   }
  		});
  	}