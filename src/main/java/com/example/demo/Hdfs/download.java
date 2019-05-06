package com.example.demo.Hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.io.IOUtils;
import java.io.IOException;
import java.io.OutputStream;

public class download {
    public static final String FS_DEFAULT_FS = "fs.defaultFS";
    public static final String HDFS_HOST = "hdfs://10.72.51.222:9000";
    public static final String CROSS_PLATFORM = "mapreduce.app-submission.cross-platform";

    public static void main(String[] args, OutputStream out) throws IOException {
        Configuration conf = new Configuration();

        conf.setBoolean(CROSS_PLATFORM, true);
        conf.set(FS_DEFAULT_FS, HDFS_HOST);

        GenericOptionsParser optionsParser = new GenericOptionsParser(conf, args);

        String[] remainingArgs = optionsParser.getRemainingArgs();
        if (remainingArgs.length < 1) {
            System.err.println("Usage: upload <source> <dest>");
            System.exit(2);
        }

        FileSystem fs = FileSystem.get(conf);

        Path source = new Path(args[0]);

        FSDataInputStream in = fs.open(source);

        IOUtils.copyBytes(in,out,4096,false);
        in.close();
        out.flush();
        out.close();
    }
}
