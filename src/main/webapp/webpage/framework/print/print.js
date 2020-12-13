/**
 * 打印相关代码
 */
var dpi=96;
var printer=null;//getPrinter();
function getDpi() {
	var dpi=96;
    if (window.screen.deviceXDPI != undefined) {
        dpi= window.screen.deviceXDPI;
    }
    else {
        var tmpNode = document.createElement("DIV");
        tmpNode.style.cssText = "width:1in;height:1in;position:absolute;left:0px;top:0px;z-index:99;visibility:hidden";
        document.body.appendChild(tmpNode);
        dpi= parseInt(tmpNode.offsetWidth);
        tmpNode.parentNode.removeChild(tmpNode);    
    }
    return dpi;
}
var pixTomm=function(pix){return pix*25.4/dpi;};
var mmTopix=function(mm){return mm*dpi/25.4;};
var mmToPrint=function(mm){return mm*1000/254;};
var getPrinter=function(){printer=document.createElement("object");printer.classid="clsid:4534AD5E-9A2E-4604-9A5D-F92BEC0497E1";printer.FullView=false;	return printer;}


// 设置相关
var dictSetting=null
	,currents=null
	winSetting=null;
var defSetting={c:20,d:20,f:'宋体',s:12};
var applyToElement=function(){
	var o=this.ele.printSetting=$.extend(this.ele.printSetting,formSetting.getFieldValues());
	if(this.ele.setPosition)this.ele.setPosition(o.x,o.y);
};
var initDefault=function(ele){
	if(!ele.printSetting){
		ele.printSetting=$.extend({field:ele.field||(ele.attributes.field||ele.attributes.name||{}).value,x:pixTomm(ele.getBoundingClientRect().left),y:pixTomm(ele.getBoundingClientRect().top)},defSetting);
		if(ele.src){
			ele.printSetting.w=pixTomm(ele.offsetWidth);
			ele.printSetting.h=pixTomm(ele.offsetHeight);
		}
	}
	if(ele.printSetting.x<0)ele.printSetting.x=0;
	if(ele.printSetting.y<0)ele.printSetting.y=0;
	return ele.printSetting;
};

var setPrintSetting=function(ele){
	// 应用到元素
	if(winSetting.ele) applyToElement.call(winSetting);
	
	if(winSetting.ele==ele)return;
	// 改表现
	winSetting.setTitle((ele.field||(ele.attributes.field||ele.attributes.name||{}).value)+"--属性设置");
	if(winSetting.ele && winSetting.ele.refer){
		winSetting.ele.refer.style.backgroundColor=winSetting.ele.refer.oldColor;
	}	
	if(ele.refer){
		ele.refer.oldColor=ele.refer.style.backgroundColor;
		ele.refer.style.backgroundColor="red";
	}
	var setting=initDefault(ele);
	winSetting.ele=ele;		
	winSetting.open();	
	formSetting.load(setting);
};
var loadSetting=function(formid,errfn){
	$.ajax("framework/print.do?elementsetting&formid="+formid,{err:errfn,success:function(j){
		dictSetting={};
		if(j.success){
			$.each(j.data,function(i,s){
				//s={id:s.ID,formid:s.FORMID,field:s.ELEMENT,setting:SETTING};
				var setting=$.decode(s.setting);
				setting.id=s.id;
				setting.field=s.element;
				dictSetting[s.element]=setting;
			});
			applySetting();
		}else{
			if(errfn)errfn();
		}
	}});
};
var applySetting=function(){// 初始化给每个对象
	if(dictSetting&&currents){
		$.each(currents,function(i,ele){
			ele.printSetting=dictSetting[ele.field||(ele.attributes.field||ele.attributes.name||{}).value];
			ele.refer=ele;
			// 对未设置的进行默认处理
			if(ele.printSetting) return; 	// 已加载 
			if(ele.offsetHeight==0) return; // 未显示
			initDefault(ele);
		});
	}
};

// 可视化相关
var dEle='<div style="background-color:red;position:absolute;width:{width}px;height:16px;left:{x}px;top:{y}px;">{field}</div>';
var Dragable=function(ele){
	var _me=this;
	$(ele).on("mousedown",function(e){
		_me.start=true;
		setPrintSetting(ele);
		_me.x=ele.offsetLeft;
		_me.y=ele.offsetTop;
		_me.ex=e.clientX;
		_me.ey=e.clientY;
	});
	$(document).on("mousemove",function(e){
		if(_me.start){
			ele.style.left=_me.x+(e.clientX-_me.ex);
			ele.style.top=_me.y+(e.clientY-_me.ey);	
			ele.printSetting.x=pixTomm(ele.offsetLeft);
			ele.printSetting.y=pixTomm(ele.offsetTop);
			formSetting.load(ele.printSetting);
		}
	});
	$(document).on("mouseup",function(e){
		_me.start=false;		
	});
};
var showTo=function(p,ele){
	var nDiv=$(tplReplace(dEle,{width:ele.offsetRight-ele.offsetLeft,field:ele.field||(ele.attributes.field||ele.attributes.name||{}).value,x:mmTopix(ele.printSetting.x),y:mmTopix(ele.printSetting.y)}))[0];
	p.append(nDiv);
	nDiv.printSetting=ele.printSetting;
	nDiv.refer=ele;
	nDiv.field=ele.field||(ele.attributes.field||ele.attributes.name||{}).value;
	ele.setPosition=nDiv.setPosition=function(x,y){
		nDiv.style.left=mmTopix(x);
		nDiv.style.top=mmTopix(y);
	};
	new Dragable(nDiv);
	return nDiv;
};

// 打印相关
var getPrintText=function(text,c,d){
	var printText="",temp=text.toString();
	temp=temp.replace(/\\r\\n/g,'\r\n');
	var count=0,len=temp.length;
	for(var i=0;i<len;i++ ){
		var cur=temp.substr(i,1);
		printText=printText+cur;
		if(cur=="\r")continue;
		if(cur=="\n"){
			count=0;
		}else{
			count=count+(cur.charCodeAt(0)>255?1:0.5);
		}
		if(count>=c){
			printText=printText+"\r\n";
			count=0;
		}
	}
	return printText;
};
var calcPrintLines=function(text,c){return getPrintText(text,c).split("\r\n").length-1;};
var offsetLine=function(text,lines){return new Array((lines||0)+1).join("\r\n")+text;};

var addToPrint=function(printer,ele,offsetX,offsetY,emptyText,imgText){
	var setting=ele.printSetting;
	if(setting.x<0||setting.y<0)return;
	var printX=mmToPrint(setting.x)+(offsetX||0),printY=mmToPrint(setting.y)+(offsetY||0);	
	if(ele.tagName=="IMG"){ 
		if(ele.src)
			printer.AddImage(ele.src,printX,printY,mmToPrint(setting.w),mmToPrint(setting.h),imgText||" "); // url,x,y,width,height,error
	}else{
		printer.AddText(getPrintText(ele.value||emptyText||" ",setting.c),printX,printY,setting.f||"宋体",1,setting.s,-16777216); // text,x,y,font,fontStyle,font-size,color
	}
};

// 打印机
window.dictPrinterSetting={};
var loadPrinterSetting=function(formid){
	$.ajax("framework/datatable.do?all&t=T_S_PRINTERSETTING"
			,{success:function(j){
				if(j.success){
					$.each(j.data,function(i,ps){
						ps={id:ps.ID,landscape:ps.LANDSCAPE,offsetX:ps.OFFSETX,offsetY:ps.OFFSETY,pageheight:ps.PAGEHEIGHT,pagewidth:ps.PAGEWIDTH,printer:ps.PRINTER};
						window.dictPrinterSetting[ps.printer]=ps;
					});
				}
			}
	});
};
var setPrinterSetting=function(printer,printerName){
	var s= window.dictPrinterSetting[printerName];
	if(s){
		printer.SetPrinter(s.printer);		
		printer.SetOffset(mmToPrint(s.offsetX),mmToPrint(s.offsetY));
		printer.SetLandscape(s.landscape);
		//printer.SetPageSize(mmToPrint(s.pageWidth),mmToPrint(s.pageHeight));	// 暂时应当是不需要的
	}
};

var getLocalPrinters=function(refresh){
	if(window.printers==null || refresh){
		window.printers=[];
		try{
			var lp=printer.GetPrinters().split("\r\n");
			$.each(lp,function(i,p){
				if(p)window.printers.push({text:p,value:p});
			});
		}catch(e){
			console.log("获取打印机列表出错了");
		}
	}
	return window.printers;
}
var PrinterSettingEditor =function(formid){
	var winSetting=new sunz.Window({width:260,height:240,title:'打印机设置',modal:true});
	var fSetting=$(
		   '<form id="formPrinter">'
		   +'<table cellpadding="1">'
		   +'<tr>'
		   +'	<td>打印机:</td>'
		   +'	<td><select  id="cmbPrinter" class="easyui-combobox" name="printer"  data-options="width:150"></select></td>'
		   +'</tr>'
		   +'<tr>'
		   +'	<td>X偏移(毫米):</td>'
		   +'	<td><input class="easyui-numberbox" type="text" name="offsetX" value="0"></input></td>'
		   +'</tr>'
		   +'<tr>'
		   +'	<td>Y偏移(毫米):</td>'
		   +'	<td><input class="easyui-numberbox" type="text" name="offsetY" value="0"></input></td>'
		   +'</tr>'
		   +'<tr>'
		   +'	<td>横向:</td>'
		   +'	<td><select class="easyui-combobox" name="landscape"  data-options="width:150">'
		   +'			<option value="1">是</option>'
		   +'			<option value="0">否</option>'
		   +'		</select></td>'
		   +'</tr>'
		   +'</table>'
		   +'</form>'
	).asForm();
	var bbar=$('<div id="bbarPrinter" style="padding:5px;position:absolute;right:10px;bottom:2px;left:20%;height:26px;"></div>');
	winSetting.append(fSetting);
	winSetting.append(bbar);
	new sunz.Linkbutton({parent:bbar[0],iconCls:'icon-ok',text:'确定',handler:function(){
 		var s=fSetting.getFieldValues();
 		var setting=$.extend(window.dictPrinterSetting[s.printer]||{},s);
		setting.formid=formid;
		var url='framework/datatable.do?'+(setting.id?"save":"add")+"&t=t_s_printersetting";
		$.ajax(url,{data:setting,success:function(r){
			if(r.success){
				setting.id=r.data.ID;
				window.dictPrinterSetting[name]=setting;
			}
		}});
 	}});
	new sunz.Linkbutton({parent:bbar[0],iconCls:'icon-cancel',text:'取消',handler:function(){
		winSetting.close();
 	}});

	$("#cmbPrinter").asCombobox({onChange:function(n,o){fSetting.load(window.dictPrinterSetting[n]);}}).loadData(getLocalPrinters());
	
	winSetting.close();
	this.edit=function(){winSetting.open();};
};

// 初始化
$(function(){
	dpi=getDpi();
	printer=getPrinter();
	loadPrinterSetting();
});