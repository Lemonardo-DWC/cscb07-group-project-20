package com.example.smartair;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;

public class SymptomCheckFragment extends Fragment {

    private RadioGroup q1, q2, q3, q4, q5;
    private Button btnCheckPEF, btnSkipPEF;
    private DatabaseReference childRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_symptom_check, container, false);

        // Firebase path: /children/{uid}/triageSessions
//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        childRef = FirebaseDatabase.getInstance().getReference("children").child(uid);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid;

//        if (user == null) {
//            uid = "testUser123";  // test
//            Toast.makeText(getContext(), "Using TEST USER (not logged in)", Toast.LENGTH_SHORT).show();
//        } else {
            uid = user.getUid();
//        }

        childRef = FirebaseDatabase.getInstance().getReference("children").child(uid);

        //==========

        q1 = view.findViewById(R.id.q1_group);
        q2 = view.findViewById(R.id.q2_group);
        q3 = view.findViewById(R.id.q3_group);
        q4 = view.findViewById(R.id.q4_group);
        q5 = view.findViewById(R.id.q5_group);

        btnCheckPEF = view.findViewById(R.id.btn_check_pef);
        btnSkipPEF = view.findViewById(R.id.btn_skip_pef);

        btnCheckPEF.setOnClickListener(v -> {
            String key = saveSymptomData();
            goToPEF(key);
        });
        btnSkipPEF.setOnClickListener(v -> {
            boolean redFlag = checkRedFlags();
            saveSymptomData();
            goToDecision(redFlag);
        });

        return view;
    }

    private boolean getYes(RadioGroup group) {
        int id = group.getCheckedRadioButtonId();
        if (id == -1) return false;
        RadioButton rb = group.findViewById(id);
        return rb.getText().toString().equals("Yes");
    }

    // Red Flags: Q1 no, Q2 yes, Q3 yes
    private boolean checkRedFlags() {
        boolean q1Danger = !getYes(q1);  // Q1: NO means danger
        boolean q2Danger = getYes(q2);
        boolean q3Danger = getYes(q3);

        return q1Danger || q2Danger || q3Danger;
    }


    private String saveSymptomData() {
        String key = childRef.child("triageSessions").push().getKey();

        Map<String, Object> map = new HashMap<>();
        map.put("speak_full_sentences", getYes(q1));
        map.put("chest_pulling_in", getYes(q2));
        map.put("blue_lips_nails", getYes(q3));
        map.put("used_rescue_meds", getYes(q4));
        map.put("dizzy_scared", getYes(q5));
        //time
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String timeString = now.format(formatter);

        map.put("timestamp", timeString);
        map.put("red_flag_detected", checkRedFlags());

        childRef.child("triageSessions").child(key).setValue(map);
        return key;
    }

    private void goToPEF(String sessionKey) {
        Fragment fragment = new ChildPEFFragment();
        Bundle b = new Bundle();
        b.putString("SESSION_KEY", sessionKey);
        fragment.setArguments(b);

        ((MainActivity) requireActivity()).loadFragment(fragment);
    }


    private void goToDecision(boolean redFlag) {
        Fragment fragment = new DecisionFragment();
        Bundle b = new Bundle();
        b.putBoolean("RED_FLAG", redFlag);
        fragment.setArguments(b);

        ((MainActivity) requireActivity()).loadFragment(fragment);
    }
}