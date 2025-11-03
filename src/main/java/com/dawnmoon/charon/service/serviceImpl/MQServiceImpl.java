package com.dawnmoon.charon.service.serviceImpl;

import com.dawnmoon.charon.service.MQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MQServiceImpl implements MQService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendMsg(String queueName, String msg) {
        rabbitTemplate.convertAndSend(queueName, msg);
    }

    @RabbitListener(queues = "java_test")
    @Override
    public void receiveMsg(String msg) {
        log.info("java_test 消息队列收到信息： {}", msg);
    }
}
