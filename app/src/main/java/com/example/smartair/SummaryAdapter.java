package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.ViewHolder> {

    private List<SummaryCardModel> items;

    public SummaryAdapter(List<SummaryCardModel> items) {
        this.items = items;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.summaryTitleText);
            content = itemView.findViewById(R.id.summaryContentText);
        }
    }

    @NonNull
    @Override
    public SummaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_summary_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryAdapter.ViewHolder holder, int position) {
        SummaryCardModel model = items.get(position);
        holder.title.setText(model.title);
        holder.content.setText(model.content);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
