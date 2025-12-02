package com.example.smartair;

import java.util.ArrayList;
import java.util.List;

public class DailyCheckIn extends AlertItem {

    public String activityLimit;
    public String author;
    public String coughWheeze;
    public String nightWaking;
    public long timestamp;
    public List<String> triggers;

    public DailyCheckIn(){}

    @Override
    public long gettimestamp() {
        return timestamp;
    }

    @Override
    public int getType() {
        return AlertItem.TYPE_CHECKIN;
    }
}
