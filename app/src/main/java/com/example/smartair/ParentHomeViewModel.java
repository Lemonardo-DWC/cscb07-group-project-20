package com.example.smartair;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ParentHomeViewModel extends ViewModel {

    private final String TAG = "ParentHomeEvent";
    private final DataManager dataManager = new DataManager();
    private final UserManager userManager = new UserManager();
    private final ChildItemHelper childItemHelper = new ChildItemHelper();

    private final Map<String, ChildItem> childItemMap = new HashMap<>();
    private final List<String> childOrderList = new ArrayList<>();
    private final Map<String, ValueEventListener> childListenerMap = new HashMap<>();

    private final MutableLiveData<List<ChildItem>> _childItemListData
            = new MutableLiveData<>();
    public LiveData<List<ChildItem>> childItemListData = _childItemListData;

    // for child detail fragment
    private final MutableLiveData<ChildItem> _selectedItem
            = new MutableLiveData<>();
    public LiveData<ChildItem> selectedItem = _selectedItem;

    public ParentHomeViewModel() {}

    public void logout() {
        userManager.logout();
    }

    public void trackChildListRef() {
        getChildListRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot childUidListSnapshot) {

                // new list of child uid
                List<String> newChildUidList = new ArrayList<>();

                // load new uid entries into list
                for(DataSnapshot childUidItem : childUidListSnapshot.getChildren()) {
                    newChildUidList.add(childUidItem.getKey());
                }

                // update list
                if (!newChildUidList.equals(new ArrayList<>(childOrderList))) {
                    updateChildList(newChildUidList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "could not retrieve child list");
            }
        });
    }

    private DatabaseReference getChildListRef() {
        return dataManager.getReference(AppConstants.USERPATH)
                .child(userManager.getCurrentUser().getUid())
                .child(AppConstants.CHILDLIST);
    }

    private void updateChildList(List<String> newChildList) {

        // remove childItems and their listeners if unlisted
        for (String childUid : new ArrayList<>(childListenerMap.keySet())) {
            if (!newChildList.contains(childUid)) {
                childItemMap.remove(childUid);
                removeChildListener(childUid);
            }
        }

        // update order list
        childOrderList.clear();
        childOrderList.addAll(newChildList);

        // no children in list
        if (newChildList.isEmpty()) {
            _childItemListData.setValue(new ArrayList<>());
            return;
        }

        // counter for async tasks
        AtomicInteger remainCount = new AtomicInteger(newChildList.size());

        // load children data
        for (String childUid : newChildList) {

            // child data reference
            DatabaseReference childRef
                    = dataManager.getUserReference(childUid);

            // get snapshot of child data
            dataManager.getDataSnapshot(childRef).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    DataSnapshot childSnapshot = task.getResult(); // get snapshot
                    ChildItem childItem =  childSnapshot.getValue(ChildItem.class);// create entry

                    // add entry to map
                    if (childItem != null) {
                        childItemMap.put(childUid, childItem);
                    }

                    // attach a listener for automatic updates
                    attachChildListener(childUid, childRef);

                } else {
                    Log.d(TAG, "could not get snapshot");
                }

                // finished all async operations and update livedata
                if (remainCount.decrementAndGet() == 0) {
                    reloadChildItemList();
                }
            });

        }

    }

    private void removeChildListener(String childUid) {
        ValueEventListener listener = childListenerMap.get(childUid);

        // remove listener
        if (listener != null){
            DatabaseReference ref = dataManager.getUserReference(childUid);
            ref.removeEventListener(listener);
        }

        // remove map entry
        childListenerMap.remove(childUid);

    }

    private void attachChildListener(String childUid, DatabaseReference childRef) {

        // don't add if listener for child data already exists
        if (childListenerMap.containsKey(childUid)) return;

        // new listener
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // read snapshot data into ChildItem object
                ChildItem newChildItem = snapshot.getValue(ChildItem.class);

                // add entry to map and update livedata
                if (newChildItem != null) {
                    childItemMap.put(childUid, newChildItem);
                    reloadChildItemList();

                    // also update for detail fragment
                    ChildItem curr = _selectedItem.getValue();
                    if (curr != null && curr.getUid().equals(childUid)) {
                        _selectedItem.setValue(newChildItem);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "could not attach child listener to ref: " + childUid);
            }
        };
        childRef.addValueEventListener(listener);

        childListenerMap.put(childUid, listener);
    }

    private void reloadChildItemList() {
        List<ChildItem> list = new ArrayList<>();

        for (String childUid : childOrderList) {
            ChildItem childItem = childItemMap.get(childUid);

            if (childItem != null) {
                list.add(childItem);
            }
        }

        list.sort(new Comparator<ChildItem>() {
            @Override
            public int compare(ChildItem o1, ChildItem o2) {
                return o1.getFirstName().compareTo(o2.getFirstName());
            }
        });

        _childItemListData.setValue(list);
    }

    public void selectItem(ChildItem childItem) {
        _selectedItem.setValue(childItem);
    }

    public LiveData<ChildItem> getSelectedItem() {
        return selectedItem;
    }

    public void updateChildNote(ChildItem childItem) {
        dataManager.writeTo(
                dataManager.getUserReference(childItem.getUid()).child(AppConstants.NOTES),
                childItem.getNotes()
        );
    }

    public void updateChildPB(ChildItem childItem) {
        dataManager.writeTo(
                dataManager.getUserReference(childItem.getUid()).child(AppConstants.PB_PATH),
                childItem.getPb()
        );
    }

    public void updateChildRescue(ChildItem childItem) {
        dataManager.writeTo(
                dataManager.getUserReference(childItem.getUid()).child(AppConstants.RESCUE),
                childItem.rescue
        );
    }

    public void updateChildController(ChildItem childItem) {
        dataManager.writeTo(
                dataManager.getUserReference(childItem.getUid()).child(AppConstants.CONTROLLER),
                childItem.controller
        );
    }

}
