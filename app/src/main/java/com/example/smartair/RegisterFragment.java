package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterFragment extends Fragment {

    UserManager userManager;
    RegisterViewModel rvm;

    private EditText emailEditText, pwEditText, pwConfirmEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userManager = new UserManager();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_register, container, false);

        rvm = new ViewModelProvider(this).get(RegisterViewModel.class);

        /// Spinner variables and behaviour
        Spinner accountTypeSpinner = view.findViewById(R.id.register_accountTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.register_accountTypeSpinner,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountTypeSpinner.setAdapter(adapter);

        /// EditText variables
        emailEditText = view.findViewById(R.id.register_emailEntry);
        pwEditText = view.findViewById(R.id.register_pwEntry);
        pwConfirmEditText = view.findViewById(R.id.register_pwEntryConfirm);

        /// Button variables
        Button buttonRegister = view.findViewById(R.id.buttonRegister);

        /// Button behaviour
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String pw = pwEditText.getText().toString();
                String pwConfirmation = pwConfirmEditText.getText().toString();

                rvm.register(email, pw, pwConfirmation);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /// Monitor registration form inputs
        // input validity
        rvm.emailError.observe(getViewLifecycleOwner(), msg -> {
            emailEditText.setError(msg);
        });

        rvm.passwordError.observe(getViewLifecycleOwner(), msg -> {
            pwEditText.setError(msg);
        });

        rvm.passwordConfirmationError.observe(getViewLifecycleOwner(), msg -> {
            pwConfirmEditText.setError(msg);
        });

        // account creation
        rvm.registerResult.observe(getViewLifecycleOwner(), result -> {
            if (result.equals(AppConstants.SUCCESS)) {
                ((MainActivity) requireActivity()).loadFragment(new LoginFragment());
            } else {
                Toast.makeText(
                        getContext(),
                        "Could not create account",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // send email verification
        rvm.sendEmailVerificationResult.observe(getViewLifecycleOwner(), result -> {
            if (result.equals(AppConstants.SUCCESS)) {
                Toast.makeText(
                        getContext(),
                        "Verification email send to: " + userManager.getCurrentUser().getEmail(),
                        Toast.LENGTH_SHORT
                ).show();
                userManager.logout();
            } else {
                Toast.makeText(
                        getContext(),
                        "Failed to send verification email, please try again",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

    }

}