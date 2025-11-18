//Unit Testing for LoginPresenter
package com.example.smartair;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.verify;
public class LoginPresenterTest {
    @Mock
    LoginView view;

    LoginPresenter presenter;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        presenter = new LoginPresenter(view);
    }

    @Test
    public void emptyEmailShowsError() {
        presenter.validateInputs("", "123456");
        verify(view).showEmailError(anyString());
    }

    @Test
    public void shortPasswordShowsError() {
        presenter.validateInputs("abc@gmail.com", "12");
        verify(view).showPasswordError(anyString());
    }

    @Test
    public void validInputsNavigateToSuccess() {
        presenter.validateInputs("abc@gmail.com", "123456");
        verify(view).loginSuccess();
    }

    @Test
    public void emptyPasswordShowsError() {
        presenter.validateInputs("abc@gmail.com", "");
        verify(view).showPasswordError(anyString());
    }

    @Test
    public void emptyPasswordAndEmailShowsError() {
        presenter.validateInputs("", "");
        verify(view).showEmailError(anyString());
        verify(view).showPasswordError(anyString());
    }


}
