package com.example.smartair;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

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

        ///  temp manage children menu
        FirebaseUser user = userManager.getCurrentUser();
        user.reload();

        dataManager.getAccountType(user.getUid()).addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               if (!task.getResult().equals(AppConstants.PARENT)) {
                   view.findViewById(R.id.manageChildrenMenu).setVisibility(View.INVISIBLE);
               }
           } else {
               Log.e("DataManager", "Unable to read account type");
           }
        });

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

        /// EditText variables ///
        EditText deletionConfirmationPassword = view.findViewById(R.id.temp_deletionConfirmationEntry);

        /// Button variables ///
        Button buttonLogout = view.findViewById(R.id.buttonLogout);
        Button buttonDeleteAccount = view.findViewById(R.id.buttonDeleteAccount);
        Button buttonManageChildren = view.findViewById(R.id.buttonManageChildren);

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
                userManager.reauthenticate(
                        deletionConfirmationPassword.getText().toString()
                ).addOnCompleteListener(reAuthTask -> {
                    if (!reAuthTask.isSuccessful()) {
                        Toast.makeText(
                                requireContext(),
                                "Failed to reauthenticate user",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    String userUID = userManager.getCurrentUser().getUid();

                    userManager.delete().addOnCompleteListener(deleteTask -> {
                        if (deleteTask.isSuccessful()) {
                            dataManager.deleteUserData(userUID);
                            ((MainActivity) requireActivity()).loadFragment(new LoginFragment());
                            Log.d("Account Deletion", "Account deletion success");
                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Failed to delete account, please try again",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });

                });


            }
        });

        buttonManageChildren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).loadFragment(new ManageChildFragment());
            }
        });

        return view;

    }

}