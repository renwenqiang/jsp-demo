<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui,print"></z:resource>

<div id="pwSet" style="display:block">
	
	<form id="ff" method="post" >
		<table cellpadding="5">
			<tr>
				<td>x坐标(毫米):</td>
				<td><input class="easyui-textbox" type="text" field="x坐标" name="x" ></input></td>
			</tr>
			<tr>
				<td>y坐标(毫米):</td>
				<td><input class="easyui-textbox" type="text" field="y坐标" name="y" ></input></td>
			</tr>
			<tr>
				<td>每行字数:</td>
				<td><input class="easyui-textbox" type="text" field="每行字数" name="c" ></input></td>
			</tr>
			<tr>
				<td>行间距(毫米):</td>
				<td><input class="easyui-textbox" type="text" field="行间距" name="d"></input></td>
			</tr>
			<tr>
				<td>字体名称:</td>
				<td>
					<select class="easyui-combobox" name="f">
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
				<td><input class="easyui-textbox" name="s" type="text"></input></td>
			</tr>
		</table>
		<fieldset>
			<legend>尺寸设置（仅图片有效）</legend>
				<table cellpadding="5">
				<tr>
					<td>宽度(毫米):</td>
					<td><input class="easyui-textbox" type="text" name="name" ></input></td>
				</tr>
				<tr>
					<td>高度(毫米):</td>
					<td><input class="easyui-textbox" type="text" name="email" ></input></td>
				</tr>
				</table>
		  </fieldset>
	</form>
	<div id="bbar" style="padding:5px;margin:20px 100px;height:26px;"></div>
</div>

<script type="text/javascript" >
$(function(){
	new sunz.Linkbutton({parent:$("#bbar")[0],iconCls:'icon-print',text:'打印测试',handler:function(){
		var w=new sunz.Window({title:'打印测试，选择打印机',width:320,height:120,modal:true});
		var f=new sunz.Form({parent:w[0],style:'padding:5px 20px'});//$('<form><table><tr><td>选择：</td><td><select id="cmbTest" class="easyui-textbox" name="printer"  data-options="width:150"></select></td></tr></table></form>').asForm();
		var cmbPrinter=new sunz.Combobox({parent:f[0],width:280,data:getLocalPrinters()});//$("#cmbTest").asCombobox({width:150,data:printers});//
		var bbar=$('<div style="padding:5px;position:absolute;right:10px;bottom:2px;left:50%;height:26px;"></div>');
		new sunz.Linkbutton({parent:bbar[0],iconCls:'icon-ok',text:'确定',handler:function(){
    		w.close();
    		// 以下几行是打印的关键所在
    		// 先进行打印机偏好设置（实际中可以Cookie缓存）
			setPrinterSetting(printer,cmbPrinter.getValue());
    		// 清空
     		printer.Clear();
    		//添加元素
    		$.each(currents,function(i,ele){
    			if(ele.offsetHeight==0)return;
    			addToPrint(printer,ele,0,0,"测试字符","图片加载出错");
    		});
    		// 打印
    		printer.Print();
    		//////////////////////////
	 	}});
		w.append(bbar);
		w.open();
	}});
	
	// 加载
	loadSetting(${param.formid});	
	currents=$(":input").toArray();
});
</script>