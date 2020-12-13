/**
 * 
 */
define(["_filelist"],function(filelist){	
	let jele=$('<div class="mcontainer" style="text-align:center;"></div>');
	
	let bid="1588756787617",//new Date().getTime(),
		acode="demo";
	
	let files= filelist.create({parent:jele,bid:"1588756787617",btype:"file",acode:acode,multiple:true,editable:true});
	let images=filelist.createImage({parent:jele,bid:bid,btype:"image",acode:"pdf",imageSize:{width:128,height:128}});
	let videos=filelist.createVideo({parent:jele,bid:bid,btype:"video",acode:"video"});
	
	let uploader=filelist.createImage({bid:"123456",btype:"",acode:"photo1inch"});
	uploader.appendTo(jele);
	
	
	return {
		title:"上传测试",
		buttons:[{text:"上传",iconCls:"tbar",handler:()=>{
			files.upload(()=>{
				images.upload(()=>{
					videos.upload();
				})
			})
		}}],
		panel:{domNode:jele},
		show:function(){
		},
		setParam:function(param){
			
		}
	};
});