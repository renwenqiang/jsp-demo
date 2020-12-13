/**
 * 调档配置页面
 */
var InvocateEditor=function(invocate){
	invocate=invocate||{};
	//<div id=\\\'div_save\\\'>基本信息</div>
	var html=`<div class="easyui-layout" data-options="fit:true">
<div id="pBase" data-options="region:\'center\',title:\'\'">
	<form>
		<table class="kv-table" cellpadding="5" style="width:100%;">
			<tr>
				<td class="kv-label">编码:</td>
				<td class="kv-content"><input type="text"  name="KEY" data-options="required:true" class="w-required" /></td>
			</tr>
			<tr>
				<td class="kv-label">名称或描述:</td>
				<td class="kv-content"><input type="text"  name="DESCRIPTION" data-options="required:true" class="w-required" /></td>
			</tr>
			<tr>
				<td class="kv-label">查询语句定义:</td>
				<td class="kv-content"><input id="qname" name="QUERYKEY" class="w-required" /></td>
			</tr>
			<tr>
				<td class="kv-label">主键字段（结果）:</td>
				<td class="kv-content"><input type="text"  name="IDFIELD" class="w-required" /></td>
			</tr>
			<tr>
				<td class="kv-label">存储过程名:</td>
				<td class="kv-content"><input type="text"  name="INVOCATEPROCEDURE" class="w-required" /></td>
			</tr>
			<tr>
				<td class="kv-label">取消时查询语句定义:</td>
				<td class="kv-content"><input id="uqname" name="UNINVOCATEQUERYKEY" class="w-required" /></td>
			</tr>
			<tr>
				<td class="kv-label">取消时主键字段（结果）:</td>
				<td class="kv-content"><input type="text"  name="UNINVOCATEIDFIELD" class="w-required" /></td>
			</tr>
			<tr>
				<td class="kv-label">取消时存储过程名:</td>
				<td class="kv-content"><input type="text"  name="UNINVOCATEPROCEDURE" class="w-required" /></td>
			</tr>
		</table>
	</form>
</div>
<div id="div_save" data-options="region:\'south\'" style="height:48px"></div>
</div>`;
	var win=new sunz.Window({title:'调档编辑窗口',width:760,height:480,modal:true,content:html}),
		//form=$(html_Invocate).appendTo(win.find("#pBase")).asForm();
		form=win.find("form").asForm();
	
	$.ajax('framework/datatable.do?all&t=T_S_QUERY',{success:function(r){
		if(r.success){
			console.log(r);
			$('#qname').combobox({    
			    data:r.data,    
			    valueField:'KEY',
			    textField:'DESCRIPTION'
			}); 
			$('#uqname').combobox({    
			    data:r.data,    
			    valueField:'KEY',
			    textField:'DESCRIPTION'
			}); 
		}else{
			$.messager.alert('提示',message);
		} 
	}});
	
	var validateKey=function(k){
		var flag=false;
		if(k==null||k==""){
			$.messager.show({title:'提示',msg:'编码不能为空'});
			return flag;
		}
		var id=invocate.ID?invocate.ID:null;
		var url='framework/datatable.do?uniqueValidate&t=T_S_DataInvocation&c=key&v='+k+'&id='+id;
		$.ajax(url,{async:false,success:function(r){
			if(r.success){
				if(r.data>0){
					$.messager.show({title:'提示',msg:'保存失败，编码已存在'});
				}else{
					flag=true;
				}
			}else{
				$.messager.alert('提示',"无法保存数据,验证key失败");
			} 
		}});
		return flag;
	};
	
	var saveBase=function(){
		var url="framework/datatable.do?t=T_S_DataInvocation", o=form.getFieldValues();
		if(validateKey(o.KEY)){
			if(invocate.ID) o.save=null;else o.add=null;
			o.id=invocate.ID;
			$.ajax(url,{data:o,success:function(r){
				if(r.success){
					invocate=r.data;
					invocate.ID=invocate.ID||invocate.id;
						$.messager.show({title:'提示',msg:'保存成功'});
				}else{
					$.messager.alert('提示',message);
				} 
			}});	
		}
	};
	$.parser.parse(win);
	new sunz.Linkbutton({parent:$("#div_save")[0],text:'保存&nbsp;',iconCls:'icon-save',handler:function(){saveBase();},style:'margin:5px 125px;float:right'});
	this.edit=function(q){
		invocate=q;
		win.setTitle((q.DESCRIPTION?(q.DESCRIPTION+"--编辑"):"新建调档"));
		win.open();
		form.reset();
		form.load(q);
		$(":input").validatebox();
	}

}