package com.andreiiorga.iot.garden.jms;

import com.andreiiorga.iot.garden.gpio.GPIOController;
import com.andreiiorga.iot.garden.messages.IncomingCommandMessage;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class ActiveMQListener {

    @Autowired
    GPIOController gpioController;

    @Autowired
    ActiveMQProducer activeMQProducer;

    @Value("${device.model}")
    private String deviceModel;

    @Value("${device.id}")
    private String deviceId;


    @JmsListener(destination = "${activemq.topic.recive-commands}")
    public void onNewCommand(final Message jsonMessage) {
        Gson gson = new Gson();
        IncomingCommandMessage commandMessage = gson.fromJson(jsonMessage.getPayload().toString(), IncomingCommandMessage.class);
        gpioController.executeCommand(commandMessage);
    }

    @JmsListener(destination = "${activemq.topic.alive}")
    public void alive(final Message jsonMessage) {
        activeMQProducer.sendMessage(deviceModel + " with id " + deviceId + " is ALIVE!");
    }
}
