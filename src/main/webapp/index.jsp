<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<h2>Hello World! </h2>
<h5>jsp-demo</h5>
<button id="btn">
    按钮
</button>
<script>
    const btn = document.getElementById('btn')
    btn.onclick = function () {
        location.href = 'login.jsp?aa=2&bb=3'
    }
</script>
</body>
</html>
