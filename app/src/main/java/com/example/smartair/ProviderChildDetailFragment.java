package com.example.smartair;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProviderChildDetailFragment extends Fragment {

    private static final String ARG_CHILD_UID = "childUid";
    private static final String ARG_CHILD_NAME = "childName";

    private String childUid;
    private String childName;

    private TextView childNameText;
    private LinearLayout moduleButtonContainer;

    private DatabaseReference usersRef;
    private FirebaseUser currentUser;

    public static ProviderChildDetailFragment newInstance(String childUid, String childName) {
        ProviderChildDetailFragment fragment = new ProviderChildDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHILD_UID, childUid);
        args.putString(ARG_CHILD_NAME, childName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_provider_child_detail, container, false);

        childNameText = view.findViewById(R.id.childNameText);
        moduleButtonContainer = view.findViewById(R.id.moduleButtonContainer);

        if (getArguments() != null) {
            childUid = getArguments().getString(ARG_CHILD_UID);
            childName = getArguments().getString(ARG_CHILD_NAME);
        }

        childNameText.setText(childName);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadModules();

        return view;
    }

    private void loadModules() {
        usersRef.child(childUid)
                .child("providerShares")
                .child(currentUser.getUid())
                .get()
                .addOnSuccessListener(snap -> {
                    if (!snap.exists()) return;

                    addModuleIfShared(snap, "symptoms", "Symptoms");
                    addModuleIfShared(snap, "triggers", "Triggers");
                    addModuleIfShared(snap, "peakFlow", "Peak Flow (PEF)");
                    addModuleIfShared(snap, "rescueLogs", "Rescue Logs");
                    addModuleIfShared(snap, "triageIncidents", "Triage Incidents");
                    addModuleIfShared(snap, "controllerSummary", "Controller Summary");
                    addModuleIfShared(snap, "summaryCharts", "Summary Charts");
                });
    }

    private void addModuleIfShared(DataSnapshot snap, String key, String text) {
        Boolean shouldShow = snap.child(key).getValue(Boolean.class);
        if (shouldShow != null && shouldShow) {
            addModuleButton(text, key);
        }
    }

    private void addModuleButton(String label, String key) {
        Context ctx = getContext();

        MaterialCardView card = new MaterialCardView(ctx);
        card.setCardElevation(4);
        card.setStrokeWidth(2);
        card.setStrokeColor(ContextCompat.getColor(ctx, R.color.black));
        card.setRadius(12);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 20);
        card.setLayoutParams(params);

        TextView tv = new TextView(ctx);
        tv.setText(label);
        tv.setTextSize(20);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(30, 30, 30, 30);
        tv.setBackgroundColor(ContextCompat.getColor(ctx, R.color.bg3));

        card.addView(tv);
        moduleButtonContainer.addView(card);

        // CLICK ACTION → Jump to next fragment
        card.setOnClickListener(v -> openModuleFragment(key, label));
    }


    private void openModuleFragment(String key, String title) {
        Fragment nextFragment;

        switch (key) {
            case "symptoms":
                nextFragment = SymptomsDetailFragment.newInstance(childUid);
                break;
            case "triggers":
                nextFragment = TriggersDetailFragment.newInstance(childUid);
                break;
            case "peakFlow":
                nextFragment = PEFDetailFragment.newInstance(childUid);
                break;
            case "triageIncidents":
                nextFragment = TriageDetailFragment.newInstance(childUid);
                break;
            case "rescueLogs":
                nextFragment = RescueLogsDetailFragment.newInstance(childUid);
                break;
            case "controllerSummary":
                nextFragment = ControllerSummaryFragment.newInstance(childUid);
                break;
            case "summaryCharts":
                nextFragment = SummaryChartsFragment.newInstance(childUid);
                break;

            default:
                return;
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, nextFragment)
                .addToBackStack(null)
                .commit();
    }
}
