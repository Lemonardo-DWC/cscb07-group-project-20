/**
 * LoginView.java
 *
 * View interface used by the LoginPresenter to communicate results back
 * to the UI. This interface is implemented by the LoginFragment, allowing
 * the Presenter to remain independent of Android framework classes.
 *
 */
package com.example.smartair;

public interface LoginView {
    void showEmailError(String msg);
    void showPasswordError(String msg);
    void loginSuccess();
}
