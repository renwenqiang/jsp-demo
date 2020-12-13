<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div data-options="region:'north'," style="width: 100%;height:6%;">
  <form id = 'uSearch' style="margin-top: 10px;margin-left: 1%;">
      <table style="width:95%;" cellpadding="0" cellspacing="0" >
      	<tr>    
         	<td align="right">
             	<label class="Validform_label" style="font-size:12px;width:10%;">
                                          用户名:
                 </label>
             </td>
             <td class="value">
                 <input id="username" name="username" type="text" style="width:80%;" class="inputxt"  value=''>
                 <span class="Validform_checktip"></span>
                 <label class="Validform_label" style="display: none;">用户名</label>
             </td>
             <td align="right">
                 <label class="Validform_label" style="font-size:12px">
                                         真实姓名:
                 </label>
             </td>
             <td class="value">
                 <input id="realname" name="realname" type="text" style="width: 80%" class="inputxt"  value=''>
                 <span class="Validform_checktip"></span>
                 <label class="Validform_label" style="display: none;">真实姓名</label>
             </td>
             <td align="left">
                 <a href="javascript:void(0)" onclick="search_user()" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >查询</a>
                 <a href="javascript:void(0)" onclick="resetForm('uSearch')"  style="margin-left:25px" class="easyui-linkbutton"  data-options="iconCls:'icon-reload'">重置</a>
             </td>
         </tr>
      </table>
    </form>
	</div>
  <div data-options="region:'center',"  title="" style="width: 100%;height: 90%">   
     <table style="width:100%;height: auto;" id="usergrid"></table>
  </div>

<script type="text/javascript">
	var roleid = '<%=request.getParameter("roleid")%>';
	var departid = '<%=request.getParameter("departid")%>';
	
	$(function(){
		
	
		var defaultCondition = '';
		if(roleid != null && "null"!=roleid && ""!=roleid) {
			defaultCondition += '&roleid=' + roleid;
		}
		if(departid != null && "null"!=departid && ""!=departid) {
			defaultCondition += '&departid=' + departid;
		}
		$("#usergrid").datagrid({
			url : 'framework/query.do?search&k=user_list_tb' + defaultCondition,
			title : '用户列表',
			fit : true,
			fitColumns : true,
			striped : true,
			idField : 'ID',
			//singleSelect:false,
			loadMsg : '数据加载中......',
			pagination : true,
			rownumbers : true,
			//sortName:'id',
			columns : [ [ {
				field : 'ID',
				title : 'ID',
				width : 250,
				hidden : true
			}, {
				field : 'USERNAME',
				title : '用户名',
				width : 50
			}, {
				field : 'REALNAME',
				title : '真实姓名',
				width : 50
			}] ],
			queryParams : {},
			onLoadError : function() {
				alert("系统繁忙，请稍后再试！");
			}
		});
	});
	function search_user() {
		var searusername = $("#username").val();
		var searrealname = $("#realname").val();

		//获取表单中填写的值
		$("#usergrid").datagrid('reload', {
			user_name : searusername,
			realname : searrealname
		});
	}
</script>