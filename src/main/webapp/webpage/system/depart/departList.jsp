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
		<div data-options="region:'center',title:'组织机构列表'">
		    <div data-options="region:'north'," style="width: 100%;height:6%;">
		         <form id = 'fSearch' style="margin-top: 10px;margin-left: 1%;">
		             <table style="width:95%;" cellpadding="0" cellspacing="0" >
		             	<tr>    
		                	<td align="right">
		                    	<label class="Validform_label" style="font-size:12px">
		                                                 机构名称:
		                        </label>
		                    </td>
		                    <td class="value">
		                        <input id="departmentname" name="departmentname" type="text" style="width: 150px" class="inputxt"  value=''>
		                        <span class="Validform_checktip"></span>
		                        <label class="Validform_label" style="display: none;">机构名称</label>
		                    </td>
		                    <td align="right">
		                        <label class="Validform_label" style="font-size:12px">
		                                                机构地址:
		                        </label>
		                    </td>
		                    <td class="value">
		                        <input id="departmentaddress" name="departmentaddress" type="text" style="width: 150px" class="inputxt"  value=''>
		                        <span class="Validform_checktip"></span>
		                        <label class="Validform_label" style="display: none;">机构地址</label>
		                    </td>
		                    <td align="left">
		                        <a href="javascript:void(0)" onclick="searchDepart()" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >查询</a>
		                        <a href="javascript:void(0)" onclick="resetForm('fSearch')"  style="margin-left:25px" class="easyui-linkbutton"  data-options="iconCls:'icon-reload'">重置</a>
		                    </td>
		                </tr>
		             </table>
		           </form>
		       	</div>
		         <div data-options="region:'center',"  title="" style="width: 100%;height: 90%">   
		            
		            <div id="deptdatagrid" style="padding:5px 5px 5px 5px;"></div>
		         </div>
			    <div id="win" class="easyui-window" title="组织机构操作" style="width:520px;height:410px;" data-options="modal:true" closed="true" >
					<iframe id="editwindow" style="" src="" height="80%" width="100%"></iframe>
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
    	<div id="dept_users" class="easyui-panel" data-options="region:'east',title:'关联用户',split:true,collapsed:true" style="width:50%;">
    		
    	</div>
    	
    </div>
    
    <script type="text/javascript">
	    var all=[],dict={};
		
		function loadGrid(reloaded) {
			var departname = $("#departmentname").val();
			var address = $("#departmentaddress").val();
			
			$.ajax({
				async: true,
			    type: "POST",
			    url: "framework/query.do?search&k=dept_list_tb&departname="+departname+"&address="+address,//framework/query.do?search&k=userIndexTree
			    contentType: "application/json; charset=utf-8",
			    dataType: "json",
			    beforeSend: ajaxLoading,
			    success: function (data) {
			    	ajaxLoadEnd();
			    	all=[],dict={};
			        $.each(data.data,function(i,d){
						ad(d.ID,d.DEPARTNAME,d.DESCRIPTION,d.PARENTDEPARTID,d.ORG_CODE,d.ORG_TYPE,d.MOBILE,d.FAX,d.ADDRESS,d.ORDERINDEX,d.PARENTDEPARTID==null?'0':d.PARENTDEPARTID);
					});
			        var treedata=fnGrep(all,"closed");
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
		
		function reloadGrid(treedata) {
			/*try {
				var roots = $("#deptdatagrid").treegrid("getRoots");
				for(var i=0;i<roots.length;i++) {
					console.log(roots[i].id);
					$("#deptdatagrid").treegrid("remove",roots[i].id);
				}
			} catch(err) {
				
			}*/
			$("#deptdatagrid").treegrid("loadData",treedata);
		}
		
		function formGrid(treedata) {
			$("#deptdatagrid").treegrid({
					data:treedata,
					idField: "id",
				    treeField: "departname",
				    fit:true,fitColumns:false,
					autoRowHeight:true,
					loadMsg : '数据加载中......',
				    columns:[[
					         {field:'id',hidden:true,width:50}, {field:'pid',hidden:true,width:50},
					         {field:'departname',title:'组织机构名称',width:'24%',styler:function(v,r){if(r.nflag)return 'color:red;';}},
					         {field:'description',title:'组织机构描述',editor:'text',width:'8%',styler:function(v,r){if(r.cflag)return 'color:red;';}},
					         {field:'org_code',title:'机构编码',editor:'text',width:'8%'},
					         {field:'org_type',title:'机构类型',editor:'text',width:'8%',formatter:function(v){if(v=="1"){return "公司"} else if(v=="2"){return "部门"} else {return "其他"}}},
					         {field:'mobile',title:'电话',editor:'text',width:'8%'},
					         {field:'fax',title:'传真',editor:'text',width:'8%'},
					         {field:'address',title:'地址',editor:'text',width:'18%'},
					         //{field:'orderindex',title:'顺序',editor:'text',width:'6%'},
					         {field:'operate',title:'操作',editor:'text',width:'18%'}
					]],
					toolbar : [
						{
							iconCls : 'icon-add',
							text : '新增组织机构',
							handler : function() {
	                                     	isNew = true;
	                                     	var rowsData = $("#deptdatagrid").datagrid('getSelected');
	                                     	var pid = '';
	                                     	if(rowsData && rowsData.id) {
	                                     		pid = rowsData.id;
	                                     	}
	                                    $('#editwindow').contents().find("body").html("");
										$("#editwindow").attr('src','departController.do?add&id=' + pid);
										$('#win').window('open');
										$("#buttons").css("display","block"); 
							}
						},'-',
						{
							iconCls : 'icon-edit',
							text : '编辑组织机构',
							handler : function() {
								var rowsData = $("#deptdatagrid").datagrid('getSelected');
								if (rowsData == null) {
									top.$.messager.alert('警告','请选择要编辑的组织机构', 'warning');
									return;
								}
								$('#editwindow').contents().find("body").html("");
								$("#editwindow").attr('src','departController.do?update&id='+ rowsData.id);
								$("#submitForm").attr("onclick","submitForm('doUpdate')");
	                                 $('#win').window('open');
	                                 $("#buttons").css("display","block"); 
							}
						},'-',{
	                             text : '删除组织机构',
	                             iconCls : 'icon-remove',
	                             handler : function() {
								var rowsData = $("#deptdatagrid").datagrid('getSelected');
								if (rowsData == null) {
									top.$.messager.alert('警告','请选择删除项目', 'warning');
									return;
								}
								top.$.messager.confirm('温馨提示','您确认想要删除此组织机构吗？',function(r){    
								    if (r){    
								       $.ajax({
									        url : 'departController.do?del',
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
		
		$(function() {
			loadGrid(false);
		});
		
		function closeWindow() {
		      /* var fileIds = $("#editwindow").contents().find('#fileids').val();
			var data = $("#editwindow").contents().find('#id').val(); */
			$("#win").window('close', true);
		}
		function reloadTable() {
			loadGrid(true);
		}
		function submitForm(action) {
			var result = $("#editwindow")[0].contentWindow.saveDept();
			if(result){
				$("#win").window('close', true);
				reloadTable();
			}
		}
		
		function searchDepart() {
			loadGrid(true);
		}
		function queryUsersByDepart(id,qt) {
			$(".layout-button-left").click();
			$('#dept_users').panel({
				href: 'webpage/system/role/userPanel.jsp?departid=' + id
			});
		}
	</script>
</body>