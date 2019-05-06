package com.example.demo.Connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect_mysql {
    public Connection connection() throws SQLException,ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://10.10.6.132:3306/test?serverTimezone=UTC&characterEncoding=utf8&useUnicode=true&useSSL=false";
        String username = "root";
        String password = "313700";
        Connection conn = DriverManager.getConnection(url,username,password);
        if (conn != null){
            System.out.println("数据库连接成功！");
        }
        else {
            System.out.println("数据库连接失败！");
        }
        return conn;
    }
}

