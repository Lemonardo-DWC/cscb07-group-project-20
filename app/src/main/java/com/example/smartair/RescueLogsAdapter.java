package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RescueLogsAdapter extends RecyclerView.Adapter<RescueLogsAdapter.LogViewHolder> {

    private ArrayList<RescueLogs> logs;

    public RescueLogsAdapter(ArrayList<RescueLogs> logs) {
        this.logs = logs;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rescue_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        RescueLogs log = logs.get(position);

        holder.logDose.setText("Puffs used: " + log.dose);
        holder.logPre.setText("Pre-breath rating: " + log.preBreathRating);
        holder.logPost.setText("Post-breath rating: " + log.postBreathRating);

        String statusTxt = "Same";
        if (log.postStatus == 1) statusTxt = "Better";
        else if (log.postStatus == -1) statusTxt = "Worse";

        holder.logStatus.setText("Overall feeling: " + statusTxt);

        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        holder.logTime.setText("Time: " + sdf.format(new java.util.Date(log.timestamp)));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView logDose, logPre, logPost, logStatus, logTime;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            logDose = itemView.findViewById(R.id.logDose);
            logPre = itemView.findViewById(R.id.logPre);
            logPost = itemView.findViewById(R.id.logPost);
            logStatus = itemView.findViewById(R.id.logStatus);
            logTime = itemView.findViewById(R.id.logTime);
        }
    }
}
