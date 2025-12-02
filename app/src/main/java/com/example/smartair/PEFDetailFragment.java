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

public class PEFDetailFragment extends Fragment {

    private static final String ARG_CHILD_UID = "childUid";

    private String childUid;
    private LinearLayout pefContainer;

    private DatabaseReference usersRef;

    public static PEFDetailFragment newInstance(String childUid) {
        PEFDetailFragment fragment = new PEFDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHILD_UID, childUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pef_detail, container, false);

        pefContainer = view.findViewById(R.id.pefContainer);

        if (getArguments() != null) {
            childUid = getArguments().getString(ARG_CHILD_UID);
        }

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadPEFLogs();

        return view;
    }

    private void loadPEFLogs() {
        usersRef.child(childUid).child("pefLogs").get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.exists()) {
                        addCard("No PEF logs recorded.");
                        return;
                    }

                    for (DataSnapshot entry : snapshot.getChildren()) {

                        String pefValue = String.valueOf(entry.child("pef").getValue());
                        String timestamp = String.valueOf(entry.child("timestamp").getValue());

                        String text =
                                "PEF: " + pefValue +
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
        pefContainer.addView(card);
    }
}
