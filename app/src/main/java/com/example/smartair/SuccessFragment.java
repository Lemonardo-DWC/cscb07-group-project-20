package com.example.smartair;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class SuccessFragment extends Fragment {

    // temp
    UserManager userManager = new UserManager();
    DataManager dataManager = new DataManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success, container, false);

        /// back button handling ///
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {

            /// home screen is navigation root after logging in, thus back button should send user
            /// out of app
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        /// Button variables ///
        Button buttonLogout = view.findViewById(R.id.buttonLogout);
        Button buttonDeleteAccount = view.findViewById(R.id.buttonDeleteAccount);

        /// Button behaviour ///

        // logout button
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sign user out of account
                userManager.logout();

                // transitions to login screen
                ((MainActivity) requireActivity()).loadFragment(new LoginFragment());
            }
        });

        // delete account button
        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = userManager.getCurrentUser();
                dataManager.deleteUser(dataManager.getReference(user.getUid()));
                userManager.delete();

                ((MainActivity) requireActivity()).loadFragment(new LoginFragment());
            }
        });

        return view;

    }

}