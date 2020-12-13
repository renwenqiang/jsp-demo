
//
////var css = document.createElement('link');
////	css.href="public/css/searchPanel.css";    
////	css.rel="stylesheet";    
////	css.type="text/css";    
////	document.getElementsByTagName('head')[0].appendChild(css);
////	
//function include_css(path)   
//{       
//    var fileref=document.createElement("link")   
//    fileref.rel = "stylesheet";  
//    fileref.type = "text/css";  
//    fileref.href =path;
//} 

//include_css("public/css/searchPanel.css");
	
	
panelsearchForm = Ext.extend(Ext.Panel,{
		title:"项目搜索",
		iconCls:'icon-edit'
});

searchForm = Ext.extend(Ext.form.FormPanel,{
	
	iconCls:"icon-edit"
	   
	
});


		
		