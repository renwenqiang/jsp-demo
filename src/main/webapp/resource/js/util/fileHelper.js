/**
 * 文件类型图标处理
 */
window.fileHelper=window.fileHelper||top.fileHelper||(function(resp){
	let baseUrl='resource/image/file/',fix=".png";
	let unknown="file";
	let config={
		jpg:'jpg',jpeg:'jpg',
		bmp:'bmp',
		png:'png',
		doc:'doc',
		docx:'docx',
		pdf:'pdf',
		xsl:'xsl',
		xslx:'xslx'
	};
	let normalize=(url)=>{return url.replace(/\\/g,"/");}
	let getFileExtension=(url)=>{
		url=normalize(url);
		url=url.substring(url.lastIndexOf("/")+1).toLowerCase();
		return url.substring(url.lastIndexOf(".")+1);
	}
	let getIconPath=(fileType)=>{
		fileType=fileType||"file";
		let t=getFileExtension(fileType);
		return baseUrl+(config[t]||t)+fix;
	},
	getIconHtml=(fileType,style,cls)=>{
		return '<img onerror="javascript:this.src=\''+baseUrl+unknown+fix+'\';" src="'+getIconPath(fileType)+'" '+(cls?' class="'+cls+'" ':'')+(style?' style="'+style+'" ':'')+'/>';
	};
	
	let attachRoot=C["file.downloadRoot"],
		imgConnectChar=C["file.imageSizeChar"]||"_";
	let fnSuffix=(url,suffix)=>{
		url=normalize(url);
		let index=url.indexOf("/");
		let rootFolder=url.substring(0, index);
		return rootFolder+"_"+suffix+url.substring(index);
	}
	let type_accept=C.attachtypes;
	if(!type_accept){
		$.ajax("resource/js/attachconfig.js",{async:false,dataType:"script"});
		type_accept=C.attachtypes
	}

	let handleCss=(css)=>{
		if(typeof css=="string")
			return 'style="'+css+'" ';
		
		var html="",style="";
		$.each(css,(n,d)=>{
			if(n=="style")
				html+=handleCss(d);
			else if(n=="class")
				html+='class="'+d+'" ';
			else if(n=="attrs"){
				if(typeof d=="string")
					html+= d+' ';
				else
					$.each(d,(ns,ds)=>{
						html+= ns+'="'+ds+'" ';
					});
			}else{
				style+=n+":"+d+";";
			}
			
		})
		return html+(style?('style="'+style+'" '):" ")
	}
	return window.fileHelper={
		getIcoPath:getIconPath,
		getIconHtml:getIconHtml,
		
		getFileExtension:getFileExtension,
		getAttachUrl:(url,force)=>{
			if(!url)return "";
			return (force?(force===true?"framework/attach.do?get&url=":force):attachRoot) + normalize(url)
		},
		getImageUrl:function(url,width,height,force){
			if(!url)return "";
			return force?"framework/attach.do?image&url="+normalize(url)+"&width="+width+"&height="+height:this.getAttachUrl(width&&height?fnSuffix(url,width+imgConnectChar+height):url)
		},
		getPdfUrl:function(url){
			if(!url)return "";
			let ext="pdf";
			if(this.getFileExtension(url)==ext)
				return this.getAttachUrl(url);
			
			url=fnSuffix(url,ext); 
			return this.getAttachUrl(url.substring(0, url.lastIndexOf(".")+1)+ext);
		},
		//getAttachConfig:(acode)=>aconfigs[acode],
		getAcceptTypes:(acode)=>{return type_accept[acode]},
		getImageHtml:function(url,w,h,css){
			return '<img src="'+this.getImageUrl(url,w,h)+'" onerror="var url=\''+this.getImageUrl(url,w,h,true)+'\';this.src=this.src.indexOf(url)>-1?C.defaultImageErrorUrl:url" '+(css?handleCss(css):"")+' />';
		},
		toAcceptAttr:(accepts,defaults)=>{return accepts?accepts.replace(/\b\w/g,".$&"):defaults},
		getAcceptAttr:function(acode,defaults){return this.toAcceptAttr(this.getAcceptTypes(acode)||defaults)}
	};
})();
window.define && define("resource/js/util/fileHelper",[],function(){return window.fileHelper})