//Adapter for ChildProviderShareFragment
package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProviderListAdapter extends RecyclerView.Adapter<ProviderListAdapter.ViewHolder> {

    public interface OnProviderClickListener {
        void onProviderClick(Provider provider);
    }

    private ArrayList<Provider> providerList;
    private OnProviderClickListener listener;

    public ProviderListAdapter(ArrayList<Provider> providerList, OnProviderClickListener listener) {
        this.providerList = providerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.provider_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Provider model = providerList.get(position);

        holder.providerName.setText(model.getFullName());
        holder.providerEmail.setText(model.getEmail());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProviderClick(model);
        });
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView providerName, providerEmail;

        ViewHolder(@NonNull View view) {
            super(view);
            providerName = view.findViewById(R.id.providerName);
            providerEmail = view.findViewById(R.id.providerEmail);
        }
    }
}

