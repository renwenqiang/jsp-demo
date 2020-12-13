/**
 * 
 */
define(["_templatelist","_fileHelper"],function(){
	let jele=$('<div class="mcontainer" style="text-align:center;padding:0 15px"></div>');
	
	fileHelper.isAttach=function(url){/*是否系统内附件--即是否需要调用getAttachUrl/getImageUrl*/
		return url && url.indexOf("?")==-1 && url.indexOf("://")==-1 && url.indexOf("/")!=0;
	}
	
	new sunz.MSearchList({
		parent:jele,
		key:"p_i_news",
		type:"panel",
		tplcode:'lr_sil',
		pagesize:5
	});
	
	return {
		title:"预定义模板使用演示",
		buttons:[],
		panel:{domNode:jele},
		show:function(){
		},
		setParam:function(param){
			
		}
	};
	
});
