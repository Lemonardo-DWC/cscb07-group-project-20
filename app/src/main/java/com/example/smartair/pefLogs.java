package com.example.smartair;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class pefLogs implements SystemTimeTimestamp {

    public int pef;
    public String timestamp;

    public pefLogs() {}

    public long gettimestamp() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime localDateTime = LocalDateTime.parse(timestamp, formatter);

        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    }

}
