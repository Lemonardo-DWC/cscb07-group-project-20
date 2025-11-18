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

public class LoginFragment extends Fragment implements LoginView {

    FirebaseAuth mAuth;

    /// this will be used to access the database
    FirebaseDatabase db;

    /// instances related to editable text fields
    private EditText emailEditText, pwEditText;

    /// log description
    private String TAG = "User Login";
    private LoginPresenter presenter;

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
        presenter = new LoginPresenter(this);

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

                presenter.validateInputs(email, password);
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

        if (!validateEmailForm(email) ){ // entry validation
            return;
        }


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.reload(); // refresh verified status

                            if (user.isEmailVerified()) {
                                // Email verified → allow login
                                MainActivity activity = (MainActivity) getActivity();
                                if (activity != null) {
                                    activity.loadFragment(new SuccessFragment());
                                }
                            } else {
                                // Email not verified → force logout
                                mAuth.signOut();
                                Toast.makeText(
                                        getActivity(),
                                        "Please verify your email before logging in.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }


                        }
                    }
                    else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(getActivity(),
                                "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /// helper method to check valid inputs for email
    /// https://console.firebase.google.com/u/0/project/cscb07-group-project/authentication/settings

    private boolean validateEmailForm(String email) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email");
            return false;
        }
        return true;
    }

    @Override
    public void showEmailError(String msg) {
        emailEditText.setError(msg);
    }

    @Override
    public void showPasswordError(String msg) {
        pwEditText.setError(msg);
    }

    @Override
    public void loginSuccess() {
        login(emailEditText.getText().toString(),
                pwEditText.getText().toString());
    }
}