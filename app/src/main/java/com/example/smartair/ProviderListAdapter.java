package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProviderListAdapter extends RecyclerView.Adapter<ProviderListAdapter.ProviderViewHolder> {

    public interface OnProviderClickListener {
        void onProviderClick(Provider provider);
    }

    private List<Provider> providerList;
    private OnProviderClickListener listener;

    public ProviderListAdapter(List<Provider> providerList, OnProviderClickListener listener) {
        this.providerList = providerList;
        this.listener = listener;
    }

    public void updateList(List<Provider> list) {
        providerList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.provider_item_layout, parent, false);
        return new ProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {

        Provider provider = providerList.get(position);

        String name = provider.getName() != null ? provider.getName() : "Unknown Provider";
        String email = provider.getEmail() != null ? provider.getEmail() : "No email";

        holder.nameText.setText(name);
        holder.emailText.setText(email);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProviderClick(provider);
        });
    }


    @Override
    public int getItemCount() {
        return providerList.size();
    }

    static class ProviderViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, emailText;

        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.providerName);
            emailText = itemView.findViewById(R.id.providerEmail);
        }
    }
}
