package com.example.netdisk.service.impl;

import com.example.netdisk.service.SendMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author monody
 * @date 2022/5/10 21:53
 */
@Service
public class SendMessageServiceImpl implements SendMessageService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public  void sendCommonMessage(String exchangeName, String routingKey, Object obj) throws JsonProcessingException {
        String s = objectMapper.writeValueAsString(obj);
        rabbitTemplate.convertAndSend(exchangeName,routingKey,s);
    }
}
