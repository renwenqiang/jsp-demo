<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:dict items=""></z:dict>
<script type="text/javascript">
	$(function() {
		$("#functionListPanel").panel(
				{
					title :'菜单列表',
					tools:[{iconCls:'icon-save',handler:function(){mysubmit();}}]
				}
		);
		$('#functionid').tree({
			checkbox : true,
			url : 'framework/query.do?search&k=sys_menu_roleauthed&roleid=${roleId}',
			loadFilter : function(jr) {
				var dm=D.createNew();
				$.each(jr.data,function(i,d){
					dm.add(d.id,d.id,d.text,d.pid).checked=d.checked;
				});
				var menus=dm.get("all");
				(function c(ms){
					$.each(ms,function(i,m){
						if(m.children && m.children.length){
							m.checked=false;
							c(m.children);
						}
					})
				}(menus))
				return menus;
			},
			onClick: function(node){
				var isRoot =  $('#functionid').tree('getChildren', node.target);
			}
		});
	});
	function mysubmit() {
		var roleId = $("#rid").val();
		var s = GetNode();
		console.log(s);
		doSubmit("roleController.do?updateAuthority","&rolefunctions=" + s + "&roleId=" + roleId);
	}
	function doSubmit(url,data) {
		$.ajax({
	        url : url,
	        type : 'post',
	        data: data,
	        cache : false,
	        success : function(data) {
				top.$.messager.show({title : '提示',msg : data.msg});
	        }
		});
	}
	function GetNode() {
		var node = $('#functionid').tree('getChecked');
		var cnodes = '';
		var pnodes = '';
		var pnode = null; //保存上一步所选父节点
		for ( var i = 0; i < node.length; i++) {
			if ($('#functionid').tree('isLeaf', node[i].target)) {
				cnodes += node[i].id + ',';
				pnode = $('#functionid').tree('getParent', node[i].target); //获取当前节点的父节点
				while (pnode!=null) {//添加全部父节点
					pnodes += pnode.id + ',';
					pnode = $('#functionid').tree('getParent', pnode.target); 
				}
			}
		}
		cnodes = cnodes.substring(0, cnodes.length - 1);
		pnodes = pnodes.substring(0, pnodes.length - 1);
		return cnodes + "," + pnodes;
	};
	
	function expandAll() {
		var node = $('#functionid').tree('getSelected');
		if (node) {
			$('#functionid').tree('expandAll', node.target);
		} else {
			$('#functionid').tree('expandAll');
		}
	}
	function selecrAll() {
		var node = $('#functionid').tree('getRoots');
		for ( var i = 0; i < node.length; i++) {
			var childrenNode =  $('#functionid').tree('getChildren',node[i].target);
			for ( var j = 0; j < childrenNode.length; j++) {
				$('#functionid').tree("check",childrenNode[j].target);
			}
	    }
	}
	function reset() {
		$('#functionid').tree('reload');
	}

	$('#selecrAllBtn').linkbutton({   
	}); 
	$('#resetBtn').linkbutton({   
	});   
</script>
<div class="easyui-layout" fit="true">
<div region="center"  style="padding:1px;">
<div class="easyui-panel" style="padding: 1px;" fit="true" border="false" id="functionListPanel"><input type="hidden" name="roleId" value="${roleId}" id="rid"> <a id="selecrAllBtn"
	onclick="selecrAll();">全选</a> <a id="resetBtn" onclick="reset();">重置</a>
<ul id="functionid"></ul>
</div>
</div>

</div>
