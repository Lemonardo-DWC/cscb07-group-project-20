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

public class SuccessFragment extends Fragment {

    FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
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

        /// Button behaviour ///

        // logout button
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sign user out of account
                mAuth.signOut();

                // transitions to login screen
                ((MainActivity) getActivity()).loadFragment(new LoginFragment());
            }
        });

        return view;

    }

}