/**
 * 修改密码页面
 */

define([],function(){
	var css='.active_btn{background-color:#fa0727;text-align:center;width:40%;}\
		.cancel_btn{background-color:#e8a203;text-align:center;width:40%;}';
		
	$("<style>").attr({ rel: "stylesheet",type: "text/css"}).html(css).appendTo("head");
	
	var jele=$('<div style="width:100%;height:100%;overflow:scroll;position:relative;background-color:#fff;padding-bottom:50px;">\
			<div class="hdn_toolbar" style="position:fixed;bottom:0;width:100%;z-index:999;display:flex;justify-content: space-around;line-height:36px;padding:5px;color:#fff;"></div>\
	    </div>'),baseConfig={parent:jele[0],blank:{value:"",text:'请选择...'}};
	var yhm= new sunz.MTextbox($.extend({label:"用户名",name:"YHM",value:loginUser.userName,readonly:true},baseConfig)),
	ymm= new sunz.MTextbox($.extend({label:"原密码",name:"YMM",type:"password",placeholder:"请输入原密码"},baseConfig)),
	xmm= new sunz.MTextbox($.extend({label:"新密码",name:"XMM",type:"password",placeholder:"请输入新密码"},baseConfig)),
	qrmm= new sunz.MTextbox($.extend({label:"确认密码",name:"QRMM",type:"password",placeholder:"请再次输入新密码"},baseConfig));
	var components=[
	    yhm,
	    ymm,
	    xmm
    ];
	var getValues=function(){
		var vo={};
		$.each(components,function(i,comp){vo[comp.options().name]=comp.getValue();});
		return vo;
	};
	var changePwd=function(){
		var param=getValues();
		var username=param.YHM,password=param.YMM,newpassword=param.XMM;
		$.ajax({
			url:"userController.do?savenewpwd",
			data:{"userName":username,"password":password,"newpassword":newpassword},
			method:"POST",
			dataType:"json",
			success:function(data){
				if(data.success){
					localStorage.setItem("lastPwd","");
				}else{
					$.tip("密码修改失败，"+data.msg,2000);
				}
			}
		});
	};
	var avtive=function(){
		var ypwd=ymm.getValue();//先验证原密码
		if(ypwd=='...'||ypwd==''){
			$.tip("请输入原密码",1000);
			return;
		}
		
		var xpwd=xmm.getValue();
		if(xpwd=='...'||xpwd==''){
			$.tip("请输入密码，密码不能为空",1000);
			return;
		}else if(xpwd==ypwd){
			$.tip("新密码和原密码不能相同",1000);
			return;
		}else{
			var reg=/(\w+.*\d+)|(\d+.*\w+)/;//包含数字和字母
			if(xpwd.length<8 || !reg.test(xpwd)){
				$.tip("请输入8位以上包含数字与字母的密码",1000);
				return;
			}
		}
		var rpwd = qrmm.getValue();
		if(rpwd=='...'||rpwd==''){
			$.tip("请再次输入密码",1000);
			return;
		}else if(rpwd!=xpwd){
			$.tip("两次密码不相同",1000);	
			return;
		}
		changePwd();
	};
	var toolbar=jele.find(".hdn_toolbar");
	$('<div class="active_btn">确认修改</div><div class="cancel_btn">暂不修改</div>').appendTo(toolbar);
	jele.find(".active_btn").on("click",function(){avtive();});
	jele.find(".cancel_btn").on("click",function(){viewport.back();});
	return  {
		title:"修改密码",
		buttons:[],
		panel:{domNode:jele[0]},
		setParam:function(param){
			
		}
	};
});