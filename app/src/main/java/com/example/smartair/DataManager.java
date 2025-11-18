package com.example.smartair;

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

    public void setupUser(DatabaseReference reference, String email, String accountType) {
        writeTo(reference.child("email"), email);
        writeTo(reference.child("accountType"), accountType);
    }
    }

}
