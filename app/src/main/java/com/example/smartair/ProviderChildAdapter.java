//Adapter for Provider Home
package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProviderChildAdapter extends RecyclerView.Adapter<ProviderChildAdapter.ViewHolder> {
    public interface OnChildClickListener {
        void onChildClick(String childUid, String childName);
    }
    private List<ChildModel> childList;
    private OnChildClickListener listener;

    public ProviderChildAdapter(List<ChildModel> childList, OnChildClickListener listener) {
        this.childList = childList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_provider_child, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChildModel child = childList.get(position);
        holder.childName.setText(child.getFullName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChildClick(child.getChildId(), child.getFullName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView childName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            childName = itemView.findViewById(R.id.childNameText);
        }
    }
}
