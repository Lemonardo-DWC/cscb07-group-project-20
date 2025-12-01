package com.example.smartair;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SummaryChartsFragment extends Fragment {

    private static final String ARG_CHILD_UID = "childUid";
    private String childUid;

    private RecyclerView recyclerView;
    private SummaryAdapter adapter;
    private List<SummaryCardModel> summaryList = new ArrayList<>();

    private DatabaseReference usersRef;

    public static SummaryChartsFragment newInstance(String childUid) {
        SummaryChartsFragment fragment = new SummaryChartsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHILD_UID, childUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_summary_charts, container, false);

        recyclerView = view.findViewById(R.id.summaryRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SummaryAdapter(summaryList);
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            childUid = getArguments().getString(ARG_CHILD_UID);
        }

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadSummary();

        return view;
    }

    private void loadSummary() {
        loadSymptoms();
        loadTriggers();
        loadPeakFlow();
        loadTriage();
        loadRescue();
        loadController();
    }

    private void loadSymptoms() {
        usersRef.child(childUid).child("dailyCheckIn").limitToLast(1).get()
                .addOnSuccessListener(snap -> {
                    if (snap.exists()) {
                        for (DataSnapshot entry : snap.getChildren()) {
                            String cough = String.valueOf(entry.child("coughwheeze").getValue());
                            String night = String.valueOf(entry.child("nightwaking").getValue());
                            String limit = String.valueOf(entry.child("activityLimit").getValue());

                            summaryList.add(new SummaryCardModel(
                                    "Symptoms",
                                    "Cough/wheeze: " + cough +
                                            "\nNight waking: " + night +
                                            "\nActivity limit: " + limit
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadTriggers() {
        usersRef.child(childUid).child("dailyCheckIn").limitToLast(1).get()
                .addOnSuccessListener(snap -> {
                    if (snap.exists()) {
                        for (DataSnapshot entry : snap.getChildren()) {

                            if (entry.hasChild("triggers")) {
                                StringBuilder sb = new StringBuilder();

                                for (DataSnapshot t : entry.child("triggers").getChildren()) {
                                    sb.append("• ").append(t.getValue(String.class)).append("\n");
                                }

                                summaryList.add(new SummaryCardModel(
                                        "Triggers",
                                        sb.toString()
                                ));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadPeakFlow() {
        usersRef.child(childUid).child("pefLogs").limitToLast(1).get()
                .addOnSuccessListener(snap -> {
                    if (snap.exists()) {
                        for (DataSnapshot entry : snap.getChildren()) {
                            String pef = String.valueOf(entry.child("pef").getValue());

                            summaryList.add(new SummaryCardModel(
                                    "Peak Flow",
                                    "Latest PEF: " + pef
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadTriage() {
        usersRef.child(childUid).child("triageSessions").limitToLast(1).get()
                .addOnSuccessListener(snap -> {
                    if (snap.exists()) {
                        for (DataSnapshot entry : snap.getChildren()) {

                            String redFlag = String.valueOf(entry.child("red_flag_detected").getValue());
                            String chest = String.valueOf(entry.child("chest_pulling_in").getValue());
                            String speak = String.valueOf(entry.child("speak_full_sentences").getValue());
                            String dizzy = String.valueOf(entry.child("dizzy_scared").getValue());

                            summaryList.add(new SummaryCardModel(
                                    "Triage",
                                    "Red flag: " + redFlag +
                                            "\nChest pulling: " + chest +
                                            "\nSpeak full sentences: " + speak +
                                            "\nDizzy/scared: " + dizzy
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadRescue() {
        usersRef.child(childUid).child("rescueLogs").limitToLast(1).get()
                .addOnSuccessListener(snap -> {
                    if (snap.exists()) {
                        for (DataSnapshot entry : snap.getChildren()) {

                            String dose = String.valueOf(entry.child("dose").getValue());
                            String pre = String.valueOf(entry.child("preBreathRating").getValue());
                            String post = String.valueOf(entry.child("postBreathRating").getValue());

                            summaryList.add(new SummaryCardModel(
                                    "Rescue",
                                    "Dose: " + dose +
                                            "\nPre rating: " + pre +
                                            "\nPost rating: " + post
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadController() {
        usersRef.child(childUid).child("controllerLogs").limitToLast(1).get()
                .addOnSuccessListener(snap -> {
                    if (snap.exists()) {
                        for (DataSnapshot entry : snap.getChildren()) {

                            String dose = String.valueOf(entry.child("dose").getValue());

                            summaryList.add(new SummaryCardModel(
                                    "Controller",
                                    "Dose taken: " + dose
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}

