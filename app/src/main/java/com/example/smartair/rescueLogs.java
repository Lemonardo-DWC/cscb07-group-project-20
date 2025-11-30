package com.example.smartair;

public class rescueLogs implements SystemTimeTimestamp {

    public int dose;
    public int preBreathRating;
    public int postBreathRating;
    public int postStatus;
    public long timestamp;

    public rescueLogs() {}

    @Override
    public long gettimestamp() {
        return timestamp;
    }
}
