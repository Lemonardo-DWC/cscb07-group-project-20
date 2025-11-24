package com.example.smartair;

import android.util.Patterns;

import androidx.lifecycle.MutableLiveData;

import java.util.regex.Pattern;

public class EntryValidator {

    private final DataManager dataManager = new DataManager();
    public void validateEmail(MutableLiveData<String> emailError, String email) {

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()
                || email.contains(AppConstants.SYNTH_EMAIL_DOMAIN)) {
            emailError.setValue(AppConstants.INVALID_EMAIL);
        } else {
            emailError.setValue(null);
        }

    }

    public void validatePassword(MutableLiveData<String> passwordError, String password) {

        if(password.length() < 6) {
            passwordError.setValue(AppConstants.INVALID_PASS);
        } else {
            passwordError.setValue(null);
        }

    }

    public void validatePasswordConfirmation(MutableLiveData<String> passwordConfirmationError,
                                             String password, String confirmation) {

        if(password.equals(confirmation)) {
            passwordConfirmationError.setValue(null);
        } else {
            passwordConfirmationError.setValue(AppConstants.PASSWORD_MISMATCH);
        }

    }

    public void validateNameFormat(MutableLiveData<String> nameError, String name, Boolean required) {
        Pattern nameFilter = Pattern.compile(AppConstants.NAME_REGEX);

        if (required) {
            if(nameFilter.matcher(name).matches()) {
                nameError.setValue(null);
            } else {
                nameError.setValue("Invalid name format");
            }
        } else {
            if (!name.isEmpty()) {
                if (nameFilter.matcher(name).matches()) {
                    nameError.setValue(null);
                } else {
                    nameError.setValue("Invalid name format");
                }
            } else {
                nameError.setValue(null);
            }
        }
    }

    public void validateUsername(MutableLiveData<String> usernameError,
                                 MutableLiveData<Boolean> usernameValidity, String username) {

        if (username.length() < 6) {
            usernameError.setValue("Username must be at least 6 characters");
            usernameValidity.setValue(false);
            return;
        }

        if (username.contains("@")) {
            usernameError.setValue("Username cannot contain '@'");
            usernameValidity.setValue(false);
            return;
        }

        if (username.contains(" ")) {
            usernameError.setValue("Username cannot contain spaces");
            usernameValidity.setValue(false);
            return;
        }

        dataManager.exists(dataManager.getReference(AppConstants.USERNAMEPATH).child(username))
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean doesExist = task.getResult();
                if (doesExist) {
                    usernameError.setValue("Username taken");
                    usernameValidity.setValue(false);
                } else {
                    usernameError.setValue(null);
                    usernameValidity.setValue(true);
                }
            } else {
                usernameError.setValue("Couldn't verify existence of username");
                usernameValidity.setValue(false);
            }
        });

    }

}
