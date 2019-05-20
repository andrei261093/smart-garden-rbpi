package com.andreiiorga.iot.garden.gpio;

import com.pi4j.io.gpio.*;

import java.util.Date;

public class Relay {
    private GpioPinDigitalOutput gpioPinDigitalOutput;
    private String description;
    private Date startDate;
    private int runForMinutes = 120;
    private int numberOnDevice;

    public Relay (Pin pin, String description, int numberOnDevice){
        GpioController gpio = GpioFactory.getInstance();
        gpioPinDigitalOutput = gpio.provisionDigitalOutputPin(pin, description,  PinState.HIGH);
        this.description = description;
        this.numberOnDevice = numberOnDevice;
    }

    public void turnOn(){
        this.gpioPinDigitalOutput.low();
        this.startDate = new Date();
    }

    public void turnOn(int minutes){
        this.gpioPinDigitalOutput.low();
        this.startDate = new Date();
        this.runForMinutes = minutes;
    }


    public void turnOff(){
        this.gpioPinDigitalOutput.high();
        this.runForMinutes = 120;
    }

    public boolean isOn(){
        return !this.gpioPinDigitalOutput.isHigh();
    }

    public String getDescription(){
        return this.description;
    }

    public int getRunForMinutes() {
        return runForMinutes;
    }

    public void setRunForMinutes(int runForMinutes) {
        this.runForMinutes = runForMinutes;
    }

    public Date getStartDate() {
        return startDate;
    }

    public int getNumberOnDevice() {
        return numberOnDevice;
    }

    public void setNumberOnDevice(int numberOnDevice) {
        this.numberOnDevice = numberOnDevice;
    }
}
