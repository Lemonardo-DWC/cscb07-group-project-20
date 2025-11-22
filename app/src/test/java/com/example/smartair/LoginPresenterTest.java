//Unit Testing for LoginPresenter
package com.example.smartair;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    @Mock
    LoginContract.Model model;

    @Mock
    LoginContract.View view;

    @Test
    public void testPresenterInitialization() {
        LoginPresenter presenter = new LoginPresenter(view, model);
        assertNotNull(presenter);
    }
    
    @Test
    public void testLoginWithEmail() {
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.requestLogin("apptest264135@gmail.com", "poiqwer");
        verify(model)
                .emailLogin(
                        eq("apptest264135@gmail.com"),
                        eq("poiqwer"),
                        any());
    }
    
    @Test
    public void testLoginWithEmailShortPassword() {
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.requestLogin("apptest264135@gmail.com", "12345");
        verify(view).showLoginError(AppConstants.INVALID_EMAIL_PASS);
    }

    @Test
    public void testLoginWithInvalidEmail() {
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.requestLogin("jack27@", "poiqwer");
        verify(view).showLoginError(AppConstants.INVALID_EMAIL_PASS);
    }

    @Test
    public void testLoginWithUsername() {
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.requestLogin("jack27", "lkjasdf");
        verify(model)
                .usernameLogin(
                        eq("jack27"),
                        eq("lkjasdf"),
                        any());
    }

    @Test
    public void testLoginWithUsernameShortPassword() {
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.requestLogin("jack27", "12345");
        verify(view).showLoginError(AppConstants.INVALID_EMAIL_PASS);
    }

    @Test
    public void testCallbackSuccess() {
        LoginPresenter presenter = new LoginPresenter(view, model);

        doAnswer(invocation -> {
            LoginContract.LoginCallback callback = invocation.getArgument(2);
            callback.onSuccess("dummyString");
            return null;
        }).when(model).emailLogin(anyString(), anyString(), any());

        presenter.requestLogin("apptest264135@gmail.com", "poiqwer");

        verify(view).loginSuccess("dummyString");
    }

    @Test
    public void testCallbackFailure() {
        LoginPresenter presenter = new LoginPresenter(view, model);

        doAnswer(invocation -> {
            LoginContract.LoginCallback callback = invocation.getArgument(2);
            callback.onFailure("dummyString");
            return null;
        }).when(model).emailLogin(anyString(), anyString(), any());

        presenter.requestLogin("apptest264135@gmail.com", "poiqwer");

        verify(view).showLoginError("dummyString");
    }


}
