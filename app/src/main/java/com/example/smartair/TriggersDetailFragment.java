package com.example.smartair;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import com.google.android.material.card.MaterialCardView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TriggersDetailFragment extends Fragment {

    private static final String ARG_CHILD_UID = "childUid";

    private String childUid;
    private LinearLayout triggersContainer;

    private DatabaseReference usersRef;

    public static TriggersDetailFragment newInstance(String childUid) {
        TriggersDetailFragment fragment = new TriggersDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHILD_UID, childUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_triggers_detail, container, false);

        triggersContainer = view.findViewById(R.id.triggersContainer);

        if (getArguments() != null) {
            childUid = getArguments().getString(ARG_CHILD_UID);
        }

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadTriggers();

        return view;
    }

    private void loadTriggers() {
        usersRef.child(childUid).child("DailyCheckIn").get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.exists()) {
                        addCard("No triggers recorded.");
                        return;
                    }

                    for (DataSnapshot entry : snapshot.getChildren()) {

                        if (entry.hasChild("triggers")) {
                            StringBuilder sb = new StringBuilder();

                            for (DataSnapshot t : entry.child("triggers").getChildren()) {
                                sb.append("• ").append(t.getValue(String.class)).append("\n");
                            }

                            addCard(sb.toString());
                        }
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
        triggersContainer.addView(card);
    }
}
