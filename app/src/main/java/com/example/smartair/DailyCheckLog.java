package com.example.smartair;

import java.util.List;

public class DailyCheckLog {
    public String nightWaking;
    public String activityLimit;
    public String coughWheeze;
    public List<String> triggers;
    public long timestamp;


    public DailyCheckLog() {
    // Required default constructor
    }


    public String getNightWaking() { return nightWaking; }
    public String getActivityLimit() { return activityLimit; }
    public String getCoughWheeze() { return coughWheeze; }
    public List<String> getTriggers() { return triggers; }
    public long getTimestamp() { return timestamp; }
}

