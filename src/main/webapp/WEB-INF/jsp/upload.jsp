<%@ page import="com.example.demo.Connect.Connect_mysql" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.ResultSet" %><%--
  Created by IntelliJ IDEA.
  User: wangj
  Date: 19.3.11
  Time: 19:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <meta http-equiv="Content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <link rel="stylesheet"  type="text/css"  href="filebox1.css"/>
    <title>单文件上传</title>
    <style>
        * {
            box-sizing:border-box;
        }
        body{
            margin: 0;
            padding: 0;
        }
        div.search {padding: 30px 0;}
        .bar6 input {
            outline: none;
            border: 2px solid #c5464a;
            border-radius: 5px 0 0 5px;
            background: transparent;
            width: 50%;
            height: 42px;
            padding-left: 13px;
        }
        .bar6 button {
            border: none;
            outline: none;
            background: #c5464a;
            border-radius: 0 5px 5px 0;
            width: 60px;
            height: 42px;
            cursor: pointer;
            position: absolute;
        }
        .bar6 button:before {
            content: "搜索";
            font-size: 13px;
            color: #F9F0DA;
        }

        table {width: 95%;text-align: center;border: 0;border-collapse: collapse;border-spacing: 0;font-size: 14px;}
        table td, table th {padding: 5px;margin: 0;}
        table thead {background-color: #bbb;color: #fff;font-weight: 800;}
        table tbody tr td{border-bottom: 1px solid #eee;}
        table tbody tr:hover td{background-color: #eee;}
        table img {max-width: 100px;}

        .div-inline{
            display:inline;
            float: left;
            width: 50%;
            height: 62px;
        }
    </style>

</head>
<body>
<form action="" method="post" enctype="multipart/form-data" name="form1">
    <input type="hidden" name="file_path" value="${requestScope.file_path}">
    <input type="hidden" name="folder_name" id="folder_name" value="">
    <input type="hidden"  name="username" value=${requestScope.username}>
    <%
        String button_value = "注销";
        String file_path = (String) request.getAttribute("file_path");
        if (!file_path.equals("/")){
            button_value = "返回";
        }
    %>
    <div class="top_div">
        <div align="left" class="div-inline">
            <input type="button" name="op" class="name_text" value="<%=button_value%>" onclick="return_op()">
        </div>
        <div align="right" class="div-inline">
            <input type="button" name="username" class="name_text" value=${requestScope.username}>
        </div>
    </div>
    <br>
    <div align="center">
        <input type="file" name="file"  id="file" onchange="upload()" class="fileInput"/>
        <label for="file" class="fileLabel">上传</label>

        <label class="fileLabel" onclick="new_folder()">新建</label>
    </div>
    <div align="center" class="search bar6">
        <input type="text" id="textfield" name="textfield" placeholder="请输入您要搜索的内容..." align="center">
        <button onclick="search_file()"></button>
    </div>
</form>
    <table align="center">
        <thead>
        <%--<tr><td width="100%" colspan="4" align="left">目录：${requestScope.file_path}</td></tr>--%>
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

            if (file_path.equals("/")){
                file_path = file_path+username;
            }
            else {
                file_path = "/"+username+file_path;
            }
            System.out.println(file_path);
            Statement stmt = con.createStatement();
            String sql = "select * from file_inf where user='"+username+"' and path='"+file_path+"'";
            ResultSet rs = stmt.executeQuery(sql);
            int id = 1;
            while (rs.next()){
                if (rs.getString("file_or_folder").equals("file")){
        %>
        <tbody>
                <tr>
                        <td align="left"><%=rs.getString("file_name")%></td>
                        <td align="center">${requestScope.file_path}</td>
                        <td align="center"><%=rs.getString("file_time")%></td>
                        <td align="center"><%=rs.getString("file_size")%></td>
                        <td align="center">
                            <form action="/download" method="post">
                                <input type="hidden" name="file_name" value="<%=rs.getString("file_name")%>">
                                <input type="hidden" name="username" value=${requestScope.username}>
                                <input type="hidden" name="file_path" value="${requestScope.file_path}">
                                <input type="hidden" name="flag" value="下载">
                                <input type="button" name="download_or_enter" value="下载"  onclick="download_file(<%=id%>)">
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
            <td align="center">${requestScope.file_path}</td>
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
    function upload() {
        if (flag) {
            flag = false;
            document.form1.action="/upload";
            document.form1.submit();
        }

    }

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

    function new_folder() {
        if (flag) {
            var x = prompt ("输入文件夹名: ", "新建文件夹");/*第一个变量为提示语，第二个变量为默认初始值*/
            if (x != null) {
                flag = false;
                document.getElementById("folder_name").value=x;
                document.form1.action="/new_folder";
                document.form1.submit();
            }
        }
    }

    function enter_folder(form_num) {
        if (flag) {
            flag = false;
            document.forms[form_num].action = "/enter_folder"
            document.forms[form_num].submit();
        }
    }

    function search_file() {
        if (flag) {
            flag = false;
            document.form1.action="/search_file";
            document.form1.submit();
        }
    }

    function return_op() {
        if (flag) {
            flag = false;
            document.form1.action="/return_op";
            document.form1.submit();
        }
    }
</script>
</body>
</html>
