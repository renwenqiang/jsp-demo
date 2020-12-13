/**
 * sql编辑框
 */
(function(){
	$(`<style>.form input{width:336px}</style>`).appendTo(document.head);
	
	window.showTableParser=(t,alais,txt,sel,s,e,autoPaste)=>{
		var win=new sunz.Window({title:"数据表字段展开",width:580,height:480});
		var lyr=$('<div class="easyui-layout"><div class="form" region="center"></div><div class="tool" region="south" data-options="height:48" style="padding:5px 25px"></div></div>')
		.appendTo(win);
		var form=$(`<form><table class="kv-table">
				<tr><td class="kv-label">目标表</td><td class="kv-content"><input class="easyui-textbox" name="t"></td></tr>
				<tr><td class="kv-label">(表)AS别名</td><td class="kv-content"><input class="easyui-textbox" name="alias"></td></tr>
				<tr><td class="kv-label">(字段)AS大写</td><td class="kv-content"><input class="easyui-checkbox" type="checkbox" name="upper"></td></tr>
				<tr><td class="kv-label">(字段)去掉前缀</td><td class="kv-content"><input class="easyui-textbox" name="prefix" value="_"></td></tr>
				<tr><td class="kv-label">(字段)去掉后缀</td><td class="kv-content"><input class="easyui-textbox" name="prefix" value="_"></td></tr>
				<tr><td class="kv-label">(字段)去掉字符</td><td class="kv-content"><input class="easyui-textbox" name="fix" value="_"></td></tr>
				<tr><td colspan="2" class="kv-content"><textarea style="height:136px;width:520px"></textarea></td></tr>
		</table></form>`).appendTo(lyr.find(".form")).asForm();
		
		form.load({t:t,alias:alais,upper:true,prefix:"_",postfix:"_",fix:"_"});
		var txtResult=form.find("textarea");
		new sunz.Linkbutton({parent:lyr.find(".tool"),text:"关闭",style:"float:right;margin:0 15px",iconCls:"icon-cancel",handler:()=>{
			win.close();
		}});
		new sunz.Linkbutton({parent:lyr.find(".tool"),style:"float:right;margin:0 15px",text:"解析",iconCls:"icon-ok",handler:()=>{
			var o=form.getFieldValues();
			$.post("framework/datatable.do?getFieldsStatement",o,(jr)=>{
				if(jr.success){
					if(autoPaste){
						win.close();
						var scroll=txt.scrollHeight - txt.scrollTop;
						txt.value=txt.value.substr(0,s)+jr.data+txt.value.substr(e);
						sqlEditor.select(txt,s,e+(jr.data.length-sel.length),scroll);
					}else{
						txtResult.val(jr.data);					
						btnFormat.show();
						if(txt){
							btnInsert.show();
						}
					}
				}else{
					$.messager.alert("字段展开解析出错",jr.msg);
				}
			})
		}});
		var btnFormat= new sunz.Linkbutton({parent:lyr.find(".tool"),text:"格式化",style:"float:right;margin:0 15px",iconCls:"json-format",handler:()=>{
			var fs=txtResult.val().trim();
			txtResult.val("\t"+fs.replace(/\s*,\s*/g,",\r\n\t"))
		}});
		var btnInsert=new sunz.Linkbutton({parent:lyr.find(".tool"),text:"写入编辑框鼠标处",style:"float:right;margin-right:15px",iconCls:"icon-edit",handler:()=>{
			var start=txt.selectionStart,end=txt.selectionEnd,nsel=txt.value.substring(start,end),scroll=txt.scrollHeight - txt.scrollTop;
			var text=txtResult.val();
			txt.value=txt.value.substr(0,start)+text+txt.value.substr(end);
			sqlEditor.select(txt,start,end+(text.length-nsel.length),scroll);
			win.close();
		}});
		btnFormat.hide();
		btnInsert.hide();
		txtResult.on("input propertychange",(e)=>{
			if(txtResult.val()){
				btnFormat.show();
				if(txt){
					btnInsert.show();
				}
			}else{
				btnFormat.hide();
			}
		});
		
		lyr.asLayout({fit:true});
	}
	var sqlEditor={
			select:(txt,s,e,scroll)=>{
				txt.focus();
				txt.selectionStart=s;
				txt.selectionEnd=e;
				txt.scrollTop=txt.scrollHeight - scroll;
			},
			toUpper:(txt,sel,s,e)=>{
				var scroll=txt.scrollHeight - txt.scrollTop;
				txt.value=txt.value.substr(0,s)+sel.toUpperCase()+txt.value.substr(e);
				sqlEditor.select(txt,s,e,scroll);
			},
			toLower:(txt,sel,s,e)=>{
				var scroll=txt.scrollHeight - txt.scrollTop;
				txt.value=txt.value.substr(0,s)+sel.toLowerCase()+txt.value.substr(e);
				sqlEditor.select(txt,s,e,scroll);
			},
			quot:(txt,sel,s,e)=>{
				var scroll=txt.scrollHeight - txt.scrollTop;
				txt.value=txt.value.substr(0,s)+'"'+sel+'"'+txt.value.substr(e);
				sqlEditor.select(txt,s,e+2,scroll);
			},
			as:(txt,sel,s,e,fn)=>{
				sel=sel.trim();
				if(!sel) 
					return;
				var temp=sel.split(/\.|\s+/);
				sel=temp[temp.length-1];
				var scroll=txt.scrollHeight - txt.scrollTop;
				var pre=txt.value.substr(0,e),post=txt.value.substr(e),deta=(/\s$/.test(pre)?"":" ")+"\""+fn(sel)+"\""+(/^\w/.test(post)?" ":"");
				txt.value=pre+deta+post;
				sqlEditor.select(txt,s,e+deta.length,scroll);
			},
			asUpper:(txt,sel,s,e)=>{
				sqlEditor.as(txt,sel,s,e,(f)=>{return f.toUpperCase()});
			},
			asLower:(txt,sel,s,e)=>{
				sqlEditor.as(txt,sel,s,e,(f)=>{return f.toLowerCase()});
			},
			expandFields:(txt,sel,s,e)=>{
				var t,alais;
				var parseTable=(a)=>{
					var reg=new RegExp("\\s+(\\w+)\\s+(as\\s+)?"+a+"\\b","i");
					var r=reg.exec(txt.value);
					if(r){
						return r[1];
					}
					r=txt.value.substr(s).split(/\bfrom\b\s*/i)[1]; // s后的第一个from
					if(r){
						return /^(\w+)\b/.exec(r)[1];
					}
					return a;
				}
				var autoPaste=false;
				if(!sel){ // 找到鼠标前的 t.		
					var lines=txt.value.substr(0,s).split(/\r|\n/);
					alais=/\b(\w+)\s?(\..*)?/.exec(lines[lines.length-1])[1];
					t=parseTable(alais);
					//autoPaste=true;
				}else if(sel.indexOf(".")>0){
					alais=sel.split(".")[0];
					t=parseTable(alais);
					//autoPaste=true;
				}else{
					var arr= sel.split(/\s+(as\s+)?/g);
					t=arr[0],alais=arr[2]||arr[1];
					if(!alais)
						alais=(/^\s*(\w+)\b/.exec(txt.value.substr(e))||[0,"t"])[1];
				}
				
				showTableParser(t,alais,txt,sel,s,e,autoPaste);
			}
	};
	
	window.sqlEditSupport=function(e){
		e.preventDefault();
		var txt=this,start=txt.selectionStart,end=txt.selectionEnd,sel=txt.value.substring(start,end);
		new sunz.Menu()
		  .appendItem({text:"展开表字段",id:"expandFields",onclick:function(){sqlEditor.expandFields(txt,sel,start,end)}})
		  .appendItem({separator: true})
		  .appendItem({text:"转为大写",id:"toUpper",onclick:function(){sqlEditor.toUpper(txt,sel,start,end)}})
		  .appendItem({text:"转为小写",id:"toLower",onclick:function(){sqlEditor.toLower(txt,sel,start,end)}})
		  .appendItem({separator: true})
		  .appendItem({text:"(字段)As大写",id:"asUpper",onclick:function(){sqlEditor.asUpper(txt,sel,start,end)}})
		  .appendItem({text:"(字段)As小写",id:"asLower",onclick:function(){sqlEditor.asLower(txt,sel,start,end)}})
		  .appendItem({separator: true})
		  .appendItem({text:"添加引号",id:"quot",onclick:function(){sqlEditor.quot(txt,sel,start,end)}})
		.show({left:e.clientX,top:e.clientY});
	}
	
})();
