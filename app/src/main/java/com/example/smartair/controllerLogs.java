package com.example.smartair;

public class controllerLogs implements SystemTimeTimestamp {

    public int dose;
    public long timestamp;

    public controllerLogs() {}

    @Override
    public long gettimestamp() {
        return timestamp;
    }
}
