package com.example.smartair;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecoveryFragment extends Fragment {

    private EditText emailEntry;
    private Button sendButton;

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recovery, container, false);

        emailEntry = view.findViewById(R.id.recovery_emailEntry);
        sendButton = view.findViewById(R.id.buttonSendRecovery);

        // 初始化 Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        sendButton.setOnClickListener(v -> sendPasswordReset());

        return view;
    }

    private void sendPasswordReset() {
        String email = emailEntry.getText().toString().trim();

        if (email.isEmpty()) {
            emailEntry.setError("Email cannot be empty");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEntry.setError("Invalid email");
            return;
        }

        //Firebase Send reset Link
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Toast.makeText(getContext(),
                                "If this email is registered, a reset link has been sent. Please check your inbox and spam folder.",
                                Toast.LENGTH_LONG).show();

                        // back to Login Fragment
                        requireActivity().getSupportFragmentManager().popBackStack();

                    }
                    else {
                        Toast.makeText(getContext(),
                                "Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}