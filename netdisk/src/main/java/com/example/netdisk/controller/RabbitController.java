package com.example.netdisk.controller;

import com.example.netdisk.entity.po.SharedFile;
import com.example.netdisk.service.SendMessageService;
import com.example.netdisk.service.ShareFileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * @author monody
 * @date 2022/5/12 22:10
 */
@RestController
@RequestMapping("/rabbit")
@Slf4j
public class RabbitController {
    @Autowired
    SendMessageService sendMessageService;
    @Autowired
    ShareFileService shareFileService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @Value("${netdisk.message.share-file-exchange}")
    String commonExchangeName;
    @Value("${netdisk.message.share-file-exchange}")
    String delayQueue;
    @Value("${netdisk.message.share-file-exchange-dlx}")
    String deadExchange;
    @Value("${netdisk.message.share-file-routing-key}")
    String routingKey;

    @GetMapping("/send")
    public void sendMessage(String msg) {
        String zeroDayRoutingKey = routingKey + 0;
        log.info("commonExchangeName {}", commonExchangeName);
        log.info("oneDayRoutingKey {}", zeroDayRoutingKey);
        for (int i = 0; i < 3; ++i) {
            rabbitTemplate.convertAndSend(commonExchangeName, zeroDayRoutingKey, i + "  " + msg, message -> {
                // 设置消息持续时间
//                message.getMessageProperties().setExpiration(String.valueOf(60 * 1000));
                // 设置消息持久化
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            });
        }
    }

    @GetMapping("/send2")
    public void sendMessage2(String msg) {
        String zeroDayRoutingKey = routingKey + 0;
        log.info("commonExchangeName {}", commonExchangeName);
        log.info("oneDayRoutingKey {}", zeroDayRoutingKey);
        for (int i = 0; i < 3; ++i) {
            rabbitTemplate.convertAndSend(commonExchangeName, zeroDayRoutingKey, i + "  " + msg);
        }
    }



}
