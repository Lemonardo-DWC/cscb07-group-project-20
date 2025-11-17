/**
 * LoginPresenter.java
 *
 * Handles all input validation logic for the Login screen following the MVP pattern.
 * This class receives user input from the View, validates it, and updates the View accordingly.
 *
 */
package com.example.smartair;

import android.util.Patterns;

public class LoginPresenter {
    private final LoginView view;

    public LoginPresenter(LoginView view) {
        this.view = view;
    }

    public void validateInputs(String email, String password) {
        boolean hasError = false;
        if (email == null || email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            view.showEmailError("Invalid email");
            hasError = true;
        }

        if (password == null || password.length() < 6) {
            view.showPasswordError("Password must be at least 6 characters");
            hasError = true;
        }

        if (!hasError) {
            view.loginSuccess();
        }
    }
}
