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
import java.util.Locale;

public class ChildItemListAdapter
        extends RecyclerView.Adapter<ChildItemListAdapter.ItemViewHolder> {

    private List<ChildItem> childItemList;

    public ChildItemListAdapter(List<ChildItem> childItemList) {
        this.childItemList = childItemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext()).inflate(
                        R.layout.adapter_child_item_list,
                        parent,
                        false
                );
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ChildItem childItem = childItemList.get(position);

        String name = childItem.getFirstName() +
                " " + childItem.getMiddleName() +
                " " + childItem.getLastName();

        holder.childName.setText(name);

    }

    @Override
    public int getItemCount() {
        return childItemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView childName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            childName = itemView.findViewById(R.id.childName);
        }
    }

}
