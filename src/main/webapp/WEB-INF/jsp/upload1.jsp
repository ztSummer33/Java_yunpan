<%@ page import="com.example.demo.Connect.Connect_mysql" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.ResultSet" %><%--
  Created by IntelliJ IDEA.
  User: wangj
  Date: 19.3.18
  Time: 18:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <title>搜索结果</title>
    <style>
        table {width: 95%;text-align: center;border: 0;border-collapse: collapse;border-spacing: 0;font-size: 14px;}
        table td, table th {padding: 5px;margin: 0;}
        table thead {background-color: #bbb;color: #fff;font-weight: 800;}
        table tbody tr td{border-bottom: 1px solid #eee;}
        table tbody tr:hover td{background-color: #eee;}
        table img {max-width: 100px;}
    </style>
</head>
<body>
<table align="center">
    <thead>
    <tr>
        <td align="left" width="55%">文件名</td>
        <td align="center"  width="10%">目录</td>
        <td align="center"  width="15%">修改时间</td>
        <td align="center"  width="10%">大小</td>
        <td align="center"  width="10%">操作</td>
    </tr>
    </thead>
    <%
        Connect_mysql connect_mysql = new Connect_mysql();
        Connection con = connect_mysql.connection();
        String username = (String) request.getAttribute("username");
        String textfield = (String) request.getAttribute("textfield");

        Statement stmt = con.createStatement();
        String sql = "select * from file_inf where user='"+username+"' and file_name like '%"+textfield+"%'";
        ResultSet rs = stmt.executeQuery(sql);
        int id = 0;
        while (rs.next()){
            String file_path="/";
            if (!rs.getString("path").equals("/"+username)){
                String[] spilt_file_path = rs.getString("path").split("/",3);
                file_path = "/"+spilt_file_path[2];
            }
            if (rs.getString("file_or_folder").equals("file")){
    %>
    <tbody>
    <tr>
        <td align="left"><%=rs.getString("file_name")%></td>
        <td align="center"><%=file_path%></td>
        <td align="center"><%=rs.getString("file_time")%></td>
        <td align="center"><%=rs.getString("file_size")%></td>
        <td align="center">
            <form action="/download" method="post">
                <input type="hidden" name="file_name" value="<%=rs.getString("file_name")%>">
                <input type="hidden" name="username" value=${requestScope.username}>
                <input type="hidden" name="file_path" value="<%=file_path%>">
                <input type="hidden" name="flag" value="下载">
                <input type="button" name="download_or_enter" value="下载" onclick="download_file(<%=id%>)">
                <input type="button" name="delete" value="删除" onclick="delete_file(<%=id%>)">
            </form>
        </td>
    </tr>
    </tbody>
    <%}
    else {%>
    <tbody>
    <tr>
        <td align="left"><%=rs.getString("file_name")%></td>
        <td align="center"><%=file_path%></td>
        <td align="center"><%=rs.getString("file_time")%></td>
        <td align="center"><%=rs.getString("file_size")%></td>
        <td align="center">
            <form action="/download" method="post">
                <input type="hidden" name="file_name" value="<%=rs.getString("file_name")%>">
                <input type="hidden" name="username" value=${requestScope.username}>
                <input type="hidden" name="file_path" value="${requestScope.file_path}">
                <input type="hidden" name="flag" value="进入">
                <input type="button" name="download_or_enter" value="进入" onclick="enter_folder(<%=id%>)">
                <input type="button" name="delete" value="删除" onclick="delete_file(<%=id%>)">
            </form>
        </td>
    </tr>
    </tbody>
    <%}
        id = id + 1;}%>
</table>
<script>
    var flag = true;
    function download_file(form_num) {
        if (flag) {
            document.forms[form_num].action = "/download1"
            document.forms[form_num].submit();
        }
    }

    function delete_file(form_num) {
        if (flag) {
            flag = false;
            document.forms[form_num].action = "/delete"
            document.forms[form_num].submit();
        }
    }

    function enter_folder(form_num) {
        if (flag) {
            flag = false;
            document.forms[form_num].action = "/enter_folder"
            document.forms[form_num].submit();
        }
    }
</script>
</body>
</html>
