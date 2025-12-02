package com.example.smartair;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProviderManageFragment extends Fragment {

    private Switch switchRescue, switchController, switchSymptoms,
            switchTriggers, switchPEF, switchTriage, switchCharts;
    private Button revokeButton;
    private TextView providerHeader, providerNameView, providerEmailView;

    private DatabaseReference userRef;

    private String childId;
    private String providerId;
    private String providerName;
    private String providerEmail;

    public ProviderManageFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_provider_manage, container, false);

        userRef = FirebaseDatabase.getInstance().getReference("users");

        if (getArguments() != null) {
            childId = getArguments().getString("childId");
            providerId = getArguments().getString("providerId");
            providerName = getArguments().getString("providerName");
            providerEmail = getArguments().getString("providerEmail");
        }

        providerHeader   = view.findViewById(R.id.providerHeader);
        providerNameView = view.findViewById(R.id.manageProviderName);
        providerEmailView= view.findViewById(R.id.manageProviderEmail);

        switchRescue     = view.findViewById(R.id.switchRescue);
        switchController = view.findViewById(R.id.switchController);
        switchSymptoms   = view.findViewById(R.id.switchSymptoms);
        switchTriggers   = view.findViewById(R.id.switchTriggers);
        switchPEF        = view.findViewById(R.id.switchPEF);
        switchTriage     = view.findViewById(R.id.switchTriage);
        switchCharts     = view.findViewById(R.id.switchCharts);

        revokeButton     = view.findViewById(R.id.revokeButton);

        if (providerName != null) {
            providerHeader.setText("Managing " + providerName);
            providerNameView.setText(providerName);
        }
        if (providerEmail != null) {
            providerEmailView.setText(providerEmail);
        }

        loadExistingShareSettings();

        setupToggleListeners();

        setupRevokeButton();

        return view;
    }

    private void loadExistingShareSettings() {
        if (childId == null || providerId == null) return;

        userRef.child(childId)
                .child("providerShares")
                .child(providerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {

                        switchRescue.setChecked(getBool(snap, "rescueLogs"));
                        switchController.setChecked(getBool(snap, "controllerSummary"));

                        switchSymptoms.setChecked(getBool(snap, "symptoms"));
                        switchTriggers.setChecked(getBool(snap, "triggers"));
                        switchPEF.setChecked(getBool(snap, "peakFlow"));
                        switchTriage.setChecked(getBool(snap, "triageIncidents"));
                        switchCharts.setChecked(getBool(snap, "summaryCharts"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private boolean getBool(DataSnapshot snap, String key) {
        Boolean val = snap.child(key).getValue(Boolean.class);
        return val != null && val;
    }

    private void setupToggleListeners() {
        if (childId == null || providerId == null) return;

        setToggleListener(switchRescue,     "rescueLogs");
        setToggleListener(switchController, "controllerSummary");
        setToggleListener(switchSymptoms,   "symptoms");
        setToggleListener(switchTriggers,   "triggers");
        setToggleListener(switchPEF,        "peakFlow");
        setToggleListener(switchTriage,     "triageIncidents");
        setToggleListener(switchCharts,     "summaryCharts");
    }

    private void setToggleListener(Switch toggle, String key) {
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userRef.child(childId)
                    .child("providerShares")
                    .child(providerId)
                    .child(key)
                    .setValue(isChecked);
        });
    }

    private void setupRevokeButton() {
        revokeButton.setOnClickListener(v -> {
            if (childId == null || providerId == null) return;

            userRef.child(childId)
                    .child("providerShares")
                    .child(providerId)
                    .removeValue()
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getContext(),
                                "Sharing revoked for this provider.",
                                Toast.LENGTH_SHORT).show();

                        if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    });
        });
    }
}
