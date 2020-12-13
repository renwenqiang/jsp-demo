/**
 * 
 */
$(function(){
	$(`<style>
		._b{font-weight:bold}._i{font-style:italic;margin-right:4px}
		.keyword{color:#00f}
		.field{color:#267af9;}
		.param{color:#f00}
		.fmtag{color:#315a04}
		.fmtag_mid{color:#c10441}
		.fn{color:#ae0df1;}
		.brackets{color:#21bbd4}
		.link{font-size:24px;color:#339;cursor:pointer}
		.comment,.comment span{color:#999 !important}
		.string ,.string span{color:#f93f3f !important}
		.alias,.alias span{color:#f93f3f;}
		.tipHead{border-bottom:1px solid #999;font-size: 36px;padding:8px 25px;word-break: break-all;}
		.tipBack,.tipEdit{padding:0 10px;color:#3b5bd2;cursor:pointer;position:absolute;top:8px;border:1px solid;}
		.tipEdit{padding:5 10px;font-size:24px;right:15px}
		.tipBody{height:calc(100% - 54px);overflow:scroll;}
		.tipBody .sql{max-height:unset !important}
		.tipButton{position:absolute;left:-32px;text-align:center;line-height:30px;width:30px;border:1px solid;background-color:#fff;cursor:pointer;}
		.tipbtn-active{box-shadow:inset 3px -1px;font-weight:bold;color:#f00}
		.tip-err{text-align:center;margin-top:25px;font-size:36px;color:#f00}
	</style>`).appendTo(document.head);
	//window.sqlHoverCss=true;
	//if(!top.sqlHoverCss){top.$('<script type="text/javascript" src="webpage/framework/query/sqlformat.js"></script>').appendTo(top.document.head);}
});

var hoverFixed=false,hoverAuto=false;
var hoverSql=function(ele,keep){
	ele.keep=keep;
	var k=$(ele||this).text().replace(/@|\s*/g,"");
	var sql=hoverSql[k];
	var loadDef=function(_k,fn){$.post("framework/datatable.do?exactly",{t:"T_S_SQLSTATEMENT",key:_k},fn);};
	var showSql=function(keep_,_k){
		_k=_k||k;
		var jtip=hoverSql.shower;
		if(!jtip){
			jtip=hoverSql.shower=$('<div style="width:800px;height:600px;background-color:#feefef;border:2px solid #333;position:absolute;top:15px;right:25px;font-size:16px;"><div class="tipHead"></div><div class="tipBody"></div></div>')
			.appendTo(document.body)
			.on("click mouseover",function(){
				jtip.stop().fadeIn(0);
				clearTimeout(htip);
			})
			.on("mouseout",function(){
				anim();
			}).resizable();
			var jh=jtip.find(".tipHead"),
				jb=jtip.find(".tipBody");
			$('<div class="tipButton" style="top:0px;" title="关闭">×</div>').appendTo(jtip).on("click",function(e){jtip.stop().fadeOut(0);clearTimeout(htip);});
			$('<div class="tipButton" style="top:40px;" title="自动隐藏切换">!</div>').appendTo(jtip).on("click",function(e){
				hoverFixed=!hoverFixed;
				if(hoverFixed){
					jtip.stop().fadeIn(0);
					clearTimeout(htip);
				}else anim();
				$(this).toggleClass("tipbtn-active");
			});
			$('<div class="tipButton" style="top:80px;" title="hover自动弹出切换">~</div>').appendTo(jtip).on("click",function(e){hoverAuto=!hoverAuto;$(this).toggleClass("tipbtn-active");});
			jtip.find(".tipButton").on("click",function(e){e.stopPropagation();});
			
			var jt=$('<div style="margin-left:60px"></div>').appendTo(jh);
			var jback=$('<div class="tipBack"> < </div>').appendTo(jh).on("click",function(){
				if(hoverSql.stack.length==1)return;
				hoverSql.stack.pop();
				var k_=hoverSql.stack[hoverSql.stack.length-1];
				hoverSql.stack.pop();
				sql=hoverSql[k_];
				showSql(true,k_);
			});
			$('<div class="tipEdit">编辑</div>').appendTo(jh).on("click",function(e){
				e.stopPropagation();
				var sk=jt.text();
				var rIndex=-1;
				var edit=function(){
					jtip.stop().fadeOut(0);
					grid.selectRow(rIndex);
					startEdit(rIndex);
				}
				if($.grep(grid.getRows(),function(d,i){return d.code==sk && (rIndex=i,true)}).length){					
					return edit();
				}
				
				loadDef(sk,function(jr){
					var def=jr.data[0];
					def.id=def.ID;
					def.code=def.KEY;
					def.name=def.DESCRIPTION;
					def.sql_group=def.SQL_GROUP;
					def.sql=def.SQL;
					
					rIndex=grid.data("datagrid").highlightIndex;
					grid.insertRow({index:rIndex,row:def});
					edit();
				});
			});
			jtip.setTitle=function(t){
				(hoverSql.stack=hoverSql.stack||[]).push(t);
				jt.html(t);
				if(hoverSql.stack.length>1){
					jback.show();
				}else{
					jback.hide();
				}
			};
			jtip.setSql=function(s){
				jb.empty().append(s);
				jb.find(".link").each(function(){this.onmouseenter=null;});
			};
		}
		jtip.stop().fadeIn(0);
		clearTimeout(htip);

		if(hoverSql.curKey!=_k){
			jtip.setTitle(_k);
			if(sql){
				jtip.setSql(sql);
			}else{
				jtip.setSql('<div class="tip-err" style="color:#000">加载中.....</div>');
				loadDef(_k,function(jr){
					if(jr.success){
						if(jr.data.length){
							sql=hoverSql[k]=hlsql(jr.data[0].SQL);
							jtip.setSql(sql);
						}else{
							jtip.setSql('<div class="tip-err">指定的sql没有定义</div>');
						}
					}else{
						jtip.setSql('<div class="tip-err">服务出错了</div>');
					}
				});
			}
		}
		hoverSql.curKey=_k;
		jtip.show();
		if(!keep_)
			htip=anim();
	};
	showSql(keep,k);
},htip,anim=function(s){if(htip)clearTimeout(htip);return hoverFixed?0:(htip=setTimeout(function(){hoverSql.shower.fadeOut(1500);},s||5000));};
var regKeywords=new RegExp("(^|[^\\$#\\{\\}:])\\b("+(C["SQL.keywords"]||"select|delete|update|from|where|like|and|or|between|insert|into|left|join|on|right|inner|outer|full|top|distinct|union|all|group|by|order|desc|asc|case|if|else|then|when|begin|end|is|as|in|not|null|connect|level|start|with|prior|sysdate|rownum")+")\\b","ig");
var hlsql=function(sql){	// performance 2019-03-20 sql.length=5827 1000/405ms
	sql=sql||"";
	// ""
	sql=sql.replace(/"(.*?[^\\])??"/g,function(m,k){
		return '<span class="alias _b">'+m+'</span>';
	});
	// ''
	sql=sql.replace(/'(.*?[^\\])??'/g,function(m,k){
		return '<span class="string">'+m+'</span>';
	});	
	// sql keyword
	sql=sql.replace(regKeywords,function(m,k,x){
		return k+'<span class="keyword _b">'+x+'</span>';
	});
	// sql fun
	/*sql=sql.replace(/(^|[^\$#\{\}:])\b(count|sum|to_char|to_date|max|min|len|length|nvl|substr|trim|wm_concat|to_number|nlssort|sysdate|rownum|exists)(?=\(|\b)/ig,function(m,k,x){
		return k+'<span class="fn _i">'+x+'</span>';
	});*/
	sql=sql.replace(/\b(\w+)(\s*\()/g,function(m,k,x){
			return '<span class="fn _i">'+k+'</span>'+x;
	});
	// ()
	sql=sql.replace(/\(|\)/g,function(m,k){
		return '<span class="brackets _b">'+m+'</span>';
	});
	// sql field
	sql=sql.replace(/(\.["\[`]?)(\w+)\b([^\(])/g,function(m,k,x,y){
		return k+'<span class="field _i">'+x+'</span>'+y;
	});
	// sql param
	sql=sql.replace(/:\s*\w*\b|[$#]{([^{}]+)}/g,function(m,k){
		return '<span class="param _b">'+m+'</span>';
	});
	// freemarker --
	sql=sql.replace(/<#--(.|[\r\n])*?-->/g,function(m,k){
		return '<span class="comment">'+m+'</span>';
	});
	// freemarker tag
	sql=sql.replace(/\?\?|<\/?#\w*>?/g,function(m,k){
		return '<span class="fmtag _b">'+m.replace("<","&lt;").replace(">","&gt;")+'</span>';
	});
	// @
	sql=sql.replace(/@\w*\b/g,function(m,k){
		return '<span class="link _b" title="点击查看定义" onmouseenter="if(hoverAuto)hoverSql(this,false)" onmouseleave="if(!this.keep)anim(1)"  onclick="hoverSql(this,true)">'+m+'</span>';
	});
	// --
	sql=sql.replace(/--.*/g,function(m,k){
		return '<span class="comment">'+m+'</span>';
	});
	// /**/
	sql=sql.replace(/\/\*(.|[\r\n])*?\*\//g,function(m,k,x){
		return '<span class="comment">'+m+'</span>';
	});
	
	return '<div class="sql"><div>'+sql+'</div></div>';
};