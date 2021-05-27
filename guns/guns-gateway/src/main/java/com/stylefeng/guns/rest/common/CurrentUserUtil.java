package com.stylefeng.guns.rest.common;

/**
 * @author LiuKang on 2021/5/15 19:27
 * 使用ThreadLocal存储当前用户id
 */
public class CurrentUserUtil {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void setUserId(String userId) {
        threadLocal.set(userId);
    }

    public static String getUserId() {
        return threadLocal.get();
    }

}
