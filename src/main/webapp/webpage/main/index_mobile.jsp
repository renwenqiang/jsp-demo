<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><!DOCTYPE html>
<html>
<head>
	<title>三正科技基础开发（移动）平台</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
	<%@include file="/sunzbase.jsp"%>
	<z:resource items="jquery,easyui,sunzui,mobile"></z:resource>
	<style type="text/css">
	.l-btn-icon-top .l-btn-text {
	    margin-top: 68px !important;
	    min-width: 68px !important;
	}
	.l-btn-icon-top .l-btn-icon {width:68px !important;height:68px !important;left: 20px !important;}
	<c:forEach items="${icons}" var="icon">.${icon.iconClas}{background: url(${icon.iconPath}) no-repeat center center;}
	</c:forEach>
	</style>
</head>
<body>
	 <div id="viewport" class="easyui-navpanel">
        <header>
            <div class="m-toolbar">
             	<div class="m-left">
                </div>
                <div class="m-title">三正移动智能工作平台</div>
                <div class="m-right">
                    <a href="javascript:void(0)" class="easyui-menubutton" data-options="iconCls:'icon-more',menuAlign:'right',hasDownArrow:false"></a>
                </div>
            </div>
        </header>
	    <!-- <footer>
	        <div class="m-toolbar">	            
	        </div>
	    </footer>-->
    </div>
    
   <script type="text/javascript">
    var icons={},ai=function(id,name,path){icons[id]={name:name,path:path};};
	<c:forEach items="${icons}" var="icon">ai('${icon.id}','${icon.iconClas}','${icon.iconPath}');</c:forEach>
    var menus=[],dict={},am=function(id,name,url,icon,order,pid){var m=dict[id]={id:id,text:name,url:url,icon:(icons[icon]||{}).name,order:order,pid:pid,iconPath:icons[icon]||{}.path};menus.push(m);};
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
   	$(function(){
   		var viewport=$("#viewport")[0],pt=$(".m-left")[0];
   		var btnBack=new sunz.Linkbutton({parent:pt,iconCls:"icon-back",width:36,onClick:function(){viewport.back();}});
   		var btnRedo=new sunz.Linkbutton({parent:pt,iconCls:"icon-redo",width:36,onClick:function(){viewport.redo();}});
   		viewport.enableBack=function(f){if(f==false)btnBack.hide();else btnBack.show();};
   		viewport.enableRedo=function(f){if(f==false)btnRedo.hide();else btnRedo.show();};
   		viewport.stackChanged=function(){
   			viewport.enableBack(viewport.current>0);
   			viewport.enableRedo(viewport.stack[viewport.current+1]!=null);
   		};
   		viewport.stack=[];
   		viewport.current=-1;
   		viewport.getActive=function(){return viewport.current<0?null:viewport.stack[viewport.current];}
   		viewport.back=function(){
   			var pre=viewport.current-1;
   			if(pre<0)return;
   			var p=viewport.stack[pre];
   			if(viewport.getActive()) {
   				viewport.getActive().hide();
   			}
   			p.show();
   			viewport.current--;
   			viewport.stackChanged();
   		};
   		viewport.redo=function(){
   			var next=viewport.current+1;
   			var p=viewport.stack[next];
   			if(viewport.getActive()) {
   				viewport.getActive().hide();
   			}
   			p.show();
   			viewport.current++;
   			viewport.stackChanged();
   		};
   		viewport.add=function(xtype,config){
   			if(viewport.getActive()) {
   				viewport.getActive().hide();
   			}
   			var v=new sunz.Layout({parent:viewport,fit:true});
   			viewport.stack[++viewport.current]=v;
   			viewport.stack[viewport.current+1]=null;
   			viewport.stackChanged();
   			
   			config=config||(typeof xtype =="object"?(config=xtype,xtype=null,config):null);
   			if(config||xtype){
   				xtype=xtype||"Panel";
   			 	new sunz[xtype]($.extend({parent:v[0],fit:true},config));
   			}
   			
   			return v;
   		};
   		
   		
   		var mLevel1=$.grep(menus,function(m){return !m.pid && !!m.children;});
   		var tpl='<a style="margin:12px 5px 5px 15px;" href="javascript:void(0);" class="category easyui-linkbutton" data-options="iconCls:\'{icon}\',size:\'large\',iconAlign:\'top\',plain:true">{text}</a>';
   		var v=viewport.add();
   		$(tplReplace(tpl,{text:"待办列表",icon:"deskIcon"})).linkbutton({onClick:function(){
			viewport.add("Datalist",{
				lines: true,
				url:'framework/flow.do?todoData',
				textFormatter:function(v,r,i){
					return tplReplace('<div class="datalist-link"><h>{title}</h><div style="margin:5px;"><li>业务类型：{jobname}</li><li>所在环节：{stepname}</li><li>流转时间：{tasktime}</li><li>状态：{signer}</li></div></div>',r);
				}
			});
		}}).appendTo(v);
   		$.each(mLevel1,function(i,m){
   			$(tplReplace(tpl,m)).linkbutton({onClick:function(){
   				var p=viewport.add("Datalist",{
   					lines: true,
   					data:m.children,
   					textFormatter:function(v,r,i){
   						return tplReplace('<div class="datalist-link"><a class="easyui-menubutton" data-options="iconCls:\'{icon}\',hasDownArrow:false" href="javascript:void(0);" onclick="viewport.add({content:\'<iframe style=width:100%;height:100%; src={url} ></iframe>\'});" >{text}</a></div>',r);
   					}
   				});
   				$.parser.parse(p);
   				/* 宫格方式
   				var p=viewport.add();
   				$.each(m.children,function(si,sm){
   					$(tplReplace(tpl,sm)).linkbutton({onClick:function(){
   						
   					}}).appendTo(p);
   				});*/
   			}}).appendTo(v);
   		});
   	});
   </script>
</body>
</html>