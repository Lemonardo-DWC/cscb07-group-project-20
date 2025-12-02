package com.example.smartair;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ChildHomeFragment extends Fragment {


    private String childId;


    public ChildHomeFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_child_home, container, false);

    }

    private void loadPBAndUpdateZone(DatabaseReference childRef,
                                     TextView homeZoneLabel,
                                     View homeZoneColor) {

        childRef.child("pb").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotPB) {

                if (!snapshotPB.exists()) return;

                double personalBest = snapshotPB.getValue(Double.class);

                childRef.child("pefLogs")
                        .limitToLast(1)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot log : snapshot.getChildren()) {

                                    Integer pef = log.child("pef").getValue(Integer.class);
                                    if (pef == null || personalBest <= 0) return;

                                    updateZone(pef, personalBest, homeZoneLabel, homeZoneColor);
                                }
                            }

                            @Override public void onCancelled(@NonNull DatabaseError error) {}
                        });
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateZone(int pef, double personalBest,
                            TextView homeZoneLabel, View homeZoneColor) {

        double percent = (pef / personalBest) * 100;
        String zone;

        if (percent >= 80) {
            zone = "Green Zone (≥80%)";
            homeZoneColor.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else if (percent >= 50) {
            zone = "Yellow Zone (50–79%)";
            homeZoneColor.setBackgroundColor(Color.parseColor("#FFC107"));
        } else {
            zone = "Red Zone (<50%)";
            homeZoneColor.setBackgroundColor(Color.parseColor("#F44336"));
        }

        homeZoneLabel.setText("Zone: " + zone);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button controllerLogs = view.findViewById(R.id.buttonControllerLogs);
        Button rescueLogs = view.findViewById(R.id.button3);
        Button dailyCheck = view.findViewById(R.id.button1);
        Button SymptomCheckFragment = view.findViewById(R.id.button4);
        Button ChildPEFFragment = view.findViewById(R.id.button5);
        Button rewardButton = view.findViewById(R.id.button6);
        Button TechniqueHelper = view.findViewById(R.id.button7);
        Button logout = view.findViewById(R.id.button8);

        TextView homeZoneLabel = view.findViewById(R.id.zoneLabel);
        View homeZoneColor = view.findViewById(R.id.zoneColor);

        Button btnRescueLow = view.findViewById(R.id.btnRescueLow);
        Button btnControllerLow = view.findViewById(R.id.btnControllerLow);


        childId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference logRef = db.getReference("users").child(childId);
        DatabaseReference firstLoginRef = logRef.child("firstLogin");

        firstLoginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Boolean firstLogin = snapshot.getValue(Boolean.class);

                // If ture or null → jump to Onboarding page
                if (firstLogin == null || firstLogin) {
                    // 跳到 Onboarding Fragment
                    ((MainActivity) requireActivity()).loadFragment(new OnboardingChildFragment());

                    return;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        loadPBAndUpdateZone(logRef, homeZoneLabel, homeZoneColor);

        //  Rescue Listener
        logRef.child("rescue").child("low")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            btnRescueLow.setText("Mark Rescue Canister Low");
                            btnRescueLow.setEnabled(true);
                            btnRescueLow.setBackgroundTintList(
                                    ContextCompat.getColorStateList(requireContext(), R.color.bg5)
                            );
                            return;
                        }

                        Boolean isLow = snapshot.getValue(Boolean.class);

                        if (Boolean.TRUE.equals(isLow)) {
                            btnRescueLow.setText("Rescue Canister Marked");
                            btnRescueLow.setEnabled(false);
                            btnRescueLow.setBackgroundTintList(
                                    ContextCompat.getColorStateList(requireContext(), R.color.neutral_grey)
                            );
                        } else {
                            btnRescueLow.setText("Mark Rescue Canister Low");
                            btnRescueLow.setEnabled(true);
                            btnRescueLow.setBackgroundTintList(
                                    ContextCompat.getColorStateList(requireContext(), R.color.bg5)
                            );
                        }
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });


//  Controller Listener
        logRef.child("controller").child("low")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            btnControllerLow.setText("Mark Controller Canister Low");
                            btnControllerLow.setEnabled(true);
                            btnControllerLow.setBackgroundTintList(
                                    ContextCompat.getColorStateList(requireContext(), R.color.bg5)
                            );
                            return;
                        }

                        Boolean isLow = snapshot.getValue(Boolean.class);

                        if (Boolean.TRUE.equals(isLow)) {
                            btnControllerLow.setText("Controller Canister Marked");
                            btnControllerLow.setEnabled(false);
                            btnControllerLow.setBackgroundTintList(
                                    ContextCompat.getColorStateList(requireContext(), R.color.neutral_grey)
                            );
                        } else {
                            btnControllerLow.setText("Mark Controller Canister Low");
                            btnControllerLow.setEnabled(true);
                            btnControllerLow.setBackgroundTintList(
                                    ContextCompat.getColorStateList(requireContext(), R.color.bg5)
                            );
                        }
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });


// press button to mark canister low

        btnRescueLow.setOnClickListener(v -> {
            logRef.child("rescue").child("low").setValue(true);
            Toast.makeText(requireContext(), "Rescue canister marked low", Toast.LENGTH_SHORT).show();
        });

        btnControllerLow.setOnClickListener(v -> {
            logRef.child("controller").child("low").setValue(true);
            Toast.makeText(requireContext(), "Controller canister marked low", Toast.LENGTH_SHORT).show();
        });



        controllerLogs.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new ControllerLogsFragment());
        });
        rescueLogs.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new RescueLogsFragment());
        });
        dailyCheck.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new DailyCheckinHistoryFragment());
        });
        TechniqueHelper.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new TechniqueHelperFragment());
        });


        SymptomCheckFragment.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putString("CHILD_ID", childId);
            SymptomCheckFragment next = new SymptomCheckFragment();
            next.setArguments(b);
            ((MainActivity) requireActivity()).loadFragment(next);
        });

        ChildPEFFragment.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putString("CHILD_ID", childId);
            b.putBoolean("FROM_SYMPTOM_CHECK", false);
            ChildPEFFragment next = new ChildPEFFragment();
            next.setArguments(b);
            ((MainActivity) requireActivity()).loadFragment(next);
        });

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            ((MainActivity) requireActivity()).loadFragment(new LoginFragment());
        });
        //RewardsFragment
        rewardButton.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putString("CHILD_ID", childId);  // 如果奖励页面需要 childId
            RewardsFragment next = new RewardsFragment();
            next.setArguments(b);

            ((MainActivity) requireActivity()).loadFragment(next);
        });

        /// back button handling ///
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {

            /// home screen is navigation root after logging in, thus back button should send user
            /// out of app
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        };
        requireActivity()
                .getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


    }

}
