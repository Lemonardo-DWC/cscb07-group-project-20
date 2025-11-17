package com.example.smartair;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginFragment extends Fragment {

    FirebaseAuth mAuth;

    /// this will be used to access the database
    FirebaseDatabase db;

    /// instances related to editable text fields
    private EditText emailEditText, pwEditText;

    /// log description
    private String TAG = "User Login";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        /// essentially loads the UI for the login fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        /// back button handling
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {

            /// NOTE: back button handling in fragments takes precedence over
            /// the back button handling of the activity when displayed. Useful
            /// if certain screens require different behaviour for back button events
            @Override
            public void handleOnBackPressed() {

                /// back button press on log in screen should exit the app
                /// prevents users from going backwards into home and registration screens
                /// after logging out or successful account creation
                requireActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        /// initializes EditText variables and relate them to the views in the corresponding XML file
        emailEditText = view.findViewById(R.id.login_emailEntry);
        pwEditText = view.findViewById(R.id.login_pwEntry);

        /// initializes Button variables
        Button buttonLogin = view.findViewById(R.id.buttonLogin);
        Button buttonAccountRecovery = view.findViewById(R.id.buttonAccountRecovery);
        Button buttonRegister = view.findViewById(R.id.buttonRegister);

        /// Button behaviours

        // login button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // sets variables to take on user input for their respective input fields
                String email = emailEditText.getText().toString();
                String password = pwEditText.getText().toString();

                login(email, password);// TODO: work in progress
            }
        });

        // account recovery button
        buttonAccountRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: recovery screen stuff
                ((MainActivity) getActivity()).loadFragment(new PlaceholderFragment());
            }
        });

        // registration button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: work in progress
                ((MainActivity) getActivity()).loadFragment(new RegisterFragment());
            }
        });

        return view;
    }

    private void login(String email, String password) {

        Log.d(TAG, "userLogin:" + email); // log action

        if (!validateForm(email, password)) { // entry validation
            return;
        }

        // TODO: login implementation
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");

                        FirebaseUser user = mAuth.getCurrentUser();

                        //Logging in Success, jump to Success Fragment
                        ((MainActivity) getActivity()).loadFragment(new SuccessFragment());

                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(getActivity(),
                                "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /// helper method to check valid inputs for email and password
    /// should match password policy settings in FirebaseAuth:
    /// https://console.firebase.google.com/u/0/project/cscb07-group-project/authentication/settings
    private boolean validateForm(String email, String password) {

        boolean valid = true;

        if(!isValidEmail(email)) {

            //displays error message to user with the editText view
            emailEditText.setError("Invalid email");
            valid = false;
        }

        if(!isValidPassword(password)) {

            //displays error message to user with the editText view
            pwEditText.setError("Password must be at least 6 characters");
            valid = false;
        }

        return valid;
    }

    ///  helper method to check email input validity
    private boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    ///  helper method to check password input validity
    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

}