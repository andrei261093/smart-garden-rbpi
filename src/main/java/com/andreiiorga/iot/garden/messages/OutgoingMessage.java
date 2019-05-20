package com.andreiiorga.iot.garden.messages;

import java.util.Date;

public class OutgoingMessage {
    private int relayIndex;
    private boolean state;
    private Date now;
    private int runForMinutes;
    private String deviceModel;
    private String deviceId;

    public OutgoingMessage(int relayIndex, boolean state, int runForMinutes, String deviceId) {
        this.relayIndex = relayIndex;
        this.state = state;
        this.now = new Date();
        this.runForMinutes = runForMinutes;
        this.deviceId = deviceId;
    }

    public int getRelayIndex() {
        return relayIndex;
    }

    public boolean isState() {
        return state;
    }

    public int getRunForMinutes() {
        return runForMinutes;
    }

    public void setRunForMinutes(int runForMinutes) {
        this.runForMinutes = runForMinutes;
    }

    public void setRelayIndex(int relayIndex) {
        this.relayIndex = relayIndex;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public Date getNow() {
        return now;
    }

    public void setNow(Date now) {
        this.now = now;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
