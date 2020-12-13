<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<%@include file="/sunzbase.jsp"%>
	<title><%=com.sunz.framework.core.Config.get("title")%></title>
	<meta http-equiv="X-UA-Compatible" content="IE=10,chrome=1" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />	
	<z:resource items="jquery,easyui,sunzui"></z:resource>
	<z:dict items="all"></z:dict>
	<z:config items="all"></z:config>
	<link rel="stylesheet" type="text/css" href="webpage/main/main.css">
	<script type="text/javascript" src="webpage/framework/resource/js/compatible.js"></script>
</head>
<body>
	<div id="viewport" class="easyui-layout" data-options="fit:true">
		<div id="pTop" data-options="region:'north',height:68" >
			<table width="100%" border="0" cellpadding="0" cellspacing="0">
				<tr>
				    <td align="left" style="vertical-align: text-bottom">
				    	<img class="logo" src="webpage/main/images/logo.png"></img> 
				    </td>
				    <td align="right" nowrap>
						<table border="0" cellpadding="0" cellspacing="0" width="100%">
				            <tr style="height:64px;">
				             	<td>
				                    <ul id="divLevel1Menu" class="menuLevel1">
				                    </ul>
				                </td>
				                <td align="right" width="" vertical-align="bottom";>				                
				                    <div class="top-bg">
					                    <div style="float: left; line-height: 35px; margin-left: 70px;">
					                        <span style="color: #386780"><img src="webpage/main/images/user.png"></span> 
					                        <span style="color: #FFFFFF">${LOCAL_CLINET_USER.realName}</span>
					                    </div>
					                    <div style="float:left; margin:0 18px;">
					                        <a href="javascript:void(0);" class="easyui-menubutton" menu="#layout_north_kzmbMenu" iconCls="icon-comturn" style="color: #FFFFFF">控制面版</a>
					                        <a href="framework/login.do?logout" style="color:#fff"><span class="l-btn-text icon-exit">注销</span></a>
					                        <div id="layout_north_kzmbMenu" style="width: 100px; display: none;">
					                            <div onclick="createwindow('密码修改','userController.do?changepassword',568,254)">密码修改</div><div class="menu-sep"></div>
					                            <div onclick="openwindow('系统消息','tSSmsController.do?getSysInfos')">系统消息</div><div class="menu-sep"></div>
					                            <div ><span>切换主题</span>
					                            <script type="text/javascript">function setTheme(t){$.post("framework/resources.do?setTheme",{theme:t},function(r){$.messager.show({title:"温馨提示",msg:r.success?"切换成功，请刷新页面":("出错了"+r.msg)});},"json");}</script>
					                            	<div id="optionThemes">
					                            		<c:forEach items="${applicationScope.optionThemes}" var="theme"><div onclick="setTheme('${theme}')">${theme}</div></c:forEach>
										            </div>
										        </div>
					                        </div>
					                    </div>
				                    </div>
				                </td>
				            
				               
				            </tr>
			        	</table>
			        </td>
			       </tr>
			</table>
		</div>
		<div id="pLeft" data-options="region:'west',width:240,title:'导航菜单',split:true"></div>
		<div data-options="region:'center'">
			<div id="maintabs" data-options="fit:true" class="easyui-tabs"></div>
		</div>
	</div>

<script type="text/javascript" >
	var icons={},ai=function(id,name,path){icons[id]={name:name,path:path};};
	<c:forEach items="${icons}" var="icon">ai('${icon.id}','${icon.iconClas}','${icon.iconPath}');</c:forEach>
	var menus=[],dict={},am=function(id,name,url,icon,order,pid){var m=dict[id]={id:id,text:name,url:url,icon:(icons[icon]||{}).name,order:order,pid:pid,iconPath:(icons[icon]||{}).path};menus.push(m);};
	<c:forEach items="${menus}" var="menu">am('${menu.ID}','${menu.FUNCTIONNAME}','${menu.FUNCTIONURL}','${menu.ICONID}',${menu.FUNCTIONORDER},'${menu.PARENTFUNCTIONID}');</c:forEach>
	// 排序
	menus.sort(function(a,b){return a.order-b.order;});
	// 组装	
	$.each(menus,function(i,m){
		if(!m.pid) return;
		
		var pm=dict[m.pid];
		if(pm){
			pm.children=pm.children||[];
			pm.children.push(m);
		}
	});
	var menu=new sunz.Menu();
	menu.appendItem({text:"关闭",onclick:function(){window.tabs.close(menu.target)}});
	menu.appendItem({text:"关闭全部",onclick:function(){$('.tabs-inner .tabs-closable').each(function(i,ele){window.tabs.close($(ele).text());});}});
	menu.appendItem({text:"关闭其它",onclick:function(){$('.tabs-inner .tabs-closable').each(function(i,ele){if($(ele).text()==menu.target)return;window.tabs.close($(ele).text());});}});
	var addTab=function(title,url,icon){
		var tabs=window.tabs;
		if(tabs.exists(title)){
			tabs.select(title);
		}else{
			tabs.add({
				title : title,
				content : '<iframe src="' + url + '" frameborder="0" scrolling="0" style="border:0;width:100%;height:100%;"></iframe>',
				closable : true,
				iconCls : icon
			});
		}
		$(".tabs-inner").bind('contextmenu', function(e) {
			menu.target=$(this).children(".tabs-closable").text();
			menu.show({
				left : e.pageX,
				top : e.pageY
			});
			return false;
		});
		var tab={title:title,href:url,icon:icon,
				show:function(){addTab(title,url,icon);},
				close:function(){tabs.close(title);},
				active:function(){tabs.select(title);},
				refresh:function(opt){tabs.update({tab:tabs.getTab(title),options:opt});},
				set:function(n,v){this[n]=v;this.refresh(this);}
		};
		$.each(["Title","Href","Id","Content","IconCls","Width","Height"],function(i,n){tab["set"+n]=function(v){
			this.set(n.substr(0,1).toLowerCase()+n.substr(1),v);}});
		return tab;
	};
	$(function(){
		window.tabs=$(maintabs).asTabs();
		window.openwindow=function(t,url,n,w,h){
			//if(!w && !h) return addTab(t,url);
			if(typeof n=="number"){h=w;w=n;}
			var c={modal:true,title:t,content:'<iframe src="'+url+'" style="width:100%;height:100%" ></iframe>',width:w,height:h};
			if(!w && !h) c.fit=true;
			return new sunz.Window(c);
		};
		var mLevel1=$.grep(menus,function(m){return !m.pid && !!m.children;});
		var html='<li class="menuMoreLeft" ><div class="dMenuMoreLeft" ></div></li>';
		$.each(mLevel1,function(i,m){
			html +=tplReplace('<li class="rootMenuItem" menuid="{id}"><div class="dLevel1Bg" style="background:url({iconPath}) no-repeat"><div class="dLevel1">{text}</div></div></li>',m);
		});
		html=html+'<li class="menuMoreRight" ><div class="dMenuMoreRight" ></div></li>';
		$(html).appendTo(divLevel1Menu);
		
		var tree=window.tree=new sunz.Treegrid({
			parent:pLeft,fit:true,fitColumns:true,
			columns:[[{field:'text',title:'',width:520,formatter:function(v,r,i){return r.children?v:tplReplace('<div class="menu_icon" style="background:url({iconPath}) no-repeat" >{text}</div>',r);}}]],
			idField:"id",treeField:"text",
			onClickRow:function(r){
				if(!r.url)return;
				addTab(r.text,r.url,r.icon);
			}
		});
		var selectedLevel1=null,toggleMenu=function(ele){$(".dLevel1",ele).toggleClass("activeMenu");};
		var menuDivs=$('.rootMenuItem',divLevel1Menu);
		menuDivs.click(function(){
			if(selectedLevel1 !=null){
				toggleMenu(selectedLevel1);
			}
			selectedLevel1=this;
			toggleMenu(this);
		    tree.loadData(dict[$(this).attr('menuid')].children);
		    $(".tree-file").remove();
		});
		menuDivs[0].click();
		var showCount=8,showPage=-1,menuCount=menuDivs.length,toggleMore=function(step){
			showPage +=step;
			var start=showPage*showCount,end=start+showCount;
			if(end>menuCount) {end=menuCount;start=end-showCount;}
			$.each(menuDivs,function(i,d){$(d).css("display",((i<start||i>=end)?"none":"block"));});
			$('.menuMoreLeft').css("display",start>0?"block":"none");
			$('.menuMoreRight').css("display",end<menuCount?"block":"none");
		};
		$('.menuMoreLeft').click(function(){toggleMore(-1);});
		$('.menuMoreRight').click(function(){toggleMore(1);});
		$('.menuMoreRight').click();
	});
	
</script>
</body>
</html>