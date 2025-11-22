package com.example.smartair;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import android.content.Context;

public class AuthProvider {

    private final FirebaseAuth auth;

    public AuthProvider(Context context) {

        FirebaseOptions options = FirebaseApp.getInstance().getOptions();

        FirebaseApp app;
        try {
            app = FirebaseApp.getInstance("app");
        } catch (IllegalStateException e) {
            app = FirebaseApp.initializeApp(context, options, "app");
        }

        auth = FirebaseAuth.getInstance(app);

    }

    public FirebaseAuth getAuthInstance() {
        return auth;
    }

}
