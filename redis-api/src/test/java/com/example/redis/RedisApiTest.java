package com.example.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author monody
 * @date 2022/4/23 9:20 下午
 */
@SpringBootTest
public class RedisApiTest {

//    @Autowired
    JedisPoolConfig jedisPoolConfig;

    @Test
    public void test(){
        System.out.println(jedisPoolConfig);
    }
}
