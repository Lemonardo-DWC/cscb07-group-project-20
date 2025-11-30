package com.example.smartair;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ParentHomeChildItemAdapter
        extends RecyclerView.Adapter<ParentHomeChildItemAdapter.ItemViewHolder> {

    private List<ChildItem> childItemList;
    private final ChildItemHelper childItemHelper = new ChildItemHelper();
    private final TimeHelper timeHelper = new TimeHelper();
    private OnDetailButtonClick callback;
    private Context context;

    public ParentHomeChildItemAdapter(List<ChildItem> childItemList, OnDetailButtonClick callback,
                                      Context context) {
        this.childItemList = childItemList;
        this.callback = callback;
        this.context = context;
    }

    public interface OnDetailButtonClick {
        void onClick(ChildItem childItem);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext()).inflate(
                        R.layout.adapter_child_item,
                        parent,
                        false
                );
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ChildItem childItem = childItemList.get(position);

        String name = childItem.basicInformation.firstName;

        holder.childName.setText(name);

        PefLogs lastPefLog = childItemHelper.getLastGenericLog(childItem.pefLogs,
                ChildItemHelper.getDescendingTimeComparator());

        if (lastPefLog == null || !timeHelper.isToday(lastPefLog.gettimestamp())) {
            holder.todayZone.setText("Today's zone: No PEF logged");
            holder.zoneStatus.setVisibility(View.INVISIBLE);
        } else {
            holder.todayZone.setText("Today's zone: ");

            Drawable zoneImage;

            double pefScore = (double) lastPefLog.pef / (double) childItem.pb;
            if (pefScore < 0.5) {
                Log.d("Status Live Update", "red: " + lastPefLog.pef + " / " + childItem.pb + " = " + pefScore);
                zoneImage = AppCompatResources.getDrawable(context, R.drawable.zone_red);
            } else if (pefScore < 0.8) {
                Log.d("Status Live Update", "yellow: " + lastPefLog.pef + " / " + childItem.pb + " = " + pefScore);
                zoneImage = AppCompatResources.getDrawable(context, R.drawable.zone_yellow);
            } else {
                Log.d("Status Live Update", "green:" + lastPefLog.pef + " / " + childItem.pb + " = " + pefScore);
                zoneImage = AppCompatResources.getDrawable(context, R.drawable.zone_green);
            }

            holder.zoneStatus.setImageDrawable(zoneImage);
            holder.zoneStatus.setVisibility(View.VISIBLE);
        }

        String temp = String.format("Last rescue: %s",
                childItemHelper.getLastGenericLogTime(
                        childItem.rescueLogs,
                        ChildItemHelper.getDescendingTimeComparator()
                ));
        holder.lastRescueTime.setText(temp);

        String wrc = String.format("Rescues this week: %s",
                String.valueOf(childItemHelper.getWeeklyLogCount(childItem.rescueLogs,
                        ChildItemHelper.getDescendingTimeComparator())));
        holder.weeklyRescueCount.setText(wrc);

        holder.otherText.setText(
                String.format("dob: %s, sex: %s, other: %s",
                        childItem.basicInformation.birthday,
                        childItem.basicInformation.sex,
                        childItem.email)
        );

        holder.detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(childItem);
            }
        });

    }

    @Override
    public int getItemCount() {
        return childItemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView childName, todayZone, lastRescueTime, weeklyRescueCount, otherText;

        ImageView zoneStatus;

        MaterialButton detailButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            childName = itemView.findViewById(R.id.childName);
            todayZone = itemView.findViewById(R.id.todayZone);
            lastRescueTime = itemView.findViewById(R.id.lastRescuetime);
            weeklyRescueCount = itemView.findViewById(R.id.weeklyRescueCount);
            otherText = itemView.findViewById(R.id.OTHERTEXT);
            zoneStatus = itemView.findViewById(R.id.todayZoneStatus);

            detailButton = itemView.findViewById(R.id.detailsButton);
        }
    }

}
