<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<z:config items="all,optionalSmartSources,smartformRuntimeConfigs"></z:config>
<z:dict items="all"></z:dict>
<link rel="stylesheet" type="text/css" href="webpage/framework/smartform/css/designer.css" />
<div id="viewport" class="easyui-layout" data-options="fit:true">
	<div class="datagrid-toolbar designer-toolbar" data-options="region:'north',height:40">
		<div id="pToolbar" style="float:left;"></div>
		<div id="pComponentShareTool" style="float:left;"></div>
		<div style="float:left;margin-left:10px;padding:5px;line-height:24px;border-left:1px solid rgba(0,0,0,0.2)"><span id="lblSelected">就绪...</span></div>
		<div id="pComponentTool" style="float:left;"></div>
		<div id="pExTools" style="float:right;"></div>
	</div>
	<div data-options="region:'west',split:true,width:200,title:'所有组件'">
		<div id="pLeft"  class="easyui-panel" data-options="fit:true">			
		</div>
	</div>
	<div data-options="region:'east',split:true,width:280,title:'选择和属性'">
		<div id="pRight"  class="easyui-layout" data-options="fit:true">	
			<div  data-options="region:'north',height:200,title:'选择',split:true,hideCollapsedContent:false">
				<div id="pSelect" style="width:100%;height:100%;"></div>
			</div>		
			<div data-options="region:'center',split:true,hideCollapsedContent:false,title:'&lt;span id=&quot;lblComponentName&quot; &gt;&lt;/span&gt;'">
				<div id="pProperty" style="width:100%;height:100%;"></div>
			</div>		
		</div>
	</div>
	<div  data-options="region:'center',border:false" style="padding-left:1px;">
		<div id="pMain" class="dash" style="width:100%;min-height:100%;padding:1px;" tabindex="0"></div>
	</div>
</div>

<script type="text/javascript" src="webpage/framework/smartform/util.js"></script>
<script type="text/javascript" src="webpage/framework/smartform/interface.js"></script>
<script type="text/javascript" src="webpage/framework/smartform/interfacex.js"></script>
<script type="text/javascript" src="webpage/framework/smartform/components/defines.js"></script>
<c:forEach items="${exdefines}" var="exdefine"><script type="text/javascript" src="webpage/framework/smartform/components/exdefines/${exdefine}"></script>
</c:forEach>
<script type="text/javascript" src="webpage/framework/smartform/components/designs.js"></script>
<c:forEach items="${exdesigns}" var="exdesign"><script type="text/javascript" src="webpage/framework/smartform/components/exdesigns/${exdesign}"></script>
</c:forEach>
<script type="text/javascript" src="webpage/framework/smartform/components/exports.js"></script>
<c:forEach items="${exexports}" var="exexport"><script type="text/javascript" src="webpage/framework/smartform/components/exexports/${exexport}"></script>
</c:forEach>
<script type="text/javascript" src="webpage/framework/smartform/topologic.js"></script>
<script type="text/javascript" src="webpage/framework/smartform/operate.js"></script>
<script type="text/javascript" src="webpage/framework/smartform/property.js"></script>
<script type="text/javascript">
	window.optionalInnerResources=$.grep([<c:forEach items="${optionalInnerResources}" var="r">"${r}",</c:forEach>null],function(r){return r && ["jquery","easyui","sunzui"].indexOf(r)<0});
	$.fn.datagrid.defaults.editors.checkbox={
		init:function(p,opt){return $("<input type=\"checkbox\">").appendTo(p);},
		getValue:function(ele){ return $(ele).is(":checked");},
		setValue:function(ele,v){$(ele)._propAttr("checked",v==true);}
	};
	window.designMode=true;
	window.componentDraggable=false;
	window.componentResizable=false;
	$(function(){
		var pComponents=new sunz.Accordion({parent:pLeft,multiple:true}),index=0,categories={};
		var innerIndex={"容器":1,"easyui组件":2,"原生Html":3,"页面关联":4,"地图":5},extIndex=6,getCateOrder=function(c){return innerIndex[c]||(extIndex++);};
		$.each($.map(defines,function(d,t){d.thumb=d.thumb||("webpage/framework/smartform/components/thumbs/"+ t.toLowerCase()+".png");return d;})
				.sort(function(a,b){return (getCateOrder(a.category)-getCateOrder(b.category))||((a.order||100)-(b.order||100));})
				,function(n,define){
			if(define.draggable==false)return;
			var strCate=define.category,cate=categories[strCate]=categories[strCate]||(function(t){
					var id="category"+index++;
					pComponents.add({title:t,selected:index<=2,content:'<div style="width:100%;" id="'+id+'" />'});
					return $("#"+id)[0];
				})(strCate);
			var comp=$(define.componentHtml?define.componentHtml:tplReplace('<div class="component" style="background-image:url({thumb})">{name}</div>',define));
			comp.attr("xtype",define.xtype);
			comp.attr("typeAlias",define.xtype.toLowerCase());
			comp.draggable({
				proxy : function(source) {
					var n = $('<div class="draggable-model-proxy"></div>');n.html($(source).html()).appendTo('body');
					return n;
				},
				revert : true,
				onDrag:function(e){
					findInsertPoint.call(this,e);
				},
				onStopDrag:function(e){
					window.onDragComplete.fire(this,e);
					if(this.dropTarget){						
					   addComponent(this.dropTarget,this.define,this.componentParams);
					   pMain.focus();
					}
				}
			});
			comp=comp[0];
			comp.define=define;
			cate.appendChild(comp);
			comp.componentParams=$.extend({},define.defaults,define.componentParams);
		});

		var dropConfig={
				accept : '*',
				onDragOver:function(e,source){
					source.dropTarget=this;
					// 计算子节点各边框,测试10万次的
					e.stopPropagation();
				},
				onDragLeave:function(e,source){
					delete source.dropTarget;
				},
				onDrop : function(e, source) {
					//addComponent(this,source.define);
					//e.stopPropagation();
				}
		};
		pMain.setting={};
		$(pMain).droppable(dropConfig)
		.on("keydown",function(e){
			if(e.keyCode==46 && e.target==this && selectedComponents.length){
				deleteComponents();
				e.stopPropagation();
			}
		})
		.on("click",function(e){
			selectComponent(null);
		});

		var treeSelect=window.tree=new sunz.Tree({parent:pSelect,fit:true,dnd:true,
			loadFilter:function(data,opt){
				var f=function(item){item.text=item.define.name+item.id;$.each(item.children,function(i,sub){f(sub);})};
				$.each(data,function(i,item){f(item);});
				return data;
			},
			onClick:function(n){
				var e=window.event||{};
				selectComponent(n.element,e.ctrlKey||e.shiftKey);
				pMain.focus();
			},
			onLoadSuccess:function(){
				
			}
		});
		window.onComponentSelectionChanged.add(function(sels,ele){
			if(ele){
				var cur=treeSelect.getSelected();
				if(cur!=null && cur.id==ele.setting.id) return;
				var n=treeSelect.find(ele.setting.id);
				treeSelect.select(n?n.target:null);
				if(n)treeSelect.scrollTo(n.target);
			}
			else treeSelect.select(null);
		});
		/*window.onComponentDelete.add(function(proxy){
			var node=treeSelect.find(proxy.setting.id);
			if(node)treeSelect.remove(node.target);
		});
		window.onComponentAdd.add(function(proxy,silent){
			var pele=proxy.parentNode,psetting=(proxy.parentMaster||{}).setting,node=$.extend({},proxy.setting);
			if(pele.insertPoint){
				treeSelect.insert({before:treeSelect.find(pele.insertPoint.setting.id).target,data:[node]});				
			}else{
				treeSelect.append({parent:(treeSelect.find((psetting||{}).id)||{}).target,data:[node]});
			}
		});
		*/window.onComponentsChanged.add(function(){treeSelect.loadData(parseDesigner());});
		
		window.onComponentAdd.add(function(compDiv,silent){
			var proxy=$(compDiv).find(".proxy-container");//$(".proxy-container[proxyid="+compDiv.setting.id+"]",compDiv);
			proxy.droppable(dropConfig);
			if(compDiv.setting.define.draggable!=false)
				$(compDiv).draggable({
					revert:true,disabled:!window.componentDraggable,
					onDrag:findInsertPoint,
					proxy:function(source) {
						var n = $('<div class="draggable-model-proxy"></div>');n.html($(source).html()).appendTo('body');
						return n;
					},onStopDrag:function(e){
						var div=this,dest=this.dropTarget;
						window.onDragComplete.fire(div,e);
						if(dest==null)return;
						if(dest.master==div.master) return;
						// is child break
						var isChild=false,pele=dest;
						while((pele=pele.parentNode)!=document.body && !(isChild=pele==div)){} 
						if(isChild) return;
						
						//div.remove();
						window.onComponentDelete.fire(div); 
						dest.appendChild(div); // 先插入，然后才能调整定点
						div.parentMaster=dest.master;
						if(dest.insertPoint){
							dest.insertBefore(div,dest.insertPoint);
						}
						window.onComponentAdd.fire(div,true);
						window.onComponentsChanged.fire();
					}
				});
			$(compDiv)/*.add(proxy)*/.on("mouseenter",function(){
				if(window.hoverComponent)$(window.hoverComponent).removeClass("hover");
				$(this).addClass("hover");
				tipHoveringComponent(this);
				window.hoverComponent=this;
			});
			$(compDiv)/*.add(proxy)*/.on("mouseleave",function(){
				$(this).removeClass("hover");
				tipHoveringComponent(null);
			});
			$(compDiv)/*.add(proxy)*/.on("click",function(e){
				selectComponent(this,e.ctrlKey||e.shiftKey);
				e.stopPropagation();
			});
			$(compDiv).resizable({
				disabled:!window.componentResizable,
				onStartResize:function(e){if(this.setting.fit)this.setting.fit=false;},
				onStopResize:function(e){
				 if(!this.master)return;
				 var deta=6;
				 var r= this.getBoundingClientRect(), w = (r.width||(r.right - r.left))-deta ,h = (r.height||(r.bottom - r.top))-deta;
				 this.setting.width=w;
				 this.setting.height=h;
				 window.onComponentSettingChanged.fire(this,this.setting);
				 try{this.master.resize({width:w,height:h});}catch(e){try{this.master.resize(w);}catch(ee){}}
			}});			
			
		});
		var id=getQueryParam("id")||getQueryParam("smartid");
		if(id)loadById(id);
	});
	
	var loadById=function(id){
		$.post("framework/datatable.do?getById&t=T_S_SMARTFORM", {id:id}, function(jr){
			if(jr.success){
				var def=jr.data;
				// 可能定义了方法给表单置用
				if(def.RESOURCES)$(def.RESOURCES).appendTo(document.head);
				
				def.FORMDEF=def.FORMDEF?$.decode(def.FORMDEF):[];
				def.LISTDEF=def.LISTDEF?$.decode(def.LISTDEF):[];
				//def.MOBILEFORMDEF=def.MOBILEFORMDEF?$.decode(def.MOBILEFORMDEF):[];
				//def.MOBILELISTDEF=def.MOBILELISTDEF?$.decode(def.MOBILELISTDEF):[];
				window.smartForm=def;
				parsePart(def.FORMDEF, pMain);
				window.onDefineLoad.fire(def);
			}else{
				$.messager.alert("表单加载出错",jr.msg+"<br/> 建议关闭此窗口，否则您的修改将作为新表单保存！");
			}				
		});
	};
	
</script>
