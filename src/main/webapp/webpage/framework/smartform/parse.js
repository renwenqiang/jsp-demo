window.onSmartDefineLoad=$.Callbacks();
window.onBeforePartRender=$.Callbacks();
window.onPartRendered=$.Callbacks();
window.onIntercept=$.Callbacks();
window.onSmartRendered=$.Callbacks();

// ({handled,goingon,新增：baseData，其它为target})
window.onBeforeAdd=$.Callbacks("stopOnFalse"); 
window.onBeforeEdit=$.Callbacks("stopOnFalse");
window.onBeforeDelete=$.Callbacks("stopOnFalse");
window.onBeforeView=$.Callbacks("stopOnFalse");
// ({handled,goingon,changes,data:fullData})
window.onBeforeSave=$.Callbacks("stopOnFalse");

window.onDeleted=$.Callbacks();// (selected)
window.onSaved=$.Callbacks();  // (fullData)

onComponentAdd.add(function(div){if(div.setting.hidden)$(div).hide();});

window.authMap={};
onSmartDefineLoad.add(function(o){
	// 字典项
	if(o.DICTS){
		if($("script[src*=dict\\.js]").length>0 || window._alldict)return;
		$.each(o.DICTS.split(/\s*,\s*/),function(i,d){
			if(!d) return;
			if(d !="all" && D.getItem(d)) return;
			$.ajax("framework/dict.do?xsubs&item="+d,{cache:true,async:false,success:function(jr){if(jr.success){D.add(jr.data);}}});
			if(d=="all"){
				window._alldict=true;
				return false;
			}
		});
	}
	// 额外资源
	if(o.RESOURCES)$(o.RESOURCES).appendTo(document.head);
	
	// 权限
	if(window.jobkey&&window.stepkey)
		$.ajax("framework/datatable.do?t=T_S_SmartUI_Step&exactly",{
			async:false,
			data:{jobkey:window.jobkey,stepkey:window.stepkey,smartcode:o.formCode},
			success:function(jr){
				$.each(jr.data,function(i,d){
					window.authMap[d.UIID]=d;
				});
			}
		});
	// editlog
	if(o.logRequired||o.historyable!=false)
		$("#editlog-support").length || $('<script id="editlog-support" type="text/javascript" src="webpage/framework/smartform/editlog.js"></script>').appendTo("head")
});
onBeforePartRender.add(function(def,setting){
	(function reviveOther(defs){
		if(defs!=null && defs!=undefined && typeof defs ==="object"){
			if(defs.otherSetting){
				$.extend(defs,parseOtherSetting(defs.otherSetting));
				delete defs.otherSetting;
			}
			for(var n in defs)
				reviveOther(defs[n]);
		}
	})(def);
	(function intercept(defs){$.each(defs,function(i,item){onIntercept.fire(item,setting);intercept(item.children)});})(def);	
	reviveFunctionInJson(def);
});

onIntercept.add(function(item,setting){
	var auth=window.authMap[item.id];
	if(auth){var c=auth.CONTROL;if(c)$.extend(item,c);else item[auth.CONTROLTYPE]=true;}
});
function callWithEvent(e,fn,data){
	if(e.goingon){
		e.remove(e.goingon);
	}
	var eventArg=$.extend({handled:false,goingon:fn},data);
	e.add(e.goingon=function(){		
		if(eventArg.handled)
			return;
		fn();
	});
	e.fire(eventArg);
}
var validateForeigns=function(rMap,pfData){
	return $.grep(rMap,function(rm,i){return !(rm.from?pfData[rm.from]:pfData.id||pfData.ID);}).length==0;
};
var smartSave=function(form,setting,pfData,fn,skipSub){
	var eventArg={handled:false,msg:null}
	if(!form.validate(eventArg)){
		if(!eventArg.handled)
			$.messager.show({title:"友情提示",msg:eventArg.msg||"验证未通过，请修改数据后再提交！"});
		
		return false;
	}
	var saveSub=function(parentData,cb){
		var calls=[],index=0;
		$.each(form.items,(i,comp)=>comp.save&&calls.push(()=>comp.save(parentData,calls[++index])))		
		calls.push(()=> cb && cb());
		calls[0]();
	};
	var realSave=function(fni){
		var nv=form.getFieldValues(),c=form.getChanges(nv),o=$.extend(true,{},form.fullData,nv);
		var inherit={}; // 继承和关联的值，须作为changes的参考对象
		loadRelation(setting.relationMap,pfData,inherit);
		$.extend(true,o,inherit);
		o.ID=o.ID||o.id;delete o.id; // 仅留大写
		inherit.ID=o.ID;delete inherit.id; // id由机制本身（fullData）保证
		
		var cb=()=> fni&&fni(form.fullData);
		if(o.ID && c.length==0){
			return !skipSub && saveSub(o,cb);
		}
		callWithEvent(onBeforeSave,function(){
			r=function(fni,o,c){
				var isAdd=!o.ID;
				var param=o;
				if(!isAdd/*add模式不能使用脏机制*/ && setting.dirty!=false){ // 仅提交脏数据支持，默认支持，原因是默认的datatable本身支持
					param={ID:o.ID};
					$.each(c,function(i,v){param[v.name]=v.to});
				}
				$.ajax(isAdd?setting.addUrl:setting.saveUrl,{data:param,method:"POST",success:jr=>{
					if(jr.success){
						$.extend(form.originalData=form.originalData||{},form.inheritData=inherit,nv);
						if(isAdd)o.ID=jr.data.ID||jr.data.id;
						if(setting.logRequired)saveEditlog(setting.formName||setting.formCode,isAdd?"add":"edit",c,o);
						form.fullData=o;
						
						onSaved.fire(form.fullData,isAdd,form);
						
						!skipSub && saveSub(o,cb);
					}else {
						$.messager.alert("操作失败",jr.msg);
						return false;
					}
				}});
			}(fni,o,c);
		},{form:form,setting:setting,baseData:pfData,data:o,changes:c});
		return r;
	};
	if(form.smartform && setting.relationMap && !validateForeigns(setting.relationMap,pfData)){
		return form.smartform.save(function(fdata){$.extend(pfData,fdata);realSave(fn);},true);
	}else{
		return realSave(fn);
	}
};
function parseForm(formDef,setting,root){
	onBeforePartRender.fire(formDef,setting);
	root=root||document.body;
	var formRoot='<form name="'+setting.formCode+'" type="smartform" class="smartform" style="width:'+setting.formWidth+';height:100%"></form>'; // 为避免滚动条+子组件百分比运算，高度100%
	var form=$(formRoot).appendTo(root).asForm(),formElement=form[0];
	form.setting=formElement.setting=setting;
	var save=formElement.save=form.save=function(parentData/*允许传递父数据*/,fn,skipSub){
		if(form.hasError==true) return;
		if($.isFunction(parentData)){skipSub=fn;fn=parentData;parentData=null;}
		if(typeof fn=="boolean"){skipSub=fn;fn=null}			
		return smartSave(form,setting,form.parentData=(parentData||form.parentData),fn,skipSub);
	};
	var subs=form.items=[],fnCollect=function(defs){
		$.each(defs,function(i,def){
			var inst=componentManager.get(def.id);
			if(inst){
				inst.smartform=form;
				subs.push(inst);
			}
			fnCollect(def.children);
		});		
	};
	
	form.setFieldValues=function(formData,skipEmpty){
		$.each(subs,function(i,comp){
			if(!comp)
				return;
			
			var curData=comp.setting.name?formData[comp.setting.name]:null;
			if(skipEmpty&&curData===undefined) 
				return;
			
			var setVal=comp.setFormValue||(comp.setFormValue=comp.setting.setFormValue||comp.setting.define.setFormValue||comp.setValue||comp.val);
			if(setVal)
				setVal.call(comp,curData,formData,setting,form.parentData);
		});
	};
	form.load=function(formData,quiet){
		form.inheritData=null;
		form.fullData=formData;
		var opts=form.options();
		if(typeof formData=="string") //需要记录加载完成时的数据，直接调用原方法记录不到return $.fn.form.method.load.call(form,"load",formData);
		{
			var param={};
			if(opts.onBeforeLoad.call(form,param)==false) return;
			$.ajax({url:formData,data:param,dataType:"json",success:function(jr){
				if(jr.success) form.load(jr.data);
				else opts.onLoadError.apply(form,arguments);
			},error:function(){
				opts.onLoadError.apply(form,arguments);
			}});
		}else{
			form.setFieldValues(formData);
			if(!quiet){
				form.originalData=form.getFieldValues();
				opts.onLoadSuccess.call(form,formData);
				form.validate();
			}
		}
	};
	form.getFieldValues=function(){
		var result={};
		$.each(subs,function(i,comp){
			if(!comp)return;
			var getVal=comp.getFormValue||(comp.getFormValue=comp.setting.getFormValue||comp.setting.define.getFormValue||comp.getValue||comp.val);
			if(getVal){
				var field=comp.setting.name;
				var v=getVal.call(comp,result,field);
				if(field)
					result[field]=v;
			}
		});
		return result;
	};
	form.getChanges=function(nvs){
		var nv=$.extend(true,{},form.inheritData/*使用originalData将不能判断字段减少的情况*/,nvs||form.getFieldValues()),ov=form.originalData||{},changes=[];		
		for(var n in $.extend({},ov,nv)){
			var oiv=ov[n],niv=nv[n];
			if($.isPlainObject(oiv)||$.isPlainObject(niv)	// 对象
				|| $.isArray(oiv)||$.isArray(niv)			// 数组
				|| (!(!oiv && !niv && oiv!=0 && niv!=0/*认为null==undefined==""，,0是有意义的*/) && oiv!=niv) // 简单值并且不相等
				)
				changes.push({name:n,from:oiv,to:niv});
		}
		return changes;
	};
	form.validate=function(eventArg){
		return form.form("validate")&&(function(){
				var v=true;
				$.each(subs,function(i,comp){
					if(!comp)return;
					var fnV=comp.validateX||(comp.validateX=comp.setting.validate||comp.setting.validate||comp.setting.define.validate);
					if(fnV)return v=fnV.call(comp,eventArg||{handled:false});
				});
				return v!==false;
			})();
	};
	form.changeMode=function(editalbe,mode,setting){
		$.each(subs,function(i,comp){
			if(!comp)return;
			var fn=getModeChangeHandler(comp);
			if(fn) fn.call(comp,editalbe,mode,setting);			
		});
	}
	form.setFormValue=function(v,parentData,s){
		form.parentData=parentData||{};
		var param=$.extend(loadRelation(setting.relationMap,parentData),v);
		loadFlag=$.map(param,n=>{return n}).length;/*如果一个参数都没有，同样不需要请求加载*/

		/*add=-1|edit=1|view=0*/ 		
		form.changeMode(0!=(setting.smartMode=s.smartMode)/*新增和修改都是可编辑状态*/,s.smartMode);
		if(setting.smartMode==-1	/*从列表新增按钮弹出，肯定不需要加载*/ 
			|| (s.hyFlag!==undefined 
					&& (!setting.selectUrl 
						|| (setting.selectUrl==setting.queryUrl || (setting.forceSelect!=true && setting.queryUrl.indexOf("framework/datatable.do")>-1 && setting.selectUrl.indexOf("framework/datatable.do")>-1))
					  )
			   ) /*从列表查看和修改时(hyFlag),如果没有selectUrl，或者selectUrl和queryUrl相同或都是datatable，则不需要重新加载*/ 
		    || !loadFlag /*抛开完全不需要加载的情况后，如果没有足够多的加载参数，请求也是白请求*/
		){
			form.load(v||{});
			form.hasError=false;
		}else{
			//form.load({},true);
			var url=setting.selectUrl||setting.queryUrl;
			if(!url) return form.mask("表单【"+setting.formName+"】未配置数据列表接口地址！");
			
			form.mask("正在加载数据，请稍候......");
			$.ajax(url,{async:true,data:param,success:function(jr){
				if(jr.success && jr.data){
					form.unmask();
					if(!jr.data || jr.data.length==0){
						form.hasError=true;
						if(setting.formNodataHandler) setting.formNodataHandler();
						else form.maskError("加载出错：数据不存在！");//$.messager.alert("加载出错了","数据不存在！");
					}else{
						var o=$.isArray(jr.data)?jr.data[0]:jr.data;
						form.load(o);
						form.hasError=false;
					}
				}else{
					form.hasError=true;
					form.maskError("加载出错了："+jr.msg+"！");//$.messager.alert("数据加载出错了",jr.msg);
				}
			}});
		}
	};
	form.mask=function(msg,mstime,fn){
		form.unmask(0);
		if(mstime>0)setTimeout(function(){form.unmask(); fn && fn()},mstime);
		return $('<div class="smartmask"></div>').appendTo(root).html('<div class="smartmask-msg">'+msg+'</div>');
	}
	form.maskError=function(){
		return form.mask.apply(form,arguments).addClass("mask-error");
	}
	form.unmask=function(t){
		$(root).find(".smartmask").fadeOut(t==null?300:t,function(){$(this).remove()});
	}
	var tipNeeded=root!=document.body;
	tipNeeded && form.mask("正在渲染界面，请稍候......");

	parsePart(formDef,formElement);	
	fnCollect(formDef);
	onPartRendered.fire(formDef,setting);
	
	tipNeeded && form.unmask();
	
	return form;
}

function parseList(listDef,formDef,setting,root){
	onBeforePartRender.fire(listDef,setting);
	var ele=root||document.body;
	var allSmarts=[],
		fnIter=function(def,fn){
			if(def.isSmartlist) {allSmarts.push(def); fn(def);}
			$.each(def.children,function(i,sub){fnIter(sub,fn);});
		};
	var getEditor=function(title,fn,okText,cancelText,hideOk,hideCancel){
		var fnSuccess=fn;
		if(!getEditor.editor){
			var formWidth=setting.formWidth,formHeight=setting.formHeight;
			var fnSize=function(raw){return raw.split("px")[0];},
				fWidth=fnSize(formWidth),
				fHeight=fnSize(formHeight);
			var wConfig={modal:true,content:'<div class="easyui-layout" data-options="fit:true">\
				<div name="divForm" data-options="region:\'center\',border:false"></div>\
				<div name="divButton" style="padding:5px 15px;text-align:center;height:42px" data-options="region:\'south\',border:false"></div>\
				</div>'};
			if(formWidth.substr(formWidth.length-1)=="%"||formHeight.substr(formHeight.length-1)=="%")
				wConfig.fit=true;
			else{
				var hOffset=C.easyuiWindowHeightOffset||82,wOffset=(C.easyuiWindowWidthOffset||14)+(C.htmlScrollWidth||14);
				wConfig.width=Number(fWidth)+wOffset;
				wConfig.height=Number(fHeight)+hOffset;
			}
			var winEdit=getEditor.editor=new sunz.Window(wConfig);
			var formElement=winEdit.form=parseForm(formDef,setting,winEdit.find("[name=divForm]"),false);
			formElement.smartform=ele.smartform;
			var pele=winEdit.find("[name=divButton]");
			winEdit.btnOk=new sunz.Linkbutton({attrs:{"class":"sunz-btn-ok"},parent:pele,style:"",text:"确定",iconCls:"icon-ok",handler:function(){
				formElement.save(function(){
					winEdit.close();
					$.messager.show({title:"恭喜您",msg:"操作成功"});
					fnSuccess && fnSuccess();
				});
			}
			});
			winEdit.btnCancel=new sunz.Linkbutton({attrs:{"class":"sunz-btn-cancel"},style:"margin-left:15px;",parent:pele,text:"取消",iconCls:"icon-cancel",handler:function(){winEdit.close();}});
			winEdit.center();
		}
		getEditor.editor.btnCancel.linkbutton({text:cancelText||"取消"});
		getEditor.editor.btnOk.linkbutton({text:okText||"确定"});
		hideOk?getEditor.editor.btnOk.hide():getEditor.editor.btnOk.show();
		hideCancel?getEditor.editor.btnCancel.hide():getEditor.editor.btnCancel.show();
		
		getEditor.editor.setTitle(title);
		return getEditor.editor;
	};
	$.each(listDef,function(i,def){
		fnIter(def,function(item){
			var setValue=item.setFormValue,
				queryUrl=item.url||setting.queryUrl;
			delete item.url; // 先不加载，setFormValue才加载
			item.setFormValue=function(v,p,s){
				this.options().url=queryUrl;
				v=$.extend(loadRelation(setting.relationMap,p),v);
				setValue && setValue(v,p,s);
				if(!validateForeigns(setting.relationMap,p)) // 关联关系不充分，清空列表
					return this.loadData([]);
				
				setValue || this.load();
			}
			var beforeLoad=item.onBeforeLoad;
			item.onBeforeLoad=function(opt){
				if(!ele.baseData) return false;
				$.fn.datagrid.defaults.onBeforeLoad.call(this,opt);
				if(setting.relationMap){
					if(!validateForeigns(setting.relationMap,ele.baseData))
						return (componentManager.get(item.id).loadData([]),false);
					
					loadRelation(setting.relationMap,ele.baseData,opt);
				} 
				// 允许配置
				if(beforeLoad) return beforeLoad.call(this,opt);
			};
			
			var tbs=[];
			if(setting.addable!=false)
				tbs.push({iconCls:setting.addIcon||'icon-add',text:setting.addTitle||'新增',handler:function(){
					var g=componentManager.get(item.id);
					callWithEvent(onBeforeAdd,function(){
						var winEdit=getEditor('新增数据',function(){g.load({total:-1});},"保存");
						winEdit.open();	
						winEdit.form.reset(); 
						winEdit.form.setFormValue(null,ele.baseData,{hyFlag:false,smartMode:-1});
					},{grid:g,compid:item.id,setting:setting,baseData:ele.baseData});
				  }},'-');
			var showWin=function(fnGetWin,hyMode,e){
				  var g=componentManager.get(item.id),sel=g.getSelected();
		    	  if(sel==null){$.messager.show({title:'提示',msg:'请选择一行进行操作'});return;}
		    	  callWithEvent(e,function(){
		    		  var winEdit=fnGetWin();
		    		  winEdit.open();
		    		  winEdit.form.setFormValue(sel,ele.baseData,{hyFlag:true,smartMode:hyMode});
		    	  },{grid:g,compid:item.id,setting:setting,row:sel});
			};
			if(setting.editable!=false)
				tbs.push({iconCls:setting.editIcon||'icon-edit',text:setting.editTitle||'修改',handler: function(){
				    	  showWin(function(){return getEditor('数据编辑',function(){componentManager.get(item.id).reload();},"保存");},1,onBeforeEdit);
				      }},'-'
				);
			if(setting.viewable!=false){
				var view=function(){showWin(function(){return getEditor('数据查看',null,"确定","关闭",true);},0,onBeforeView);};
				tbs.push({iconCls:setting.viewIcon||'icon-view',text:setting.viewTitle||'查看',handler:view},'-');
				if(!item.onDblClickRow){
					item.onDblClickRow=view;
				}
			}
			if(setting.deleteable!=false){
				tbs.push({iconCls:setting.removeIcon||'icon-remove',text:setting.removeTitle||'删除',handler: function(){
					var g=componentManager.get(item.id),sel=g.getSelected();
					if(sel==null){$.messager.show({title:'提示',msg:'请选择一行进行操作'});return;}
					callWithEvent(onBeforeDelete,function(){
						$.messager.confirm('请确认','您确定删除当前数据吗？',function(ok){
							if(ok){
								$.post(setting.deleteUrl,{id:sel.ID||sel.id},function(jr){
									if(jr.success){
										$.messager.show({title:'恭喜您',msg:'操作成功'});
										onDeleted.fire(sel);
										if(setting.logRequired)
											saveEditlog(setting.formName||setting.formCode,"delete",undefined,sel);
										g.reload({total:-1});
									}else 
										$.messager.alert('操作失败',jr.msg);
								});
							}
						});
					},{grid:g,compid:item.id,setting:setting,row:sel});
				}},'-');
			}
			if(setting.historyable!=false){
				tbs.push({iconCls:setting.historyIcon||'icon-history',text:setting.historyTitle||'数据历史',handler:function(){
					var g=componentManager.get(item.id),sel=g.getSelected();
					if(sel==null){$.messager.show({title:'提示',msg:'请选择需要查看的数据'});return;}
					viewEditlog(sel.id||sel.ID);
				}},'-');
			}
			
			item.toolbar=tbs.concat(item.toolbar||[]);
		});
	});	
	
	ele.setting=setting;
	ele.setFormValue=function(v,parentData,s){
		// list认为所有参数均为smartList准备，因此不理会v，不调用普通组件的setFormValue
		ele.baseData=parentData;
		$.each(allSmarts,function(i,def){
			def.setFormValue.call(componentManager.get(def.id),v,parentData,s);
		});
	};
	
	parsePart(listDef,ele);
	onPartRendered.fire(formDef,setting);
	return ele;
}