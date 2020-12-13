/**
 * 打印相关代码
 */
var ExportEditor=function(dexport){
	dexport=dexport||{};
	var me=this;
	var html_Window='<div style="height:100%;padding:0 0 50px">\
						<div class="easyui-layout" data-options="fit:true">\
							<div class="pBase" data-options="region:\'center\',title:\'基本信息\',split:true" style="overflow-y:scroll;"></div>\
							<div class="pRField" data-options="region:\'south\',title:\'字段配置\',split:true " style="height:30%"></div>\
						</div>\
					</div>\
					<div class="pToolbar" style="position:absolute;bottom:0;width:100%;height:50px;padding:5px 15px;text-align:center;background-color:#eee;z-index:999"></div>\
					 ';
	
	var html_Export='<form id="exportForm">\
					<table class="kv-table" cellpadding="1">\
						<tr>\
							<td class="kv-label">编码:</td><td class="kv-content"><input type="text" class="easyui-textbox" data-options="required:true" name="KEY" id="KEY" style="width:200px"/></td>\
						</tr>\
						<tr>\
							<td class="kv-label">名称或描述:</td><td class="kv-content"><input type="text" class="easyui-textbox" data-options="required:true" name="DESCRIPTION" style="width:200px"/></td>\
						</tr>\
						<tr>\
							<td class="kv-label">起始行:</td><td class="kv-content"><input type="text" class="easyui-textbox" name="STARTROW" style="width:200px"/></td>\
						</tr>\
						<tr>\
							<td class="kv-label">起始列:</td><td class="kv-content"><input type="text" class="easyui-textbox" name="STARTCOLUMN" style="width:200px"/></td>\
						</tr>\
						<tr>\
							<td class="kv-label">跳过行:</td><td class="kv-content"><input type="text" class="easyui-textbox" name="IGNOREROWS" style="width:200px"/></td>\
						</tr>\
						<tr>\
							<td class="kv-label">跳过列:</td><td class="kv-content"><input type="text" class="easyui-textbox" name="IGNORECOLUMNS" style="width:200px"/></td>\
						</tr>\
						<tr>\
							<td class="kv-label">模板文件:</td><td class="kv-content"><input type="file" name="TEMPLATE" id="TEMPLATE" /><input type="button" id="uploadButton" value="上传"/><input type="button" id="downloadButton" value="下載"/><label><input type="text" name="TEMPLATE_label" readonly="readonly" style="border:none;color:#FF0000;"/></label></td>\
						</tr>\
						<tr>\
							<td class="kv-label">SQL语句:</td><td class="kv-content"><select id="cmbSql" style="width:100%;" class="easyui-combogrid" name="SQLID" data-options="url:\'framework/query.do?search&k=dictForSql\',delay:1000,mode:\'remote\',pagination:true,fitColumns:true,idField:\'id\',textField:\'code\',columns:[[{field:\'code\',title:\'编码\',width:100,},{field:\'name\',title:\'名称\',width:160},{field:\'sql\',title:\'sql详情\',width:200}]],width:600,formatter:function(v,r){return r.code+\',\'+r.name;}" ></select></td>\
						</tr>\
						<tr>\
							<td class="kv-label">附加参数:</td><td class="kv-content"><input type="text" class="easyui-textbox "style="width:200px" id="fjcs"/><input type="button" value="采样" id="sField" style="margin-left:5px;"/></td>\
						</tr>\
						<tr>\
							<td class="kv-label">字段映射:</td><td  class="kv-content"><textarea id="FIELDMAPPING" class="easyui-textarea htmlcell" name="FIELDMAPPING"></textarea></td>\
						</tr>\
						<tr>\
							<td class="kv-label">编码字段:</td><td class="kv-content"><textarea id="DICTFIELDS" class="easyui-textarea htmlcell" name="DICTFIELDS"></textarea></td>\
						</tr>\
					</table></form>';
	
	var win=new sunz.Window({title:'导出编辑窗口',modal:true,height:'98%',width:'80%'}).append(html_Window), 
		form=$(html_Export).asForm(),
		pBase=win.find(".pBase"),
		pRField=win.find(".pRField");
	form.appendTo(pBase);
	
	$("#TEMPLATE").change(function(){
		//上傳文件校驗
		var type=$(this).val().match(/^(.*)(\.)(.{1,8})$/)[3];//文件类型
		if(type!="xls" && type!="xlsx" ){
			alert("非Excel格式文件,请重新选择文件");
		}
	});
	
	$("#uploadButton").click(function(){
		var formData = new FormData($( "#exportForm" )[0]);
		var id=dexport.ID;
		var url='framework/export.do?uploadTemplate&t=T_S_ExcelExport&id='+id;
		$.ajax({  
	          url: url,  
	          type: 'POST',  
	          data: formData,  
	          async: false,  
	          cache: false,  
	          contentType: false,  
	          processData: false,
	          dataType:"json",
	          success: function (jr) {
	        	  if(jr.success){
	                dexport=jr.data;
					dexport.ID=dexport.ID||jr.data.id;
					$.messager.show({title:'提示',msg:'上传成功'});
	        	  }
	          },  
	          error: function (returndata) {  
	              alert(returndata);  
	          }  
	     });	
	});
	
	$("#downloadButton").click(function(){
		var id=dexport.ID;
		var fileName=$("#KEY").val();
		var url='framework/export.do?downloadTemplate&id='+id+'&fileName='+fileName;
		window.open(url);
	});
	
	var cmbSql=$("#cmbSql").asCombogrid({"onChange":function(nv,ov){
		var row=cmbSql.grid().datagrid("getSelected");
		dexport.row=row;
	}});
	
	$("#sField").click(function(){
		var pmpt=$("#fjcs").val();
		setSql(dexport.row,pmpt);
	});
	
	var setSql=function(row,sampleParam){
		// 结果字段，要取样
		console.log(sampleParam);
		var url="framework/query.do?search";
		$.ajax(url,{
			data:$.extend({sqlid:row.id,start:0,limit:1,total:1},sampleParam),
			success:function(r){
				if(r.success&&r.data){
					count=0;
					$.each(r.data[0],function(n,v){
						gFields.appendRow({NAME:n});
					});
				}
			}
		});
	}
	
	var validateKey=function(k){
		var flag=false;
		if(k==null||k==""){
			$.messager.show({title:'提示',msg:'编码不能为空'});
			return flag;
		}
		var id=dexport.ID?dexport.ID:null;
		var url='framework/datatable.do?uniqueValidate&t=T_S_ExcelExport&c=key&v='+k+'&id='+id;
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
	
	var saveBase=function(fn){
		var url="framework/datatable.do?t=T_S_ExcelExport", o=form.getFieldValues();
		if(validateKey(o.KEY)){
			if(dexport.ID) o.save=null;else o.add=null;
			o.id=dexport.ID;
			$.ajax(url,{data:o,success:function(r){
				if(r.success){
					dexport.ID=dexport.ID||r.data.id;
					if(fn) 
						fn();
					else
						$.messager.show({title:'提示',msg:'保存成功'});
				}else{
					var message=r.msg.indexOf("ORA-00001")==-1 ? '保存时出错了，' : '字段<编码>重复，' ;
					$.messager.alert('提示',message+r.msg);
				} 
			}});
		}
	};
	
	var startEdit=function(g,i,d){
		if(g.editingIndex==i)return true;
		if(g.editingIndex !=undefined){
			if(g.validateRow(g.editingIndex)){
				g.endEdit(g.editingIndex);
			}
			else 
				return false;
		}
		g.beginEdit(i);
		$(":input",g).validatebox();
		g.editingIndex=i;
	},saveRow=function(g){
		var CfinedMapping="",DICTFIELDS="";
		if(g.editingIndex!=undefined && g.validateRow(g.editingIndex)){
			g.endEdit(g.editingIndex);
		}
		var rows=g.datagrid("getRows");
		for(var i=0;i<rows.length;i++){
			var zNAME=rows[i].NAME;
			var zFIELDMAPPING=rows[i].FIELDMAPPING;
			var zISDICT=rows[i].ISDICT;
			if(zFIELDMAPPING)
				CfinedMapping=CfinedMapping+zNAME+":"+zFIELDMAPPING+",";
			console.log(zISDICT);
			if(zISDICT&&zISDICT=="true")
				DICTFIELDS=DICTFIELDS+zNAME+",";
		}
		CfinedMapping=CfinedMapping.substring(0,CfinedMapping.length-1);
		DICTFIELDS=DICTFIELDS.substring(0,DICTFIELDS.length-1);
		$("#DICTFIELDS").val(DICTFIELDS);
		$("#FIELDMAPPING").val(CfinedMapping);
	},editRow=function(g){
		var sel=g.getSelected();
		if(sel==null){
			$.messager.show({title:'提示',msg:'请选择一行进行操作'});
			return;
		}
		g.beginEdit(g.getRowIndex(sel));
	},cancelEdit=function(g){
		g.cancelEdit(g.editingIndex);
	};
	
	var appendParam=function(opt){
		if(!dexport.ID){
			return false;
		}		
		opt.queryid=dexport.ID;
	};
	
	var gFields=new sunz.Datagrid({
		parent:pRField,
		fit:true,fitColumns:true,
		autoRowHeight:true,
		rownumbers:true,striped:true,
		checkOnSelect: false, selectOnCheck: false,
		url:'framework/query.do?search&k=resultFieldsByQuery',
		onBeforeLoad:appendParam,
		columns:[[{field:'ID',hidden:true},
				  {field:'NAME',title:'字段名称',width:60},
		          {field:'FIELDMAPPING',title:'映射字段名称',width:150,editor:'text'},
		          {field:'ISDICT', title:'是否编码',width:36,
		        	  editor:{
		        	        type:'checkbox',  
		        	        options:{
		        	            on: "true",  
		        	            off: "false"
		        	        }
		        	    }
		          },
		          
		]],
		sortName:'ORDERINDEX',
		toolbar:[{iconCls:'icon-save',text:'应用',handler:function(){saveRow(gFields);}},
		         {iconCls:'icon-edit',text:'编辑',handler:function(){editRow(gFields);}},
		         {iconCls:'icon-undo',text:'取消',handler:function(){cancelEdit(gFields);}}
		],
		onDblClickRow:function(i,d){startEdit(gFields,i,d);}
	});
	
	$.parser.parse(win);
	new sunz.Linkbutton({parent:win.find(".pToolbar"),text:'保存&nbsp;',iconCls:'icon-save',handler:function(){saveBase(function(){grid.reload()});},style:'padding:0 15px'});
	this.edit=function(q){
		dexport=q;
		win.setTitle((q.DESCRIPTION?(q.DESCRIPTION+"--编辑"):"新建导出"));
		win.open();
		form.load(q);

		if(dexport.ID){
			gFields.load();
		}else{
			gFields.loadData([]);
		}
		$(":input").validatebox();
	}
}