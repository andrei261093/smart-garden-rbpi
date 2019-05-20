package com.andreiiorga.iot.garden.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class ActiveMQProducer {

    @Autowired
    JmsTemplate jmsTemplate;

    @Value("${activemq.topic.send-to-server}")
    private String topic;

    public void sendMessage(String message) {
        jmsTemplate.convertAndSend(topic, message);
        System.out.println("Message sent to server: ");
        System.out.println(message);
    }
}

