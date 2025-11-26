package com.example.smartair;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment implements LoginContract.View {

    /// instances related to editable text fields
    private EditText usernameEmailEditText, pwEditText;

    LoginContract.Presenter loginPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        /// essentially loads the UI for the login fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        /// initialize presenter
        loginPresenter = LoginPresenterFactory.provideLoginPresenter(this);

        /// back button handling
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {

            /// NOTE: back button handling in fragments takes precedence over
            /// the back button handling of the activity when displayed. Useful
            /// if certain screens require different behaviour for back button events
            @Override
            public void handleOnBackPressed() {

                /// back button press on log in screen should exit the app
                /// prevents users from going backwards into home and registration screens
                /// after logging out or successful account creation
                requireActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        /// initializes EditText variables and relate them to the views in the corresponding XML file
        usernameEmailEditText = view.findViewById(R.id.login_usernameEmailEntry);
        pwEditText = view.findViewById(R.id.login_pwEntry);

        /// initializes Button variables
        Button buttonLogin = view.findViewById(R.id.buttonLogin);
        Button buttonAccountRecovery = view.findViewById(R.id.buttonAccountRecovery);
        Button buttonRegister = view.findViewById(R.id.buttonRegister);

        /// Button behaviours

        // login button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // sets variables to take on user input for their respective input fields
                String usernameEmail = usernameEmailEditText.getText().toString();
                String password = pwEditText.getText().toString();

                loginPresenter.requestLogin(usernameEmail, password);
            }
        });

        // account recovery button
        buttonAccountRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: recovery screen stuff

                ((MainActivity) requireActivity()).loadFragment(new RecoveryFragment());

            }
        });

        // registration button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: work in progress
                ((MainActivity) requireActivity()).loadFragment(new RegisterFragment());
            }
        });

        return view;
    }

    @Override
    public void showLoginError(String msg) {
        Toast.makeText(
                requireContext(),
                msg,
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    public void loginSuccess(String accountType) {
        switch(accountType){
            case AppConstants.PARENT:
                ((MainActivity) requireActivity()).loadFragment(new ParentHomeFragment());
                break;

            case AppConstants.CHILD:
                ((MainActivity) requireActivity()).loadFragment(new ChildHomeFragment());
                break;

            case AppConstants.PROVIDER:
                // TODO: redirect to provider home screen
                break;

        }
    }
}