package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DailyCheckFragment extends Fragment {
    private String childId;

    public DailyCheckFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dailycheck, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonSave = view.findViewById(R.id.saveButton);
        RadioGroup group1 = view.findViewById(R.id.nightWakingGroup);
        RadioGroup group2 = view.findViewById(R.id.activityLimitGroup);
        RadioGroup group3= view.findViewById(R.id.coughWheezeGroup);
        CheckBox exercise = view.findViewById(R.id.triggerExercise);
        CheckBox coldAir = view.findViewById(R.id.triggerColdAir);
        CheckBox dustPets = view.findViewById(R.id.triggerDustPets);
        CheckBox smoke = view.findViewById(R.id.triggerSmoke);
        CheckBox illness = view.findViewById(R.id.triggerIllness);
        CheckBox odors = view.findViewById(R.id.triggerOdors);

        List<String> triggers = new ArrayList<>();

        childId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference logRef = db.getReference("users").child(childId).child("rescueLogs");

        buttonSave.setOnClickListener(v -> {


            int num1 = group1.getCheckedRadioButtonId();
            int num2 = group2.getCheckedRadioButtonId();
            int num3 = group3.getCheckedRadioButtonId();
            if (num1 == -1 || num2 == -1 || num3 == -1) {
                Toast.makeText(getContext(),
                        "Please answer all required questions.",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            RadioButton selectedButton1 = view.findViewById(num1);
            String selectedString1 = selectedButton1.getText().toString();


            RadioButton selectedButton2 = view.findViewById(num2);
            String selectedString2 = selectedButton2.getText().toString();


            RadioButton selectedButton3 = view.findViewById(num3);
            String selectedString3 = selectedButton3.getText().toString();



            if (exercise.isChecked()) triggers.add("Exercise");
            if (coldAir.isChecked()) triggers.add("Cold air");
            if (dustPets.isChecked()) triggers.add("Dust / Pets");
            if (smoke.isChecked()) triggers.add("Smoke");
            if (illness.isChecked()) triggers.add("Illness");
            if (odors.isChecked()) triggers.add("Perfume / Cleaners / Strong odors");

            Map<String, Object> data = new HashMap<>();
            data.put("nightWaking", selectedString1);
            data.put("activityLimit", selectedString2);
            data.put("coughWheeze", selectedString3);
            data.put("triggers", triggers);
            data.put("author", "Child");
            data.put("timestamp", System.currentTimeMillis());

            // --- 5. Upload to Firebase Realtime Database ---
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("dailyCheckIn")
                    .child(childId)
                    .push();

            ref.setValue(data)
                    .addOnSuccessListener(a -> {
                        Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to save.", Toast.LENGTH_SHORT).show();
                    });

        });

    }
}