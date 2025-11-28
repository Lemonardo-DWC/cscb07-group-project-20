package com.example.smartair;

import java.util.Map;

public class ChildItem {

    public String uid;
    public String accountType;
    public String email;
    public BasicInformation basicInformation;

    public Map<String, String> parentList;
    public Map<String, DailyCheckIn> DailyCheckIn;
    public Map<String, ControllerLogs> controllerLogs;
    public Map<String, RescueLogs> rescueLogs;
    public ChildItem() {}
}
