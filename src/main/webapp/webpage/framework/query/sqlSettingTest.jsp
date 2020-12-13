<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>

<html>
	<title>sql配置测试</title>
	<head>
		<style type="text/css">
			<!--
			 .searchTableH {width:90%;margin:2px 5%;}
			 .searchTableV {width:100%;margin:2px 0;}
			 .searchTableH td,.searchTableV td{padding:5px;}
			 .tdSearchButtonV {text-align:right}
			 .tdSearchTitleH {text-align:right}
			 
			 #sql,#params{height:100%}
			 #sql{background-color: #150202;color: #fff;overflow:scroll;}
			 #params{background-color:#333;color:#0f0}
			 .sql,pre{white-space:pre-wrap;}
			 .sql,.params,pre{color:#c10441;padding:15px !important;font-size:18px !important;line-height:24px !important}
			-->
		</style>
		<z:resource items="jquery,easyui,sunzui"></z:resource>
		<z:config items="SQL.keywords"></z:config>
		<script type="text/javascript" src="webpage/framework/query/sqlformat.js"></script>
		<script type="text/javascript" src="webpage/framework/smartform/util.js"></script>
	</head>
	<body>		
        <script type="text/javascript">
	        var getTestParam=function(){
	        	var q=form.getFieldValues();
	        	delete q.qt;
				if(document.getElementById("cbClearNull").checked){
					for(var n in q){
						if (q[n]==""||q[n]==null)
							delete q[n];
					}
				}
				q.sqlid=getQueryParam("sqlid");
				q.k=getQueryParam("k");
				$.each(($("input[name='qt']").val()||"").split("&"),function(i,kv){
					if(!kv)return;
					var o=kv.split("=");
					q[o[0]]=o[1];
				});
				return q;
	        }
	        var grid = null,calcColumns=function(sample){
	        	var columns=[];
				for(var c in sample) {
					if(sample.hasOwnProperty(c) && "RN" != c) {
						columns.push({title:c,field:c});
					}
				}
				return columns;
	        };
        	function submitForm() {  
        		$(pDebug).hide();
        		var start = $("input[name='start']").val();
        		var limit = $("input[name='limit']").val();
        		
        		var numReg = /^[0-9]*$/;
        		if(!start || !limit || !numReg.test(start) || !numReg.test(limit) || limit > 100) {
        			$.messager.alert('提示',"请填写分页参数start和limit，只能是数字，limit最大为100");
        			return;
        		}
        		
        		$.ajax({
        			url:'framework/query.do?search',
        			data : getTestParam(),
        			type : "post",
        			async : true,
        			success : function(data) {
        				if(!data.success){
        					$.messager.alert("sql执行出错误啦",data.msg+"<br/><br/>请检查必须参数是否已填写");
        					return;
        				}
        				var dd = data.data;
        				if(dd && dd.length >0) {
       						var sample = dd[0];
       						if(grid){
       							grid.datagrid({columns:[calcColumns(sample)],data:dd})
       						}
        						
        					if(grid == null) {
        						var gridConfig={
               						iconCls:'icon-edit',
               						parent:pResult,
               						columns:[calcColumns(sample)],data:dd,
               						singleSelect:true,
               						fit:true,fitColumns:true,
               						autoRowHeight:true,
               						pagination:true,rownumbers:true,striped:true
               					};
               					grid=window.grid=new sunz.Datagrid(gridConfig);               					
        					}
        				} else {
        					grid && grid.loadData(dd);
        					grid || $.messager.show({title:"提示",msg:"sql执行成功，但未获取到数据，请检查传入的参数"});
        				}
        			},
        			fail : function(e) {
        				$.messager.alert("服务请求失败","请确认服务有效");
        			}
        		});
        	}
        	var defaultStyle = {width:'90%'};
        	var html='<input type="hidden" name="total" value="1" />',searchConfig={id:'pSearch',title:'查询条件',split:true};
        	var searchFields = [];
        	
        	<c:forEach items='${sFields}' var='item' varStatus="status">
        		searchFields.push({name:'${item}',title:'${item}',inputType:'textbox',styles:defaultStyle,order:'${status.index}'});
        	</c:forEach>
        	
        	var getVSearch=function(){
        		html +='<table class="searchTableV" width="100%" cellpadding="5" style="margin:2px 0;" >';
        		$.each(searchFields,function(i,f){
        			f.i=i;
        			html+=tplReplace('<tr><td >{title}</td><td><input id="tdSearchField{i}" type="text" name="{name}"   value="{value}" /></td></tr>',f);
        		});
        		html+='<tr><td colspan="2">分页参数start</td></tr><tr><td></td><td><input type="text" name="start" value="0"/></td></tr>';
        		html+='<tr><td colspan="2">分页参数limit</td></tr><tr><td></td><td><input type="text" name="limit" value="10"/></td></tr>';
        		html+='<tr><td colspan="2">额外参数(名称=值的形式,多个&连接)</td></tr><tr><td></td><td><input type="text" name="qt" value=""/></td></tr>';
        		html+='<tr><td colspan="2" id="divButtons" align="right"></td></tr>'
        				+'<tr><td colspan="2" id="divButtons2"><input id="cbClearNull" type="checkbox"  checked="true"/>空值不传参数</td></tr> </table>';	

        		$(viewport).asLayout().add($.extend(searchConfig,{region:"west",width:360}));
        	};
        	
        	$(function(){
        		$(pDebug).hide();
        		getVSearch();
        		
        		var pSearch=$("#pSearch").asPanel();
        		var pForm=window.form=new sunz.Form({parent:pSearch[0],style:{"margin-bottom":0}});
        		$(html).appendTo(pForm);
        		$.each(searchFields,function(i,f){
        			$("#tdSearchField"+i)[f.inputType](f.styles); 
        		});
        		
        		new sunz.Linkbutton({parent:divButtons,text:'查询',iconCls:'icon-search',handler:submitForm});
        		new sunz.Linkbutton({parent:divButtons,text:'重置',style:'margin-left:25px',iconCls:'icon-redo',handler:function(){form.reset();}});
        		new sunz.Linkbutton({parent:divButtons,text:'输出sql',style:'margin-left:25px',iconCls:'icon-redo',handler:function(){
        			$.post("framework/query.do?debug",getTestParam(),function(jr){
						if(jr.success){
							$(pDebug).show();
							$("#sql").html(hlsql(jr.data.sql));
							$("#params").text(formatJson($.encode(jr.data.params)));
						}else{
							$.messager.show({title:"提示",msg:"使用当前参数无法正确输出SQL，信息："+jr.msg});
						}
					})
        		}});
        		
        	});
        </script>
        
        <div id="viewport" class="easyui-layout" data-options="fit:true">
			<div id="pResult" data-options="region:'center',title:'查询结果'">
				<div id="pDebug" class="easyui-layout" data-options="fit:true" style="z-index:9999">
					<div data-options="region:'center',title:'解析后的SQL',split:true">
						<div id="sql"></div>
					</div>
					<div style="height:30%" data-options="region:'south', title:'传给SQL的参数' ,split:true">
						<pre id="params"></pre>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>