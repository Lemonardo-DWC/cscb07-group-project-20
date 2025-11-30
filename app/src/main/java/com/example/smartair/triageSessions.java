package com.example.smartair;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TriageSessions implements SystemTimeTimestamp {

    public String SymptomCheckTimestamp;
    public boolean blue_lips_nails;
    public boolean chest_pulling_in;
    public boolean dizzy_scared;
    public boolean red_flag_detected;
    public boolean speak_full_sentences;
    public boolean used_rescue_meds;

    public TriageSessions() {}

    public long gettimestamp() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime localDateTime = LocalDateTime.parse(SymptomCheckTimestamp, formatter);

        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    }

}
