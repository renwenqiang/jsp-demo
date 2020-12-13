<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>机构管理</title>
<z:resource items="jquery,easyui,sunzui,tools,autocomplete"></z:resource>
<script type="text/javascript" src="webpage/framework/resource/js/compatible.js"></script>
<script type="text/javascript" src="webpage/system/user/js/user.js"></script>
</head>
<body>
	<div id="cc" class="easyui-layout" style="height:100%;  width:100%;  margin:0 auto;/*设置整个容器在浏览器中水平居中*/ position:absolute;">
		<div data-options="region:'center',title:'菜单列表'">
			<div data-options="region:'north'," style="width: 100%;height:6%;">
	         <form id = 'fSearch' style="margin-top: 10px;margin-left: 1%;">
	             <table style="width:95%;" cellpadding="0" cellspacing="0" >
	             	<tr>    
	                	<td align="right">
	                    	<label class="Validform_label" style="font-size:12px">
	                                                 菜单名称:
	                        </label>
	                    </td>
	                    <td class="value">
	                        <input id="menuname" name="menuname" type="text" style="width: 150px" class="inputxt"  value=''>
	                        <span class="Validform_checktip"></span>
	                        <label class="Validform_label" style="display: none;">菜单名称</label>
	                    </td>
	                    <td align="right">
	                        <label class="Validform_label" style="font-size:12px">
	                                                菜单地址:
	                        </label>
	                    </td>
	                    <td class="value">
	                        <input id="menuaddress" name="menuaddress" type="text" style="width: 150px" class="inputxt"  value=''>
	                        <span class="Validform_checktip"></span>
	                        <label class="Validform_label" style="display: none;">菜单地址</label>
	                    </td>
	                    <td align="left">
	                        <a href="javascript:void(0)" onclick="loadGrid(true)" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >查询</a>
	                        <a href="javascript:void(0)" onclick="resetForm('fSearch')"  style="margin-left:25px" class="easyui-linkbutton"  data-options="iconCls:'icon-reload'">重置</a>
	                    </td>
	                </tr>
	             </table>
	           </form>
	       	</div>
	       	
	       	<div data-options="region:'center',"  title="" style="width: 100%;height: 90%">  
	       	 	<div id="functiondatagrid" style="padding:5px 5px 5px 5px;"></div>
	       	</div>
	       	
	       	<div id="win" class="easyui-window" title="菜单操作" style="width:800px;height:540px;" data-options="modal:true" closed="true" >
				<iframe id="editwindow" style="height:calc(100% - 48px);width:100%"></iframe>
				<div id="buttons" style="margin:auto;margin-top:10px;position:relative; text-align:center;">
					<a href="javascript:void(0);" onclick="submitForm()" class="easyui-linkbutton" id="submitForm" data-options="iconCls:'icon-save'">
						<span style="font-size:12px;"><strong>保存</strong></span>
					</a> 
					<a style="margin-left:30px" href="javascript:void(0);" onclick="closeWindow()" class="easyui-linkbutton"  data-options="iconCls:'icon-cancel'"  >
						<span style="font-size:12px;"><strong>取消</strong></span>
					</a>
				</div>
			</div>
		</div>
	</div>
	
	<script type="text/javascript">
		var allfunc=[],dictfunc={};
		
		var adFunc = function(id,functionname,functionorder,functionurl,functiontype,path,pid) {
			var item=dictfunc[id]=$.extend(dictfunc[id]||{},{id:id,functionname:functionname,functionorder:functionorder,functionurl:functionurl,functiontype:functiontype
				,path:path,pid:pid});
			var pItem=dictfunc[pid]=dictfunc[pid]||{}
				,list=pItem.children=pItem.children||[];
			list.push(item);
			allfunc.push(item);
		};
		
		function loadGrid(reloaded) {
			var menuname = $("#menuname").val();
			var menuaddress = $("#menuaddress").val();
			
			var defaultCondition = "";
			if(menuname != null && ""!=menuname) {
				defaultCondition += '&functionname=' + menuname;
			}
			if(menuaddress != null && ""!=menuaddress) {
				defaultCondition += '&functionurl=' + menuaddress;
			}
			
			$.ajax({
				async: true,
			    type: "POST",
			    url: "framework/query.do?search&k=function_list_tree" + defaultCondition,//&functionname="+menuname+"&functionurl="+menuaddress
			    contentType: "application/json; charset=utf-8",
			    dataType: "json",
			    beforeSend: ajaxLoading,
			    success: function (data) {
			    	ajaxLoadEnd();
			    	allfunc=[],dictfunc={};
			        data.data.sort(function(a,b) {
			        	return a.FUNCTIONORDER - b.FUNCTIONORDER;
			        });
			        var lowerData = toLowerCaseData(data.data);
			        $.each(lowerData,function(i,d){
						adFunc(d.id,d.functionname,d.functionorder,d.functionurl,d.functiontype,d.path,d.parentfunctionid==null?0:d.parentfunctionid);
					});
			        var treedata=fnGrep(allfunc,"closed");
			        if(reloaded) {
			        	reloadGrid(treedata);
			        } else
			        	formGrid(treedata);
			    },
			    error: function (msg) {
			        alert("加载数据失败");
			        ajaxLoadEnd();
			    }
			});
		}
	
		function closeWindow() {
			$("#win").window('close', true);
		}
		
		function submitForm(action) {
			var result = $("#editwindow")[0].contentWindow.saveFunction();
			if(result){
				$("#win").window('close', true);
				loadGrid(true);
			}
		}
		
		$(function() {
			loadGrid(false);
		});
		
		function reloadGrid(treedata) {
			$("#functiondatagrid").treegrid("loadData",treedata);
		}
		
		function formGrid(treedata) {
			$("#functiondatagrid").treegrid({
					data:treedata,
					idField: "id",
				    treeField: "functionname",
				    fit:true,fitColumns:false,
					autoRowHeight:true,
					loadMsg : '数据加载中......',
				    columns:[[
					         {field:'id',hidden:true,width:50}, {field:'pid',hidden:true,width:50},
					         {field:'functionname',title:'菜单名称',width:'15%',styler:function(v,r){if(r.nflag)return 'color:red;';}},
					         {field:'path',title:'图标',width:'10%',formatter:function(v){return '<img style="width:35px;height:35px;" src="' + v + '"/>';}},
					         {field:'functiontype',title:'菜单类型',editor:'text',width:'12%',formatter:function(v){return v==1?'访问类型':'菜单类型'}},
					         {field:'functionurl',title:'菜单地址',editor:'text',width:'40%'},
					         {field:'functionorder',title:'菜单顺序',editor:'text',width:'8%'}
					       
					]],
					toolbar : [
						{
							iconCls : 'icon-add',
							text : '新增菜单',
							handler : function() {
                                     	isNew = true;
                                     	var rowsData = $("#functiondatagrid").datagrid('getSelected');
                                     	var pid = '';
                                     	var addCondition = '';
                                     	if(rowsData && rowsData.id) {
                                     		pid = rowsData.id;
                                     		addCondition = '&TSFunction.id=' + pid;
                                     	}
	                                    $('#editwindow').contents().find("body").html("");
										$("#editwindow").attr('src','functionController.do?addorupdate' + addCondition);
										$('#win').window('open');
										$("#buttons").css("display","block"); 
							}
						},'-',
						{
							iconCls : 'icon-edit',
							text : '编辑菜单',
							handler : function() {
								var rowsData = $("#functiondatagrid").datagrid('getSelected');
								if (rowsData == null) {
									top.$.messager.alert('警告','请选择要编辑的菜单', 'warning');
									return;
								}
								$('#editwindow').contents().find("body").html("");
								$("#editwindow").attr('src','functionController.do?addorupdate&id='+ rowsData.id);
								$("#submitForm").attr("onclick","submitForm('doUpdate')");
	                                 $('#win').window('open');
	                                 $("#buttons").css("display","block"); 
							}
						},'-',{
	                             text : '删除菜单',
	                             iconCls : 'icon-remove',
	                             handler : function() {
								var rowsData = $("#functiondatagrid").datagrid('getSelected');
								if (rowsData == null) {
									top.$.messager.alert('警告','请选择删除项目', 'warning');
									return;
								}
								top.$.messager.confirm('温馨提示','您确认想要删除此菜单吗？',function(r){    
								    if (r){    
								       $.ajax({
									        url : 'functionController.do?del',
									        type : 'post',
									        cache : false,
									        data:{
									        	id:rowsData.id
									        },
									        success : function(data) {
												top.$.messager.show({title : '提示',msg : data.msg});
												reloadTable();
									        }
										}); 
								    }    
								});
	                             }
	                         } ]
				});
			    
		}
	</script>
	
</body>
</html>