package com.example.smartair;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataManager {

    FirebaseDatabase database;

    public DataManager() {
        database = FirebaseDatabase.getInstance();
    }

    public DatabaseReference getReference(String path) {
        return database.getReference(path);
    }

    public <T> void writeTo(DatabaseReference reference, T value) {
        reference.setValue(value);
    }

    public Task<String> getAccountType(String uid) {
        DatabaseReference ref = getReference("users").child(uid).child("accountType");
        return ref.get().continueWith(task -> {
           if (task.isSuccessful() && task.getResult() != null) {
               DataSnapshot snapshot = task.getResult();
               return snapshot.getValue(String.class);
           } else {
               return null;
           }
        });
    }

    public void setupUser(DatabaseReference reference, String accountType) {
        writeTo(reference.child("accountType"), accountType);
    }

    public void deleteUserData(String userUID) {
        getReference("users").child(userUID).removeValue();
    }

}
