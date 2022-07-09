package com.example.redis.template;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Redis 操作模板
 * @author monody
 * @date 2022/4/13 8:01 下午
 */
public interface RedisTemplateUtil {

    /**
     * 将Java对象转为json对象存入Redis
     * @param key Redis键
     * @param o Java对象
     * @param timeout 超时时间
     * @param unit 时间单元
     */
    public abstract void insertObject(String key,Object o,long timeout, TimeUnit unit) throws JsonProcessingException;

    /**
     * 以JSON形式存储一个对象
     * @param key
     * @param o
     * @throws JsonProcessingException
     */
    public abstract void insertObject(String key,Object o) throws JsonProcessingException;

    /**
     * 将String字符串存入Redis
     * @param key Redis键
     * @param value 字符串
     * @param timeout 超时时间
     * @param unit 时间单元
     */
    public abstract void insertString(String key,String value,long timeout, TimeUnit unit);

    /**
     * 存储一个String字符串
     * @param key
     * @param value
     */
    public abstract void insertString(String key,String value);

    /**
     * 为一个Hash类型的对象添加一个String键值对
     * @param key
     * @param hashKey
     * @param value
     */
    public abstract void insertHashString(String key,String hashKey,String value);

    /**
     * 为一个set类型的对象添加一个String
     * @param key
     * @param value
     */
    public abstract void insetSetString(String key,String value);

    /**
     * 查看一个set中是否包含一个字符串
     * @param key
     * @param value
     * @return
     */
    public abstract boolean containSetString(String key,String value);

    /**
     * 删除一个set中的String
     * @param key
     * @param value
     * @return
     */
    public abstract long deleteSetValue(String key,String value);

    /**
     * 删除整个set
     * @param key
     * @return
     */
    public abstract boolean deleteSet(String key);

    /**
     * 查询一个Hash中key的值
     * @param key
     * @param hashKey
     * @return
     */
    public abstract String getHashString(String key,String hashKey);

    /**
     * 删除一个Hash表中的若干个key
     * @param key
     * @param hashKey
     * @return
     */
    public abstract long deleteHashString(String key,String[] hashKey);

    /**
     * 删除任意类型的key
     * @param key
     * @return
     */
    public abstract boolean deleteKey(String key);

    /**
     * 批量删除任意类型的key
     * @param keys
     * @return
     */
    public abstract boolean deleteKeys(Collections keys);

    /**
     * 查询一个String类型的值
     * @param key
     * @return
     */
    public abstract String getString(String key);

    /**
     * 从redis中获取一个指定类型以JSON格式存储的Java对象
     * @param key
     * @param type
     * @param <T>
     * @return
     * @throws IOException
     */
    public abstract <T> T getObject(String key,Class<T> type) throws IOException;

    /**
     * 为一个key设置过期时间
     * @param key
     * @param timeout
     * @param timeUnit
     * @return
     */
    public abstract boolean expire(String key,int timeout,TimeUnit timeUnit);
}
