package com.example.redis.template.impl;


import com.example.redis.template.RedisTemplateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author monody
 * @date 2022/4/13 8:37 下午
 */
@Component
public class RedisTemplateUtilImpl implements RedisTemplateUtil {


    @Autowired
    @Qualifier(value = "myStringRedisTemplate")
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    @Qualifier(value = "myRedisTemplate")
    private RedisTemplate template;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public void insertObject(String key, Object o, long timeout, TimeUnit unit) throws JsonProcessingException {
        ValueOperations<String,String> ops = redisTemplate.opsForValue();
        String json = objectMapper.writeValueAsString(o);
        ops.set(key,json,timeout,unit);
    }

    @Override
    public void insertObject(String key, Object o) throws JsonProcessingException {
        ValueOperations<String,String> ops = redisTemplate.opsForValue();
        String json = objectMapper.writeValueAsString(o);
        ops.set(key,json);
    }

    @Override
    public void insertString(String key, String value, long timeout, TimeUnit unit) {
        ValueOperations<String,String> ops = redisTemplate.opsForValue();
        ops.set(key,value,timeout,unit);
    }

    @Override
    public void insertString(String key, String value) {
        ValueOperations<String,String> ops = redisTemplate.opsForValue();
        ops.set(key,value);
    }

    @Override
    public void insertHashString(String key, String hashKey, String value) {
        HashOperations<String,String,String> ops = redisTemplate.opsForHash();
        ops.put(key, hashKey, value);
    }

    @Override
    public void insetSetString(String key, String value) {
        SetOperations<String,String> ops = redisTemplate.opsForSet();
        ops.add(key,value);
    }

    @Override
    public boolean containSetString(String key, String value) {
        BoundSetOperations<String,String> ops = redisTemplate.boundSetOps(key);
        return ops.isMember(value);
    }

    @Override
    public long deleteSetValue(String key, String value) {
        BoundSetOperations<String,String> ops = redisTemplate.boundSetOps(key);
        return ops.remove(value);
    }

    @Override
    public boolean deleteSet(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public String getHashString(String key, String hashKey) {
        HashOperations<String,String,String> ops = redisTemplate.opsForHash();
        return ops.get(key,hashKey);
    }

    @Override
    public long deleteHashString(String key, String[] hashKey) {
        HashOperations<String,String,String> ops = redisTemplate.opsForHash();
        return ops.delete(key,hashKey);
    }

    @Override
    public boolean deleteKey(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public boolean deleteKeys(Collections keys) {
        return template.delete(keys);
    }

    @Override
    public String getString(String key) {
        ValueOperations<String,String> ops = redisTemplate.opsForValue();
        return ops.get(key);
    }

    @Override
    public <T> T getObject(String key, Class<T> type) throws IOException {
        ValueOperations<String,String> ops = redisTemplate.opsForValue();
        String json = ops.get(key);
        return objectMapper.readValue(json, type);
    }

    @Override
    public boolean expire(String key, int timeout, TimeUnit timeUnit) {
        return redisTemplate.expire(key,timeout,timeUnit);
    }


}
