package com.example.smartair;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserManager {

    FirebaseAuth mAuth;

    public UserManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public Task<AuthResult> login(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public void logout() {
        mAuth.signOut();
    }

    public Task<AuthResult> register(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<Void> delete() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null){
            return user.delete();
        } else {
            return Tasks.forException(new Exception("No user logged in"));
        }
    }

}
