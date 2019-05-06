<%--
  Created by IntelliJ IDEA.
  User: wangj
  Date: 19.3.15
  Time: 8:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>跳转</title>
</head>
<body>

<form action="" method="post"  name="form3">
    <input type="hidden" name="username" value="${requestScope.username}" />
    <input type="hidden" name="file_path" value="${requestScope.file_path}" />
    <%--上传成功，5秒后返回主界面...--%>
    <script>

        document.form3.action = "/return_upload";
        document.form3.submit();

    </script>
</form>
</body>
</html>
