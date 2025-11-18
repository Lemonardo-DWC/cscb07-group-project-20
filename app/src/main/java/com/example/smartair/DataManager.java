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

    public <T> void writeTo(String path, T value) {
        database.getReference(path).setValue(value);
    }

}
