/**
 * 
 */
if(!window.fileHelper){
	$.ajax("resource/js/util/fileHelper.js",{async:false,dataType:"script"});
}
(function(){
	var resolveUrl=function(url){
		if(url.indexOf("://")>=0 || url.indexOf("/")==0) // 绝对路径
			return url;
		
		return fileHelper.getAttachUrl(url);
	}
	smart.define("Img",{
		name:"图片或附件",isInput:true,
		defaults:{width:"100%",height:"100%"},
		changeMode:function(editable){
			this.readOnly=!editable;
		},
		setFormValue:function(v,o){
			if(!this.setting.name){	// 未设置name时，认为是固定值
				return;
			}
			this.url=v;
			this.src=v?resolveUrl(v):(this.readOnly?C.defaultNoImageUrl:(this.setting.uploadImage||C.defaultNoImageUrl));
		},
		getFormValue:function(){
			return this.url;
		},
		validate:function(){
			if(!this.readOnly && this.setting.required){
				return !!this.url;
			}
			return true;
		},
		customAdd:function(pele,params){
			var ele=document.createElement("img");
			
			$(ele).on("error",function(){
				var url=params.errorImage||C.defaultImageErrorUrl;
				url=this.src==url?C.defaultImageErrorUrl:url;
				this.src=url;
			});
			
			if(params.acode){
				$(ele).on("click",function(){
					if(this.readOnly)
						return;
					
					$('<input type="file" accept="'+(fileHelper.getAcceptAttr(params.acode)||"image/*")+'">').on("change",function(){
						var file=this.files[0];
						if(file.size>params.maxsize){
							var M=1<<20;
							$.messager.alert("温馨提示","所选择文件【"+fname+"】过大，当前最大允许"+(params.maxsize>M?(Math.round(params.maxsize*100/M)/100+"M"):(Math.round(params.maxsize*100/1024)/100+"K")));
							return;
						}
						var fd=new FormData();
						fd.append("acode",params.acode);
						fd.append("file",file);				
						$.ajax("framework/attach.do?upload",{data:fd,processData:false,contentType:false,success:function(jr){
							if(jr.success){
								var fj=jr.data;
								/*$(ele).off("error").on("error",function(){
									var forceUrl="framework/attach.do?image&url="+fj.url+"&width="+params.imageWidth+"&height="+params.imageHeight;
									ele.src=ele.src==forceUrl?params.errorImage||C.defaultNoImageUrl:forceUrl;
								});*/
								ele.attachid=fj.id;
								ele.src=fileHelper.getAttachUrl(ele.url=fj.url);//fileHelper.getImageUrl(fj.url,params.imageWidth,params.imageHeight);
							}else{
								$.messager.alert("上传失败",jr.msg);
							}
						}});
					}).click()
				});
				Object.defineProperty(ele,"url",{
					set:function(v){
						this.value=v;
						ele.onImageChanged && ele.onImageChanged.fire(v);
					},get:function(){
						return this.value;
					}
				});	
				ele.onImageChanged=$.Callbacks();
				ele.onImageChanged.add(function(url){
					if(ele.readOnly) return;
					if(url ){
						if(ele.removeProxy) return ele.removeProxy.show();
						var offset=10,
							pos=$(ele).position(),r=ele.getClientRects()[0],
							x=pos.left+r.width-offset,
							y=pos.top-offset;
							//rp=ele.parentNode.getClientRects(),r=ele.getClientRects(),						
							//x=r.left-rp.left+r.width-offset,
							//y=r.top-rp.top+offset;
						var pmode=$(pele).css("position");
						if(!(pmode=="relative"||pmode=="absolute"||pmode=="fixed"))
							$(pele).css("position","relative");
						ele.removeProxy=$('<div style="position: absolute;width:20px;height:20px;background-size:100% 100%;background-image:url(resource/image/upload/filecancel.png)"></div>')
						.appendTo(pele)
						.css("left",x).css("top",y)
						.on("click",function(){
							defines.Img.setFormValue.call(ele,null);
						})
					}else{
						ele.removeProxy && ele.removeProxy.hide();
					}
				});
			}		
			
			pele.insertPoint?pele.insertBefore(ele,pele.insertPoint):pele.appendChild(ele);
			$(ele).addClass(smart.selectProxyClass);
			ele.master=ele;
			ele.setting=params;
			this.customUpdate(ele);
			
			return ele;
		},
		customUpdate:function(ele,setting){
			smart.applyToElement(ele.master,setting||ele.setting,["src"]);
		}
	});
})();