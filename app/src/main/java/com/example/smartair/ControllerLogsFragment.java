package com.example.smartair;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ControllerLogsFragment extends Fragment {

    private String childId;

    public ControllerLogsFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            childId = getArguments().getString("childId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controller_logs, container, false);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CheckBox checkBox = view.findViewById(R.id.checkBox);
        EditText editTextNumber = view.findViewById(R.id.editTextNumber);
        Button buttonSave = view.findViewById(R.id.button2);

        editTextNumber.setText("1");
        childId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference logRef = db.getReference("users").child(childId).child("controllerLogs");


        buttonSave.setOnClickListener(v -> {

            boolean took = checkBox.isChecked();
            String doseTxt = editTextNumber.getText().toString().trim();

            int dose = 1;

            if (!took) {
                Toast.makeText(getContext(), "Please take your controller medicine first.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                dose = Integer.parseInt(doseTxt);
            } catch (Exception e) {
                editTextNumber.setError("Enter a number");
                return;
            }

            Map<String, Object> log = new HashMap<>();
            log.put("dose", dose);
            log.put("timestamp", System.currentTimeMillis());

            logRef.push().setValue(log)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}

