sunz.MFileBox = function () {
	/**
	 * 文件框
	 */
    var defaults = {
        readonly: null,
        overview:true,//预览
        type: "file",
        inline:false,
        multiple:"multiple", //多文件
        accept:"*",    //"image/*" 文件类型
        
		_description:{
			title:"文件上传",
			remark:"" 
		}
    };
    var MFileBox = function (_option) {
    	/**
    	 * exoption  如果在option中配置的这些属性则option中的这些配置不生效
    	 */
    	this.exoption = ["width","height","type","inline"];
    	this.initOption(defaults,_option);
    	
        
        /**
         * 组件基本容器
         */
        var div = document.createElement("div");
        div.className += "  sunzmui-field  ";
       
        if(this.inline){
        	div.className += " sunzmui-field-inline "
        }
        this.domNode = div;
        this.initDom();
    },proto=MFileBox.prototype;

    proto.initDom = function(){
    	this.domNode.innerHTML = "";
    	if(this.inline){
    		$(this.domNode).addClass("sunzmui-field-inline");
    	}else{
    		$(this.domNode).removeClass("sunzmui-field-inline");
    	}
        /**
         * 文字
         */
        if (this.label) {
            var labelNode = document.createElement("label");
            labelNode.className += " sunzmui-field-lable ";
            if(this.inline){
            	labelNode.className += "  sunzmui-field-lable-inline   ";
            	if(this.labelwidth){
            		if(typeof this.labelwidth =="number"){
            			this.labelwidth = ""+this.labelwidth+"px";
            		}
            		else if(typeof this.labelwidth =="string"){
            			if(this.labelwidth.indexOf("px")>=0||this.labelwidth.indexOf("%")>=0||this.labelwidth.indexOf("rem")>=0){
            				
            			}else{
            				this.labelwidth +="px";
            			}
            		}
            		labelNode.style.cssText += " ; width:"+this.labelwidth +"; "
            	}
            }else{
            	labelNode.className += "    ";
            }
            labelNode.style.cssText += " ";
            this.domNode.appendChild(labelNode);
            labelNode.innerHTML = this.label;
        }
        var imageDiv = this.imageDiv = document.createElement("div");
        imageDiv.className += " sunzmui-field-images ";
        this.domNode.appendChild(imageDiv);
        if(this.inline){
        	imageDiv.className += "  sunzmui-field-input-inline   ";
        	if(this.labelwidth){
        		imageDiv.style.cssText += " ; width:calc(100% - "+this.labelwidth +"); "
        	}
        }else{
        	imageDiv.className += "    col-sm-9    ";
        }
        /**
         * 输入框容器
         */
        var contentNode = document.createElement("div");
        contentNode.className += " sunzmui-field-input ";
        if(this.inline){
        	contentNode.className += "  sunzmui-field-input-inline   ";
        	if(this.labelwidth){
        		contentNode.style.cssText += " ; width:calc(100% - "+this.labelwidth +"); "
        	}
        }else{
        	contentNode.className += "    col-sm-9    ";
        }
        this.domNode.appendChild(contentNode);
        contentNode.style.cssText += "position:relative; height: 50px;margin-right: 18px;";
        
        /**
         * 输入框
         */
        var inputNode = document.createElement("input");
        inputNode.className += " sunzmui-field-input form-control ";
        inputNode.type = this.option.type;
        contentNode.appendChild(inputNode);
        inputNode.style.cssText += " ";

        
        this.elecontentNode = contentNode;
        this.element = inputNode;
        this.listenerNode = inputNode;
        sunz.applyToElement(this.element, this.option);
        //sunz.registListener(this.listenerNode, this.listener);
        this.initListener();
        this._oncreate();
    };
    /**
     * 初始化完成后
     */
    proto._oncreate = function () {
    	if(!this.loaded){
    		this.addToParent?this.addToParent():"";
    	}
    	this.trigger("create", this); 
    	this.loaded = true;
    	this.fileChange();
    	this.onblur();
    };
   
    proto.onblur = function () {
    	var self = this;
    	this.bind("blur",function(){
    		self.validate?self.validate():"";
    	}) 
    };
    proto.fileChange = function(){  
    	var self = this; 
    	this.bind("change",function(e){
    	   self.imageDiv.innerHTML = "";
    	   var files = e.target.files||e.dataTransfer.files; 
 	       if(files){
 	    	   for(var i=0;i<files.length;i++){
 	    		   var file = files[i];
 	    		   self._addFile(file);
 	    		   
 	    	   }
 	          
 	      }
    	});
    }
    proto._addFile = function(file){
    	var self = this; 
    	var fileType = file.type;
		var fileName = file.name;
		if(this.overview){
		   if(fileType.indexOf("image")>=0){
			   var reader = new FileReader();
	           reader.onload=function(){
	                  $("<img src='"+this.result+"'/>").appendTo(self.imageDiv);
	           }
	           reader.readAsDataURL(file);
		   }
		   else if(fileType.indexOf("video/mp4")>=0){
			   var reader = new FileReader();
			   var video = document.createElement("video");
			   video.controls = true;
			   $(video).appendTo(self.imageDiv);
	           reader.onload=function(){
	        	 video.src=this.result;
	           }
	           reader.readAsDataURL(file);
		   }//"audio/mp3"
		   else if(fileType.indexOf("audio/mp3")>=0){
			   var container = document.createElement("li");
			   container.className += " sunzmui-field-input-audiobox ";
			   var reader = new FileReader();
			   var audio = document.createElement("audio");
			   audio.controls = true;
			   
			   $("<li class='sunzmui-field-input-filename'>"+fileName+"</li>").appendTo(container);
			   container.appendChild(audio);
			   $(container).appendTo(self.imageDiv);
	           reader.onload=function(){
	        	 audio.src=this.result;
	           }
	           reader.readAsDataURL(file);
		   }
		   else{
			  $("<li class='sunzmui-field-input-filename'>"+fileName+"</li>").appendTo(self.imageDiv);
		   }
		}
		 else{
			  $("<li class='sunzmui-field-input-filename'>"+fileName+"</li>").appendTo(self.imageDiv);
		   }
    }
    /**
     * 获取当前值
     */
    proto.getValue = function () {
        var value = this.element.value;
        return value;
    };
    
    /**
     * 设置值
     */
    proto.setValue = function (value) {
    	if(this.element){
    		$(this.element).val(value);
    	}
    };
 
    return MFileBox;
}();

