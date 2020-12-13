/**
 * 
 */
var tolerance=3;
function neighbor(rect,refX,refY){
	var point="left",
	deta=dl=Math.abs(refX-rect.left),
	dr=Math.abs(rect.right-refX),
	dt=Math.abs(refY-rect.top),
	db=Math.abs(rect.bottom-refY);
	// 在里面时，计算比重,在外面时，要先计算容差
	if(rect.left>refX|| rect.right<refX || rect.top>refY||rect.bottom<refY){
		if((dl>tolerance&&dr>tolerance) || (dt>tolerance&&db>tolerance))
			return null;
	}
	deta=dl;
	if(dr<deta){deta=dr;point="right"}
	if(dt<deta){deta=dt;point="top"}
	if(db<deta){deta=db;point="bottom"}
	
	return point;
}
function tipInsertPoint(pele,rect){
	var point=pele.insertType,ele=window.eleTip;
	if(!ele){
		ele=window.eleTip=document.createElement("div");
		document.body.appendChild(ele);
		ele.className="insertTip";
		//ele.style.position="absolute";
		//ele.style.backgroundColor="green";
	}
	ele.style.display="block";
	var offsetSize=4,offsetPosition=-2;
	if(!point || !rect){
		if(pele.lastChild){
			point="right";
			rect=pele.lastChild.getBoundingClientRect();
		}else{
			offsetSize=-4;
			offsetPosition=2;
			point="left";
			rect=pele.getBoundingClientRect();
		}
	}
	ele.style.width=(point=="left"||point=="right")?"2px":(rect.right-rect.left)+offsetSize;
	ele.style.height=(point=="top"||point=="bottom")?"2px":(rect.bottom-rect.top)+offsetSize;
	ele.style.left=point=="right"?rect.right:(rect.left+offsetPosition);
	ele.style.top=point=="bottom"?rect.bottom:(rect.top+offsetPosition);
}
window.onDragComplete.add(function(e){if(window.eleTip)window.eleTip.style.display="none";});
function findInsertPoint(e){
	var ele=this,refX=e.clientX,refY=e.clientY;//offsetX=ele.clientLeft,offsetY=ele.clientTop
	if(ele.dropTarget){
		ele.dropTarget.insertPoint=null;
		var rectFind=null;
		$.each(ele.dropTarget.childNodes,function(i,item){
			var rect=item.getBoundingClientRect();
			var point=neighbor(rect,refX,refY);
			if(point!=null){
				rectFind=rect;
				ele.dropTarget.insertType=point;
				ele.dropTarget.insertPoint=(point=="left" || point=="top")?item:item.nextSibling;
				return false;
			}
		});
		tipInsertPoint(ele.dropTarget,rectFind);
	}
}
function tipHoveringComponent(proxy){
	if(!window.eleComponentTip){
		var ele=window.eleComponentTip=document.createElement("div");
		ele.className="componentTip";
		document.body.appendChild(ele);
	}
	if(!proxy){
		window.eleComponentTip.style.display="none";
		return;
	}
	window.eleComponentTip.style.display="block";		
	var rect=proxy.getBoundingClientRect();
	window.eleComponentTip.style.top=rect.top-26;
	window.eleComponentTip.style.left=rect.left+4;
	window.eleComponentTip.innerHTML=(proxy.setting.define.name||proxy.setting.define.xtype)+proxy.setting.id+($(proxy).hasClass("hidden")?' <span style="color:red">已设置隐藏</span>':"");	
}