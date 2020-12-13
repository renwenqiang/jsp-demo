<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<style>
<!--
	.htmlcell{width:100%;height:48px;}
-->
</style>

<div class="easyui-layout" data-options="fit:true">
	<div id="pSearch" data-options="region:'north',title:'搜索'" style="height:88px">
		<form id="fSearch">
			<table style="width:80%;margin:5px 10%;">
				<tr>
					<td width="5%"></td>
					<td >年份：</td><td><input type="text" class="easyui-numberspinner" data-options="min:2016,max:2018" name="year" /></td>
					<td >月份：</td><td><input type="text" class="easyui-numberspinner" data-options="min:1,max:12" name="month" /></td>
					<td>
					<a onclick="window.search()" class="easyui-linkbutton"  data-options="iconCls:'icon-search'" >查询</a>
					<a onclick="$('#fSearch').asForm().reset();window.search();" style="margin-left:25px" class="easyui-linkbutton"  data-options="iconCls:'icon-redo'" >重置</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="pMain" data-options="region:'center',title:'节假日一览表--需要维护某日期请右键'" style="padding:5px 5px 5px 5px;"></div>
</div>

<script type="text/javascript" >
var curYear=defaultYear=new Date().getFullYear(),curMonth;
var search=function(){
	var o=$("#fSearch").asForm().getFieldValues();
	o.year=o.year||defaultYear;
	curMonth=o.month;
	if(curYear!=o.year){
		curYear=o.year;
		tree.load(o);
	}else
		tree.loadData(tree.orgData);
};
$(function(){
	$("#fSearch").asForm().load({year:defaultYear,month:null});
	var renderer=function(v,r,i){
		return v?('<span style="color:'+(v.holiday?"green":"blue")+'">'+v.day+(v.remark?('('+v.remark+')'):"")+'</span>'):null;
	};
	var tree=window.tree=new sunz.Treegrid({
		parent:pMain,
		fit:true,fitColumns:true,
		autoRowHeight:true,
		pagination:false,rownumbers:false,striped:true,
		columns:[[{field:'month',title:'月份/周次',width:120,rowspan:2,formatter:function(v){return $.isNumeric(v)?(v+"月"):v;}},{field:"day",title:'日期/星期',colspan:8}],[
		         {field:'id',hidden:true},
		         {field:'mon',title:'星期一',width:100,formatter:renderer},
		         {field:'tue',title:'星期二',width:100,formatter:renderer},
		         {field:'wed',title:'星期三',width:100,formatter:renderer},
		         {field:'thu',title:'星期四',width:100,formatter:renderer},
		         {field:'fri',title:'星期五',width:100,formatter:renderer},
		         {field:'sat',title:'星期六',width:100,formatter:renderer},
		         {field:'sun',title:'星期日',width:100,formatter:renderer}
		]],
		idField:"id",treeField:'month',
		url:"framework/calendar.do?yearDays",
		queryParams:{year:curYear},
		loadFilter:function(jr){
			if(!jr.success)return null;
			tree.orgData=jr;
			var days=[],keys=["mon","tue","wed","thu","fri","sat","sun"], samp=jr.data[0],offset=new Date(samp.year,samp.month-1,samp.day).getDay()-1;
			var month,week;
			if(offset<0)offset=offset+7;
			$.each(jr.data,function(i,d){
				if(curMonth && curMonth!=d.month) return;
				if(month==null){month={month:d.month,children:[],id:d.month};}
				var dOfWeek=(i+offset)%7;
				if(week==null||month.month!=d.month||dOfWeek==0){
					if(week)month.children.push(week);
					week={month:"第"+((month.month!=d.month?0:month.children.length)+1)+"周",id:d.id};
				}
				if(month.month!=d.month){
					days.push(month);
					month={month:d.month,children:[],id:d.month};
				}
				
				week[keys[dOfWeek]]=d;
			});
			// 最后一波
			month.children.push(week);
			days.push(month);
			
			return {rows:days,total:days.length};
		},
		onContextMenu:function(e, r){
			if(tree.selectedCell){tree.selectedCell.style.backgroundColor=tree.selectedCell.orgColor;}
			var ele=e.target,cell=ele.tagName=="TD"?ele:(ele.tagName=="DIV"?ele.parentNode:ele.parentNode.parentNode),attr=cell.attributes.field;
			tree.selectedCell=cell;
			if(attr==null)
				return;

			var field=attr.value
			var day=r[field];
			if(day==null)
				return;
			
			cell.orgColor=cell.style.backgroundColor;
			cell.style.backgroundColor="red";
			var menu=new sunz.Menu();
			var text=day.holiday?"设置为工作日":"设置为节假日";
			menu.appendItem({text:text,onclick:function(){
				$.messager.prompt("您确定要"+text+"吗","若继续请填写备注：",function(pmp){
					if(!pmp)return;
					$.post("framework/calendar.do?"+(day.holiday?"setWorkday":"setHoliday"),{date:day.year+'-'+(day.month<10?('0'+day.month):day.month)+'-'+(day.day<10?('0'+day.day):day.day),remark:pmp},function(jr){
						if(jr.success){
							$.messager.show({title:"恭喜您",msg:"设置成功"});
							day.holiday=!day.holiday;
							day.remark=pmp;
							tree.update(r);
						}
						else 
							$.messager.show({title:"对不起",msg:jr.msg});
					});
				});
			}});
			
			e.preventDefault();
			menu.show({left: e.pageX,top: e.pageY});
		}
	});	
});
</script>