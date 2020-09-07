<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>

    <style>
        .content{
            width: 100%;
            height: 100%;
        }
        .content-layer{
            opacity: 0.4;
            background: #000000;
            position: absolute;
        }
        .dialog-box{
            visibility: hidden;
            width: 380px;
            height: 200px;
            border: 1px solid red;
            position:absolute;
            left:50%;
            top:40%;
            margin-left: -190px;
            background: #fff;
        }
        .send-btn1{
            height: 40px;
            width: 100px;
            color:#F25956;
            outline-style: none;
            border: 1px solid #F25956;
            border-radius: 3px;
            cursor: pointer;
            margin-left: 10px;
        }
        .send-btn2{
            height: 40px;
            width: 100px;
            color:#333;
            outline-style: none;
            border: 1px solid #D1D1D1;
            border-radius: 3px;
            margin-left: 10px;

        }
        .confirm-btn1{
            height: 40px;
            width: 300px;
            color:#fff;
            outline-style: none;
            border: 1px solid #ccc;
            border-radius: 3px;
            /* cursor: pointer; */
            display: block;
            margin: auto;
            background: #CACACA;
        }
        body{
            background: #ffffff url(http://localhost:8080/lzzhdj/webpage/login/images/background.jpg) no-repeat fixed top;
            background-size: 100% 100%;
            width: 100%;
            height: 100%;
        }
    </style>
</head>
<body>
<button id="btn">按钮</button>
<div class="content">

</div>
<div class="dialog-box">
    <div style="height: 40px; width: 100%; background-color: #F25956;">
        <p style="width: 135px; color: #fff; line-height: 0.5; float: left; font-size: 18px;">&nbsp;&nbsp;验证码识别</p>
        <p id="close-btn" style="width: 25px; color: #fff; float: right; line-height: 0.5; cursor: pointer;">×</p>
    </div>
    <div style="height: 40px; width: 100%; margin-top: 30px;">
        <p style="margin-left: 32px; width: 65px; color: #F25956; line-height: 0.8; float: left; font-size: 14px;">&nbsp;&nbsp;验证码</p>
        <input id="input-box" type="text" style="height: 40px; width: 100px;outline-style: none;border: 1px solid #ccc;border-radius: 3px;padding: 0px 10px;margin-left: 4px;" placeholder="请输入验证码"/>
        <input type="button" id="send-btn" class="send-btn1" value="发送验证码"/>
    </div>
    <div style="height: 40px; width: 100%; margin-top: 30px;">
        <input id="confirm-btn" type="button" class="confirm-btn1" value="确认"/>
    </div>
</div>
<script>
    let flag = false;
    let start = true;
    let limit = 10;
    let btn = document.getElementById("btn");
    let inputBox = document.getElementById("input-box");
    let sendBtn = document.getElementById("send-btn");
    let confirmBtn = document.getElementById("confirm-btn");
    let dialog = document.getElementsByClassName("dialog-box")[0];
    let contextBox = document.getElementsByClassName("content")[0];
    let closeBtn = document.getElementById("close-btn");
    confirmBtn.disabled="disabled";
    confirmBtn.style.cursor = "auto";
    inputBox.addEventListener('input', function(e) {
        e.stopPropagation();
        if (inputBox.value == "") {
            confirmBtn.disabled="disabled";
            confirmBtn.style.backgroundColor = "#CACACA";
            confirmBtn.style.cursor = "auto";
        } else {
            confirmBtn.disabled="";
            confirmBtn.style.backgroundColor = "#F65250";
            confirmBtn.style.cursor = "pointer";
        }
    })
    confirmBtn.addEventListener('click', function(e) {
        e.stopPropagation();
        dialog.style.visibility = flag ? "hidden" : "visible";
        contextBox.classList.remove("content-layer");
        flag = !flag;
        console.log(55)
    })
    btn.addEventListener('click', function(e) {
        e.stopPropagation();
        dialog.style.visibility = flag ? "hidden" : "visible";
        flag = !flag;
        contextBox.classList.add("content-layer");
    })
    sendBtn.addEventListener('click', function(e) {
        e.stopPropagation();
        sendBtn.classList.remove("send-btn1");
        sendBtn.classList.add("send-btn2");
        sendBtn.disabled="disabled"
        makeSetInterval(selfClock);
    })
    closeBtn.addEventListener('click', function(e) {
        e.stopPropagation();
        dialog.style.visibility = flag ? "hidden" : "visible";
        flag = !flag;
        contextBox.classList.remove("content-layer");
    })
    function makeSetInterval(f) {
        if (start) {
            setTimeout(() => {
                selfClock();
                makeSetInterval(start);
            }, 1000);
        } else {
            sendBtn.classList.remove("send-btn2");
            sendBtn.classList.add("send-btn1");
            sendBtn.value = "发送验证码";
            sendBtn.disabled="";
            limit = 10;
            start = !start;
        }
    }
    var selfClock = function () {
        limit--;
        if(limit < 1) {
            start = false;
        }
        sendBtn.value = "重新发送(" + limit + "s)";
    };
</script>
</body>
</html>
