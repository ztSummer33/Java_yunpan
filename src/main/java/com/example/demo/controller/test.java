package com.example.demo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.Hdfs.Upload;
import com.example.demo.Hdfs.download;
import com.example.demo.Connect.Connect_mysql;
import com.example.demo.Hdfs.Hdfs_file;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller

public class test {

    @RequestMapping("/login")
    public String Login(){
        return "Login";
    }

    @RequestMapping("/enter_registered")
    public String Enter_registered(){
        return "Registered";
    }

    @PostMapping("/enter_login")
    public String Enter_login(@RequestParam("user") String user, @RequestParam("pass") String pass, ModelMap map) throws SQLException, ClassNotFoundException {
        map.addAttribute("username", user);
        map.addAttribute("file_path", "/");
        Connect_mysql connect_mysql = new Connect_mysql();
        Connection con = connect_mysql.connection();
        Statement stmt = con.createStatement();
        String sql = "select * from user where username='"+user+"' and password='"+pass+"'";
        ResultSet rs = stmt.executeQuery(sql);
        int row = 0;
        while (rs.next()){
            row = row + 1;
        }
        if (row == 1){
            rs.close();
            stmt.close();
            con.close();
            return "upload";
        }
        else {
            con.close();
            return "失败";
        }
    }

    @PostMapping("/return_upload")
    public String Return_upload( ModelMap map, @RequestParam("username") String user, @RequestParam("file_path") String file_path){
        map.addAttribute("file_path", file_path);
        map.addAttribute("username", user);
        return "upload";
    }

    @PostMapping("/return_registered")
    public String Return_registered(){
        return "Registered";
    }

    @PostMapping("/registered")
    public String Registered(@RequestParam("user1") String user1, @RequestParam("pass1") String pass1, @RequestParam("pass2") String pass2) throws SQLException, ClassNotFoundException {
        Connect_mysql connect_mysql = new Connect_mysql();
        Connection con = connect_mysql.connection();
        Hdfs_file hdfs_file = new Hdfs_file();
        Statement stmt = con.createStatement();
        String sql = "select * from user where username='"+user1+"'";
        ResultSet rs = stmt.executeQuery(sql);
        int row = 0;
        while (rs.next()){
            row = row + 1;
        }
        rs.close();
        stmt.close();
        if (pass1.equals(pass2) && row == 0){
            hdfs_file.mkdir("/"+user1);
            String sql1 = "insert into user(username,password) values(?,?)";
            PreparedStatement ps = con.prepareStatement(sql1);
            ps.setString(1,user1);
            ps.setString(2,pass1);
            int row1 = ps.executeUpdate();
            if (row1 > 0){
                System.out.println("添加成功");
            }
            ps.close();
            con.close();
            return "Login";
        }
        con.close();
        return "fail";
    }

    @PostMapping("/upload")
//    @ResponseBody
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("username") String username, @RequestParam("file_path") String file_path,ModelMap map) throws IOException, SQLException, ClassNotFoundException {

        if (!file.isEmpty()) {
            Connect_mysql connect_mysql = new Connect_mysql();
            Connection con = connect_mysql.connection();
            String directory = file_path;

            String file_size;
            double kb = file.getSize()/1024;
            double mb = kb/1024;
            double gb = mb/1024;
            if (Math.ceil(kb) > 999 && Math.ceil(mb)<1000){
                DecimalFormat df = new DecimalFormat("#.00");
                file_size = df.format(mb)+"MB";

            }
            else if (Math.ceil(mb) > 999){
                DecimalFormat df = new DecimalFormat("#.00");
                file_size = df.format(gb)+"GB";
            }
            else {
                file_size = (int)Math.ceil(kb)+"KB";
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
            String file_time = sdf.format(new Date());// new Date()为获取当前系统时间

            String originalFilename = file.getOriginalFilename();
            if (file_path.equals("/")){
                file_path = "/"+username;
            }
            else {
                file_path = "/"+username+file_path;
            }


            String[] split_originalFilename = originalFilename.split("\\.");
            Statement stmt = con.createStatement();
            String sql1 = "select * from file_inf where file_name='"+originalFilename+"' and user='"
                    +username+"' and path='"+file_path+"' and file_or_folder='file'";
            ResultSet rs = stmt.executeQuery(sql1);
            int row1 = 0;
            while (rs.next()){
                row1 = row1 + 1;
            }
            rs.close();
            stmt.close();
            if (row1 == 1){
                String split_originalFilename_name = "";
                for (int i=0;i<split_originalFilename.length-1;i++){
                    if (i != split_originalFilename.length-2){
                        split_originalFilename_name = split_originalFilename_name+split_originalFilename[i]+".";
                    }
                    else {
                        split_originalFilename_name = split_originalFilename_name+split_originalFilename[i];
                    }

                }
                Statement stmt1 = con.createStatement();
                String sql2 = "SELECT * FROM file_inf WHERE USER='"
                        +username+"' AND path='"+file_path+"' and file_or_folder='file' and file_name LIKE '"
                        +split_originalFilename_name+"(%)."+split_originalFilename[split_originalFilename.length-1]+"'";
                ResultSet rs1 = stmt1.executeQuery(sql2);
                int row2 = 1;
                while (rs1.next()){
                    row2 = row2 + 1;
                }
                rs1.close();
                stmt1.close();

                originalFilename = split_originalFilename_name+"("+row2+")."+split_originalFilename[split_originalFilename.length-1];
            }

            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(
                            new File(originalFilename)
                    )
            );

            out.write(file.getBytes());
            out.flush();
            out.close();

            String destFileName = file_path+"/" + originalFilename;

            Upload.main(new String[]{originalFilename, destFileName});

            String sql = "insert into file_inf(file_name,file_or_folder,file_size,file_time,user,path) values(?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1,originalFilename);
            ps.setString(2,"file");
            ps.setString(3,file_size);
            ps.setString(4,file_time);
            ps.setString(5,username);
            ps.setString(6,file_path);
            int row = ps.executeUpdate();
            if (row > 0){
                System.out.println("添加成功");
            }
            ps.close();
            con.close();

            map.addAttribute("username", username);
            map.addAttribute("file_path", directory);
            return "success";

        } else {
            return "fail";
        }

    }

//    @PostMapping("/download")
//    public String Download(@RequestParam("file_name") String file_name, @RequestParam("username") String username, @RequestParam("file_path") String directory,ModelMap map) throws Exception {
//        Connect_mysql connect_mysql = new Connect_mysql();
//        Connection con = connect_mysql.connection();
//        Statement stmt = con.createStatement();
//        String sql = "select * from file_inf where file_or_folder='file'and file_name='"+file_name+"' and user='"+username+"'";
//        ResultSet rs = stmt.executeQuery(sql);
//        rs.next();
//        String file_path = rs.getString("path");
//
//        String sql1 = "select * from user where username='"+username+"'";
//        ResultSet rs1 = stmt.executeQuery(sql1);
//        rs1.next();
//        String save_path = rs1.getString("save_path");
//        rs1.close();
//        rs.close();
//        stmt.close();
//        con.close();
//        Hdfs_file hdfs_file = new Hdfs_file();
//        hdfs_file.getFile(file_path+"/"+file_name,save_path+"/"+file_name);
//        map.addAttribute("username", username);
//        map.addAttribute("file_path", directory);
//        return "success";
//    }
    @ResponseBody
    @RequestMapping("/download1")
    public  void Download1(HttpServletResponse response,@RequestParam("file_name") String file_name,@RequestParam("username") String username) throws IOException, SQLException, ClassNotFoundException {
        Connect_mysql connect_mysql = new Connect_mysql();
        Connection con = connect_mysql.connection();
        Statement stmt = con.createStatement();
        String sql = "select * from file_inf where file_or_folder='file'and file_name='"+file_name+"' and user='"+username+"'";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        String file_path = rs.getString("path");
        rs.close();
        stmt.close();
        con.close();

        response.setContentType("application/octet-stream; charset=utf-8");
        response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file_name, "UTF-8"));
        OutputStream out = response.getOutputStream();
        download.main(new String[]{file_path+"/"+file_name},out);
//        map.addAttribute("username", username);
//        map.addAttribute("file_path", directory);
    }

    @PostMapping("/delete")
    public String Delete(@RequestParam("file_name") String file_name, @RequestParam("flag") String flag, @RequestParam("username") String username, @RequestParam("file_path") String file_path,ModelMap map) throws SQLException, ClassNotFoundException {
        Connect_mysql connect_mysql = new Connect_mysql();
        Connection con = connect_mysql.connection();
        Statement stmt = con.createStatement();
        String directory = file_path;
        if (file_path.equals("/")){
            file_path = "/"+username;
        }
        else {
            file_path = "/"+username+file_path;
        }

        Hdfs_file hdfs_file = new Hdfs_file();
        hdfs_file.rmdir(file_path+"/"+file_name);

        String sql1 = "";
        if (flag.equals("下载")){
            sql1 = "delete from file_inf where file_or_folder='file'and file_name=? and user=? and path=?";
        }
        else {
            sql1 = "delete from file_inf where file_or_folder='folder'and file_name=? and user=? and path=?";
        }
        PreparedStatement ps = con.prepareStatement(sql1);
        ps.setString(1,file_name);
        ps.setString(2,username);
        ps.setString(3,file_path);
        ps.executeUpdate();
        ps.close();
        con.close();
        map.addAttribute("username", username);
        map.addAttribute("file_path", directory);
        return "success";
    }

    @PostMapping("/new_folder")
    public String New_folder(@RequestParam("username") String username,@RequestParam("file_path") String file_path, @RequestParam("folder_name") String textfield,ModelMap map) throws SQLException, ClassNotFoundException {
        String directory = file_path;
        if (file_path.equals("/")){
            file_path = "/"+username;
        }
        else {
            file_path = "/"+username+file_path;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
        String file_time = sdf.format(new Date());// new Date()为获取当前系统时间

        Connect_mysql connect_mysql = new Connect_mysql();
        Connection con = connect_mysql.connection();
        Statement stmt = con.createStatement();
        String sql = "select * from file_inf where file_name='"+textfield+"' and user='"
                +username+"' and path='"+file_path+"' and file_or_folder='folder'";
        ResultSet rs = stmt.executeQuery(sql);
        int row = 0;
        while (rs.next()){
            row = row + 1;
        }
        rs.close();
        stmt.close();
        if (row==1){
            Statement stmt1 = con.createStatement();
            String sql1 = "SELECT * FROM file_inf WHERE USER='"
                    +username+"' AND path='"+file_path+"' and file_or_folder='folder' and file_name LIKE '"
                    +textfield+"(%)"+"'";
            ResultSet rs1 = stmt1.executeQuery(sql1);
            int row1 = 1;
            while (rs1.next()){
                row1 = row1 + 1;
            }
            rs1.close();
            stmt1.close();
            textfield = textfield+"("+row1+")";
        }

        String folder_name = file_path+"/"+textfield;
        Hdfs_file hdfs_file = new Hdfs_file();
        hdfs_file.mkdir(folder_name);

        String sql3 = "insert into file_inf(file_name,file_or_folder,file_size,file_time,user,path) values(?,?,?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(sql3);
        ps.setString(1,textfield);
        ps.setString(2,"folder");
        ps.setString(3,"-");
        ps.setString(4,file_time);
        ps.setString(5,username);
        ps.setString(6,file_path);
        int row3 = ps.executeUpdate();
        if (row3 > 0){
            System.out.println("添加成功");
        }
        ps.close();
        con.close();

        map.addAttribute("username", username);
        map.addAttribute("file_path", directory);
        return "success";
    }

    @PostMapping("/enter_folder")
    public String Enter_folder(@RequestParam("username") String username, @RequestParam("file_name") String file_name, @RequestParam("file_path") String file_path, ModelMap map) throws SQLException, ClassNotFoundException {
//        Connect_mysql connect_mysql = new Connect_mysql();
//        Connection con = connect_mysql.connection();
//        Statement stmt = con.createStatement();
//        String sql = "select * from file_inf where file_or_folder='folder' and file_name='"+file_name+"'and user='"+username+"'";
//        ResultSet rs = stmt.executeQuery(sql);
//        rs.next();
//        String file_path = rs.getString("path");
//        if (file_path.equals("/"+username)){
//            file_path = "/"+file_name;
//        }
//        else {
//            String[] spilt_file_path = file_path.split("/",3);
//            file_path = "/"+spilt_file_path[2]+"/"+file_name;
//        }
        if (file_path.equals("/")){
            file_path = file_path+file_name;
        }
        else {
            file_path = file_path+"/"+file_name;
        }
        map.addAttribute("file_path",file_path);
        map.addAttribute("username", username);
        return "success";
    }

    @PostMapping("/search_file")
    public String Search_file(@RequestParam("username") String username, @RequestParam("textfield") String textfield, ModelMap map){
        map.addAttribute("username", username);
        map.addAttribute("textfield",textfield);
        return "upload1";
    }

    @RequestMapping("/return_op")
    public String Return_op(@RequestParam("username") String username, @RequestParam("file_path") String file_path,ModelMap map){
        if (file_path.equals("/")){
            return "Login";
        }
        else {
            String[] spilt_file_path = file_path.split("/");
            if (spilt_file_path.length == 2){
                map.addAttribute("file_path","/");
                map.addAttribute("username", username);
            }
            else {
                String directory = "/";
                for (int i = 1; i< (spilt_file_path.length - 1);i++){
                    directory = directory + spilt_file_path[i];
                }
                map.addAttribute("file_path",directory);
                map.addAttribute("username", username);
            }
            return "upload";
        }
    }
}
