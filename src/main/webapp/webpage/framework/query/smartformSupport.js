var smartPre=`top.smartformHelper.getConfig("{smartid}",(info)=>{$.extend(window,info);`,
	smartPost="})";
var smartRely=`top.$("#smartform-support").length || top.$('<'+'script id="smartform-support" type="text/javascript" src="webpage/framework/query/smartformHelper.js"><'+'/script'+'>').appendTo("head")`;

var autoFields={formWidth:"formWidth",formHeight:"formHeight",deleteUrl:""};
var smartOperations={
	add:{text:"新增",icon:"icon-add",
		helperNeeded:true,
		handler:`()=>{{autoPre}top.createwindow("{addTitle}","framework/smartform.do?parse&smartMode=-1&smartcode={smartcode}",{formWidth},{formHeight},(frm,fn)=>{frm.save(()=>{$.messager.show({title:"恭喜您",msg:"操作成功"});fn();grid.reload()});return false}){autoPost}}`
	},
	edit:{text:"修改",icon:"icon-edit",
		helperMust:true,
		helperNeeded:true,
		handler:`createRowHandler((sel)=>{{autoPre}top.createwindow("{editTitle}","framework/smartform.do?parse&smartMode=1&smartcode={smartcode}"+top.paramRelation("{smartIdField}",sel,"{gridIdField}"),{formWidth},{formHeight},(frm,fn)=>{frm.save(()=>{$.messager.show({title:"恭喜您",msg:"操作成功"});fn();grid.reload()});return false}){autoPost}})`
	},
	view:{text:"查看",icon:"icon-view",
		helperMust:true,
		helperNeeded:true,
		handler:`createRowHandler((sel)=>{{autoPre}top.createwindow("{viewTitle}","framework/smartform.do?parse&smartMode=0&smartcode={smartcode}"+top.paramRelation("{smartIdField}",sel,"{gridIdField}"),{formWidth},{formHeight}).btnOk.hide(){autoPost}})`
	},
	del:{text:"删除",icon:"icon-remove",
		helperNeeded:true,
		handler:`createRowHandler(sel=>{deleteUrl="{deleteUrl}";{autoPre}
	$.messager.confirm("删除确认","您确定要删除当前数据吗？",(ok)=>{
		if(ok){
			var o={};
			o.{smartIdField}=sel.{gridIdField};
			$.post(deleteUrl,o,(r)=>{
				if(r.success){
					grid.reload();
				}else{
					$.messager.alert('提示','删除时出错了:'+r.msg);
				}
			});
		}
	});
{autoPost}})`},
	log:{text:"台账",icon:"icon-history",
		handler:`(top.$("#editlog-support").length || top.$('<'+'script id="editlog-support" type="text/javascript" src="webpage/framework/smartform/editlog.js"><'+'/script'+'>').appendTo("head"),
createRowHandler(sel=>{top.viewEditlog(sel.{gridIdField},"{tableName}","{logTitle}")}))`		
	}
 }
$(`<style type="text/css">
	.smartbind{padding:5px 25px;font-size:16px;}
	.smartbind fieldset{border:1px solid}
	.smartbind legend{margin-left:5px;padding:5px 15px 0;}
	.smartbind div{padding:3px 25px}
	.smartbind .textbox-label{margin-left:25px;width:auto;}
	.sb-label{margin-right:5px}
</style>`).appendTo(document.head);

function bindSmartForm(g,fnAdd,fnEdit,fnSave){
	 var dlg=new sunz.Dialog({title:"Smart表单绑定配置--手动在“额外资源”中->右键->“引入Smartform动态解析”",width:800,height:640,
		 content:
`<form class="smartbind">
<fieldset>
	<legend>Smart表单选择</legend>
	<div><input label="绑定->" name="smartcode" ><input class="easyui-textbox x" type="hidden" name="smartid" ></div>
	<div><input readonly name="formWidth" label="宽度" class="easyui-textbox x"><input readonly name="formHeight" label="高度" class="easyui-textbox x"><input readonly name="tableName" label="关联表" class="easyui-textbox x" value=""></div>
</fieldset>		
<fieldset>
	<legend>功能选择</legend>
	<div><span class="sb-label">新增</span><input name="add" type="checkbox" class="easyui-checkbox" checked="checked"><input name="addTitle" label="弹出框标题" class="easyui-textbox" value="新增"></div>
	<div><span class="sb-label">修改</span><input name="edit" type="checkbox" class="easyui-checkbox" checked="checked"><input name="editTitle" label="弹出框标题" class="easyui-textbox" value="修改"></div>
	<div><span class="sb-label">删除</span><input name="del" type="checkbox" class="easyui-checkbox" checked="checked"></div>
	<div><span class="sb-label">查看</span><input name="view" type="checkbox" class="easyui-checkbox"><input name="viewTitle" label="弹出框标题" class="easyui-textbox" value="查看"></div>
	<div><span class="sb-label">编辑日志</span><input name="log" type="checkbox" class="easyui-checkbox"><input name="logTitle" label="弹出框标题" class="easyui-textbox" value="数据台账查看"></div>
</fieldset>
<fieldset>
	<legend>设置</legend>
	<div><input name="gridIdField" label="列表中的字段" class="easyui-textbox" value="id"> 映射为 <input name="smartIdField" label="Smart表单字段" class="easyui-textbox" value="id">（作为主键）</div>
	<div><input name="width" label="宽度" class="easyui-numberbox x s"><input name="height" label="高度" class="easyui-numberbox x s"></div>
	<div><input name="deleteUrl" label="删除接口" class="easyui-textbox x s" style="width:550px"></div>
	<div><input name="auto" type="checkbox" class="easyui-checkbox" checked="checked"><span class="sb-label">动态计算--宽度、高度、删除接口信息运行时从表单配置中获取，但需注意表单宽高不能为百分比，否则无法计算大小</span></div>
</fieldset>
</form>`,
		 buttons:[{text:"确定",iconCls:"icon-ok",handler:()=>{
			 var o=dlg.find("form").asForm().getFieldValues();
			 console.log(o)
			 if(cb.checked){
				 o.width="null";
				 o.height="null";
				 o.deleteUrl="null";
			 }
			 var idx=g.getRows().length;
			 // 使用同步ajax来保存
			 var async=$.ajaxSettings.async;
			 $.ajaxSettings.async=false;
			 $.each(smartOperations,(n,v)=>{
				 if(!o[n]) return ;
				 
				 if(cb.checked){
					 o.autoPre=tplReplace(smartPre,o);
					 o.autoPost=tplReplace(smartPost,o);
					 $.extend(o,autoFields);
				 }else{
					 o.autoPre="";
					 o.autoPost="";
				 }
				 //if(v.config) o.autoPre = tplReplace(v.config,o)+";"+o.autoPre;				 
				 var js	= tplReplace(v.handler,o);
				 // 自动引入helper
				 if(v.helperMust || (cb.checked && v.helperNeeded)){
					 js="("+smartRely+"," + js +")";
				 }
				 
				 fnAdd(g,{TEXT:v.text,ICON:v.icon,JSHANDLER:js,ORDERINDEX:idx++});
				 //fnEdit(g);
				 fnSave(g);
			 });
			 $.ajaxSettings.async=async;
			 dlg.destroy();
		 }},{text:"取消",iconCls:"icon-cancel",handler:()=>{
			 dlg.destroy();
		 }}]
	 });
	 var cmb=dlg.find("[name=smartcode]").combogrid({
		 width:550,panelWidth:600,
		 idField:'code',textField:'name',
		 mode:'remote',
		 url:"framework/query.do?search&k=queryForSmartform&exinfo=true",
		 //fitColumns:true,
		 columns:[[{field:'id',hidden:true},
			 		{field:'code',title:'编码',width:120},
			 		{field:'name',title:'描述',width:480}]
		 ],
		 onSelect:function(nv,ov){
			 var info=cmb.combogrid('grid').datagrid('getSelected');
			 if(!info) return;
			 info.smartid=info.id;
			 info.width=info.formWidth;
			 info.height=info.formHeight;
			 console.log(info)
			 dlg.find('.x').each((i,ele)=>{
				 $(ele).textbox('setValue',info[$(ele).attr('textboxname')])
			})
		}
	 })
	 var cb=dlg.find("[name=auto]").on("change",function(){
		 dlg.find(".s").textbox(this.checked?"disable":"enable");
	 })[0];
	 
	 return dlg;
 }