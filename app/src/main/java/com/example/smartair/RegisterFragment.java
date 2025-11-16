package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterFragment extends Fragment {

    FirebaseDatabase db;
    FirebaseAuth mAuth;
    private final String TAG = "User Registration";

    private EditText emailEditText, pwEditText, pwConfirmEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_register, container, false);

        /// Spinner variables and behaviour
        Spinner accountTypeSpinner = view.findViewById(R.id.register_accountTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.register_accountTypeSpinner,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountTypeSpinner.setAdapter(adapter);

        /// EditText variables
        emailEditText = view.findViewById(R.id.login_emailEntry);
        pwEditText = view.findViewById(R.id.login_pwEntry);
        pwConfirmEditText = view.findViewById(R.id.login_pwEntryConfirm);

        /// Button variables
        Button buttonRegister = view.findViewById(R.id.buttonRegister);

        /// Button behaviour
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String pw = pwEditText.getText().toString();
                String pwConfirmation = pwConfirmEditText.getText().toString();

                createAccount(email, pw, pwConfirmation);
            }
        });

        return view;
    }

    /// Helper Functions ///
    private void createAccount(String email, String password, String pwConfirmation) {

        Log.d(TAG, "createAccount:" + email); // log action

        if (!validateForm(email, password, pwConfirmation)) { // entry validation
            return;
        }

        // user creation, automatically logs user in on success
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {

                    // completion behaviour
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // successfully creates user

                            Log.d(TAG, "createUserWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser(); // instantiates user

                            if (user != null) { // if user is non-null, send email verification
                                sendEmailVerification(user);
                                mAuth.signOut(); // signs user out with intent of having them verify their email first before logging in
                            }

                            // redirect to login screen
                            ((MainActivity) getActivity()).loadFragment(new LoginFragment());

                        } else { // failed to create user
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(
                                    getContext(),
                                    "Could not create account",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    }
                });
    }


    /// helper methods to determine entry validity
    /// NOTE: restrictions must match FirebaseAuth password policy
    /// https://console.firebase.google.com/u/0/project/cscb07-group-project/authentication/settings
    private boolean validateForm(String email, String password, String pwConfirmation) {

        boolean valid = true;

        if(!isValidEmail(email)) {
            emailEditText.setError("Invalid email");
            valid = false;
        }

        if(!isValidPassword(password)) {
            pwEditText.setError("Password must be at least 6 characters");
            valid = false;
        }

        if(!isValidPasswordConfirmation(password, pwConfirmation)) {
            pwConfirmEditText.setError("Passwords do not match");
            valid = false;
        }

        return valid;
    }

    private boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private boolean isValidPasswordConfirmation(String password, String pwConfirmation) {
        return password.equals(pwConfirmation);
    }

    // method to send email verification
    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(
                                        getContext(),
                                        "Verification email sent to: " + user.getEmail(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                Toast.makeText(
                                        getContext(),
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );

    }

}