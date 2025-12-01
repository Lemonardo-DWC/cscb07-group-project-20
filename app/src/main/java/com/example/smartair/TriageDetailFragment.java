package com.example.smartair;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TriageDetailFragment extends Fragment {

    private static final String ARG_CHILD_UID = "childUid";

    private String childUid;
    private LinearLayout triageContainer;

    private DatabaseReference usersRef;

    public static TriageDetailFragment newInstance(String childUid) {
        TriageDetailFragment fragment = new TriageDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHILD_UID, childUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_triage_detail, container, false);

        triageContainer = view.findViewById(R.id.triageContainer);

        if (getArguments() != null) {
            childUid = getArguments().getString(ARG_CHILD_UID);
        }

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadTriageIncidents();

        return view;
    }

    private void loadTriageIncidents() {
        usersRef.child(childUid).child("triageSessions").get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.exists()) {
                        addCard("No triage incidents recorded.");
                        return;
                    }

                    for (DataSnapshot entry : snapshot.getChildren()) {

                        String timestamp = String.valueOf(entry.child("SymptomCheckTimestamp").getValue());
                        String blueLips = String.valueOf(entry.child("blue_lips_nails").getValue());
                        String chestPulling = String.valueOf(entry.child("chest_pulling_in").getValue());
                        String dizzy = String.valueOf(entry.child("dizzy_scared").getValue());
                        String redFlag = String.valueOf(entry.child("red_flag_detected").getValue());
                        String speakFull = String.valueOf(entry.child("speak_full_sentences").getValue());
                        String usedRescue = String.valueOf(entry.child("used_rescue_meds").getValue());

                        String text =
                                "Red flag detected: " + redFlag +
                                        "\nDizzy / Scared: " + dizzy +
                                        "\nChest pulling: " + chestPulling +
                                        "\nSpeak full sentences: " + speakFull +
                                        "\nBlue lips/nails: " + blueLips +
                                        "\nUsed rescue meds: " + usedRescue +
                                        "\nTimestamp: " + timestamp;

                        addCard(text);
                    }
                });
    }

    private void addCard(String content) {
        Context ctx = getContext();

        MaterialCardView card = new MaterialCardView(ctx);
        card.setCardElevation(4);
        card.setStrokeWidth(2);
        card.setStrokeColor(ContextCompat.getColor(ctx, R.color.black));
        card.setRadius(12);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 20);

        card.setLayoutParams(params);

        TextView tv = new TextView(ctx);
        tv.setPadding(30, 30, 30, 30);
        tv.setTextSize(18);
        tv.setTextColor(ContextCompat.getColor(ctx, R.color.black));
        tv.setBackgroundColor(ContextCompat.getColor(ctx, R.color.bg3));
        tv.setText(content);

        card.addView(tv);
        triageContainer.addView(card);
    }
}
