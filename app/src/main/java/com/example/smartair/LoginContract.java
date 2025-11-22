package com.example.smartair;

/// Interface that allows the presenter to work with LoginFragment and LoginModel
/// while staying independent of Android and Firebase implementations
public interface LoginContract {
    interface View {
        void showLoginError(String msg);
        void loginSuccess(String accountType);
    }

    interface Model {
        void emailLogin(String usernameEmail, String password, LoginCallback callback);
        void usernameLogin(String username, String password, LoginCallback callback);
    }

    interface Presenter {
        void requestLogin(String usernameEmail, String password);
    }

    interface LoginCallback {
        void onSuccess(String accountType);
        void onFailure(String msg);
    }
}
