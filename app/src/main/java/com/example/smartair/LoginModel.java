package com.example.smartair;

import android.util.Log;
import com.google.firebase.auth.FirebaseUser;

public class LoginModel implements LoginContract.Model {

    UserManager userManager = new UserManager();
    DataManager dataManager = new DataManager();
    private final String TAG = "User Login";

    @Override
    public void emailLogin(String email, String password, LoginContract.LoginCallback callback) {
        Log.d(TAG, "userLogin:" + email); // log event

        // clear user
        userManager.logout();

        // log in
        userManager.login(email, password).addOnCompleteListener(loginTask -> {

            // listen for task success
            if (loginTask.isSuccessful()){
                FirebaseUser user = userManager.getCurrentUser();

                if (user != null) {

                    // reload user information
                    user.reload().addOnCompleteListener(reloadTask -> {
                        if (user.isEmailVerified()) {
                            // Email verified → allow login
                            dataManager.getAccountType(user.getUid())
                                    .addOnSuccessListener(accountType -> {
                                        callback.onSuccess(accountType);
                                    })
                                    .addOnFailureListener(e -> {
                                        callback.onFailure(AppConstants.CANT_LOAD_ACCOUNT_TYPE);
                                        userManager.logout();
                                    });

                        } else {
                            // Email not verified → force logout
                            callback.onFailure(AppConstants.VERIFY_EMAIL);
                            userManager.logout();
                        }
                    });
                } else {
                    callback.onFailure(AppConstants.LOGIN_ERROR);
                }

            } else {
                Log.d(TAG, "signInWithEmail:fail");
                userManager.logout();
                callback.onFailure(AppConstants.INVALID_EMAIL_PASS);
            }
        });
    }

    @Override
    public void usernameLogin(String username, String password,
                              LoginContract.LoginCallback callback) {
        Log.d(TAG, "userLogin:" + username); // log event

        // clear user
        userManager.logout();

        // log in
        userManager.login(username + AppConstants.SYNTH_EMAIL_DOMAIN, password)
                .addOnCompleteListener(loginTask -> {

            // listen for task success
            if (loginTask.isSuccessful()){
                FirebaseUser user = userManager.getCurrentUser();

                if (user != null) {
                    dataManager.getAccountType(user.getUid())
                            .addOnSuccessListener(accountType -> {
                                callback.onSuccess(accountType);
                            })
                            .addOnFailureListener(e -> {
                                callback.onFailure(AppConstants.CANT_LOAD_ACCOUNT_TYPE);
                                userManager.logout();
                            });
                } else {
                    callback.onFailure(AppConstants.LOGIN_ERROR);
                }

            } else {
                Log.d(TAG, "signInWithUsername:fail");
                userManager.logout();
                callback.onFailure(AppConstants.INVALID_EMAIL_PASS);
            }
        });
    }

}
