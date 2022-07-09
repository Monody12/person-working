package com.example.redis.bean;


import com.example.redis.template.RedisTemplateUtil;
import com.example.redis.template.impl.RedisTemplateUtilImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;

/**
 * @author monody
 * @date 2022/4/23 8:33 下午
 */
@Component
@RefreshScope
@PropertySource(value = {"classpath:redis.properties"})
public class RedisConfigBean {
    @Value("${redis.server}")
    private String server;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.auth}")
    private String auth;

    @Value("${redis.maxTotal}")
    private int maxTotal;
    @Value("${redis.maxWaitMillis}")
    private int maxWaitMillis;
    @Value("${redis.maxIdle}")
    private int maxIdle;
    @Value("${redis.minIdle}")
    private int minIdle;
    @Value("${redis.testOnBorrow}")
    private boolean testOnBorrow;


    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        return config;
    }

    @Bean(name = "myFactory")
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(server);
        factory.setPort(port);
        factory.setPassword(auth);
        factory.setPoolConfig(jedisPoolConfig);
        return factory;
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }


    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean(name = "myStringRedisTemplate")
    public RedisTemplate<String,String> redisTemplate(@Autowired @Qualifier("myFactory") JedisConnectionFactory connectionFactory, StringRedisSerializer stringRedisSerializer){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);
        return redisTemplate;
    }

    @Bean(name = "myRedisTemplate")
    public RedisTemplate template(@Autowired @Qualifier("myFactory") JedisConnectionFactory connectionFactory,StringRedisSerializer stringRedisSerializer){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);
        return redisTemplate;
    }

    @Bean
    public RedisTemplateUtil redisTemplateUtil(){
        return new RedisTemplateUtilImpl();
    }
}
