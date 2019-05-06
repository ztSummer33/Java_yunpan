<%--
  Created by IntelliJ IDEA.
  User: wangj
  Date: 19.3.13
  Time: 19:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>失败</title>
</head>
<body>
<form action="" method="post"  name="form4">
    注册失败，5秒后返回注册界面...
    <script>
        setTimeout(function (){
            document.form4.action="/return_registered";
            document.form4.submit();
        },5000);
    </script>
</form>
</body>
</html>
