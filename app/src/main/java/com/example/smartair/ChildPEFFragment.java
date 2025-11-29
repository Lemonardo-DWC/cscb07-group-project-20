package com.example.smartair;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChildPEFFragment extends Fragment {

    private EditText inputPEF;
    private EditText inputPreMedPEF;
    private EditText inputPostMedPEF;
    private Button submitBtn;
    private TextView zoneLabel;
    private TextView personalBestLabel;
    private View zoneColor;
    private CheckBox togglePrePost;
    private LinearLayout prePostContainer;
    private DatabaseReference childRef;
    private double personalBest = -1;
    private final double defaultPB = 300;

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_child_pef, container, false);

        // findViewById
        inputPEF = rootView.findViewById(R.id.inputPEF);
        inputPreMedPEF = rootView.findViewById(R.id.inputPreMedPEF);
        inputPostMedPEF = rootView.findViewById(R.id.inputPostMedPEF);
        togglePrePost = rootView.findViewById(R.id.togglePrePost);
        prePostContainer = rootView.findViewById(R.id.prePostContainer);
        submitBtn = rootView.findViewById(R.id.submitPEF);
        zoneLabel = rootView.findViewById(R.id.zoneLabel);
        zoneColor = rootView.findViewById(R.id.zoneColor);
        personalBestLabel = rootView.findViewById(R.id.personalBestLabel);

        Bundle args = getArguments();
        String childId = null;
        if (args != null) {
            childId = args.getString("CHILD_ID");
        }
        //parent
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String parentUid = null;
        if (user != null) {
            parentUid = user.getUid();
        }

        childRef = null;
        if (parentUid != null && childId != null) {
            childRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(parentUid);
        }

        loadPersonalBest();

        togglePrePost.setOnCheckedChangeListener(
                (btn, checked) -> prePostContainer.setVisibility(checked ? View.VISIBLE : View.GONE)
        );

        submitBtn.setOnClickListener(v -> handlePEFSubmit());

        return rootView;
    }

    private void loadPersonalBest() {
        childRef.child("personalBest").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            personalBest = snapshot.getValue(Double.class);
                        } else {
                            personalBest = defaultPB;
                            Toast.makeText(getContext(),
                                    "Parent has not set PB. Using default PB = " + defaultPB,
                                    Toast.LENGTH_LONG).show();
                        }

                        personalBestLabel.setText("PB: " + personalBest);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                }
        );
    }

    private void handlePEFSubmit() {

        if (personalBest <= 0) {
            Toast.makeText(getContext(), "PB missing. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String input = inputPEF.getText().toString().trim();
        if (input.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a PEF value.", Toast.LENGTH_SHORT).show();
            return;
        }

        int pef = Integer.parseInt(input);
        double percent = (pef / personalBest) * 100;

        String zone;

        if (percent >= 80) {
            zone = "Green Zone (≥80%)";
            zoneColor.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else if (percent >= 50) {
            zone = "Yellow Zone (50–79%)";
            zoneColor.setBackgroundColor(Color.parseColor("#FFC107"));
        } else {
            zone = "Red Zone (<50%)";
            zoneColor.setBackgroundColor(Color.parseColor("#F44336"));
        }

        zoneLabel.setText("Zone: " + zone);

        String logId = childRef.child("pefLogs").push().getKey();

        Map<String, Object> logData = new HashMap<>();
        logData.put("pef", pef);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        logData.put("timestamp", now.format(formatter));

        if (togglePrePost.isChecked()) {
            String pre = inputPreMedPEF.getText().toString().trim();
            if (!pre.isEmpty()) logData.put("preMedPEF", Integer.parseInt(pre));

            String post = inputPostMedPEF.getText().toString().trim();
            if (!post.isEmpty()) logData.put("postMedPEF", Integer.parseInt(post));
        }

        //childRef.child("pefLogs").child(logId).setValue(logData);
        childRef.child("pefLogs").child(logId).setValue(logData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "PEF saved!", Toast.LENGTH_SHORT).show();
                        // If from SymptomCheckFragment
                        Bundle args = getArguments();
                        boolean fromSymptomCheck = args != null && args.getBoolean("FROM_SYMPTOM_CHECK", false);
                        if (fromSymptomCheck) {
                            goToDecision(args);
                        }
                    }
                    else {
                        Toast.makeText(getContext(), "Failed to save PEF.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void goToDecision(Bundle args) {
        Fragment fragment = new DecisionFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        ((MainActivity) requireActivity()).loadFragment(fragment);
    }


}
