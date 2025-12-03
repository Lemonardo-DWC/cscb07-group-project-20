package com.example.smartair;

public abstract class AlertItem implements SystemTimeTimestamp{

    public String ownerName;

    public abstract int getType();

    public static final int TYPE_CHECKIN = 0;
    public static final int TYPE_CONTROLLER = 1;
    public static final int TYPE_RESCUE = 2;
    public static final int TYPE_PEF = 3;
    public static final int TYPE_TRIAGE = 4;
}