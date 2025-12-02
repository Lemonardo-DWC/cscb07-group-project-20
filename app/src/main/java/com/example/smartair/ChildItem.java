package com.example.smartair;

import java.util.Map;

public class ChildItem {

    public String uid;
    public String accountType;
    public String email;
    public String notes = "Optional notes...";
    public int pb;

    public BasicInformation basicInformation;

    public ControllerItem controller;
    public RescueItem rescue;

    public Map<String, String> parentList;
    public Map<String, DailyCheckIn> DailyCheckIn;
    public Map<String, ControllerLogs> controllerLogs;
    public Map<String, RescueLogs> rescueLogs;

    public Map<String, PefLogs> pefLogs;
    public Map<String, TriageSessions> triageSessions;

    public ChildItem() {}

    public String getUid() {
        return uid;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getEmail() {
        return email;
    }

    public String getNotes() {
        if (notes != null) {
            return notes;
        }
        return "";
    }

    public int getPb() {
        return pb;
    }

    // BasicInformation fields
    public String getBirthday() {
        if (basicInformation != null) {
            return basicInformation.getBirthday();
        }
        return "";
    }

    public String getFirstName() {
        if (basicInformation != null) {
            return basicInformation.getFirstName();
        }
        return "";
    }

    public String getMiddleName() {
        if (basicInformation != null) {
            return basicInformation.getMiddleName();
        }
        return "";
    }

    public String getLastName() {
        if (basicInformation != null) {
            return basicInformation.getLastName();
        }
        return "";
    }

    public String getSex() {
        if (basicInformation != null) {
            return basicInformation.getSex();
        }
        return "";
    }
}
