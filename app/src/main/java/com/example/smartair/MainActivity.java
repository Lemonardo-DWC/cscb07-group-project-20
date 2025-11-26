package com.example.smartair;


import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    /// This will be used for authentication operations such as getting instances of current user,
    /// signing in and out, account creation, etc.
    UserManager userManager;
    DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // lets UI extend behind system bars, not too important
        setContentView(R.layout.activity_main); // sets the layout XML to be used as activity's UI

        /// sets boundaries for UI content, prevents content from going behind system displays
        /// such as status bar or navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /// initialize UserManager, DataManager instance, used for FirebaseAuth and FirebaseDatabase
        /// operations
        userManager = new UserManager();
        dataManager = new DataManager();

        /// loads the login fragment if this is the first creation of the activity
        /// brand new launches of the app will have a null savedInstanceState
        if(savedInstanceState == null){
            loadFragment(new LoginFragment());
        }

        /// general back button handling instance
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                /// if not displaying last fragment, pop the top fragment from back stack
                /// and goes to previous fragment
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    getSupportFragmentManager().popBackStack();
                } else { /// if displaying the last fragment, destroy the activity and exit the app
                    finish();
                }
            }
        };
        /// attaches custom back button behaviour to system back button
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void onStart(){
        super.onStart();

        /// gets instance of currently logged in user and update local user information
        /// from FirebaseAuth server, null if currently no user logged in
        /// jumps to home screen if the instance is non-null
        /// TODO: direct to appropriate home screen based on account type (parent, child, provider)
        FirebaseUser user = userManager.getCurrentUser();

        if (user != null){
            user.reload().addOnCompleteListener(reloadTask -> {
                if(reloadTask.isSuccessful()) {

                    dataManager.getAccountType(user.getUid())
                                    .addOnSuccessListener(accountType -> {

                                        switch (accountType) {
                                            case AppConstants.PARENT:
                                                loadFragment(new SuccessFragment());
                                                break;

                                            case AppConstants.CHILD:
                                                // TODO: redirect to child homescreen
                                                break;

                                            case AppConstants.PROVIDER:
                                                // TODO: redirect to provider homescreen
                                                break;
                                        }

                                    })
                                    .addOnFailureListener(e -> {
                                        userManager.logout();
                                        Toast.makeText(
                                                this,
                                                "Error: could not determine account type",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    });

                } else {
                    userManager.logout();
                    Toast.makeText(
                            this,
                            "An error has occurred, please sign in again",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }


    }

    /// fragment loader, replaces the host fragment_container_view with fragment argument
    /// and adds to back stack, allowing user to go back to previous fragment in general
    ///
    /// essentially used to update screen while allowing for back navigation
    ///
    /// fragments will access this method by invoking the following line to reduce redundancy
    /// ((MainActivity) getActivity()).loadFragment(new fragment());
    ///
    protected void loadFragment(Fragment fragment) {

        /// this instance is useful for adding, replacing, removing, showing/hiding fragments
        /// as well as adding them to the back stack, however, has other operations as well
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        /// replaces whatever is being hosted by the container with the provided fragment
        transaction.replace(R.id.fragment_container_view, fragment);

        /// saves the current fragment state onto the back stack
        transaction.addToBackStack(null);

        /// finalizes transaction and tells the system to schedule the updates
        transaction.commit();
    }

}