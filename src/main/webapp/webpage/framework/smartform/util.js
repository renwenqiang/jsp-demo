/**
 * tab键支持
 */
var tabSupport=function(e){
	if(e.keyCode===9){
		var shift=e.shiftKey;
		var start=this.selectionStart,end=this.selectionEnd,deta=1,
			sel=this.value.substring(start,end),
			hasN=sel.indexOf("\n")>-1,
			pre=this.value.substr(0,start),
			pad="\t";
		if(shift){
			pad="";
			var last=pre.substr(start-1);
			if(last=="\t" || last==" " || last=="　"/*中文*/){
				pre=pre.substr(0,start-1);
				deta=-1;
			}
			if(hasN)pad=sel.replace(/\n\s/g,"\n");
		}else{
			if(hasN)pad +=sel.replace(/\n/g,"\n\t");
		}
		this.value=pre+pad+this.value.substr(end);
		this.focus();
		this.selectionStart=start+deta;
		this.selectionEnd=start+pad.length;
		e.preventDefault();
	}
};
var formatJson = function(json, options) {
	options = options || {};
	
	if (typeof json !== 'string') {return JSON.stringify(json,null,"\t");}
	json=trimJson(json); // 防止多次
	var formatted = '',PADDING = '\t',indent=0,
		preChar,nextChar=json.charAt(0),quotCount=0,brackets=0,charEsc="\\",charFomat="\r\n",preFormat,
		dQuot='"',sQuot="'",quot;
	var tmp;
	var getPad=function(count){
		var pad="";
		for (var i= 0;i< count;i++) {
			pad += PADDING;
		}
		return pad;
	},getFormat=function(deta){
		lastIndented=true;
		indent=indent+deta;
		return charFomat+getPad(indent);
	}
	for(var i=0,len=json.length,mx=len-1;i<len;i++){
		var c=nextChar;
		nextChar=json.charAt(i+1);
		if((c==dQuot||c==sQuot) && preChar!=charEsc){
			if(!quot || quotCount==0) // 双数后顺序优先
				quot=c;
			if(c==quot){
				quotCount=(quotCount+1)%2;
			}
		}
		if(quotCount==0){
			if(c=="(")brackets++;
			if(c==")") brackets--;
		}
		tmp=c,lastIndented=false;
		if(quotCount==0 && brackets==0){
			if((c=="[" && nextChar=="]") || (c=="{" && nextChar=="}")){
				tmp=c+getFormat(0)+nextChar;
				preChar=nextChar;
				nextChar=json.charAt(i+2);
				i++;
			}else{
				if(c=="[" || c=="]" || c=="{" || c=="}"){		// 括号上下换行
					tmp=(i==0 || preFormat/*连接不用换行*/ || (options.removeNewline && preChar==":")/*冒号+不换行*/?"":getFormat(c=="]"||c=="}"?-1:0))
						+c
						+(i==mx||nextChar==","?"":getFormat(c=="["||c=="{"?1:-1));
				}else if(c==","||c==";"){		// 逗号下换行，但前面已换行不再换行
					tmp=c+getFormat(nextChar=="]"||nextChar=="}"?-1:0);
				}
			}
			if (options.spaceAfterColon && c==":")
				nc=c+" ";
		}
		preFormat=lastIndented;
		formatted=formatted+tmp;
		preChar=c;
	}
	return formatted;
};
var trimJson=function(json){
	var njson="";
	var preChar=null,quotCount=0,charEsc="\\",
		dQuot='"',sQuot="'",quot,regTrim=/\s/,regBound=/\W/,nextChar;
	for(var i=0,len=json.length,mx=len-1;i<len;i++){
		var c=json.charAt(i),nextChar=json.charAt(i+1);
		if((c==dQuot||c==sQuot) && preChar!=charEsc){
			if(!quot || quotCount==0) // 双数后顺序优先
				quot=c;
			if(c==quot){
				quotCount=(quotCount+1)%2;
			}
		}
		if(quotCount==0 && regTrim.test(c)){
			var last=njson.charAt(njson.length-1);
			if((regBound.test(last) && last!=dQuot && last!=sQuot) // exc: x=""\r\n return x
			 ||(regBound.test(nextChar) && nextChar!=dQuot && nextChar!=sQuot )){} // exc: return ""
			else njson=njson+" ";
		}else{
			njson=njson+c;
		}
		preChar=c;
	}
	return njson;
};