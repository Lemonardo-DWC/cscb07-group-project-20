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
    private final ChildItemHelper childItemHelper = new ChildItemHelper();

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

//        if (!childItem.basicInformation.middleName.equals("")) {
//            name += " " + childItem.basicInformation.middleName;
//        }
//        name += " " + childItem.basicInformation.lastName;

        holder.childName.setText(name);

        holder.childUid.setText(childItemHelper.getChildUid(childItem));

        String lrt = String.format("Last rescue: %s",
                childItemHelper.getLastRescueTime(childItem));
        holder.lastRescueTime.setText(lrt);

        String wrc = String.format("Rescues this week: %s",
                String.valueOf(childItemHelper.getWeeklyRescueCount(childItem)));
        holder.weeklyRescueCount.setText(wrc);

        holder.otherText.setText(
                String.format("dob: %s, sex: %s, other: %s",
                        childItem.basicInformation.birthday,
                        childItem.basicInformation.sex,
                        childItem.email)
        );

    }

    @Override
    public int getItemCount() {
        return childItemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView childName, childUid, lastRescueTime, weeklyRescueCount, otherText;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            childName = itemView.findViewById(R.id.childName);
            childUid = itemView.findViewById(R.id.childUid);
            lastRescueTime = itemView.findViewById(R.id.lastRescuetime);
            weeklyRescueCount = itemView.findViewById(R.id.weeklyRescueCount);
            otherText = itemView.findViewById(R.id.OTHERTEXT);
        }
    }

}
