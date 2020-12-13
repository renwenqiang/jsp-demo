<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui,print"></z:resource>

<div class="easyui-layout" data-options="fit:true">
	<div data-options="region:'north'" style="height:48px">
		<div id="toolbar" style="padding:5px 5px 5px 5px;"></div>
	</div>
	<div id="pForm" data-options="region:'west',split:true" title="表单" style="width:300px;"></div>
	<div id="pConfig" data-options="region:'center',title:'配置'"></div>
</div>
<div id="pwSet" style="display:none;padding-left:10px;">
	<form id="pProperty" method="post">
		<table cellpadding="1">
			<tr>
				<td>x坐标(毫米):</td>
				<td><input class="easyui-numberbox" type="text" name="x" value="0"></input></td>
			</tr>
			<tr>
				<td>y坐标(毫米):</td>
				<td><input class="easyui-numberbox" type="text" name="y" value="0"></input></td>
			</tr>
			<tr>
				<td>每行字数:</td>
				<td><input class="easyui-numberbox" type="text" name="c" value="20"></input></td>
			</tr>
			<tr>
				<td>行间距(毫米):</td>
				<td><input class="easyui-numberbox" type="text" name="d" value="20"></input></td>
			</tr>
			<tr>
				<td>字体名称:</td>
				<td>
					<select id="cmbFont" class="easyui-combobox" name="f"  data-options="width:150">
						<option value="sl">宋体</option>
						<option value="es">楷体</option>
						<option value="sv">新魏</option>
						<option value="th">新宋</option>
						<option value="tr">黑体</option>
						<option value="uk">微软雅黑</option>
				</td>
			</tr>
			<tr>
				<td>字体大小:</td>
				<td><input class="easyui-numberbox" name="s" type="text"  value="12"></input></td>
			</tr>
		</table>
		<fieldset>
			<legend>尺寸设置（仅图片有效）</legend>
				<table cellpadding="1">
				<tr>
					<td>宽度(毫米):</td>
					<td><input class="easyui-numberbox" type="text" name="w" value="0"></input></td>
				</tr>
				<tr>
					<td>高度(毫米):</td>
					<td><input class="easyui-numberbox" type="text" name="h" value="0"></input></td>
				</tr>
				</table>
		  </fieldset>
	</form>
	<div id="bbar" style="padding:5px;position:absolute;right:10px;bottom:2px;left:50%;height:26px;"></div>
</div>


<script type="text/javascript">
//全局
var elementInit=function(eles){
	currents=eles;	
	applySetting();
	$.each(eles,function(i,ele){
		$(ele).on("click",function(){
			setPrintSetting(this);
		});
	});
};
$(function(){
 	var pToolbar=$("#toolbar"), pForm=$("#pForm").asPanel(),pCenter=$("#pConfig").asPanel(),formSetting=window.formSetting=$("#pProperty").asForm();
 	winSetting=$("#pwSet").asWindow({left:310,top:72,iconCls:'icon-comturn',width:260,height:365,title:"打印元素设置",minimizable:false,maximizable:false,closable:false});
 	winSetting.show();
 	
 	new sunz.Linkbutton({parent:$("#bbar")[0],iconCls:'icon-save',text:'　应用　　',handler:function(){
 		applyToElement.call(winSetting);
 	}});
	
 	var loadForm=function(url){pForm.html('<iframe style="width:100%;height:100%" fit="true" src="'+url+'"></iframe>');};
 	// 加载
 	new sunz.Linkbutton({parent:pToolbar[0],iconCls:'icon-add',text:'加载表单',handler:function(){
 		$.messager.prompt("修改表单地址","表单地址",function(r){
	 		if(r) loadForm(r);
 		});
 	}});
 	// dpi
 	new sunz.Linkbutton({parent:pToolbar[0],iconCls:'icon-reload',text:'手动调整dpi',handler:function(){
 		var dpibase=Math.sqrt(screen.width*screen.width+screen.height*screen.height);
		$.messager.prompt('手动调整dpi','DPI='+dpibase+'/你电脑的英寸数，比如当前电脑为14寸的话dpi为'+(dpibase/14),function(ppi){if(ppi)dpi=ppi/1;});
 	}});
 	// 可视化
 	var all=[];
	new sunz.Linkbutton({parent:pToolbar[0],iconCls:'icon-tip',text:'可视化修改',handler:function(){
		$("#divSetting").remove();
		var dSetting=$('<div id="divSetting" style="position:aboslute;top:0;left:0;right:0;bottom:0;"></div>');
		pCenter.append(dSetting);
		if(!currents){
			elementInit(frames[0].$(":input,img").toArray());
		}
		$.each(currents=currents,function(i,ele){
			if(ele.offsetHeight==0) return; // 未显示
			
			initDefault(ele);
			all.push(showTo(dSetting,ele));
		});
 	}});
	//偏移
	var winOffset=null;
	new sunz.Linkbutton({parent:pToolbar[0],iconCls:'icon-sum',text:'整体偏移',handler:function(){
		if(!winOffset){
			var fSet = $('<form >'
				   +'<table cellpadding="1">'
				   +'<tr>'
				   +'	<td>X偏移(毫米):</td>'
				   +'	<td><input class="easyui-numberbox" type="text" name="dx" value="0"></input></td>'
				   +'</tr>'
				   +'<tr>'
				   +'	<td>Y偏移(毫米):</td>'
				   +'	<td><input class="easyui-numberbox" type="text" name="dy" value="0"></input></td>'
				   +'</tr>'
				   +'</table>'
				   +'</form>').asForm();
			var bbar=$('<div style="padding:5px;position:absolute;right:10px;bottom:2px;left:20%;height:26px;"></div>');
			new sunz.Linkbutton({parent:bbar[0],iconCls:'icon-ok',text:'确定',handler:function(){
		 		var o=fSet.getFieldValues(),dx=o.dx,dy=o.dy;
				$.each(all,function(i,ele){
					var newx=ele.printSetting.x+dx,newy=ele.printSetting.y+dy;
					$.extend(ele.printSetting,{x:newx,y:newy});
					ele.setPosition(newx,newy);
				});	
				winOffset.close();
		 	}});
			winOffset=new sunz.Window({title:'整体偏移设置',width:320,height:140,modal:true});
			winOffset.append(fSet);
			winOffset.append(bbar);
		}
		winOffset.open();
 	}});
	//打印
	var winTest=null;
	new sunz.Linkbutton({parent:pToolbar[0],iconCls:'icon-print',text:'打印测试',handler:function(){
 		if(winTest==null){
 			var w=new sunz.Window({title:'打印测试，选择打印机',width:320,height:120,modal:true});
 			var f=new sunz.Form({parent:w[0],style:'padding:5px 20px'});//$('<form><table><tr><td>选择：</td><td><select id="cmbTest" class="easyui-textbox" name="printer"  data-options="width:150"></select></td></tr></table></form>').asForm();
 			var cmbPrinter=new sunz.Combobox({parent:f[0],width:280,data:getLocalPrinters()});//$("#cmbTest").asCombobox({width:150,data:printers});//
 			var bbar=$('<div style="padding:5px;position:absolute;right:10px;bottom:2px;left:50%;height:26px;"></div>');
			new sunz.Linkbutton({parent:bbar[0],iconCls:'icon-ok',text:'确定',handler:function(){
				setPrinterSetting(printer,cmbPrinter.getValue());
        		w.close();
         		printer.Clear();
        		$.each(currents,function(i,ele){
        			if(ele.offsetHeight==0)return;
        			addToPrint(printer,ele,0,0,"测试字符","图片加载出错");
        		});
        		printer.Print();
		 	}});
 			//f.show();//cmbPrinter.show();
 			f.append(cmbPrinter);
 			w.append(f);
 			w.append(bbar);
 			winTest=w;
 			//var f=$('<form><table cellpadding="1"><tr><td>打印机:</td><td><select id="cmbTestPrinter" class="easyui-textbox" type="text" name="printer"  data-options="width:150"></select></td></tr>')
 		}

		winTest.open();
 	}});
	// 设置
	var printerSettingEditor=null;
	new sunz.Linkbutton({parent:pToolbar[0],iconCls:'icon-comturn',text:'打印机设置',handler:function(){
		if(!printerSettingEditor)
			printerSettingEditor=new PrinterSettingEditor(${param.formid});
		
		printerSettingEditor.edit();
 	}});
	// 保存
	new sunz.Linkbutton({parent:pToolbar[0],iconCls:'icon-save',text:'保存',handler:function(){
		var count=currents.length,complete=0,success=0;
		$.each(currents,function(i,ele){
			var s=initDefault(ele);
			if(ele.tagName=="IMG"){delete s.d;delete s.c;delete s.f;delete s.s;}
			else{delete s.w;delete s.h; }
			
			var param=$.extend({},s);
			delete param.id;delete param.field;
			
			param.setting=$.encode(param);
			param.formid=${param.formid};	
			param.element=s.field;
			
			var url='framework/datatable.do?'+(s.id?'save':'add')+'&t=T_S_PrintElementSetting';
			$.ajax(url,{data:param,success:function(r){
				complete++;
				if(r.success){
					success++;	
					s.id=r.data.ID;//ele.printSetting.id=r.data.id; // 因为作用域的关系，用ele来处理
				}
				if(complete==count){
					if(success==count)
						$.messager.alert("完成");
					else 
						$.messager.alert("完成"+success+"/"+count);
				}
			}});
		});
 	}});
	
	// 加载
	loadSetting(${param.formid});
	var url=(function(){var reg=new RegExp("[?&]url=(.*)");var m=window.location.search.match(reg);return m?decodeURIComponent(m[1]):"";})();
	if(url) loadForm(url);
});	
 </script>