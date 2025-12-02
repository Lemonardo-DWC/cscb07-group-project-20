package com.example.smartair;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ParentInventoryAdapter
        extends RecyclerView.Adapter<ParentInventoryAdapter.ItemViewHolder> {

    private List<ChildItem> childItemList;
    private ParentInventoryAdapter.OnUpdateButtonClick callback;
    
    private final String NULLDATE = "--/--/----";
    private final String NULLRESERVE = "0 / 0 | --%";
    private final int LOW_THRESHOLD = 20;
    private Context context;

    public ParentInventoryAdapter(List<ChildItem> childItemList,
                                  ParentInventoryAdapter.OnUpdateButtonClick callback,
                                  Context context) {
        this.childItemList = childItemList;
        this.callback = callback;
        this.context = context;
    }

    public interface OnUpdateButtonClick {
        void onClick(ChildItem childItem);
    }

    @NonNull
    @Override
    public ParentInventoryAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext()).inflate(
                        R.layout.adapter_inventory_parent,
                        parent,
                        false
                );
        return new ParentInventoryAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentInventoryAdapter.ItemViewHolder holder, int position) {
        ChildItem childItem = childItemList.get(position);

        holder.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(childItem);
            }
        });

        // name
        String name = childItem.getFirstName();
        holder.childName.setText(name);

        // controller
        String controllerFieldText = "Controller reserve:\n  ";
        String controllerPurchaseDate;
        String controllerExpiryDate;

        if (childItem.controller == null) {
            holder.controllerAlert.setVisibility(View.VISIBLE);

            controllerFieldText += NULLRESERVE;
            controllerPurchaseDate = "Purchased on:\n  " + NULLDATE;
            controllerExpiryDate = "Expires on:\n  " + NULLDATE;
        } else {
            int controllerRC = childItem.controller.remainingCapacity;
            int controllerTC = childItem.controller.totalCapacity;
            double controllerRemainingPercentage;

            if (controllerTC != 0) {
                controllerRemainingPercentage = (double) controllerRC / (double) controllerTC * 100;
            } else {
                controllerRemainingPercentage = 0;
            }

            if (controllerRemainingPercentage < LOW_THRESHOLD || childItem.controller.low) {
                holder.controllerAlert.setVisibility(View.VISIBLE);
            } else {
                holder.controllerAlert.setVisibility(View.INVISIBLE);
            }

            controllerFieldText += String.format(
                    Locale.getDefault(),
                    "%d / %d | %.0f%%",
                    controllerRC, controllerTC, controllerRemainingPercentage
            );

            controllerPurchaseDate
                    = Objects.requireNonNullElse(
                            "Purchased on:\n  " + childItem.controller.purchaseDate,
                            "Purchased on:\n  " + NULLDATE
            );

            controllerExpiryDate
                    = Objects.requireNonNullElse(
                            "Expires on:\n  " + childItem.controller.expiryDate,
                            "Expires on:\n  " + NULLDATE
            );
        }

        holder.controllerField.setText(controllerFieldText);
        holder.controllerPurchaseDate.setText(controllerPurchaseDate);
        holder.controllerExpiryDate.setText(controllerExpiryDate);

        if (childItem.controller != null && childItem.controller.expiryDate != null) {
            if (isExpired(childItem.controller.expiryDate)) {
                holder.controllerExpiryDate.setTextColor(context.getColor(R.color.zone_red));
            } else {
                holder.controllerExpiryDate.setTextColor(context.getColor(R.color.text_primary));
            }
        } else {
            holder.controllerExpiryDate.setTextColor(context.getColor(R.color.text_primary));
        }


        // rescue
        String rescueFieldText = "Rescue reserve:\n  ";
        String rescuePurchaseDate;
        String rescueExpiryDate;

        if (childItem.rescue == null) {
            holder.rescueAlert.setVisibility(View.VISIBLE);

            rescueFieldText += NULLRESERVE;
            rescuePurchaseDate = "Purchased on:\n  " + NULLDATE;
            rescueExpiryDate = "Expires on:\n  " + NULLDATE;
        } else {
            int rescueRC = childItem.rescue.remainingCapacity;
            int rescueTC = childItem.rescue.totalCapacity;
            double rescueRemainingPercentage;

            if (rescueTC != 0) {
                rescueRemainingPercentage = (double) rescueRC / (double) rescueTC * 100;
            } else {
                rescueRemainingPercentage = 0;
            }

            if (rescueRemainingPercentage < LOW_THRESHOLD || childItem.rescue.low) {
                holder.rescueAlert.setVisibility(View.VISIBLE);
            } else {
                holder.rescueAlert.setVisibility(View.INVISIBLE);
            }

            rescueFieldText += String.format(
                    Locale.getDefault(),
                    "%d / %d | %.0f%%",
                    rescueRC,
                    rescueTC,
                    rescueRemainingPercentage
            );

            rescuePurchaseDate
                    = Objects.requireNonNullElse(
                            "Purchased on:\n  " + childItem.rescue.purchaseDate,
                                "Purchase on:\n  " + NULLDATE
            );

            rescueExpiryDate
                    = Objects.requireNonNullElse(
                            "Expires on:\n  " + childItem.rescue.expiryDate,
                            "Expires on:\n  " + NULLDATE
            );
        }

        holder.rescueField.setText(rescueFieldText);
        holder.rescuePurchaseDate.setText(rescuePurchaseDate);
        holder.rescueExpiryDate.setText(rescueExpiryDate);

        if (childItem.rescue != null && childItem.rescue.expiryDate != null) {
            if (isExpired(childItem.rescue.expiryDate)) {
                holder.rescueExpiryDate.setTextColor(context.getColor(R.color.zone_red));
            } else {
                holder.rescueExpiryDate.setTextColor(context.getColor(R.color.text_primary));
            }
        } else {
            holder.rescueExpiryDate.setTextColor(context.getColor(R.color.text_primary));
        }

        
    }

    @Override
    public int getItemCount() {
        return childItemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView childName;
        TextView rescueAlert, controllerAlert;
        TextView controllerField, controllerPurchaseDate, controllerExpiryDate;
        TextView rescueField, rescuePurchaseDate, rescueExpiryDate;
        MaterialButton updateButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            childName = itemView.findViewById(R.id.childName);

            rescueAlert = itemView.findViewById(R.id.lowRescueAlert);
            controllerAlert = itemView.findViewById(R.id.lowControllerAlert);

            controllerField = itemView.findViewById(R.id.controllerField);
            controllerPurchaseDate = itemView.findViewById(R.id.purchaseDateController);
            controllerExpiryDate = itemView.findViewById(R.id.expiryDateController);

            rescueField = itemView.findViewById(R.id.rescueField);
            rescuePurchaseDate = itemView.findViewById(R.id.purchaseDateRescue);
            rescueExpiryDate = itemView.findViewById(R.id.expiryDateRescue);

            updateButton = itemView.findViewById(R.id.updateInventoryButton);
        }
    }

    private boolean isExpired(String expiryDateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date expiryDate = sdf.parse(expiryDateString);
            if (expiryDate != null) {
                long currentTimeMillis = System.currentTimeMillis();

                return expiryDate.getTime() < currentTimeMillis;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;

    }

}