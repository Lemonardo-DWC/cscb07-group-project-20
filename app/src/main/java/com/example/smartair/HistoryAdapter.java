package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter<T extends SystemTimeTimestamp>
        extends RecyclerView.Adapter<HistoryAdapter.LogViewHolder> {

    private final List<T> logs;
    private final LogFormatter<T> formatter;

    public interface LogFormatter<T> {
        public String format(T log);
    }

    public HistoryAdapter(List<T> logs, LogFormatter<T> formatter) {
        this.logs = logs;
        this.formatter = formatter;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_history_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        T log = logs.get(position);
        holder.textView.setText(formatter.format(log));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
