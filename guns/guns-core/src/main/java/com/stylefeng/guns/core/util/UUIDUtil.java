package com.stylefeng.guns.core.util;

import java.util.UUID;

/**
 * @author LiuKang on 2021/5/19 17:29
 */
public class UUIDUtil {

    public static String genUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
