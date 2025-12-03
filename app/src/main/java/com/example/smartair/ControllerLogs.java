package com.example.smartair;

public class ControllerLogs extends AlertItem {

    public int dose;
    public long timestamp;

    public ControllerLogs() {}

    @Override
    public long gettimestamp() {
        return timestamp;
    }

    @Override
    public int getType() {
        return AlertItem.TYPE_CONTROLLER;
    }
}
