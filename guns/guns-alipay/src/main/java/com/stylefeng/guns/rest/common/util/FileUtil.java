package com.stylefeng.guns.rest.common.util;

import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author LiuKang on 2021/5/19 21:19
 */
public class FileUtil {

    public static String getJson(String address){
        try {
            File file = ResourceUtils.getFile("classpath:"+address);
            boolean exists = file.exists();
            System.out.println(exists);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            while(true){
                String line = bufferedReader.readLine();
                if (line == null){
                    break;
                }
                builder.append(line);
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
