package com.example.smartair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ChildItemHelper {

    private final TimeHelper timeHelper = new TimeHelper();

    public ChildItemHelper() {}

    public <T extends SystemTimeTimestamp> List<T> getGenericLog(Map<String, T> logMap,
                                                                  Comparator<T> comparator) {

        if (logMap == null) {
            return new ArrayList<>(0);
        }

        List<T> output = new ArrayList<>(logMap.values());
        output.sort(comparator);

        return output;
    }

    public <T extends SystemTimeTimestamp> List<T>
            getRangeGenericLog(Map<String, T> logMap,
                               long startTime, long endTime,
                               Comparator<T> comparator) {

        List<T> output = new ArrayList<>();

        for(T log : getGenericLog(logMap, comparator)) {
            if (startTime <= log.gettimestamp() && log.gettimestamp() <= endTime) {
                output.add(log);
            }
        }

        return output;
    }

    public <T extends SystemTimeTimestamp> String getLastGenericLogTime(Map<String, T> logMap,
                                                                        Comparator<T> comparator) {

        List<T> logList = getGenericLog(logMap, comparator);

        if (logList.isEmpty()) {
            return "N/A";
        }

        long lastLogTimeMs = logList.get(0).gettimestamp();

        String time = timeHelper.formatTime(AppConstants.DATE_HMMDY, lastLogTimeMs);

        return time;

    }

    public <T extends SystemTimeTimestamp> T getLastGenericLog(Map<String, T> logMap,
                                                               Comparator<T> comparator) {

        List<T> logList = getGenericLog(logMap, comparator);

        if (logList.isEmpty()) {
            return null;
        }

        return logList.get(0);

    }

    public <T extends SystemTimeTimestamp> int getWeeklyLogCount(Map<String, T> logMap,
                                                                 Comparator<T> comparator) {

        long[] timeRange = timeHelper.getCurrentWeekRange();
        List<T> currWeekLogs
                = getRangeGenericLog(logMap,timeRange[0], timeRange[1], comparator);

        return currWeekLogs.size();
    }

    public static <T extends SystemTimeTimestamp> Comparator<T> getDescendingTimeComparator() {
        return (o1, o2) -> Long.compare(o2.gettimestamp(), o1.gettimestamp());
    }

    public static <T extends SystemTimeTimestamp> Comparator<T> getAscendingTimeComparator() {
        return (o1, o2) -> Long.compare(o1.gettimestamp(), o2.gettimestamp());
    }
}
