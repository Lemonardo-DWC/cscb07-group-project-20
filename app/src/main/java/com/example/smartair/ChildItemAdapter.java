package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChildItemAdapter extends RecyclerView.Adapter<ChildItemAdapter.ItemViewHolder> {

    private List<ChildItem> childItemList;

    public ChildItemAdapter(List<ChildItem> childItemList) {
        this.childItemList = childItemList;
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
        if (!childItem.basicInformation.middleName.equals("")) {
            name += childItem.basicInformation.middleName;
        }
        name += childItem.basicInformation.lastName;

        holder.childName.setText(name);

        holder.tempInfo.setText(
                String.format("dob: %s, sex: %s, other: %s", childItem.basicInformation.birthday, childItem.basicInformation.sex, childItem.email)
        );
    }

    @Override
    public int getItemCount() {
        return childItemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView childName, tempInfo;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            childName = itemView.findViewById(R.id.childName);
            tempInfo = itemView.findViewById(R.id.tempInfo);
        }
    }

}
