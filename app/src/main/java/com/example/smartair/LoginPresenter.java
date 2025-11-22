/**
 * LoginPresenter.java
 *
 * Handles all input validation logic for the Login screen following the MVP pattern.
 * This class receives user input from the View, validates it, and updates the View accordingly.
 *
 */
package com.example.smartair;

import java.util.regex.Pattern;

public class LoginPresenter implements LoginContract.Presenter {

    private final LoginContract.View view;
    private final LoginContract.Model model;

    public LoginPresenter(LoginContract.View view, LoginContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void requestLogin(String usernameEmail, String password) {

        if (isEmailEntry(usernameEmail)) {

            // email login
            Pattern emailPattern = Pattern.compile(AppConstants.EMAIL_REGEX);

            if (!emailPattern.matcher(usernameEmail).matches() || password.length() < 6) {
                view.showLoginError(AppConstants.INVALID_EMAIL_PASS);
            } else {
                model.emailLogin(usernameEmail, password, getCallback());
            }

        } else {

            // username login
            if(password.length() < 6) {
                view.showLoginError(AppConstants.INVALID_EMAIL_PASS);
            } else {
                model.usernameLogin(usernameEmail, password, getCallback());
            }

        }

    }

    private LoginContract.LoginCallback getCallback() {
        return new LoginContract.LoginCallback() {
            @Override
            public void onSuccess(String accountType) {
                view.loginSuccess(accountType);
            }

            @Override
            public void onFailure(String msg) {
                view.showLoginError(msg);
            }
        };
    }

    private boolean isEmailEntry(String usernameEmail) {
        return usernameEmail.contains("@");
    }

}