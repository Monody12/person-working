package com.example.netdisk.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author monody
 * @date 2022/5/10 21:36
 */
@Configuration
public class RabbitConfig {
    public static final String EXCHANGE_NAME = "netdisk_topic_exchange";
    public static final String QUEUE_NAME = "netdisk_queue";

    @Bean
    public Exchange exchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).build();
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public Binding bindQueueExchange() {
        return BindingBuilder.bind(queue()).to(exchange()).with("netdisk.#").noargs();
    }
}
