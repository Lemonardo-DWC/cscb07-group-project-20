package com.example.smartair;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProviderManageFragment extends Fragment {

    private String childId;
    private String providerUid;
    private String providerName;
    private String parentId;

    private Switch switchRescue, switchController, switchSymptoms, switchTriggers, switchPEF, switchTriage, switchCharts;
    private boolean isInitializingSwitches = true;

    public ProviderManageFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            childId = getArguments().getString("childId");
            providerUid = getArguments().getString("providerUid");
            providerName = getArguments().getString("providerName");
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            parentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_provider_manage, container, false);

        TextView providerHeader = view.findViewById(R.id.providerHeader);
        providerHeader.setText("Managing " + providerName);

        switchRescue = view.findViewById(R.id.switchRescue);
        switchController = view.findViewById(R.id.switchController);
        switchSymptoms = view.findViewById(R.id.switchSymptoms);
        switchTriggers = view.findViewById(R.id.switchTriggers);
        switchPEF = view.findViewById(R.id.switchPEF);
        switchTriage = view.findViewById(R.id.switchTriage);
        switchCharts = view.findViewById(R.id.switchCharts);

        loadShareSettings();
        setupSwitchListeners();

        return view;
    }

    private void loadShareSettings() {

        isInitializingSwitches = true;

        DatabaseReference shareRef = FirebaseDatabase.getInstance().getReference("users")
                .child(parentId)
                .child("childProviderShare")
                .child(childId)
                .child(providerUid);

        shareRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Boolean rescue = snapshot.child("rescue").getValue(Boolean.class);
                Boolean controller = snapshot.child("controller").getValue(Boolean.class);
                Boolean symptoms = snapshot.child("symptoms").getValue(Boolean.class);
                Boolean triggers = snapshot.child("triggers").getValue(Boolean.class);
                Boolean pef = snapshot.child("pef").getValue(Boolean.class);
                Boolean triage = snapshot.child("triage").getValue(Boolean.class);
                Boolean charts = snapshot.child("charts").getValue(Boolean.class);

                switchRescue.setChecked(Boolean.TRUE.equals(rescue));
                switchController.setChecked(Boolean.TRUE.equals(controller));
                switchSymptoms.setChecked(Boolean.TRUE.equals(symptoms));
                switchTriggers.setChecked(Boolean.TRUE.equals(triggers));
                switchPEF.setChecked(Boolean.TRUE.equals(pef));
                switchTriage.setChecked(Boolean.TRUE.equals(triage));
                switchCharts.setChecked(Boolean.TRUE.equals(charts));

                isInitializingSwitches = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load share settings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSwitchListeners() {

        View.OnClickListener listener = v -> {
            if (isInitializingSwitches) return;
            updateShareSettings();
        };

        switchRescue.setOnClickListener(listener);
        switchController.setOnClickListener(listener);
        switchSymptoms.setOnClickListener(listener);
        switchTriggers.setOnClickListener(listener);
        switchPEF.setOnClickListener(listener);
        switchTriage.setOnClickListener(listener);
        switchCharts.setOnClickListener(listener);
    }

    private void updateShareSettings() {

        DatabaseReference shareRef = FirebaseDatabase.getInstance().getReference("users")
                .child(parentId)
                .child("childProviderShare")
                .child(childId)
                .child(providerUid);

        shareRef.child("rescue").setValue(switchRescue.isChecked());
        shareRef.child("controller").setValue(switchController.isChecked());
        shareRef.child("symptoms").setValue(switchSymptoms.isChecked());
        shareRef.child("triggers").setValue(switchTriggers.isChecked());
        shareRef.child("pef").setValue(switchPEF.isChecked());
        shareRef.child("triage").setValue(switchTriage.isChecked());
        shareRef.child("charts").setValue(switchCharts.isChecked());
    }
}
