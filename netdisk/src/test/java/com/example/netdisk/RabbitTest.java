package com.example.netdisk;

import com.example.netdisk.service.SendMessageService;
import com.example.netdisk.websocket.WebSocketConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author monody
 * @date 2022/5/10 21:56
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RabbitTest {

    @Autowired
    SendMessageService sendMessageService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Value("${netdisk.message.share-file-exchange}")
    String commonExchangeName;
    @Value("${netdisk.message.share-file-exchange}")
    String delayQueue;
    @Value("${netdisk.message.share-file-exchange-dlx}")
    String deadExchange;
    @Value("${netdisk.message.share-file-routing-key}")
    String routingKey;

    @Test
    public void test1(){
        String oneDayRoutingKey = routingKey + 1;
        log.info("commonExchangeName {}",commonExchangeName);
        log.info("oneDayRoutingKey {}",oneDayRoutingKey);
        for(int i = 0;i<3;++i)
        rabbitTemplate.convertAndSend(commonExchangeName,oneDayRoutingKey,"test");
    }

    @Test
    public void test2(){
        String oneDayRoutingKey = routingKey + 1;
        log.info("deadExchange {}",deadExchange);
        log.info("oneDayRoutingKey {}",oneDayRoutingKey);
        for(int i = 0;i<3;++i)
            rabbitTemplate.convertAndSend(deadExchange,oneDayRoutingKey,"test"+i);
    }

    @Test
    public void test3(){

    }

}
