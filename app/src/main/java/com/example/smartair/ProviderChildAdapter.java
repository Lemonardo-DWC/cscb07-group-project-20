//Adapter for Provider Home
package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProviderChildAdapter extends RecyclerView.Adapter<ProviderChildAdapter.ViewHolder> {

    private ArrayList<ChildModel> childList;

    public ProviderChildAdapter(ArrayList<ChildModel> childList) {
        this.childList = childList;
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
        holder.childName.setText(child.getFirstName() + " " + child.getLastName());
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
