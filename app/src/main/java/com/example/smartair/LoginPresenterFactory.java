package com.example.smartair;

public class LoginPresenterFactory {

    public static LoginContract.Presenter provideLoginPresenter(LoginContract.View view) {
        LoginContract.Model model = new LoginModel();
        return new LoginPresenter(view, model);
    }

}
