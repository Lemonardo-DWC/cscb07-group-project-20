package com.example.smartair;

import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterViewModel extends ViewModel {

    private final UserManager userManager = new UserManager();
    private final String TAG = "User Registration";


    private final MutableLiveData<String> _emailError
            = new MutableLiveData<String>();
    public LiveData<String> emailError = _emailError;

    private final MutableLiveData<String> _passwordError
            = new MutableLiveData<String>();
    public LiveData<String> passwordError = _passwordError;

    private final MutableLiveData<String> _passwordConfirmationError
            = new MutableLiveData<String>();
    public LiveData<String> passwordConfirmationError = _passwordConfirmationError;

    private final MutableLiveData<String> _registerResult
            = new MutableLiveData<String>();
    public LiveData<String> registerResult = _registerResult;

    private final MutableLiveData<String> _sendEmailVerificationResult
            = new MutableLiveData<String>();
    public LiveData<String> sendEmailVerificationResult = _sendEmailVerificationResult;

    public void register(String email, String password, String passwordConfirmation) {
        Log.d(TAG, "createUser: " + email);

        if(!isValidInputs(email, password, passwordConfirmation)) {
            return;
        }

        userManager.register(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                Log.d(TAG, "createUserWithEmailAndPassword: SUCCESS");
                _registerResult.setValue(AppConstants.SUCCESS);

                if(user != null) {
                    sendEmailVerification(user);
                }

            } else {
                Log.w(TAG, "createUserWithEmailAndPassword: FAIL", task.getException());
                _registerResult.setValue(AppConstants.FAIL);
            }
        });

    }

    private boolean isValidInputs(String email, String password, String passwordConfirmation) {
        boolean valid = true;

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.setValue("Invalid Email");
            valid = false;
        } else {
            _emailError.setValue(null);
        }

        if(password.length() < 6) {
            _passwordError.setValue("Password must be at least 6 characters");
            valid = false;
        } else {
            _passwordError.setValue(null);
        }

        if(!password.equals(passwordConfirmation)) {
            _passwordConfirmationError.setValue("Passwords do not match");
            valid = false;
        } else {
            _passwordConfirmationError.setValue(null);
        }

        return valid;
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                _sendEmailVerificationResult.setValue(AppConstants.SUCCESS);
                            } else {
                                _sendEmailVerificationResult.setValue(AppConstants.FAIL);
                            }
                        }
                );
    }

}
