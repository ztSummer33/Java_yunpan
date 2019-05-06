package com.example.demo.Mysql_data;

public class User {
    private String user;
    private String pass1;
    private String pass2;

    public String getUser(){
        return user;
    }

    public String getPass1() {
        return pass1;
    }

    public String getPass2() {
        return pass2;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPass1(String pass1) {
        this.pass1 = pass1;
    }

    public void setPass2(String pass2) {
        this.pass2 = pass2;
    }
}
