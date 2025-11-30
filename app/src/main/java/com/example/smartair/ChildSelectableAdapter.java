package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChildSelectableAdapter extends RecyclerView.Adapter<ChildSelectableAdapter.ChildViewHolder> {

    public interface OnChildClickListener {
        void onChildClick(Child child);
    }

    private List<Child> childList;
    private OnChildClickListener listener;

    public ChildSelectableAdapter(List<Child> childList, OnChildClickListener listener) {
        this.childList = childList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_child_entry, parent, false);  //
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        Child child = childList.get(position);

        BasicInformation info = child.getBasicInformation();
        String fullName = info.getFirstName() + " " + info.getLastName();

        holder.name.setText(fullName);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onChildClick(child);
        });
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.childName); //
        }
    }
}


