<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!Doctype html>
<html>
<head>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,sunzmobile,requirejs"></z:resource>
<z:dict items="all"></z:dict>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width,
initial-scale=1.0,
maximum-scale=1.0,
user-scalable=no">

<title>list</title>
 
</head>

<body style=" 
    overflow-x: hidden; ">


<video id="video" autoplay><ideo>  
</body>
<script>
var getUserMedia = (navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia);   
getUserMedia.call(navigator, {   
video: true,   
audio: true   
}, function(localMediaStream) {   
var video = document.getElementById('video');   
video.src = window.URL.createObjectURL(localMediaStream);   
video.onloadedmetadata = function(e) {   
console.log("Label: " + localMediaStream.label);   
console.log("AudioTracks" , localMediaStream.getAudioTracks());   
console.log("VideoTracks" , localMediaStream.getVideoTracks());   
};   
}, function(e) {   
console.log('Reeeejected!', e);   
});   

</script>
<script>
SHAKE_THRESHOLD = 500;
navigator.vibrate(100);
oldx = 1;
var num = 1
last_update = new Date().getTime();  
var kk =true;
function deviceMotionHandler(eventData) {  
    var acceleration = eventData.accelerationIncludingGravity;  
    var currTime = new Date().getTime();  
    var diffTime = currTime - last_update;  
    //console.info(diffTime);  
   
    if (diffTime > 100) {  
    	try{
    		last_update = currTime;  
            x = acceleration.x;  
            y = acceleration.y;  
            z = acceleration.z;  
            if(!window.last_x){
            	last_x = x;  
                last_y = y;  
                last_z = z;  
            }
            var speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 30000;  
           //console.info(speed);  
            //showa(speed)
            //$("body").append(speed+"</br>");
            //alert(speed)
            if (speed > SHAKE_THRESHOLD) {  
                //要一摇之后进行业务逻辑处理  
               // doResult();  

                $("body").append("speed"+speed+"</br>"+x+"11111</br>"+y+"</br>"+z+"</br>");
            }  
              
              
            last_x = x;  
            last_y = y;  
            last_z = z;  
    	}catch(e){
    		showa(e.message)
    	}
        
    } 
    //$("body").append(x+"11111</br>"+y+"</br>"+z+"</br>");
}
function showa (info){
	  if(kk){
			alert( info);
			num ++ 
			if(num>10) kk=false
		}
}
window.addEventListener('devicemotion', deviceMotionHandler, false);
</script>
</html>