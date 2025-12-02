package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ParentAlertAdapter
        extends RecyclerView.Adapter<ParentAlertAdapter.ItemViewHolder> {

    private final ChildItemHelper childItemHelper = new ChildItemHelper();
    private final TimeHelper timeHelper = new TimeHelper();

    private List<String> alertList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();

    public ParentAlertAdapter(List<ChildItem> childItemList) {

        alertList.clear();
        nameList.clear();

        for (ChildItem childItem : childItemList) {
            populateAlertList(childItem);
        }

    }

    private void populateAlertList(ChildItem childItem) {

        long todayMS
                = System.currentTimeMillis() - System.currentTimeMillis() % AppConstants.MS_DAY;
        long weekAgoMS = todayMS - AppConstants.MS_WEEK;

        if (childItem.pefLogs != null) {
            PefLogs lastLog = childItemHelper.getLastGenericLog(
                    childItem.pefLogs,
                    ChildItemHelper.getDescendingTimeComparator()
            );

            long lastLogTimeDay
                    = lastLog.gettimestamp() - lastLog.gettimestamp() % AppConstants.MS_DAY;

            double zoneValue = (double) lastLog.pef / (double) childItem.getPb() * 100;

            if (lastLogTimeDay == todayMS && zoneValue < 50) {
                String alert
                        = "Red zone day:  "
                        + lastLog.pef + "/" + childItem.getPb()
                        + " ("
                        + String.format(Locale.getDefault(), "%.0f%%", zoneValue)
                        + ")";

                nameList.add(childItem.getFirstName());
                alertList.add(alert);
            }
        }

        if (childItem.rescueLogs != null) {
            List<RescueLogs> rescueLogsList
                    = childItemHelper.getRangeGenericLog(
                            childItem.rescueLogs,
                            weekAgoMS, todayMS,
                            ChildItemHelper.getDescendingTimeComparator()
            );

            RescueLogs rapidRescue = findRapidRescue(rescueLogsList);

            if (rapidRescue != null) {
                String alert = "More than 2 rescues within 3 hours @ "
                        + timeHelper.formatTime(
                                AppConstants.DATE_HMMDY, rapidRescue.gettimestamp());

                nameList.add(childItem.getFirstName());
                alertList.add(alert);
            }

            if (rescueLogsList.get(0).postStatus == -1) {
                String alert = "Worse after dose @ "
                        + timeHelper.formatTime(
                                AppConstants.DATE_HMMDY, rescueLogsList.get(0).gettimestamp());

                nameList.add(childItem.getFirstName());
                alertList.add(alert);
            }

        }

        if (childItem.controller != null){
            if (childItem.controller.low) {
                nameList.add(childItem.getFirstName());
                alertList.add("Controller reserve low");
            }

            long expireDateMS = parseDateToMillis(childItem.controller.expiryDate);

            if (expireDateMS > System.currentTimeMillis()) {
                nameList.add(childItem.getFirstName());
                alertList.add("Controller expired");
            }
        }

        if (childItem.rescue != null) {
            if (childItem.rescue.low){
                nameList.add(childItem.getFirstName());
                alertList.add("Rescue reserve low");
            }

            long expireDateMS = parseDateToMillis(childItem.rescue.expiryDate);

            if (expireDateMS > System.currentTimeMillis()) {
                nameList.add(childItem.getFirstName());
                alertList.add("Rescue expired");
            }
        }

    }

    private long parseDateToMillis(String dateStr) {
        DateTimeFormatter[] formatters = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yy")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            } catch (DateTimeParseException ignored) {
            }
        }

        return System.currentTimeMillis();
    }

    private RescueLogs findRapidRescue(List<RescueLogs> rescueLogsList) {

        RescueLogs rapidStartLog = null;

        for (int i = 0; i + 2 < rescueLogsList.size(); i++) {

            long startTime = rescueLogsList.get(i).gettimestamp();
            long endTime = startTime + 3 * AppConstants.MS_HOUR;

            int rescueCount = 1;

            int j = i + 1;

            while (j < rescueLogsList.size()) {
                if (startTime <= rescueLogsList.get(j).gettimestamp()
                        && rescueLogsList.get(j).gettimestamp() <= endTime) {
                    rescueCount++;
                } else {
                    break;
                }

                j++;
            }

            if (rescueCount >= 3) {
                rapidStartLog = rescueLogsList.get(j - 1);
                break;
            }

        }

        return rapidStartLog;

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_parent_alert, parent, false);

        return new ItemViewHolder(view);
    }

    public void updateChildList(List<ChildItem> childItemList) {
        alertList.clear();
        nameList.clear();

        for (ChildItem childItem : childItemList) {
            populateAlertList(childItem);
        }

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String childName = nameList.get(position);
        String alertText = alertList.get(position);

        holder.title.setText(childName);
        holder.body.setText(alertText);
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView title, body;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.alertTitle);
            body = itemView.findViewById(R.id.alertBody);

        }
    }
}
