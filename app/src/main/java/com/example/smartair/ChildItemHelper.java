package com.example.smartair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChildItemHelper {

    private final TimeHelper timeHelper = new TimeHelper();

    public ChildItemHelper() {}

    public List<RescueLogs> getRescueLogs(ChildItem childItem) {

        if (childItem.rescueLogs == null) {
            return new ArrayList<>(0);
        }

        List<RescueLogs> output = new ArrayList<>(childItem.rescueLogs.values());
        sortRescueLogs(output);

        return output;
    }

    public List<RescueLogs> getRangeRescueLogs(ChildItem childItem, long startTime, long endTime) {

        List<RescueLogs> output = new ArrayList<>();

        for(RescueLogs log : getRescueLogs(childItem)) {
            if (startTime <= log.timestamp && log.timestamp <= endTime) {
                output.add(log);
            }
        }

        sortRescueLogs(output);

        return output;
    }

    public String getLastRescueTime(ChildItem childItem) {

        List<RescueLogs> logs = getRescueLogs(childItem);

        if (logs.isEmpty()) {
            return "N/A";
        }

        long lastResTimeMs = getRescueLogs(childItem).get(0).timestamp;

        String time = timeHelper.formatTime(AppConstants.DATE_YMDHM, lastResTimeMs);

        return time;
    }

    public int getWeeklyRescueCount(ChildItem childItem) {

        long[] timeRange = timeHelper.getCurrentWeekRange();
        List<RescueLogs> currWeekLogs = getRangeRescueLogs(childItem, timeRange[0], timeRange[1]);

        return currWeekLogs.size();
    }

    public String getChildUid(ChildItem childItem) {

        if (childItem.uid == null) {
            return "";
        }

        return childItem.uid;
    }

    private void sortRescueLogs(List<RescueLogs> logList) {

        logList.sort(new Comparator<RescueLogs>() {
            @Override
            public int compare(RescueLogs o1, RescueLogs o2) {
                return Long.compare(o2.timestamp, o1.timestamp);
            }
        });

    }

}
