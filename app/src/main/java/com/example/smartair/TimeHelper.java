package com.example.smartair;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;

public class TimeHelper {

    public TimeHelper() {}

    public String formatTime(String dateFormat, long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return sdf.format(new Date(milliseconds));
    }

    public long[] getCurrentWeekRange() {

        LocalDate now = LocalDate.now();

        LocalDate monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        long weekStart = monday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long weekEnd = weekStart + AppConstants.MS_WEEK;

        return new long[] {weekStart, weekEnd};
    }

    public boolean isToday(long logTimestamp) {
        LocalDate currentDate = LocalDate.now();
        LocalDate logDate
                = Instant.ofEpochMilli(logTimestamp).atZone(ZoneId.systemDefault()).toLocalDate();

        return currentDate.isEqual(logDate);
    }

}
