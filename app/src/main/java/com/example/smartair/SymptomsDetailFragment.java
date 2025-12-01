package com.example.smartair;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SymptomsDetailFragment extends Fragment {

    private static final String ARG_CHILD_UID = "childUid";
    private String childUid;

    private LinearLayout symptomsContainer;

    private DatabaseReference usersRef;

    public static SymptomsDetailFragment newInstance(String childUid) {
        SymptomsDetailFragment fragment = new SymptomsDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHILD_UID, childUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_symptoms_detail, container, false);
        symptomsContainer = view.findViewById(R.id.symptomsContainer);

        if (getArguments() != null) {
            childUid = getArguments().getString(ARG_CHILD_UID);
        }

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadSymptoms();
        return view;
    }

    private void loadSymptoms() {
        usersRef.child(childUid).child("DailyCheckIn").get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.exists()) {
                        addCard("No symptoms logged.");
                        return;
                    }

                    for (DataSnapshot entry : snapshot.getChildren()) {

                        String cough = String.valueOf(entry.child("coughWheeze").getValue());
                        String night = String.valueOf(entry.child("nightWaking").getValue());
                        String limit = String.valueOf(entry.child("activityLimit").getValue());
                        String rawTimestamp = String.valueOf(entry.child("timestamp").getValue());
                        String timestamp = formatTimestamp(rawTimestamp);

                        String text =
                                "Cough/Wheeze: " + cough +
                                        "\nNight waking: " + night +
                                        "\nActivity Limit: " + limit +
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
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
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
        symptomsContainer.addView(card);
    }

    private String formatTimestamp(String millisString) {
        try {
            long millis = Long.parseLong(millisString);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
            java.util.Date date = new java.util.Date(millis);
            return sdf.format(date);
        } catch (Exception e) {
            return millisString;
        }
    }
}

