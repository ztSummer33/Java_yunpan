package com.example.demo.Hdfs;

import java.io.*;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class Hdfs_file {

    private static String HDFSUri = "hdfs://10.72.51.222:9000";
    private FileSystem fs;
    private String localpath;
    private String hdfspath;

//    public Hdfs_file(String localpath, String hdfspath){
//        fs = getFileSystem();
//        this.localpath = localpath;
//        this.hdfspath = hdfspath;
//    }

    public FileSystem getFileSystem(){
        Configuration conf = new Configuration();
        FileSystem fs = null;
        String hdfsUri = HDFSUri;

        if (StringUtils.isBlank(hdfsUri)){
            try {
                fs = FileSystem.get(conf);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        else {
            try {
                URI uri = new URI(hdfsUri.trim());
                fs = FileSystem.get(uri,conf);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return fs;
    }

    public void mkdir(String username) {
        try {
            FileSystem fs = getFileSystem();
            String user_path = username;
            System.out.println("FilePath="+user_path);
            fs.mkdirs(new Path(user_path));
            fs.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean existDir(boolean create){
        boolean flag = false;

        if (StringUtils.isEmpty(hdfspath)){
            return flag;
        }

        try {
            Path path = new Path(hdfspath);
            // FileSystem对象
            FileSystem fs = getFileSystem();

            if (create){
                if (!fs.exists(path)){
                    fs.mkdirs(path);
                }
            }

            if (fs.isDirectory(path)){
                flag = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return flag;
    }

    public void copyFileToHDFS()throws Exception{

        String[] file_name = localpath.split("\\\\");
        String destPath = hdfspath + "/" +file_name[file_name.length - 1];
        FileInputStream fis = new FileInputStream(new File(localpath));//读取本地文件
        Configuration config = new Configuration();
        FileSystem fs=FileSystem.get(URI.create(HDFSUri+destPath), config);
        OutputStream os=fs.create(new Path(destPath));
        //copy
        IOUtils.copyBytes(fis, os, 4096, true);
        System.out.println("拷贝完成...");
        fs.close();
    }

    public void getFile(String srcFile,String destPath)throws Exception {
        //hdfs文件 地址
        String file=HDFSUri+srcFile;
        Configuration config=new Configuration();
        //构建FileSystem
        FileSystem fs = FileSystem.get(URI.create(file),config);
        //读取文件
        InputStream is=fs.open(new Path(file));
        IOUtils.copyBytes(is, new FileOutputStream(new File(destPath)),2048, true);//保存到本地  最后 关闭输入输出流
        System.out.println("下载完成...");
        fs.close();
    }

    public void rmdir(String path) {
        try {
            // 返回FileSystem对象
            FileSystem fs = getFileSystem();

            String hdfsUri = HDFSUri;
            if(StringUtils.isNotBlank(hdfsUri)){
                path = hdfsUri + path;
            }
            System.out.println("path:"+path);
            // 删除文件或者文件目录  delete(Path f) 此方法已经弃用
            System.out.println( fs.delete(new Path(path),true));

            // 释放资源
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
