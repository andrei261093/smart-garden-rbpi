package com.andreiiorga.iot.garden.messages;

public class IncomingCommandMessage {
    private int relayIndex;
    private boolean state;
    private int noOfMinutes;

    public int getRelayIndex() {
        return relayIndex;
    }

    public void setRelayIndex(int relayIndex) {
        this.relayIndex = relayIndex;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getNoOfMinutes() {
        return noOfMinutes;
    }

    public void setNoOfMinutes(int noOfMinutes) {
        this.noOfMinutes = noOfMinutes;
    }
}
