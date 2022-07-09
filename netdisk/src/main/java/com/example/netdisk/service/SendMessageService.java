package com.example.netdisk.service;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 消息队列发送消息
 * @author monody
 * @date 2022/5/10 21:46
 */
public interface SendMessageService {

    public void sendCommonMessage(String exchangeName,String routingKey,Object obj) throws JsonProcessingException;

}
