package com.andreiiorga.iot.garden.gpio;

import com.andreiiorga.iot.garden.jms.ActiveMQProducer;
import com.andreiiorga.iot.garden.messages.IncomingCommandMessage;
import com.andreiiorga.iot.garden.messages.OutgoingMessage;
import com.google.gson.Gson;
import com.pi4j.io.gpio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@EnableScheduling
public class GPIOController  implements CommandLineRunner {

    private List<Relay> relays = new ArrayList<>();

    @Value("${device.model}")
    private String deviceModel;

    @Value("${device.id}")
    private String deviceId;

    @Autowired
    ActiveMQProducer activeMQProducer;

    @Autowired
    JmsTemplate jmsTemplate;


    @Override
    public void run(String...args) throws Exception {
        Relay relay1 = new Relay(RaspiPin.GPIO_15, "Relay 1, Pin 15", 1);
        Relay relay2 = new Relay(RaspiPin.GPIO_16, "Relay 2, Pin 16", 2);
        Relay relay3 = new Relay(RaspiPin.GPIO_01, "Relay 3, Pin 01", 3);
        Relay relay4 = new Relay(RaspiPin.GPIO_07, "Relay 4, Pin 07", 4);

        relays.add(relay1);
        relays.add(relay2);
        relays.add(relay3);
        relays.add(relay4);
    }

    public void turnRelayOn(int index, int minutes){
        Relay relay = this.relays.get(index);
        relay.turnOn(minutes);
        OutgoingMessage outgoingMessage = new OutgoingMessage(relay.getNumberOnDevice(), relay.isOn(), relay.getRunForMinutes(), deviceId);
        Gson gson = new Gson();
        activeMQProducer.sendMessage(gson.toJson(outgoingMessage));
        logRelaysStates();
    }

    public void turnRelayOn(int index){
        Relay relay = this.relays.get(index);
        relay.turnOn();
        OutgoingMessage outgoingMessage = new OutgoingMessage(relay.getNumberOnDevice(), relay.isOn(), relay.getRunForMinutes(), deviceId);
        Gson gson = new Gson();
        activeMQProducer.sendMessage(gson.toJson(outgoingMessage));
        logRelaysStates();
    }

    public void turnRelayOff(int index){
        Relay relay = this.relays.get(index);
        OutgoingMessage outgoingMessage = new OutgoingMessage(relay.getNumberOnDevice(), false, relay.getRunForMinutes(), deviceId);
        Gson gson = new Gson();
        activeMQProducer.sendMessage(gson.toJson(outgoingMessage));
        logRelaysStates();
        jmsTemplate.convertAndSend("smart-outlet/server-topic", gson.toJson(outgoingMessage));
        relay.turnOff();
    }

    public boolean relayIsOn(int index){
        Relay relay = this.relays.get(index);
        return relay.isOn();
    }

    public void logRelaysStates(){
        for(Relay relay: relays){
            if(relay.isOn()){
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                String strDate = dateFormat.format(relay.getStartDate());
                System.out.println(relay.getDescription() + " is ON for " + relay.getRunForMinutes() + " minutes. Started at: " + strDate);
            }else{
                System.out.println(relay.getDescription() + " is OFF");
            }
        }
        System.out.println("========================================");
    }

    public void executeCommand(IncomingCommandMessage commandMessage) {
        if(commandMessage.getRelayIndex() <= relays.size()){
            if(commandMessage.isState() && commandMessage.getNoOfMinutes() == 0){
                turnRelayOn(commandMessage.getRelayIndex() - 1);
            }else if(commandMessage.isState() && commandMessage.getNoOfMinutes() != 0){
                turnRelayOn(commandMessage.getRelayIndex() - 1, commandMessage.getNoOfMinutes());
            }else if(!commandMessage.isState()){
                turnRelayOff(commandMessage.getRelayIndex() - 1);
            }

        }else{
            System.out.println("ERROR: Index #" + commandMessage.getRelayIndex() + " too big!");
        }
    }

    @Scheduled(cron = "0 * * * * ?")
    public void turnRelaysOff() {
        System.out.println("Cron check started...");
        for(Relay relay: relays){
            if(relay.getStartDate() != null && relay.isOn()){
                System.out.println("Relay " + relay.getNumberOnDevice() + " is on and has start date");
                if(getDateDiff(relay.getStartDate(), new Date(), TimeUnit.MINUTES) >= relay.getRunForMinutes() && relay.isOn()){
                    System.out.println("Relay " + relay.getNumberOnDevice() + " must be stopped!");
                    System.out.println(getDateDiff(relay.getStartDate(), new Date(), TimeUnit.MINUTES) + " => " + relay.getRunForMinutes());
                    turnRelayOff(relay.getNumberOnDevice() - 1);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                    String now = dateFormat.format(new Date());
                    System.out.println("The Cron stopped " + relay.getDescription() + ". Current Time: " + now);
                    System.out.println();
                }else{
                    System.out.println("Relay " + relay.getNumberOnDevice() + " continues to run...");
                }
            }else{
                System.out.println("Relay " + relay.getNumberOnDevice() + " is off or has no start date");
            }
        }

    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


}
