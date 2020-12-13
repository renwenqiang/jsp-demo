/**
 * 系统【实时】消息【承载界面】
 */
define(["mobile/framework/message/imessage","_listtemplates"],function(IM,templates){
	$('<link rel="stylesheet" type="text/css" href="mobile/framework/message/imessage.css" />').appendTo("head");
	var jele=$('<div class="mcontainer"><div class="imm-cates"><div class="tab-decorate"></div></div><div class="imm-list"></div></div>'),
		jcates=jele.find(".imm-cates"),
		jlist=jele.find(".imm-list"),
		jdecorate=jele.find(".tab-decorate")[0];
	
	// text:,cls:,param:{},countRequired:bool,filter:function(msg,deta)
	var cates=eval("("+(C["im.categories"]||"0")+")")||[{text:"全部",cls:"imm-cate-all"},{text:"已读",param:{readed:true}},{text:"未读",countRequired:true,param:{unread:true}}];
	window.onMessageReaded=$.Callbacks();
	
	var activeClass="tab-active",activeCate=null,
		msgListUrl="framework/query.do?search&k=sys_user_message";
	var msgTemplate=function(m,c){
		m.time=m.time||new Date(getServerTime());
		if(m.unread==undefined)m.unread=new Date(m.readtime)-getServerTime()>86400000; // 未读实际在几千年后，这儿使用1天作为容差
		let jitem=$('<div class="imm-item" mid="'+m.id+'"><div class="imm-index '+(m.unread?"unread":"readed")+'"></div></div>')
			.on("click",function(){
				if(m.unread){
					setTimeout(function(){
						$.post("framework/datatable.do?save",{t:"t_s_message",id:m.id,timeFields:"readtime_"},function(jr){
							m.readtime_=jr.data.readtime_;
							m.unread=false;
							/*if(c.unread){
								jitem.parent().remove();
							}else{
								jitem.find(".unread").removeClass("unread").addClass("readed");
							}*/
							onMessageReaded.fire(m);
						});
					},200);
				}
			});
		let tpl=templates[m.type];
		if(tpl){
			let jcontent=tpl(m);
			if(typeof jcontent=="string")
				jcontent=$(jitem);
			jcontent.appendTo(jitem);
		}else{
			$('<div class="imm-unknown">【不可识别的消息】</div>').appendTo(jitem);
		}
		
		return jitem;
	}
	$.each(cates,(i,c)=>{
		let jcate=c.jele=$('<div class="imm-cate">'+c.text+'<span class="imm-cate-badge '+(c.countRequired?"count-required":"")+'"></span></div>').appendTo(jcates)
		.on("click",()=>{
			if(activeCate==jcate)
				return;
			
			jdecorate.style.width=jcate.width();
			jdecorate.style.transform="translateX("+jcate[0].offsetLeft+"px)";
			activeCate && activeCate.removeClass(activeClass);
			jcate.addClass(activeClass);
			$(activeCate?activeCate.jlist.domNode:'<div>').fadeOut(300,()=>{
				if(!jcate.jlist){
					jcate.jlist=new sunz.MList({
						parent:jlist,
						url:c.url||msgListUrl,
						type:"panel",
						queryParams:c.param,
						template:(m)=>msgTemplate(m,c.param||{}),
						noresultContent:'<div class="imm-noresult">暂无消息</div>',
						pagesize:10,
						listener:{
							load:(e,t,jr)=>{
								jcate.find(".imm-cate-badge").text(jr.total)//.show();
							}
						}
					})
				}else{
					$(jcate.jlist.domNode).fadeIn()
				}
			});
			activeCate=jcate;
		});
		c.cls && jcate.addClass(c.cls);
		
	})
	
	
	var onChange=function(m,type){
		$.each(cates,(i,c)=>{
			if(!c.filter)
				c.filter=(m,deta)=>{
					let opt=c.param;
					if(deta<0){
						if(!opt) return 0;
						if(opt.readed==true) return 1;
						if(opt.unread==true) return -1;
						return 0;
					}
					return (!opt || ((!opt.readed==m.unread || !!opt.unread==m.unread) && (!opt.cate || opt.cate==m.type) && (!opt.cateStart || m.type.startsWith(opt.cateStart))))?1:0;
				}

			let jcate=c.jele,jlist=jcate.jlist;
			let deta=c.filter(m,type);
			if(deta){
				let jbadge=jcate.find(".imm-cate-badge"),
					bdg=jbadge.text();
				
				// 1.脚标，仅在已经有过加载时更新
				bdg && jbadge.text(Number(bdg)+deta);
				// 2.list
				if(jlist){
					if(deta>0){			// 新消息
						let datas=[m].concat(jlist.data);
						jlist.setData(datas);
					}else if(deta<0){	// 消息作废
						let jdom=$(jlist.domNode);
						jdom.find("[mid="+m.id+"]").parent().remove();
					}
				}
			}else{
				jlist && $(jlist.domNode).find("[mid="+m.id+"] .unread").removeClass("unread").addClass("readed");
			}
		});
	}
	onMessageReaded.add((m)=>onChange(m,-1));
	onMessageReceived.add(function(m){
		m.time=new Date(getServerTime());
		m.unread=true;
		onChange(m,1);
	});
	let jim=$('<div class="imm-cate-badge count-required"></div>').appendTo(C["im.badgeSelector"]||".bbar-item.imm");
	if(jim[0]){
		setTimeout(()=>$.post(msgListUrl.replace("search","count"),{unread:true},(jr)=>jim.text(jr.data)),300);
		onMessageReceived.add((m)=>{
			let count=Number(jim.text());
			jim.text(count+1);
		});
		onMessageReaded.add((m)=>{
			let count=Number(jim.text());
			jim.text(count-1);
		});
	}
	
	
	let init=()=>{
		$.each(cates,(i,c)=>{
			let jcate=c.jele;
			if(!i){
				setTimeout(()=>jcate.click(),100)
			}else{
				if(c.countRequired)
					setTimeout(()=>{
						$.post(msgListUrl.replace("search","count"),c.param,(jr)=>jcate.find(".imm-cate-badge").text(jr.data))
					},300)
			}
		});
		
	}

	return {
		title:"消息",
		buttons:[],
		panel:{domNode:jele},
		setParam:function(param){
			
		},
		onActive:function(){
			!init.inited && (init(),init.inited=1);
		}
	};
});