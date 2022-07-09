package com.example.netdisk.utils;

import java.util.UUID;

/**
 * 简化UUID
 * @author monody
 * @date 2022/4/24 7:28 下午
 */
public class UUIDUtil {
    public static String get() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getFour(){
        return UUID.randomUUID().toString().substring(0,4);
    }
}
