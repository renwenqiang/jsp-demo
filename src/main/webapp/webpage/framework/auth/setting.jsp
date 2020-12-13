<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/webpage/framework/auth/resourceEdit.jsp"%>
<z:dict items="all"></z:dict>
<script type="text/javascript">
	$.fn.menu.defaults.hideOnUnhover=false;
	var types=[],dictType={};at=function(c,n){types.push({text:n,value:c});dictType[c]=n;};
	<c:forEach items="${controlTypes}" var="t">at('${t.code}','${t.name}');</c:forEach>	
	columns.push({field:"defaultControl",title:"默认权限",width:200,formatter:function(v,r){if(!r.page)return ''; return v?('<span style="color:#00f">'+dictType[v]+'</span>'):"无控制";}});
	columns.push({field:"controlType",title:"当前设置",width:200/*,editor:{type:'combobox',options:{data:types}}*/,formatter:function(v){if(v===undefined)return "";return v?('<span style="color:#f00">'+dictType[v]+'</span>'):'<span style="color:#0f0">默认</span>';}});
	
	$(function(){
		var pele=$("tr","#fSearch")[0].insertCell(6);		
		new sunz.Dictcombo({parent:pele,width:132,attrs:{name:"only"},data:[{text:"全部显示",value:""},{text:"仅显示已配置",value:true}]});	
		
		/*var td=$("#dToolbar")[0];//.parentNode.insertCell();
		$.each([{iconCls:'icon-reload',text:'刷新',handler:function(){
					tree.load();
				}
			},'-',{iconCls:'icon-edit',text:'修改',handler:function(){
					tree.options().onDblClickRow.call(tree);
				}
			},{iconCls:'icon-save',text:'保存修改',handler:function(){
					var sel=tree.editingData;
					if(!sel)return;
					tree.endEdit(sel.id);
				}
			},'-',
			{iconCls:'icon-cancel',text:'删除',handler:function(){
				$.messager.confirm("删除确认","您确定要删除当前数据吗？",function(ok){
					if(ok){
						$.post(deleteUrl,{id:tree.getSelected().cid},function(jr){
							if(jr.success){
								tree.load();
							}else{
								$.messager.alert('提示','删除出错了:'+r.msg);
							}
						});
					}
				});
				}
			}
		],function(i,t){
			if(t=='-'){
				$('<div class="datagrid-btn-separator"></div>').appendTo(td);
			}else{
				t.parent=td;
				new sunz.Linkbutton(t);
			}
		});*/

		// 菜单
		//var am=new sunz.Menu(),dm=new sunz.Menu();
		var m=new sunz.Menu()
			  .appendItem({text:"默认为",id:"dm"})
			  .appendItem({separator: true})
			  .appendItem({text:"设置为",id:"am"})
			  .appendItem({separator: true})
			  .appendItem({
				text:"删除设置",iconCls:'icon-remove',onclick:function(){
					$.messager.confirm("删除确认","您确定要删除当前设置吗？",function(ok){
						if(ok){
							$.post(deleteUrl,{id:tree.getSelected().cid},function(jr){
								if(jr.success){
									tree.load();
								}else{
									$.messager.alert('提示','删除出错了:'+r.msg);
								}
							});
						}
					});
				}
			});
		var dm=m.findItem("默认为").target,am=m.findItem("设置为").target;
		$.each(types,function(i,type){
			m.appendItem({parent:dm,text:type.text,iconCls:'icon-tip',onclick:function(){
				$.post("framework/datatable.do?save",{t:"T_S_UIResource",id:tree.getSelected().id,defaultControl:type.value},function(jr){
					if(jr.success)
						search();
					else 
						$.messager.alert('提示','默认权限控制设置出错了:'+jr.msg);
				});
			}});
			
			if(i>0) m.appendItem({parent:am,separator: true});
			m.appendItem({parent:am,text:type.text,iconCls:'icon-ok',onclick:function(){
				var sel=tree.getSelected();
				if(sel.cid){ // 有cid值进行保存，否则新增
					sel.controlType=type.value;
					$.post(saveUrl,{id:sel.cid,controlType:sel.controlType},function(jr){
						if(jr.success){
							tree.load();
						}else{
							$.messager.alert('提示','保存出错了:'+jr.msg);
						}
					});
				}else{					
					fnAdd(sel.id,type.value,function(jr){
						if(jr.success)
							tree.load();
						else 
							$.messager.alert('提示','添加权限出错了:'+jr.msg);
						}
					);			
				}
			}});
		});

		tree.treegrid({
			onContextMenu:function(e,r){
				if(!r || !r.page/*code name children都可以进行判断，不能用id*/) return;
				e.preventDefault();
				m.show({left: e.pageX,top: e.pageY});
			}
		});		
	});
</script>
<script type="text/javascript" src="webpage/framework/auth/${param.type==null||"".equals(param.type)?"step":param.type}.js"></script>
