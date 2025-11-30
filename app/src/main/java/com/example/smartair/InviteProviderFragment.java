package com.example.smartair;

import android.app.DownloadManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class InviteProviderFragment extends Fragment {

    private EditText providerEmailEditText;
    private CheckBox checkRescue, checkController, checkSymptoms, checkTriggers, checkPEF, checkTriage, checkCharts;
    private Button generateInviteButton;
    private TextView inviteCodeTextView;

    private String childId;
    private DatabaseReference databaseRef;
    private FirebaseFunctions functions;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invite_provider, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        providerEmailEditText = view.findViewById(R.id.providerEmailEditText);
        checkRescue = view.findViewById(R.id.checkRescue);
        checkController = view.findViewById(R.id.checkController);
        checkSymptoms = view.findViewById(R.id.checkSymptoms);
        checkTriggers = view.findViewById(R.id.checkTriggers);
        checkPEF = view.findViewById(R.id.checkPEF);
        checkTriage = view.findViewById(R.id.checkTriage);
        checkCharts = view.findViewById(R.id.checkCharts);
        generateInviteButton = view.findViewById(R.id.generateInviteButton);
        inviteCodeTextView = view.findViewById(R.id.inviteCodeTextView);

        if (getArguments() != null) {
            childId = getArguments().getString("childId");
        }

        databaseRef = FirebaseDatabase.getInstance().getReference();
        functions = FirebaseFunctions.getInstance(FirebaseApp.getInstance());

        generateInviteButton.setOnClickListener(v -> sendInviteCode());
    }

    private void sendInviteCode() {
        String email = providerEmailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Please enter provider email", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        user.getIdToken(true).addOnSuccessListener(tokenResult -> {
            String inviteCode = UUID.randomUUID().toString();

            Map<String, Object> sharedData = new HashMap<>();
            sharedData.put("rescueLogs", checkRescue.isChecked());
            sharedData.put("controllerSummary", checkController.isChecked());
            sharedData.put("symptoms", checkSymptoms.isChecked());
            sharedData.put("triggers", checkTriggers.isChecked());
            sharedData.put("peakFlow", checkPEF.isChecked());
            sharedData.put("triageIncidents", checkTriage.isChecked());
            sharedData.put("summaryCharts", checkCharts.isChecked());

            Map<String, Object> inviteObject = new HashMap<>();
            inviteObject.put("childId", childId);
            inviteObject.put("providerEmail", email);
            inviteObject.put("sharedData", sharedData);
            inviteObject.put("createdAt", System.currentTimeMillis());

            // Step 1: Write to /invites/{code}
            databaseRef.child("invites").child(inviteCode)
                    .setValue(inviteObject)
                    .addOnSuccessListener(unused -> {
                        // Step 2: Trigger Cloud Function
                        Map<String, Object> data = new HashMap<>();
                        data.put("email", email);
                        data.put("inviteCode", inviteCode);

                        String url = "https://us-central1-cscb07-group-project.cloudfunctions.net/sendProviderInvite";

                        JSONObject json = new JSONObject();
                        try {
                            json.put("email", email);
                            json.put("inviteCode", inviteCode);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        RequestQueue queue = Volley.newRequestQueue(requireContext());

                        JsonObjectRequest request = new JsonObjectRequest(
                                Request.Method.POST,
                                url,
                                json,
                                response -> {
                                    inviteCodeTextView.setText("Invite code sent to: " + email + "\nCode: " + inviteCode);
                                    Toast.makeText(getContext(), "Invitation sent!", Toast.LENGTH_SHORT).show();
                                },
                                error -> {
                                    Toast.makeText(getContext(), "Email failed: " + error.toString(), Toast.LENGTH_SHORT).show();
                                }
                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                return headers;
                            }
                        };

                        queue.add(request);

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Auth token refresh failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
