/**
 * 
 */
define(["_fileHelper"],function(helper){
	helper=helper||window.fileHelper;
	var css='.sunzmui-lfile-container{text-align:left}\
			.sunzmui-lfile-div{position:relative;margin:10px 10px 0 0;background-color:#eee;padding:5px;height:46px;}\
			.lfile-proxy{background-image:url(resource/image/upload/fileinput_mid.png);background-size:100% 100%;}\
			.sunzmui-limg-div,.sunzmui-lvideo-div{position:relative;display: inline-block;overflow:hidden;padding:12px 12px 2px 0;width:33%;height:33vw;}\
			.sunzmui-limg-divx{width:100% !important;height:auto !important;padding:0 !important}\
			.sunzmui-limg,.sunzmui-lvideo{width:100%;margin:1px 0;height:100%;border:1px dashed #aaa;display:flex;justify-content:center;align-items:center;}\
			.sunzmui-limg-img,.sunzmui-video-preview{max-width:100%;max-height:100%}\
			.sunzmui-limg-div .sunzmui-lfile-remove,.sunzmui-lvideo-div .sunzmui-lfile-remove{top:2px;right:2px;}\
			.limg-proxy,.lvideo-proxy,.lvideo-video,.sunzmui-lvideo-video{width:100%;height:100%;background-size:100% 100%;}\
			.limg-proxy{background-image:url(resource/image/upload/uploadimage.jpg);}\
			.sunzmui-lvideo-div{}\
			.sunzmui-lvideo-img{}\
			.img-preview{width:100%;height:100%;display:flex;justify-content:center;align-items:center}\
			.lvideo-proxy{background-image:url(resource/image/upload/videoinput.png);}\
			.lvideo-video{background-image:url(resource/image/upload/video.png)}\
			.sunzmui-lfile-type{height:36px;float:left;}\
			.sunzmui-lfile-title{height:36px;width:calc(100% - 36px);border:none;padding:5px;background-color:transparent;}\
			.sunzmui-lfile-remove{position:absolute;top:-10px;right:-10px;width:20px;height:20px;background-size:100% 100%;}\
			.remote{background-image:url(resource/image/upload/fileremove.png)}\
			.locale{background-image:url(resource/image/upload/filecancel.png)}\
			.sunzmui-hide{display:none;}\
		';
	$("<style>").attr({ rel: "stylesheet",type: "text/css"}).html(css).appendTo("head");
	var M=1048576;
	var createDefaultMask=tname=>{
		return function(eventArg){
			eventArg.tname=tname;			
			if(eventArg.count==1){
				return eventArg.loaded==eventArg.total?"已完成上传，后台正在处理...":tplReplace("{tname}正在上传...",eventArg);
			}else{
				return tplReplace("正在处理第{index}个{tname}："+(eventArg.loaded==eventArg.total?"上传已完成，后台正在处理...":("上传中..."+Math.round(eventArg.loaded*100/eventArg.total)+"%")),eventArg);
			}
		}
	},defaults={
			caption:"附件标题",
			mask:createDefaultMask("图片"),
			path:"attachment",
			url:"framework/attach.do?getAttachments"
	},upload=function(file,bid,mc,lx,acode,fnProgress,fnSuccess,fnErr){
		var formData=new FormData();
		formData.append("file", file);
		formData.append("mc", mc);
		formData.append("ywlx",lx);
		formData.append("acode",acode);
		formData.append("bid",bid);
		$.ajax({  
	          url: 'framework/attach.do?upload',  
	          data: formData, 
	          type: 'POST',cache: false,contentType: false,processData: false,  
	          progress:fnProgress,
	          success: fnSuccess,
	          error:fnErr
		});
	},bandRemove=function(jnew){
		jnew.find(".sunzmui-lfile-remove").on("click",function(e){
			(e||window.event).stopPropagation();
			var _this=this;
			if($(_this).hasClass("remote"))
				$.prompt("提示","您确认要删除吗？",null,function(yes){
					if(yes)
						$.post('framework/attach.do?delete',{id:$(_this).attr("fjid")},function(jr){if(jr.success)_this.parentNode.remove();});
				});
			else
				_this.parentNode.remove();
		});
	},isImg=fj=>{
		return fj.mineType && fj.mineType.indexOf("image")>-1;
	},isPdf=fj=>{
		return helper.getFileExtension(fj.url)=="pdf";
	},isOffical=fj=>{
		var ext=helper.getFileExtension(fj.url);
		return ext && ["doc","docx","xls","xlsx"].indexOf(ext)>-1;
	},resolvePdfUrl=url=>{
		if(url.indexOf("://")<0){
			url=url.indexOf("/")==0?url:(webRoot+url);
			url=location.href.substr(0,location.href.indexOf(webRoot))+url;			
		}
		return "plug-in/pdfjs/web/viewer.html?file="+encodeURIComponent(url);
	},previewImg=(fj)=>{
		viewport.add({destroyOnback:true,title:fj.name||"图片预览",panel:{domNode:$('<img src="'+helper.getAttachUrl(url)+'" style="width:100%;height:auto" />')}});
	}
	return {
		createSimpleMask:createDefaultMask,
		docMask:createDefaultMask("文件"),
		videoMask:createDefaultMask("视频"),
		create:function(config){
			config=$.extend({},defaults,{mask:this.docMask},config);
			if(config.acode){
				config.filetypes=helper.getAcceptTypes(config.acode);
			}else{
				console.error("未指定附件上传类型信息，将无法上传");
			}
			var getRemoveable=function(fj){
				return config.editable&&($.isFunction(config.deleteable)?config.deleteable(fj):config.editable);
			};

			var jele=$('<div class="sunzmui-lfile-container"></div>');
			jele.getDispalyProxyHtml=function(fj){
				var j=$('<div class="sunzmui-lfile-div">'
						+helper.getIconHtml(fj.url,null,"sunzmui-lfile-type")
						+'<input name="mc" class="sunzmui-lfile-title" placeholder="'+config.caption+'"/>'
						+'</div>');
				j.find("input").attr("value",fj.name);
				return j;
			};
			jele.getInputProxyHtml=function(cfg){
				return '<div class="sunzmui-lfile-div lfile-proxy"></div>';
			};
			jele.onInputComplete=function(ele,file,cfg){
				var fileName=file.name;
				fileName=fileName.substr(fileName.lastIndexOf("\\")+1);
				
				return $('<div class="sunzmui-lfile-div"></div>')
				.append(helper.getIconHtml(fileName,null,"sunzmui-lfile-type"))
				.append('<input name="mc" class="sunzmui-lfile-title" placeholder="'+cfg.caption+'" value="'+fileName+'"/>')				
				.insertBefore(ele);
			};
			jele.previewFile=function(fj){
				if(isImg(fj))return previewImg(fj);
				var ourl=helper.getAttachUrl(fj.url),
					url=ourl;
				if(isPdf(fj)){
					url=resolvePdfUrl(url);
				}else if(isOffical(fj)){
					url="framework/attach.do?pdf&url="+fj.url;
					url=resolvePdfUrl(url);
				}
				viewport.add({destroyOnback:true,title:fj.name,
					panel:{domNode:$('<iframe src="'+url+'" style="width:100%;height:100%"></iframe>')[0]},
					buttons:[{text:"下载原文件",handler:function(){
						window.open(ourl);
					}}]
				});
			};
			jele.getAttachName=function(div){
				return $("[name=mc]",div).val();
			};
			jele.createNew=function(fj){
				var jnew=$(jele.getDispalyProxyHtml(fj)).on("click",function(){jele.previewFile(fj);}).appendTo(jele);
				if(getRemoveable(fj))
					jnew.append('<div fjid="'+fj.id+'" class="sunzmui-lfile-remove remote "></div>');
				bandRemove(jnew);
				
				jnew.addClass("_attach")[0].fj=fj;
				
				return jnew;
			};
			var acceptTypes=config.filetypes?config.filetypes.split(","):[];
			var isAvalidType=function(fname,customTypes){
				if(!customTypes ||!customTypes.length)return true;
				var idx=fname.lastIndexOf("."),ex=idx>=0?fname.substring(idx+1):"";
				ex=ex.toLowerCase();
				return customTypes.indexOf(ex)>-1;
			};
			jele.createProxy=function(){
				var proxy=$(jele.getInputProxyHtml(config)).addClass("file-proxy").on("click",()=>{
					if(config.maxcount && jele.getAttachments().length+jele.getDirties().length >= config.maxcount){
						$.tip("当前最大允许文件个数限制为【"+config.maxcount+"】，如需替换请先删除已选文件");
						return;
					}
					$('<input name="file" type="file" '+(config.multiple?'multiple="true"':"")
							+' accept="'+(config.accepts||$.map(acceptTypes,t=>"."+t).join(","))
							+'" class="sunzmui-lfile" style="display:none"/>')
					.on("change",function(e){
						(e||window.event).stopPropagation();
						
						var files=e.target.files||e.dataTransfer.files; 
						if(!files) return;
						
						$.each(files,(i,file)=>{
							var fname=file.name;
							if(!isAvalidType(fname,acceptTypes)){
								$.tip("文件【"+fname+"】格式不支持",2000);
								return;
							}
							if(config.maxsize){
								if(file.size>config.maxsize){
									$.tip("所选择文件【"+fname+"】过大，当前最大允许"+(config.maxsize>M?(Math.round(config.maxsize*100/M)/100+"M"):(Math.round(config.maxsize*100/1024)/100+"K")),2000);
									return;
								}
							}
							if(config.maxcount && jele.getAttachments().length+jele.getDirties().length >= config.maxcount){
								$.tip("当前最大允许文件个数限制为【"+config.maxcount+"】，多余文件将被忽略");
								return false;
							}
							
							var jnew=jele.onInputComplete(proxy,file,config);
							jnew.addClass("dirty").append('<div class="sunzmui-lfile-remove locale"></div>');
							jnew[0].file=file;
							bandRemove(jnew);
						});
					}).click();
				}).appendTo(jele)[0];
			};
			jele.load=function(){
				jele.html("");
				if(!config.bid){ //否则导致加载全部
					if(config.editable!=false)
						jele.createProxy();
					return;
				}
				$.post(config.url,{bid:config.bid,btype:config.btype||config.fjlx},function(jr){
					if(jr.success){
						$.each(jr.data,function(i,fj){jele.createNew(fj);});
						config.onLoaded && config.onLoaded(jr.data);
					}
					if(config.editable!=false)
						jele.createProxy();
				});
			};
			jele.getDirties=function(){
				return $.map(jele.find(".dirty"),function(div){return {file:div.file,mc:jele.getAttachName(div)}});
			};
			jele.getAttachments=function(){
				return $.map(jele.find("._attach"),function(div){return div.fj});
			}
			jele.upload=function(fn,bid,fnErr){
				bid=bid||config.bid;
				if(!bid)return fnErr&&fnErr();
				
				config.bid=bid;
				var index=0,
					dirties=jele.getDirties(),
					count=dirties.length;
				if(count==0){
					return fn && fn();
				}
				var fnFail=function(msg){
					process.destroy();
					if(fnErr){
						fnErr();
					}else{
						$.tip("上传出错啦！<br/>"+msg,2000);
					}
				},fnSuccess=function(jr) {
					if(jr.success){process.setValue((++index)/count);fnUpload();}else{fnFail(jr.msg);}
				},
				fnUpload=function(){
					var info=dirties[index];
					if(index<count){
						upload(info.file,
								bid,
								info.mc,
								config.btype||config.fjlx,
								config.acode,
								function(e){
									process.setValue((index+e.loaded/e.total)/count);
									var eventArg={count:count,index:index+1,loaded:e.loaded,total:e.total};
									process.setInfo($.isFunction(config.mask)?config.mask(eventArg):tplReplace(config.mask,eventArg));
								},fnSuccess,fnFail);
					}else{
						process.destroy();
						fn&&fn();
						count && config.durable && jele.load();
					}
				};				
				var process=new sunz.MProgress({loop:false});
				process.setValue(0);
				fnUpload();
			};
			jele.update=function(opt,preventRefresh){
				$.extend(config,opt);
				
				if(!preventRefresh)
					jele.load();
			};
			
			if(config.parent)
				jele.appendTo(config.parent);
			
			if(!config.preventLoad)
				jele.load();
			
			return jele;
		},
		createImage:function(config){
			config=$.extend({},defaults,{accept:"image/*"},config);
			var imgSize=config.imageSize||{width:110,height:100};
			// 中断
			var preventLoad=config.preventLoad;
			config.preventLoad=true;
			var jele=this.create(config);
			
			var proxyHtml=(html)=>{
				return '<div class="sunzmui-limg-div '+(config.viewMode?"sunzmui-limg-divx":"")+'">'+html+'</div>';
			},getProxy=(url,img)=>{
				img=img||('<img class="sunzmui-limg-img"'+(url?(' src="'+url+'"'):"")+'/>');
				return proxyHtml('<div class="sunzmui-limg">'+img+'</div>');
			}
			// 重载
			jele.getDispalyProxyHtml=function(fj){
				var forceUrl="framework/attach.do?image&url="+fj.url+"&width="+imgSize.width+"&height="+imgSize.height;
				return getProxy(null,helper.getImageHtml(fj.url,imgSize.width,imgSize.height,{"class":"sunzmui-limg-img"}));
			};
			jele.getInputProxyHtml=function(cfg){
				return proxyHtml('<div class="limg-proxy"></div>');
			};
			jele.onInputComplete=function(ele,file,cfg){
				var jele=$(getProxy()).insertBefore(ele);
				var reader =new FileReader();
				reader.onload=function(){
					jele.find("img").attr("src",this.result);
				}
				reader.readAsDataURL(file);
				return jele;
			};
			jele.previewFile=function(fj){
				viewport.add({title:fj.name||"图片预览",panel:{domNode:$('<div class="img-preview"><img src="'+helper.getAttachUrl(fj.url)+'" style="max-width:100%;" /></div>')}});
			};
			jele.getAttachName=function(input){
				return config.getName?config.getName(input):"";
			};
			
			// 恢复
			if(!(config.preventLoad=preventLoad))
				jele.load();
			
			return jele;
		},
		createVideo:function(config){
			config=$.extend({},defaults,{accept:"video/*",mask:this.videoMask},config);
			// 中断
			var preventLoad=config.preventLoad;
			config.preventLoad=true;
			var jele=this.create(config);
			
			var getProxy=(html)=>{
				return '<div class="sunzmui-lvideo-div'+(config.viewMode?"sunzmui-lvideo-divx":"")+'">'+html+'</div>';
			};
			// 重载
			jele.getDispalyProxyHtml=function(fj){
				return getProxy('<div class="sunzmui-lvideo"><video class="sunzmui-lvideo-video" src="'+helper.getAttachUrl(fj.url)+'" preload="metadata"/></div>');
			};
			jele.getInputProxyHtml=function(cfg){
				return getProxy('<div class="lvideo-proxy"></div>');			
			};
			jele.onInputComplete=function(ele,file,cfg){
				return $(getProxy('<video class="sunzmui-lvideo-video lvideo-video" preload="metadata"/>')).insertBefore(ele)
				.on("click",function(){
					var video=$(this).find("video")[0];
					if(video.isloadding)
						return;
					if($(video).attr("src"))
						return video.paused?video.play():video.pause();
					
					var reader =new FileReader();
					reader.onload=function(){
						video.isloadding=false;
						$(video).attr("src",this.result).removeClass("lvideo-video");
						video.play();
					}
					reader.readAsDataURL(file);
					video.isloadding=true;
				});
			};
			jele.previewFile=function(fj){
				viewport.add({destroyOnback:true,onDeactive:function(){this.panel.domNode.pause();}, title:'视频预览',panel:{domNode:$('<video class="sunzmui-video-preview" controls preload="metadata" src="'+helper.getAttachUrl(fj.url)+'"/>')[0]}});
			};
			jele.getAttachName=function(input){
				return config.getName?config.getName(input):"";
			};
			
			// 恢复
			if(!(config.preventLoad=preventLoad))
				jele.load();
			
			return jele;
		}
	};
	
});
