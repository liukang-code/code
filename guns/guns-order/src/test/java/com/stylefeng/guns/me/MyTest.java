package com.stylefeng.guns.me;

import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.Buffer;

/**
 * @author LiuKang on 2021/5/16 16:04
 */
public class MyTest {

    @Test
    public void test1(){
        try {
            File file = ResourceUtils.getFile("classpath:seats.json");
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
            System.out.println(builder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
