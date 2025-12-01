package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RescueLogsFragment extends Fragment {

    private String childId;

    public RescueLogsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            childId = getArguments().getString("childId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rescue_logs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayList<RescueLogs> logs = new ArrayList<>();
        RescueLogsAdapter adapter = new RescueLogsAdapter(logs);
        recyclerView.setAdapter(adapter);

        DatabaseReference logRef1 = FirebaseDatabase
                .getInstance()
                .getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("rescueLogs");

        logRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                logs.clear();

                for (DataSnapshot s : snapshot.getChildren()) {
                    RescueLogs log = s.getValue(RescueLogs.class);
                    if (log != null) logs.add(log);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        EditText editTextNumber = view.findViewById(R.id.editTextNumber);
        Button buttonSave = view.findViewById(R.id.button2);
        RadioGroup groupBef = view.findViewById(R.id.preBreathGroup);
        RadioGroup groupAft = view.findViewById(R.id.aftBreathGroup);
        RadioGroup groupFeel= view.findViewById(R.id.postStatusGroup);

        editTextNumber.setText("1");
        childId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference logRef = db.getReference("users").child(childId).child("rescueLogs");

        buttonSave.setOnClickListener(v -> {

            String doseTxt = editTextNumber.getText().toString().trim();
            TextView text1=view.findViewById(R.id.textView2);
            int num1 = groupBef.getCheckedRadioButtonId();
            if (num1 == -1) {
                text1.setError("Please select an option.");
                return;
            }
            RadioButton selectedButton1 = view.findViewById(num1);
            String selectedString1 = selectedButton1.getText().toString();
            int selectedInt1 = Integer.parseInt(selectedString1);
            TextView text2=view.findViewById(R.id.textView3);
            int num2 = groupAft.getCheckedRadioButtonId();
            if (num2 == -1) {
                text2.setError("Please select an option.");
                return;
            }
            RadioButton selectedButton2 = view.findViewById(num2);
            String selectedString2 = selectedButton2.getText().toString();
            int selectedInt2 = Integer.parseInt(selectedString2);
            TextView text3=view.findViewById(R.id.postStatusLabel);
            int num3 = groupFeel.getCheckedRadioButtonId();
            if (num3 == -1) {
                text3.setError("Please select an option.");
                return;
            }
            RadioButton selectedButton3 = view.findViewById(num3);
            String selectedString3 = selectedButton3.getText().toString();
            int selectedInt3 = 0;
            int dose = 1;
            try {
                dose = Integer.parseInt(doseTxt);
            } catch (Exception e) {
                editTextNumber.setError("Enter a number");
                return;
            }
            if (dose<=0) {
                editTextNumber.setError("Puffs must be at least 1");
                return;
            }
            switch (selectedString3) {
                case "Worse":
                    selectedInt3 = -1;
                    break;
                case "Better":
                    selectedInt3 = 1;
                    break;
            }


            Map<String, Object> log = new HashMap<>();
            log.put("dose", dose);
            log.put("preBreathRating", selectedInt1);
            log.put("postBreathRating", selectedInt2);
            log.put("postStatus", selectedInt3);
            log.put("timestamp", System.currentTimeMillis());

            logRef.push().setValue(log)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}