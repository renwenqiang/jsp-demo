function saveEditlog(formName,type,c,o){
	if(type!="delete" && c.length==0)return;
	top.$.post("framework/datatable.do?add", 
			{	t:"T_S_EDITLOG",
				FormName:formName,
				JobKey:jobkey,
				StepKey:stepkey,
				Jobid:jobid,
				BusinessKey:getQueryParam("businesskey")||o.ID||o.id,
				EditType:type,
				Content:$.encode(type=="delete"?{from:{},to:null}:(function(){var l={from:{},to:{}};$.each(c,function(i,f){l.from[f.name]=f.from;l.to[f.name]=f.to});return l})())/*[{name,from,to},]模式，体积要大一些*/ /*type=="delete"?null:$.encode(c)*/,
				Editor:loginUser.id,
				Remark:formCode,
				ipFields:"IP"
			},
			function(jr){if(!jr.success&&window.console)console.log("修改日志记录出错："+jr.msg);}
	);
}

(function(){
	// 样式1			
	var render=function(pele,mapping,from,to,logs){
		var dContent=$('<table class="elog-i-list"></table>').appendTo(pele);			
		var tpl='<tr class="elog-i-field">\
					<td class="elog-i-caption"><span class="elog-i-title">{name}:</span></td>\
					<td class="elog-i-from"><span class="elog-i-value"></span></td>\
					<td class="elog-i-to"><span class="elog-i-value"></span></td>\
				</tr>';
		$.each(logs,function(i,log){
			var n=log.name,c=mapping[n.toLowerCase()]||{};
			var jlog=$(tpl.replace("{name}",c.caption||n)).appendTo(dContent);				
			var vfrom=jlog.find(".elog-i-from .elog-i-value");
			vfrom.append((c.formatter||defFormatter)(from[n],from,vfrom));				
			var vto	 =jlog.find(".elog-i-to .elog-i-value");
			vto.append((c.formatter||defFormatter)(to[n],to,vto));
		});
	}
	var cssx='.elog-i-field:not(:last-child) td{border-bottom:1px solid #ccc;}.elog-i-caption{width:20%;padding-left:15px;background-color: #f0f0f0;}.elog-i-from,.elog-i-to{width:40%;background-color:#fff !important;}.elog-i-from{border-right: 1px dashed #e0e0e0 !important;}' // css1

	// 样式2			
	/*var render=function(pele,mapping,from,to,logs){
		var dContent=$('<table class="elog-i-list"><tr><td class="elog-i-from" ></td><td class="elog-i-to" ></td></tr></table>').appendTo(pele),
		dFrom=dContent.find(".elog-i-from"),
		dTo=dContent.find(".elog-i-to");			
		$.each(logs,function(i,log){
			var n=log.name,c=mapping[n.toLowerCase()]||{};
			var caption=c.caption||n;
			var dHtml='<div class="elog-i-field"><span class="elog-i-title">'+caption+'：</span><span class="elog-i-value"></span></div>',
			dfFrom=$(dHtml).appendTo(dFrom),
			dfTo=$(dHtml).appendTo(dTo);
			
			var vfrom=dfFrom.find(".elog-i-value").append((c.formatter||defFormatter)(from[n],from,vfrom));
			var vto	 =dfTo.find(".elog-i-value").append((c.formatter||defFormatter)(to[n],to,vto));
		});
	}
	var cssx= '.elog-i-list td{width:50%;}';*/

	var vscope=top.sunz&&top.sunz.Window?top:window;
	vscope.$("#editlog-css").length || vscope.$('<style id="editlog-css" type="text/css">'+cssx+'.elog-i-list td{padding:5px 15px;}.elog-i-value{word-break:break-word;}.elog-i-from .elog-i-value{color:#999}.elog-i-to .elog-i-value{color:#f00}.elog-condition{background-color:#f0f0f0;padding:5px 15px;border-bottom:1px solid #ccc;}.elog-container{height:calc(100% - 52px);overflow-y:scroll;padding:15px;background-color:#fff;box-sizing:border-box;-moz-box-sizing:border-box;-webkit-box-sizing:border-box;}.elog-i-pre{line-height:36px}.elog-i-editor{}.elog-i-time{margin-left:25px}.elog-i-list{border:1px solid #ccc;width:100%;margin-bottom:15px;}.elog-i-from,.elog-i-to{background-color:#f0f0f0}.elog-i-from{border-right:1px dashed #fff;}.elog-i-field{line-height:36px;}span.elog-i-title{margin-right:10px}.editTip{margin-left:0px;color:#C70400;line-height:32px}.noHistoy{line-height:36px;font-size:24px;color:#933;text-align:center}.btnHistory{margin-left:5px;border:1px solid;padding:5px 15px;border-radius:3px;}</style>').appendTo(vscope.document.head);
	
	var defFormatter=function(v){return v||""};
	var mappingLog=function(fn,t){
		var m=mappingLog[t];
		if(!m){
			$.post("framework/datatable.do?exactly",{t:"T_S_EDITLOG_FIELDCONFIG",form_:t},function(jr){
				if(jr.success){
					m=mappingLog[t]={};
					$.each(jr.data,function(i,d){
						var isDict=d.DICT_ && d.DICT_!="false";
						m[d.FIELD_.toLowerCase()]={
							name:d.FIELD_,
							caption:d.CAPTION_,
							formatter:isDict?(d.D=d.DICT_=="true"?D:D.createNew(),d.DICT_ !="true" && d.D.add(D.get(d.DICT_)), function(v){
								return d.D.getText(v);
							}):(d.FORMATTER_?eval("("+d.FORMATTER_+")"):null)
						}
					});
				}
				m["_len_"]=jr.data.length;
				fn && fn(m);
			});
		}else{
			fn && fn(m);
		}
	};
	window.viewEditlog=function(id,t,title_){		
		t=t||window.tableName||window.formName;
		t=t.toLowerCase();
		title_=title_||"编辑历史查看"
		var win=new vscope.sunz.Window({title:title_,width:800,height:600,modal:true,content:'<div class="dlog" style="width:100%;height:100%;"></div>'}),
			dc=win.find(".dlog"),
			ds=$('<div class="elog-condition"></div>').appendTo(dc),
			dr=$('<div class="elog-container"></div>').appendTo(dc);
		
		var txtName=new vscope.sunz.Textbox({parent:ds,width:200,label:"修改人姓名",labelAlign:"right"});
		var dtStart=new vscope.sunz.Datebox({parent:ds,style:"margin-left:25px",width:200,label:"起止时间",labelAlign:"right"});
		var dtEnd=new vscope.sunz.Datebox({parent:ds,width:160,labelWidth:40,label:"　-　"});	
		
		var getCondition=function(){return {dataid:id,q:txtName.getValue(),stime:dtStart.getValue(),etime:dtEnd.getValue()}};
		var fnTemplate=function(log,mapping){
			mapping=mapping||{};
			
			var json=$.decode(log.content||"{}");
			// 兼容两种写法--因体积更小，save已经切换到from-to模式
			if($.isArray(json)){					// [{name,from,to},]$.decode(log.content||"[]")
				var logs=json,from={id:id},to={id:id};
				$.each(logs,function(i,c){var n=c.name;from[n]=c.from;to[n]=c.to;});
			}else{									// {from:{name},to:{name}}
				var from=json.from||{},to=json.to||{};
				var logs=$.map($.extend({},from,to)/*不对等*/,function(i,n){return {name:n}});
				from.id=to.id=id;
			}
			
			if(mapping["_len_"]){logs=$.grep(logs,function(log){return mapping[log.name.toLowerCase()]});} // 有配置的情况下只显示已配置字段--否则不好看
			
			var dItem=$('<div class="elog-item"></div>');
			$(tplReplace('<div class="elog-i-pre"><span class="elog-i-editor">{editor}</span><span class="elog-i-time">{edittime}</span></div>',log)).appendTo(dItem);
			render(dItem,mapping,from,to,logs);
			return dItem;
		};
		var load=function(){
			dr.empty();
			$.post("framework/query.do?search&k=sys_editlog_history",getCondition(),function(jr){
				if(jr.success){
					if(jr.data.length){
						var render=function(m){						
							$.each(jr.data,function(i,log){
								$(fnTemplate(log,m)).appendTo(dr);
							});
						};
						if(t){
							mappingLog(render,t);
						}else{
							render();
						}
					}else{
						dr.append('<div class="noHistoy">当前数据没有任何编辑历史记录</div>');
					}
				}else{
					$.messager.alert("查询服务出错",jr.msg);
				}
			});
		};
		
		new vscope.sunz.Linkbutton({parent:ds,width:75,style:"margin-left:25px;",text:"查询",handler:load});
		load();
	}
})()