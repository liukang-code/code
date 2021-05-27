package com.stylefeng.guns.rest.common.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author LiuKang on 2021/5/19 14:20
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FTPUtil {

    private String hostName = "";
    private Integer port = 2100;
    private String username = "ftp";
    private String password = "ftp";

    private FTPClient ftpClient = null;

    private void initFtpClient() {
        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            ftpClient.connect(hostName,port);
            ftpClient.login(username, password);
        } catch (Exception e) {
            log.error("FTPClient初始化失败",e);
        }
    }

//    输入一个ftp路径，返回文件里的内容
    public String getFileStringByAddress(String ftpAddress){

        BufferedReader bufferedReader = null;


        try {
            bufferedReader = new BufferedReader(new InputStreamReader(ftpClient.retrieveFileStream(ftpAddress)));

            StringBuilder stringBuilder = new StringBuilder();
            while (true){
                String line = bufferedReader.readLine();
                if (line == null){
                    break;
                }
                stringBuilder.append(line);
            }

            ftpClient.logout();
            return stringBuilder.toString();

        } catch (Exception e) {
            log.error("读取文件信息失败",e);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
