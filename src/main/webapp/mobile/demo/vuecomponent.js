/**
 *  演示使用vue组件
 */
define(["_vuesupport"],function(vueSupport){
	let jele=$('<div class="mcontainer"></div>');
		
	renderVueComponent("views/About",{partyid:"123"},$('<div style="border:1px solid #933;margin:5px 0;padding:5px">这是一个Vue组件（来自于views/About.vue）：</div>').appendTo(jele));
	
	/* 异步加载 上面的define不引入vuesupport 建议用这种写法
	require(["mobile/framework/vue/vuesupport"],function(){
		renderVueComponent("views/About",{},jele);
	});*/
	
	$(`<div class="_msplit" style="color:#339;white-space:pre-wrap;padding:5px;word-break:break-word;">
		任何代码可使用以下方式将vue组件呈现在jele上
		require(["mobile/framework/vue/vuesupport"],function(){
			renderVueComponent("{相对于vue根目录的路径，如views/About}",{},jele);
		});
		
		大量vue组件需要呈现时可以用
		define(["mobile/framework/vue/vuesupport"],function(vs){
			renderVueComponent("{相对于vue根目录的路径，如views/About}",{},jele);
		});
		
		当组件本身足以作为一个完整界面时，使用
		addVue("{相对于vue根目录的路径，如views/About}")或viewport.addVue("{相对于vue根目录的路径，如views/About}")
		来呈现一屏，此时，vue组件中methods可以暴露以下方法来支撑其它参数：
			getTitle
			getMenus	// 与getButtons二选一
			getButtons
			onActive
			onDeactive
			setParam
			
			
	</div>`).appendTo(jele);
	
	return {
		title:"演示使用vue组件",
		buttons:[{text:"Home",iconCls:"tbar",handler:()=>{
			//viewport.addVue("views/Home");
			addVue("views/Home",{});
		}}],
		panel:{domNode:jele},
		show:function(){
			
		},
		setParam:function(param){
			
		},
		onActive:()=>{
			
		},
		onDeactive:()=>{
			
		}
	};
});