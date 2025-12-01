package com.example.smartair;

public class ControllerLogs implements SystemTimeTimestamp {

    public int dose;
    public long timestamp;

    public ControllerLogs() {}

    @Override
    public long gettimestamp() {
        return timestamp;
    }
}
