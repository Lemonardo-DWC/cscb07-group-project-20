package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DailyCheckAdapter extends RecyclerView.Adapter<DailyCheckAdapter.ViewHolder> {


    private ArrayList<DailyCheckLog> logList;


    public DailyCheckAdapter(ArrayList<DailyCheckLog> logList) {
        this.logList = logList;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNight, textActivity, textCough, textTrigger, textTime;


        public ViewHolder(View itemView) {
            super(itemView);
            textNight = itemView.findViewById(R.id.NightWaking);
            textActivity = itemView.findViewById(R.id.ActivityLimit);
            textCough = itemView.findViewById(R.id.CoughWheeze);
            textTrigger = itemView.findViewById(R.id.Triggers);
            textTime = itemView.findViewById(R.id.Timestamp);
        }
    }


    @NonNull
    @Override
    public DailyCheckAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.daily_check_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull DailyCheckAdapter.ViewHolder holder, int position) {
        DailyCheckLog log = logList.get(position);


        holder.textNight.setText("Night waking: " + log.nightWaking);
        holder.textActivity.setText("Activity limit: " + log.activityLimit);
        holder.textCough.setText("Cough/Wheeze: " + log.coughWheeze);
        holder.textTrigger.setText("Triggers: " + String.join(", ", log.triggers));


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        holder.textTime.setText("Time: " + sdf.format(new Date(log.timestamp)));
    }


    @Override
    public int getItemCount() {
        return logList.size();
    }
}
