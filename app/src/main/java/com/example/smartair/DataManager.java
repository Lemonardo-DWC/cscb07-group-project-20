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

    public DatabaseReference getUserReference(String uid) {
        return database.getReference(AppConstants.USERPATH).child(uid);
    }

    public <T> void writeTo(DatabaseReference reference, T value) {
        reference.setValue(value);
    }

    public Task<String> getAccountType(String uid) {
        DatabaseReference ref
                = getReference(AppConstants.USERPATH).child(uid).child(AppConstants.ACCOUNTTYPE);
        return ref.get().continueWith(task -> {
           if (task.isSuccessful() && task.getResult() != null) {
               DataSnapshot snapshot = task.getResult();
               return snapshot.getValue(String.class);
           } else {
               return null;
           }
        });
    }

    public void setupUser(DatabaseReference reference, String email, String accountType,
                          String firstName, String middleName, String lastName) {
        writeTo(reference.child(AppConstants.EMAIL), email);
        writeTo(reference.child(AppConstants.ACCOUNTTYPE), accountType);
        writeTo(reference.child(AppConstants.FIRSTNAME), firstName);
        writeTo(reference.child(AppConstants.MIDDLENAME), middleName);
        writeTo(reference.child(AppConstants.LASTNAME), lastName);
    }

    public void deleteUserData(String userUID) {
        getReference(AppConstants.USERPATH).child(userUID).removeValue();
    }

    public Task<Boolean> exists(DatabaseReference reference) {
        return reference.get().continueWith(task -> {
           if(!task.isSuccessful() || task.getResult() == null) {
               return false;
           }
           DataSnapshot snapshot = task.getResult();
           return snapshot.exists();
        });
    }

    public void setupChild(DatabaseReference userReference,
                           String accountType, String parentUid, String childUid,
                           String username, String email,
                           String firstName, String middleName, String lastName,
                           String birthday, String sex) {

        // user path setup
        writeTo(userReference.child(AppConstants.EMAIL), email);
        writeTo(userReference.child(AppConstants.ACCOUNTTYPE), accountType);

        DatabaseReference basicInfoRef = userReference.child(AppConstants.BASICINFORMATION);
        writeTo(basicInfoRef.child(AppConstants.FIRSTNAME), firstName);
        writeTo(basicInfoRef.child(AppConstants.MIDDLENAME), middleName);
        writeTo(basicInfoRef.child(AppConstants.LASTNAME), lastName);
        writeTo(basicInfoRef.child(AppConstants.BIRTHDAY), birthday);
        writeTo(basicInfoRef.child(AppConstants.SEX), sex);

        // username path setup
        writeTo(getReference(AppConstants.USERNAMEPATH).child(username), email);

        // link parent child
        DatabaseReference childParentListRef = userReference.child(AppConstants.PARENTLIST);
        linkParentChild(childParentListRef, parentUid, childUid);

    }

    public void linkParentChild(DatabaseReference childParentListRef,
                                String parentUid, String childUid) {

        // link child to parent
        writeTo(childParentListRef.child(parentUid), parentUid);

        // link parent to child
        DatabaseReference parentChildListRef
                = getReference(AppConstants.USERPATH).child(parentUid)
                    .child(AppConstants.CHILDLIST).child(childUid);
        writeTo(parentChildListRef, childUid);

    }

    public Task<DataSnapshot> getChildrenUidList(DatabaseReference childUidListRef) {
        return childUidListRef.get();
    }

    public Task<DataSnapshot> getDataSnapshot(DatabaseReference ref) {
        return ref.get();
    }

}
