package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ParentInventoryFragment
        extends Fragment
        implements ParentInventoryAdapter.OnUpdateButtonClick {

    private ParentHomeViewModel phvm;
    private List<ChildItem> childItemList;

    private RecyclerView childInventoryListRecycler;
    private ParentInventoryAdapter ParentInventoryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parent_inventory, container, false);

        phvm = new ViewModelProvider(requireActivity()).get(ParentHomeViewModel.class);

        MaterialButton backButton = view.findViewById(R.id.inventory_back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // child status recycler setup
        childInventoryListRecycler = view.findViewById(R.id.childInventoryListRecycler);

        childItemList = new ArrayList<>();
        ParentInventoryAdapter = new ParentInventoryAdapter(childItemList, this, requireContext());
        childInventoryListRecycler.setAdapter(ParentInventoryAdapter);
        childInventoryListRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        phvm.childItemListData.observe(getViewLifecycleOwner(), newChildItemList -> {
            childItemList.clear();
            childItemList.addAll(newChildItemList);
            ParentInventoryAdapter.notifyDataSetChanged();
        });

    }

    @Override
    public void onClick(ChildItem childItem) {
        showUpdateInventoryDialog(childItem);
    }

    private void showUpdateInventoryDialog(ChildItem childItem) {

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View popupView = inflater.inflate(R.layout.popup_update_inventory, null);

        EditText newRescueRemaining = popupView.findViewById(R.id.remainingInputRescue);
        EditText newRescueTotal = popupView.findViewById(R.id.totalInputRescue);
        EditText rescuePurchaseDate = popupView.findViewById(R.id.purchaseDateInputRescue);
        EditText rescueExpiryDate = popupView.findViewById(R.id.expiryDateInputRescue);
        CheckBox rescueLow = popupView.findViewById(R.id.rescueLow);

        EditText newControllerRemaining = popupView.findViewById(R.id.remainingInputController);
        EditText newControllerTotal = popupView.findViewById(R.id.totalInputController);
        EditText controllerPurchaseDate = popupView.findViewById(R.id.purchaseDateInputController);
        EditText controllerExpiryDate = popupView.findViewById(R.id.expiryDateInputController);
        CheckBox controllerLow = popupView.findViewById(R.id.controllerLow);

        if (childItem.rescue != null) {
            newRescueRemaining.setText(String.valueOf(Objects.requireNonNullElse(
                    childItem.rescue.remainingCapacity,
                    0))
            );
            newRescueTotal.setText(String.valueOf(Objects.requireNonNullElse(
                    childItem.rescue.totalCapacity,
                    0
            )));
            rescuePurchaseDate.setText(Objects.requireNonNullElse(
                    childItem.rescue.purchaseDate,
                    ""
            ));
            rescueExpiryDate.setText(Objects.requireNonNullElse(
                    childItem.rescue.expiryDate,
                    ""
            ));
            rescueLow.setChecked(Objects.requireNonNullElse(
                    childItem.rescue.low,
                    false
            ));
        }

        if (childItem.controller != null) {
            newControllerRemaining.setText(String.valueOf(Objects.requireNonNullElse(
                    childItem.controller.remainingCapacity,
                    0
            )));
            newControllerTotal.setText(String.valueOf(Objects.requireNonNullElse(
                    childItem.controller.totalCapacity,
                    0
            )));
            controllerPurchaseDate.setText(Objects.requireNonNullElse(
                    childItem.controller.purchaseDate,
                    ""
            ));
            controllerExpiryDate.setText(Objects.requireNonNullElse(
                    childItem.controller.expiryDate,
                    ""
            ));
            controllerLow.setChecked(Objects.requireNonNullElse(
                    childItem.controller.low,
                    false
            ));
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Update " + childItem.getFirstName() + "'s inventory")
                .setView(popupView)
                .setPositiveButton("Save", ((dialog, which) -> {
                    String nrr = newRescueRemaining.getText().toString();
                    String nrt = newRescueTotal.getText().toString();
                    String rpd = rescuePurchaseDate.getText().toString();
                    String red = rescueExpiryDate.getText().toString();
                    boolean rl = rescueLow.isChecked();

                    setRescue(childItem, new RescueItem(),
                            nrr, nrt, rpd, red, rl);
                    phvm.updateChildRescue(childItem);

                    String ncr = newControllerRemaining.getText().toString();
                    String nct = newControllerTotal.getText().toString();
                    String cpd = controllerPurchaseDate.getText().toString();
                    String ced = controllerExpiryDate.getText().toString();
                    boolean cl = controllerLow.isChecked();

                    setController(childItem, new ControllerItem(),
                            ncr, nct, cpd, ced, cl);
                    phvm.updateChildController(childItem);

                }))
                .setNegativeButton("Cancel",null)
                .show();

    }

    private void setRescue(ChildItem childItem, RescueItem rescueItem,
                           String nrr, String nrt, String rpd, String red, boolean rl) {
        rescueItem.remainingCapacity = Integer.parseInt(nrr);
        rescueItem.totalCapacity = Integer.parseInt(nrt);
        rescueItem.purchaseDate = rpd;
        rescueItem.expiryDate = red;
        rescueItem.low = rl;

        childItem.rescue = rescueItem;
    }

    private void setController(ChildItem childItem, ControllerItem controllerItem,
                               String ncr, String nct, String cpd, String ced, boolean cl) {
        controllerItem.remainingCapacity = Integer.parseInt(ncr);
        controllerItem.totalCapacity = Integer.parseInt(nct);
        controllerItem.purchaseDate = cpd;
        controllerItem.expiryDate = ced;
        controllerItem.low = cl;

        childItem.controller = controllerItem;
    }
}