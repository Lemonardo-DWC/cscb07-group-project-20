package com.example.smartair;

import java.util.List;
import java.util.Map;

public class ChildItem {

    public String accountType;
    public String email;
    public BasicInformation basicInformation;

    public Map<String, String> parentList;
    public Map<String, DailyCheckIn> DailyCheckIn;
    public Map<String, controllerLogs> controllerLogs;
    public Map<String, rescueLogs> rescueLogs;
    public ChildItem() {}
}
