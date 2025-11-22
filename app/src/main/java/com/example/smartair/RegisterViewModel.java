package com.example.smartair;

import android.provider.ContactsContract;
import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterViewModel extends ViewModel {

    private final UserManager userManager = new UserManager();
    private final DataManager dataManager = new DataManager();
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

    private final MutableLiveData<String> _userEmail
            = new MutableLiveData<String>();
    public LiveData<String> userEmail = _userEmail;

    public void logout() {
        userManager.logout();
    }

    public void register(String email, String password, String passwordConfirmation, String accountType) {
        Log.d(TAG, "createUser: " + email);

        if(!isValidInputs(email, password, passwordConfirmation)) {
            return;
        }

        userManager.register(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                FirebaseUser user = userManager.getCurrentUser();
                DatabaseReference userReference
                        = dataManager.getReference("users").child(user.getUid());

                Log.d(TAG, "createUserWithEmailAndPassword: SUCCESS");
                _registerResult.setValue(AppConstants.SUCCESS);

                dataManager.setupUser(userReference, email, accountType);

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
                                _userEmail.setValue(user.getEmail());
                                _sendEmailVerificationResult.setValue(AppConstants.SUCCESS);
                            } else {
                                dataManager.deleteUserData(user.getUid());
                                userManager.delete();
                                _userEmail.setValue(null);
                                _sendEmailVerificationResult.setValue(AppConstants.FAIL);
                            }
                        }
                );
    }

}
