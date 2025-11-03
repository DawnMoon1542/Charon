package com.dawnmoon.charon.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

public interface MQService {

    void sendMsg(String queueName, String msg);

    @RabbitListener(queues = "java_test")
    void receiveMsg(String msg);
}
